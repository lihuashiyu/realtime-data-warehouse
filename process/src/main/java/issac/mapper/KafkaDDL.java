package issac.mapper;

import issac.annotation.Mapper;
import issac.annotation.Method;
import issac.annotation.Parameter;
import issac.utils.XmlUtil;

/**
 * ********************************************************************
 * ProjectName   ：  realtime
 * Package       ：  issac.mapper
 * ClassName     ：  DWD
 * CreateTime    ：  2022-11-25 21:48
 * Author        ：  Issac_Al
 * Email         ：  IssacAl@qq.com
 * IDE           ：  IntelliJ IDEA 2020.3.4
 * Version       ：  1.0
 * CodedFormat   ：  utf-8
 * Description   ：  DWD 被用于 ==> 调用 xml 中的 sql
 * ********************************************************************
 */
@Mapper(value = "mapper.kafka")
public class KafkaDDL
{
    /**
     * 获取 @Mapper 注解对应的注解 中 与 @Method 注解相同的节点的 sql 
     * 
     * @param topic          ： 参数
     * @param kafkaServer    ： 参数
     * @param groupId        ： 参数
     * @return               ： sql
     */
    @Method(value = "KafkaDDL")
    public String getKafkaDDL(@Parameter("topic") String topic, @Parameter("kafkaServer") String kafkaServer, @Parameter("groupId") String groupId) 
    { 
        return XmlUtil.parser(topic, kafkaServer, groupId); 
    }
    
    
    @Method(value = "SinkDDL")
    public String getKafkaSinkDDL(@Parameter("topic") String topic, @Parameter("kafkaServer") String kafkaServer) 
    { 
        return XmlUtil.parser(topic, kafkaServer); 
    }
    
    @Method(value = "UpsertDDL")
    public String getUpsertKafkaDDL(@Parameter("topic") String topic, @Parameter("kafkaServer") String kafkaServer) 
    { 
        return XmlUtil.parser(topic); 
    }
    
    
    @Method(value = "GetTopicDb")
    public String getTopicDb() 
    { 
        return XmlUtil.parser(); 
    }
}
