package issac.app.dwd;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import issac.constant.DwdConstant;
import issac.constant.NumberConstant;
import issac.utils.DateFormatUtil;
import issac.utils.IssacKafkaUtil;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

// 数据流：web/app -> Nginx -> 日志服务器(.log) -> Flume -> Kafka(ODS) -> FlinkApp -> Kafka(DWD)
// 程  序：     Mock(lg.sh) -> Flume(f1) -> Kafka(ZK) -> BaseLogApp -> Kafka(ZK)
public class BaseLogApp
{
    public static void main(String[] args) throws Exception
    {
        // 1.获取执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1); // 生产环境中设置为Kafka主题的分区数
        
        // 1.1 开启CheckPoint
        // env.enableCheckpointing(5 * 60000L, CheckpointingMode.EXACTLY_ONCE);
        // env.getCheckpointConfig().setCheckpointTimeout(10 * 60000L);
        // env.getCheckpointConfig().setMaxConcurrentCheckpoints(2);
        // env.setRestartStrategy(RestartStrategies.fixedDelayRestart(3, 5000L));
        
        // 1.2 设置状态后端
        // env.setStateBackend(new HashMapStateBackend());
        // env.getCheckpointConfig().setCheckpointStorage("hdfs:// hadoop102:8020/211126/ck");
        // System.setProperty("HADOOP_USER_NAME", "atguigu");
        
        // 2.消费 Kafka topic_log 主题的数据创建流
        String topic = DwdConstant.DWD_BASE_LOG_KAFKA_TOPIC;
        String groupId = DwdConstant.DWD_BASE_LOG_CONSUMER_GROUP;
        DataStreamSource<String> kafkaDS = env.addSource(IssacKafkaUtil.getFlinkKafkaConsumer(topic, groupId));
        
        // 3.过滤掉非 JSON 格式的数据&将每行数据转换为 JSON 对象
        OutputTag<String> dirtyTag = new OutputTag<String>(DwdConstant.FIELD_DIRTY) { };
        
        ProcessFunction<String, JSONObject> processFunction = new ProcessFunction<String, JSONObject>()
        {
            @Override
            public void processElement(String value, Context ctx, Collector<JSONObject> out)
            {
                try
                {
                    JSONObject jsonObject = JSON.parseObject(value);
                    out.collect(jsonObject);
                } catch (Exception e) {ctx.output(dirtyTag, value);}
            }
        };
        
        SingleOutputStreamOperator<JSONObject> jsonObjDS = kafkaDS.process(processFunction);
        
        // 获取侧输出流脏数据并打印
        DataStream<String> dirtyDS = jsonObjDS.getSideOutput(dirtyTag);
        dirtyDS.print("Dirty>>>>>>>>>>>>");
        
        // 4.按照Mid分组
        KeyedStream<JSONObject, String> keyedStream = jsonObjDS.keyBy(json -> json.getJSONObject(DwdConstant.FIELD_COMMON).getString(DwdConstant.FIELD_MID));
        
        // 5.使用状态编程做新老访客标记校验
        SingleOutputStreamOperator<JSONObject> jsonObjWithNewFlagDS = keyedStream.map(new RichMapFunction<JSONObject, JSONObject>()
        {
            private ValueState<String> lastVisitState;
            
            @Override
            public void open(Configuration parameters)
            {
                ValueStateDescriptor<String> descriptor = new ValueStateDescriptor<>(DwdConstant.FIELD_LAST_VISIT, String.class);
                lastVisitState = getRuntimeContext().getState(descriptor);
            }
            
            @Override
            public JSONObject map(JSONObject value) throws Exception
            {
                // 获取is_new标记 & ts 并将时间戳转换为年月日
                JSONObject commonJsonObject = value.getJSONObject(DwdConstant.FIELD_COMMON);
                String isNew = commonJsonObject.getString(DwdConstant.FIELD_IS_NEW);
                Long ts = value.getLong(DwdConstant.FIELD_TS);
                String curDate = DateFormatUtil.toDate(ts);
                
                // 获取状态中的日期
                String lastDate = lastVisitState.value();
                
                // 判断is_new标记是否为"1"
                if ("1".equals(isNew))
                {
                    if (lastDate == null) { lastVisitState.update(curDate); } 
                    else if (!lastDate.equals(curDate)) 
                    {
                        commonJsonObject.put(DwdConstant.FIELD_IS_NEW, StrUtil.format("{}", NumberConstant.ZERO)); 
                    }
                } else if (lastDate == null)
                {
                    lastVisitState.update(DateFormatUtil.toDate(ts - 24 * 60 * 60 * 1000L));
                }
                
                return value;
            }
        });
        
        // 6.使用侧输出流进行分流处理  页面日志放到主流  启动、曝光、动作、错误放到侧输出流
        OutputTag<String> startTag = new OutputTag<String>(DwdConstant.FIELD_START) { };
        OutputTag<String> displayTag = new OutputTag<String>(DwdConstant.FIELD_DISPLAY) { };
        OutputTag<String> actionTag = new OutputTag<String>(DwdConstant.FIELD_ACTION) { };
        OutputTag<String> errorTag = new OutputTag<String>(DwdConstant.FIELD_ERROR) { };
        SingleOutputStreamOperator<String> pageDS = jsonObjWithNewFlagDS.process(new ProcessFunction<JSONObject, String>()
        {
            @Override
            public void processElement(JSONObject value, Context ctx, Collector<String> out)
            {
                // 尝试获取错误信息
                String err = value.getString("err");
                
                // 将数据写到 error 侧输出流
                if (err != null) { ctx.output(errorTag, value.toJSONString()); }         
                
                // 移除错误信息
                value.remove(DwdConstant.FIELD_ERR);
                
                // 尝试获取启动信息
                String start = value.getString(DwdConstant.FIELD_START);
                if (start != null)
                {
                    // 将数据写到start侧输出流
                    ctx.output(startTag, value.toJSONString());
                } else
                {
                    // 获取公共信息&页面id&时间戳
                    String common = value.getString(DwdConstant.FIELD_COMMON);
                    String pageId = value.getJSONObject(DwdConstant.FIELD_PAGE).getString(DwdConstant.FIELD_PAGE_ID);
                    Long ts = value.getLong(DwdConstant.FIELD_TS);
                    
                    // 尝试获取曝光数据
                    JSONArray displays = value.getJSONArray(DwdConstant.FIELD_DISPLAY);
                    if (displays != null && displays.size() > 0)
                    {
                        // 遍历曝光数据&写到display侧输出流
                        for (int i = 0; i < displays.size(); i++)
                        {
                            JSONObject display = displays.getJSONObject(i);
                            display.put(DwdConstant.FIELD_COMMON, common);
                            display.put(DwdConstant.FIELD_PAGE_ID, pageId);
                            display.put(DwdConstant.FIELD_TS, ts);
                            ctx.output(displayTag, display.toJSONString());
                        }
                    }
                    
                    // 尝试获取动作数据
                    JSONArray actions = value.getJSONArray(DwdConstant.FIELD_ACTIONS);
                    if (actions != null && actions.size() > 0)
                    {
                        // 遍历曝光数据&写到display侧输出流
                        for (int i = 0; i < actions.size(); i++)
                        {
                            JSONObject action = actions.getJSONObject(i);
                            action.put(DwdConstant.FIELD_COMMON, common);
                            action.put(DwdConstant.FIELD_PAGE_ID, pageId);
                            ctx.output(actionTag, action.toJSONString());
                        }
                    }
                    
                    // 移除曝光和动作数据&写到页面日志主流
                    value.remove(DwdConstant.FIELD_DISPLAYS);
                    value.remove(DwdConstant.FIELD_ACTIONS);
                    out.collect(value.toJSONString());
                }
            }
        });
        
        // 7.提取各个侧输出流数据
        DataStream<String> startDS = pageDS.getSideOutput(startTag);
        DataStream<String> displayDS = pageDS.getSideOutput(displayTag);
        DataStream<String> actionDS = pageDS.getSideOutput(actionTag);
        DataStream<String> errorDS = pageDS.getSideOutput(errorTag);
        
        // 8.将数据打印并写入对应的主题
        pageDS.print("Page>>>>>>>>>>");
        startDS.print("Start>>>>>>>>");
        displayDS.print("Display>>>>");
        actionDS.print("Action>>>>>>");
        errorDS.print("Error>>>>>>>>");
        
        pageDS.addSink(IssacKafkaUtil.getFlinkKafkaProducer(DwdConstant.PAGE_TOPIC));
        startDS.addSink(IssacKafkaUtil.getFlinkKafkaProducer(DwdConstant.START_TOPIC));
        displayDS.addSink(IssacKafkaUtil.getFlinkKafkaProducer(DwdConstant.DISPLAY_TOPIC));
        actionDS.addSink(IssacKafkaUtil.getFlinkKafkaProducer(DwdConstant.ACTION_TOPIC));
        errorDS.addSink(IssacKafkaUtil.getFlinkKafkaProducer(DwdConstant.ERROR_TOPIC));
        
        // 9.启动任务
        env.execute(DwdConstant.DWD_BASE_APP_NAME);
    }
}
