package issac.utils;

import cn.hutool.core.util.ArrayUtil;
import issac.constant.ApplicationConstant;
import issac.constant.NumberConstant;
import issac.constant.SignalConstant;
import issac.constant.UtilConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.state.hashmap.HashMapStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.time.Duration;

/**
 * ***********************************************************************************************
 * ProjectName   ：  test
 * Package       ：  com.yuanwang.utils
 * ClassName     ：  FlinkConfigUtil
 * PATH          ：  src/main/java/com/yuanwang/utils
 * CreateTime    ：  2023-05-31 10:10:35
 * USER          ：  admin
 * IDE           ：  IntelliJ IDEA 2022.3.2
 * Version       ：  1.0
 * CodedFormat   ：  utf-8
 * Description   ：  FlinkConfigUtil 被用于 ==>
 * ***********************************************************************************************
 */
@Slf4j
public class FlinkConfigUtil
{
    
    private static final String HDFS_USER_NAME = "HADOOP_USER_NAME";
    private static final String HDFS_PASS_WORD = "HADOOP_USER_NAME";
    
    
    /**
     * 获取 Flink 环境
     *
     * @return ： 配置好的 env
     */
    public static StreamExecutionEnvironment getFlinkEnv()
    {
        return getFlinkEnv(null);
    }
    
    
    /**
     * 根据配置获取 Flink 环境
     *
     * @param configuration ： Flink 配置信息
     * @return ： 配置好的 env
     */
    public static StreamExecutionEnvironment getFlinkEnv(Configuration configuration)
    {
        System.setProperty(HDFS_USER_NAME, ApplicationConstant.HDFS_USER_NAME);
        
        if (ObjectUtils.isEmpty(configuration))
        {
            return StreamExecutionEnvironment.getExecutionEnvironment();
        }
        
        return StreamExecutionEnvironment.getExecutionEnvironment(configuration);
    }
    
    
    /**
     * 根据配置文件设置 CP
     *
     * @param properties ： 配置文件
     * @return ： 配置好 CP 的 env
     */
    public static StreamExecutionEnvironment checkPointConfig(ParameterTool properties)
    {
        return checkPointConfig(getFlinkEnv(), properties);
    }
    
    
    /**
     * 根据配置文件设置 CP
     *
     * @param env        ： Flink 环境变量
     * @param properties ： 配置文件
     * @return ： 配置好 CP 的 env
     */
    public static StreamExecutionEnvironment checkPointConfig(StreamExecutionEnvironment env, ParameterTool properties)
    {
        // 1. 配置是否开启 CheckPoint
        boolean enable = properties.getBoolean(ApplicationConstant.FLINK_CHECK_POINT_ENABLE, false);
        if (!enable)
        {
            return env;
        }
        
        // 2. 配置 CheckPoint 多长时间一次
        long cpTime = properties.getLong(ApplicationConstant.FLINK_CHECK_POINT_TIME);
        if (cpTime > 0)
        {
            env.enableCheckpointing(cpTime);
        }
        
        // 3. 设置模式为精确一次 (这是默认值)
        String mode = properties.get(ApplicationConstant.FLINK_CHECK_POINT_MODE);
        if (UtilConstant.EXACTLY_ONCE.equalsIgnoreCase(mode))
        {
            env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        } else if (UtilConstant.AT_LEAST_ONCE.equalsIgnoreCase(mode))
        {
            env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.AT_LEAST_ONCE);
        } else
        {
            log.error("配置参数中的参数错误：{} = {}", ApplicationConstant.FLINK_CHECK_POINT_MODE, mode);
        }
        
        // 4. 确认 checkpoints 之间的时间会进行 500 ms
        long minPause = properties.getLong(ApplicationConstant.FLINK_CHECK_POINT_MIN_PAUSE);
        if (minPause > 0)
        {
            env.getCheckpointConfig().setMinPauseBetweenCheckpoints(minPause);
        }
        
        // 5. Checkpoint 完成时间，否则就会被抛弃
        String flinkCheckPointTimeout = ApplicationConstant.FLINK_CHECK_POINT_TIMEOUT;
        long timeout = properties.getLong(flinkCheckPointTimeout);
        if (timeout > 0)
        {
            env.getCheckpointConfig().setCheckpointTimeout(timeout);
        }
        
        // 6. 同一时间允许 checkpoint 进行的个数
        int maxConcurrent = properties.getInt(ApplicationConstant.FLINK_CHECK_POINT_MAX_CONCURRENT);
        if (maxConcurrent > 0)
        {
            env.getCheckpointConfig().setMaxConcurrentCheckpoints(maxConcurrent);
        }
        
        // 7. 开启在 job 中止后仍然保留的 externalized checkpoints
        String cleanup = properties.get(ApplicationConstant.FLINK_CHECK_POINT_CLEANUP).toLowerCase();
        CheckpointConfig.ExternalizedCheckpointCleanup retainOnCancellation;
        switch (cleanup)
        {
            case UtilConstant.RETAIN_ON_CANCELLATION:
                retainOnCancellation = CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION;
                break;
            case UtilConstant.DELETE_ON_CANCELLATION:
                retainOnCancellation = CheckpointConfig.ExternalizedCheckpointCleanup.DELETE_ON_CANCELLATION;
                break;
            case UtilConstant.NO_EXTERNALIZED_CHECKPOINTS:
                retainOnCancellation = CheckpointConfig.ExternalizedCheckpointCleanup.NO_EXTERNALIZED_CHECKPOINTS;
                break;
            default:
                log.error("配置参数中的参数错误： {} = {}", ApplicationConstant.FLINK_CHECK_POINT_CLEANUP, cleanup);
                retainOnCancellation = CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION;
                break;
        }
        
        env.getCheckpointConfig().setExternalizedCheckpointCleanup(retainOnCancellation);
        
        // 8. CheckPoint 保存路径
        String storage = properties.get(ApplicationConstant.FLINK_CHECK_POINT_DIR);
        if (StringUtils.isNotBlank(storage))
        {
            env.getCheckpointConfig().setCheckpointStorage(storage);
        }
        
        // 9. 设置超时时间，超时会被抛弃
        String[] delayRestarts = properties.get(ApplicationConstant.FLINK_CHECK_POINT_DELAY_RESTART).split(SignalConstant.COMMA);
        if (ArrayUtils.isNotEmpty(delayRestarts) && NumberConstant.TWO == delayRestarts.length)
        {
            int restartAttempts = Integer.parseInt(delayRestarts[0].trim());
            long delayBetweenAttempts = Long.parseLong(delayRestarts[1].trim());
            RestartStrategies.RestartStrategyConfiguration strategy = RestartStrategies.fixedDelayRestart(restartAttempts, delayBetweenAttempts);
            env.setRestartStrategy(strategy);
        }
        
        // 10. 闯将状态后端
        env.setStateBackend(new HashMapStateBackend());
        
        return env;
    }
    
    
    /**
     * 配置 Hadoop 代理
     *
     * @param properties ： 配置文件
     */
    public static void hadoopProxy(ParameterTool properties)
    {
        String hdfsUser = properties.get(ApplicationConstant.HDFS_USER_NAME);
        if (StringUtils.isNotBlank(hdfsUser))
        {
            System.setProperty(HDFS_USER_NAME, hdfsUser);
        }
        
        String hdfsPassword = properties.get(ApplicationConstant.HDFS_PASS_WORD);
        if (StringUtils.isNotBlank(hdfsPassword))
        {
            System.setProperty(HDFS_PASS_WORD, hdfsPassword);
        }
    }
    
    
    /**
     * 根据配置文件设置水位线持续时间
     *
     * @return ： 水位线持续时间
     */
    public static <T> WatermarkStrategy<T> watermarkStrategy()
    {
        return watermarkStrategy(UtilConstant.FLINK_WATERMARK_SIZE, UtilConstant.FLINK_WATERMARK_UNIT);
    }
    
    
    /**
     * 根据配置文件设置水位线持续时间
     *
     * @param size ： 水位线大小
     * @param unit ： 水位线单位
     *             
     * @return ： 水位线持续时间
     */
    public static <T> WatermarkStrategy<T> watermarkStrategy(long size, String unit)
    {
        // 若配置的 参数异常，就不使用水位线
        if (size <= 0 || StringUtils.isBlank(unit))
        {
            return WatermarkStrategy.noWatermarks();
        }
        
        Duration duration;
        if (ArrayUtil.containsIgnoreCase(UtilConstant.MILLISECONDS, unit))
        {
            duration = Duration.ofMillis(size);
        } else if (ArrayUtil.containsIgnoreCase(UtilConstant.SECONDS, unit))
        {
            duration = Duration.ofSeconds(size);
        } else if (ArrayUtil.containsIgnoreCase(UtilConstant.MINUTES, unit))
        {
            duration = Duration.ofMinutes(size);
        } else if (ArrayUtil.containsIgnoreCase(UtilConstant.HOURS, unit))
        {
            duration = Duration.ofHours(size);
        } else
        {
            log.error(" 输入的水位线单位配置错误：TimeUnit = {} ", unit);
            return WatermarkStrategy.noWatermarks();
        }
        
        return WatermarkStrategy.<T>forBoundedOutOfOrderness(duration);
    }
    
    
    /**
     * 给操作算子设置 uid，名称 和 slot
     *
     * @param operator ： 算子
     * @param uid      ： uid 和
     * @param <T>
     * @return
     */
    public static <T> SingleOutputStreamOperator<T> operatorUidSlotConfig(SingleOutputStreamOperator<T> operator, String uid)
    {
        return operator.uid(uid).name(uid).slotSharingGroup(uid);
    }
}
