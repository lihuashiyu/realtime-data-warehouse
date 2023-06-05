package issac.constant;

/**
 * **************************************************************************************************
 * ProjectName   ：  realtime
 * Package       ：  issac.constant
 * ClassName     ：  UtilConstant
 * CreateTime    ：  2023-06-04 17:18
 * Author        ：  lihuashiyu
 * Email         ：  lihuashiyu@github.com
 * IDE           ：  IntelliJ IDEA 2020.3.4
 * Version       ：  1.0
 * CodedFormat   ：  utf-8
 * Description   ：  Java Class
 * **************************************************************************************************
 */

public class UtilConstant
{
    // =============================================== ConfigurationUtil ===============================================
    public static final String CONFIG_DIRECTORY = "config";                    // 配置文件夹名称
    public static final String CONF_DIRECTORY = "conf";                        // 配置文件夹名称
    public static final String FILE_ENCODING = "UTF-8";                        // 配置文件编码
    
    // ================================================ FlinkConfigUtil ================================================
    public static final long FLINK_WATERMARK_SIZE = 10;                       // 默认情况下，WaterMark 大小
    public static final String FLINK_WATERMARK_UNIT = "s";                    // 默认情况下，WaterMark 单位
    
    public final static String EXACTLY_ONCE = "exactly_once";                  // 精确一次
    public final static String AT_LEAST_ONCE = "at_least_once";                // 至少一次
    
    public final static String RETAIN_ON_CANCELLATION = "retain";              // 取消时保留
    public final static String NO_EXTERNALIZED_CHECKPOINTS = "no";             // 没有外部检查点
    public final static String DELETE_ON_CANCELLATION = "delete";              // 取消删除
    
    // ================================================== Kafka Util ===================================================
    public final static String COMMIT_OFFSET = "commit";                       // 从消费组提交的位点开始消费，如果提交位点不存在，使用最早位点
    public final static String EARLIEST_OFFSET = "earliest";                   // 从最早位点开始消费
    public final static String LATEST_OFFSET = "latest";                       // 从最末尾位点开始消费
    
    public static final String TRANSACTION_TIMEOUT = "transaction.timeout.ms";
    public static final String TRANSACTION_TIMEOUT_SIZE = "3000";
    
    // ================================================ Flink CDC Util =================================================
    public static final String CDC_INSERT = "insert";
    public static final String CDC_DELETE = "delete";
    public static final String CDC_UPDATE = "update";
    public static final String CDC_TRUNCATE = "truncate";
    public static final String CDC_SELECT = "select";
    
    // =================================================== 时间单位 ====================================================
    public static final String[] MILLISECONDS = {"ms", "millisecond", "milli_second"};   // 毫秒
    public static final String[] SECONDS = {"s", "second"};                     // 秒
    public static final String[] MINUTES = {"m", "min", "minute"};              // 分钟
    public static final String[] HOURS = {"h", "hour"};                         // 小时
}
