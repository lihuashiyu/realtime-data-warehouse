#!/usr/bin/env bash


# 服务所在位置
PROJECT_DIR=$(cd "$(dirname "$0")" || exit; pwd)           # 项目路径
MYSQL_HOME=/opt/mysql                                      # Mysql 安装路径
REDIS_HOME=/opt/redis                                      # Redis 安装路径
ZOOKEEPER_HOME=/opt/apache/zookeeper                       # Zookeeper 安装路径
KAFKA_HOME=/opt/apache/kafka                               # Kafka 安装路径
EFAK_HOME=/opt/apache/kafka/efak                           # Kafka 监控路径
HADOOP_HOME=/opt/apache/hadoop                             # Hadoop 安装路径
HBASE_HOME=/opt/apache/hbase                               # HBase 安装路径
MOCK_LOG="${PROJECT_DIR}/../flume"                         # 数据库数据同步工具
MOCK_DB="${PROJECT_DIR}/../maxwell"                        # 数据库数据同步工具
LOG_FILE="real-time-$(date +%F).log"                       # 操作日志


printf "\n"
#  匹配输入参数
case "$1" in
    #  1. 运行程序
    start)
        echo "****************************** 启动 Mysql **********************************"
        "${MYSQL_HOME}/support-files/mysql.server" start >> "${PROJECT_DIR}/${LOG_FILE}" 2>&1

        echo "****************************** 启动 Redis **********************************"
        "${REDIS_HOME}/bin/redis.sh" start >> "${PROJECT_DIR}/${LOG_FILE}" 2>&1

        echo "****************************** 启动 Zookeeper ******************************"
        "${ZOOKEEPER_HOME}/bin/zookeeper.sh" start >> "${PROJECT_DIR}/${LOG_FILE}" 2>&1
         
        echo "****************************** 启动 Kafka **********************************"
        "${KAFKA_HOME}/bin/kafka.sh" start   >> "${PROJECT_DIR}/${LOG_FILE}" 2>&1
                 
        echo "****************************** 启动 Efak ***********************************"
        "${EFAK_HOME}/bin/ke.sh" start   >> "${PROJECT_DIR}/${LOG_FILE}" 2>&1
        
        echo "***************************** 启动 Hadoop **********************************"
        "${HADOOP_HOME}/bin/hadoop.sh" start   >> "${PROJECT_DIR}/${LOG_FILE}" 2>&1
        
        echo "****************************** 启动 Hbase ***********************************"
        "${HBASE_HOME}/bin/hbase.sh" start   >> "${PROJECT_DIR}/${LOG_FILE}" 2>&1
        
        echo "**************************** 启动 Flume 同步 *********************************"
        "${MOCK_LOG}/file-kafka.sh" start   >> "${PROJECT_DIR}/${LOG_FILE}" 2>&1
        
        echo "*************************** 启动 MaxWell 同步 ********************************"
        "${MOCK_DB}/mysql_kafka.sh" start   >> "${PROJECT_DIR}/${LOG_FILE}" 2>&1
    ;;
    
      
    #  2. 停止
    stop)        
        echo "**************************** 停止 Flume 同步 *********************************"
        "${MOCK_LOG}/file-kafka.sh" stop   >> "${PROJECT_DIR}/${LOG_FILE}" 2>&1
        
        echo "*************************** 停止 MaxWell 同步 ********************************"
        "${MOCK_DB}/mysql_kafka.sh" stop   >> "${PROJECT_DIR}/${LOG_FILE}" 2>&1
                
        echo "***************************** 停止 Hadoop **********************************"
        "${HADOOP_HOME}/bin/hadoop.sh" stop   >> "${PROJECT_DIR}/${LOG_FILE}" 2>&1
        
        echo "****************************** 停止 Hbase ***********************************"
        "${HBASE_HOME}/bin/hbase.sh" stop   >> "${PROJECT_DIR}/${LOG_FILE}" 2>&1
        
        echo "****************************** 停止 Kafka **********************************"
        "${KAFKA_HOME}/bin/kafka.sh" stop   >> "${PROJECT_DIR}/${LOG_FILE}" 2>&1
        
        echo "****************************** 停止 Efak ***********************************"
        "${EFAK_HOME}/bin/ke.sh" stop   >> "${PROJECT_DIR}/${LOG_FILE}" 2>&1
                
        echo "****************************** 停止 Zookeeper ******************************"
        "${ZOOKEEPER_HOME}/bin/zookeeper.sh" stop  >> "${PROJECT_DIR}/${LOG_FILE}" 2>&1

        echo "****************************** 停止 Redis **********************************"
        "${REDIS_HOME}/bin/redis.sh" stop >> "${PROJECT_DIR}/${LOG_FILE}" 2>&1

        echo "****************************** 停止 Mysql **********************************"
        "${MYSQL_HOME}/support-files/mysql.server" stop >> "${PROJECT_DIR}/${LOG_FILE}" 2>&1
    ;;
    
    
    #  3. 状态查询
    status)
        echo "****************************** 启动的进程如下： ******************************"
        jps -l | grep -v sun.tools.jps.Jps | grep -v intellij | grep -v jetbrains | sort -n
        "${MYSQL_HOME}/support-files/mysql.server"  status
    ;;
    
    
    #  4. 其它情况
    *)
        echo "    脚本可传入一个参数，如下所示： "
        echo "        +-----------------------------+ "
        echo "        |  start  |  stop  |  status  | "
        echo "        +-----------------------------+ "
        echo "        |    start   ：  启动服务     | "
        echo "        |    stop    ：  关闭服务     | "
        echo "        |    status  ：  查看状态     | "
        echo "        +-----------------------------+ "
    ;;
esac
printf "\n"
