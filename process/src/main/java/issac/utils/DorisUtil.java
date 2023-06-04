package issac.utils;

import issac.constant.ApplicationConstant;
import issac.constant.SignalConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.doris.flink.cfg.DorisExecutionOptions;
import org.apache.doris.flink.cfg.DorisOptions;
import org.apache.doris.flink.cfg.DorisReadOptions;
import org.apache.doris.flink.deserialization.SimpleListDeserializationSchema;
import org.apache.doris.flink.sink.DorisSink;
import org.apache.doris.flink.source.DorisSource;
import org.apache.doris.flink.source.DorisSourceBuilder;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.List;

/**
 * ***********************************************************************************************
 * ProjectName   ：  test
 * Package       ：  com.yuanwang.utils
 * ClassName     ：  DorisUtil
 * PATH          ：  src/main/java/com/yuanwang/utils
 * CreateTime    ：  2023-06-02 13:53:21
 * USER          ：  admin
 * IDE           ：  IntelliJ IDEA 2022.3.2
 * Version       ：  1.0
 * CodedFormat   ：  utf-8
 * Description   ：  DorisUtil 被用于 ==> Doris
 * ***********************************************************************************************
 */
@Slf4j
public class DorisUtil
{
    
    private static final String SOURCE_NAME_PREFIX = "doris-source-";
    private static DorisOptions options;
    private static DorisReadOptions readOptions;
    private static DorisExecutionOptions executionOptions;
    
    
    /**
     * 初始化必须参数
     *
     * @param properties ： 配置参数
     * @param table      ： 表名
     */
    private static void initOptionsParams(ParameterTool properties, String table)
    {
        String host = properties.get(ApplicationConstant.DORIS_HOST);
        if (StringUtils.isBlank(host))
        {
            log.error("Doris 主机为空：DorisHost = {} ，将使用 localhost ", host);
            host = "localhost";
        }
        
        String fePort = properties.get(ApplicationConstant.DORIS_FE_PORT);
        if (StringUtils.isBlank(fePort))
        {
            log.error("Doris 端口号为空：DorisPort = {} ，将使用 8030 默认值 ", fePort);
            fePort = "8030";
        }
        
        String user = properties.get(ApplicationConstant.DORIS_USER);
        if (StringUtils.isBlank(user))
        {
            log.error("Doris 用户名为空：DorisUser = {} ，将使用 root 默认值 ", user);
            user = "root";
        }
        
        String passWord = properties.get(ApplicationConstant.DORIS_PASSWORD);
        if (StringUtils.isBlank(passWord))
        {
            log.error("Doris 密码为空：DorisUser = {} ，将使用 root 默认值 ", passWord);
            throw new NullPointerException();
        }
        
        String database = properties.get(ApplicationConstant.DORIS_DB);
        if (StringUtils.isBlank(database))
        {
            log.error("Doris 数据库必须非空：DorisDatabase = {} ", database);
            throw new NullPointerException();
        }
        
        if (StringUtils.isBlank(table))
        {
            table = properties.get(ApplicationConstant.DORIS_TABLE);
        }
        
        String url = host + SignalConstant.COLON + fePort;
        String tableIdentifier = database + SignalConstant.DOT + table;
        
        DorisUtil.options = DorisOptions.builder().setFenodes(url)
            .setUsername(user)
            .setPassword(passWord)
            .setTableIdentifier(tableIdentifier)
            .build();
    }
    
    
    /**
     * 初始化读取 Doris 时的参数
     *
     * @param properties ： 配置参数
     */
    private static void initReadOptions(ParameterTool properties)
    {
        DorisReadOptions.Builder builder = DorisReadOptions.builder();
        
        // 向 Doris 发送请求的重试次数
        int retry = properties.getInt(ApplicationConstant.DORIS_RETRY, 0);
        if (retry != 0)
        {
            builder = builder.setRequestRetries(retry);
        }
        
        // 向 Doris 发送请求的连接超时时间
        int connectTimeout = properties.getInt(ApplicationConstant.DORIS_CONNECT_TIMEOUT);
        if (connectTimeout > 0)
        {
            builder = builder.setRequestConnectTimeoutMs(connectTimeout);
        }
        
        // 向 Doris 发送请求的读取超时时间
        int readTimeout = properties.getInt(ApplicationConstant.DORIS_READ_TIMEOUT, 0);
        if (readTimeout > 0)
        {
            builder = builder.setRequestReadTimeoutMs(readTimeout);
        }
        
        // 查询 Doris 的超时时间，默认值为1小时，-1表示无超时限制
        int queryTimeout = properties.getInt(ApplicationConstant.DORIS_QUERY_TIMEOUT, 0);
        if (queryTimeout > 0)
        {
            builder = builder.setRequestQueryTimeoutS(queryTimeout);
        }
        
        // 一次从 BE 读取数据的最大行数
        int batchSize = properties.getInt(ApplicationConstant.DORIS_BATCH, 0);
        if (batchSize > 0)
        {
            builder = builder.setRequestBatchSize(batchSize);
        }
        
        DorisUtil.readOptions = builder.build();
    }
    
    
    private static void initExecutionOptions(ParameterTool properties)
    {
        DorisExecutionOptions.Builder builder = DorisExecutionOptions.builder();
        
        builder.setLabelPrefix("label-doris"); //streamload label prefix
        
        DorisUtil.executionOptions = builder.build();
    }
    
    
    /**
     * 读取 Doris 表中数据
     *
     * @return ：  反序列化的 Doris Source
     */
    public static SingleOutputStreamOperator<List<?>> getDorisSource(StreamExecutionEnvironment env)
    {
        return getDorisSource(env, ConfigurationUtil.getProperties());
    }
    
    
    /**
     * 读取 Doris 表中数据
     *
     * @param properties ：  配置参数
     * @return ：  反序列化的 Doris Source
     */
    public static SingleOutputStreamOperator<List<?>> getDorisSource(StreamExecutionEnvironment env, ParameterTool properties)
    {
        return getDorisSource(env, properties, null);
    }
    
    
    /**
     * 读取 Doris 表中数据
     *
     * @param properties ：  配置参数
     * @param table      ：  需要读取的表
     * @return ：  反序列化的 Doris Source
     */
    public static SingleOutputStreamOperator<List<?>> getDorisSource(StreamExecutionEnvironment env, ParameterTool properties, String table)
    {
        if (ObjectUtils.isEmpty(options) || ObjectUtils.isEmpty(readOptions))
        {
            initOptionsParams(properties, table);
            initReadOptions(properties);
        }
        
        DorisSource<List<?>> dorisSource = DorisSourceBuilder.<List<?>>builder()
            .setDorisOptions(options)
            .setDorisReadOptions(readOptions)
            .setDeserializer(new SimpleListDeserializationSchema())
            .build();
        
        WatermarkStrategy<List<?>> strategy = WatermarkStrategy.noWatermarks();
        String sourceName = SOURCE_NAME_PREFIX + table;
        DataStreamSource<List<?>> streamSource = env.fromSource(dorisSource, strategy, sourceName);
        
        return streamSource.uid(sourceName).name(sourceName);
    }
    
    
    public static <T> DorisSink<T> getDorisSink(ParameterTool properties, String table)
    {
        if (ObjectUtils.isEmpty(options) || ObjectUtils.isEmpty(readOptions) || ObjectUtils.isEmpty(executionOptions))
        {
            initOptionsParams(properties, table);
            initReadOptions(properties);
            initExecutionOptions(properties);
        }
        
        DorisSink.Builder<T> builder = DorisSink.builder();
        
        builder.setDorisReadOptions(readOptions)
            .setDorisExecutionOptions(executionOptions)
            // .setSerializer(new SimpleStringSerializer()) //serialize according to string 
            .setDorisOptions(options);
        
        return builder.build();
    }
}
