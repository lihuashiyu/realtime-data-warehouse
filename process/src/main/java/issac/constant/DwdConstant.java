package issac.constant;

/**
 * ********************************************************************
 * ProjectName   ：  realtime
 * Package       ：  issac.constant
 * ClassName     ：  DwdConstant
 * CreateTime    ：  2022-11-11 00:21
 * Author        ：  Issac_Al
 * Email         ：  IssacAl@qq.com
 * IDE           ：  IntelliJ IDEA 2020.3.4
 * Version       ：  1.0
 * CodedFormat   ：  utf-8
 * Description   ：  DwdConstant 被用于 ==>
 * ********************************************************************
 */

public class DwdConstant
{
    public static final String DWD_BASE_APP_NAME = "BaseLogApp";
    
    // 表
    public static final String DWD_TABLE_BASE_DIC = "base_dic";
    
    // Kafka 配置
    public static final String DWD_BASE_LOG_KAFKA_TOPIC = "topic_log";
    public static final String DWD_BASE_DB_KAFKA_TOPIC = "topic_db";
    
    // BaseLog Topic
    public static final String DWD_BASE_LOG_CONSUMER_GROUP = "base_log_app";
    public static final String PAGE_TOPIC = "dwd_traffic_page_log";
    public static final String START_TOPIC = "dwd_traffic_start_log";
    public static final String DISPLAY_TOPIC = "dwd_traffic_display_log";
    public static final String ACTION_TOPIC = "dwd_traffic_action_log";
    public static final String ERROR_TOPIC = "dwd_traffic_error_log";
    
    // DwdInteractionComment Topic
    public static final String DWD_INTERACTION_COMMENT = "dwd_interaction_comment"; 
    
    // BaseLog 的 Json 日志中的相关字段
    public static final String FIELD_ACTIONS = "actions";
    public static final String FIELD_DISPLAYS = "displays";
    
    public static final String FIELD_DIRTY = "Dirty";
    public static final String FIELD_COMMON = "common";
    public static final String FIELD_MID = "mid";
    public static final String FIELD_LAST_VISIT = "last-visit";
    public static final String FIELD_IS_NEW = "is_new";    
    public static final String FIELD_TS = "ts";    
    public static final String FIELD_START = "start";    
    public static final String FIELD_DISPLAY = "display";    
    public static final String FIELD_ACTION = "action";    
        
    public static final String FIELD_ERROR = "error";    
    public static final String FIELD_ERR = "err";    
    public static final String FIELD_PAGE = "page";    
    public static final String FIELD_PAGE_ID = "page_id";    
}
