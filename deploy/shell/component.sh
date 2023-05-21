#!/usr/bin/env bash

# =========================================================================================
#    FileName      ：  component.sh
#    CreateTime    ：  2023-02-24 01:44
#    Author        ：  lihua shiyu
#    Email         ：  lihuashiyu@github.com
#    Description   ：  component.sh 被用于 ==> 大数据组件（Hadoop、Flink、Zookeeper、Kafka
#                                             Mysql、Hbase、Phoenix、Doris）的启停脚本  
# =========================================================================================


# 服务所在位置
SERVICE_DIR=$(cd "$(dirname "$0")" || exit; pwd)           # 脚本所在路径
MYSQL_HOME="/opt/db/mysql"                                 # Mysql 安装路径
REDIS_HOME=/opt/redis                                      # Redis 安装路径
HADOOP_HOME="/opt/apache/hadoop"                           # Hadoop 安装路径
Flink_HOME="/opt/apache/flink"                             # Flink 安装路径
HBase_HOME="/opt/apache/hbase"                             # HBase 安装路径
PHOENIX_HOME="/opt/apache/phoenix"                         # Phoenix 安装路径
DORIS_HOME="/opt/apache/doris"                             # Doris 安装路径
ZOOKEEPER_HOME="/opt/apache/zookeeper"                     # Zookeeper 安装路径
KAFKA_HOME="/opt/apache/kafka"                             # Kafka 安装路径
EFAK="/opt/apache/kafka/efak"                              # EFAK 安装路径

LOG_FILE="component-$(date +%F).log"                       # 操作日志


# 组件启动的 java 进程
function service_status()
{
    # 1. 查看 Mysql 运行状态
    "${SERVICE_DIR}/xcall.sh" "\"${MYSQL_HOME}/bin/mysql.sh status\""
    
    # 2. 查看启动的 jvm 进程
    "${SERVICE_DIR}/xcall.sh" "\"jps -l | sort -t ' ' -k 2 | grep -v sun.tools.jps.Jps \""
}


# 大数据组件的启动
function service_start()
{
    echo "****************************** 启动 Mysql **********************************"
    "${MYSQL_HOME}/bin/mysql.sh"         start >> "${SERVICE_DIR}/logs/${LOG_FILE}" 2>&1
    
    echo "****************************** 启动 Redis **********************************"
    "${REDIS_HOME}/bin/redis.sh" start >> "${PROJECT_DIR}/${LOG_FILE}" 2>&1
    
    echo "****************************** 启动 Zookeeper ******************************"
    "${ZOOKEEPER_HOME}/bin/zookeeper.sh" start >> "${SERVICE_DIR}/logs/${LOG_FILE}" 2>&1
    
    echo "****************************** 启动 Hadoop *********************************"
    "${HADOOP_HOME}/bin/hadoop.sh"       start >> "${SERVICE_DIR}/logs/${LOG_FILE}" 2>&1
     
    echo "****************************** 启动 Flink **********************************"
    "${Flink_HOME}/bin/spark.sh"         start >> "${SERVICE_DIR}/logs/${LOG_FILE}" 2>&1
     
    echo "****************************** 启动 HBase **********************************"
    "${HBase_HOME}/bin/hive.sh"           start >> "${SERVICE_DIR}/logs/${LOG_FILE}" 2>&1
     
    echo "****************************** 启动 Phoenix ********************************"
    # "${PHOENIX_HOME}/bin/phoenix.py"    start >> "${SERVICE_DIR}/logs/${LOG_FILE}" 2>&1
    
    echo "****************************** 启动 Kafka **********************************"
    "${KAFKA_HOME}/bin/kafka.sh"         start >> "${SERVICE_DIR}/logs/${LOG_FILE}" 2>&1
    
    echo "****************************** 启动 Doris **********************************"
    "${DORIS_HOME}/bin/doris.sh"         start >> "${SERVICE_DIR}/logs/${LOG_FILE}" 2>&1
        
    echo "****************************** 启动 EFAK ***********************************"
    "${EFAK}/bin/ke.sh"                  start >> "${SERVICE_DIR}/logs/${LOG_FILE}" 2>&1
}


# 大数据组件的停止
function service_stop()
{
            
    echo "****************************** 停止 EFAK ***********************************"
    "${EFAK}/bin/ke.sh"                  stop >> "${SERVICE_DIR}/logs/${LOG_FILE}" 2>&1
    
    echo "****************************** 停止 Kafka **********************************"
    "${KAFKA_HOME}/bin/kafka.sh"         stop >> "${SERVICE_DIR}/logs/${LOG_FILE}" 2>&1
        
    echo "****************************** 停止 HBase **********************************"
    "${HBase_HOME}/bin/hive.sh"           stop >> "${SERVICE_DIR}/logs/${LOG_FILE}" 2>&1
        
    echo "****************************** 停止 Phoenix ********************************"
    "${PHOENIX_HOME}/bin/phoenix.py"         stop >> "${SERVICE_DIR}/logs/${LOG_FILE}" 2>&1
    
    echo "****************************** 停止 Flink **********************************"
    "${Flink_HOME}/bin/flink.sh"         stop >> "${SERVICE_DIR}/logs/${LOG_FILE}" 2>&1
    
    echo "****************************** 停止 Hadoop *********************************"
    "${HADOOP_HOME}/bin/hadoop.sh"       stop >> "${SERVICE_DIR}/logs/${LOG_FILE}" 2>&1
        
    echo "****************************** 停止 Zookeeper ******************************"
    "${ZOOKEEPER_HOME}/bin/zookeeper.sh" stop >> "${SERVICE_DIR}/logs/${LOG_FILE}" 2>&1
        
    echo "****************************** 停止 Mysql **********************************"
    "${MYSQL_HOME}/bin/mysql.sh"         stop >> "${SERVICE_DIR}/logs/${LOG_FILE}" 2>&1
    
    echo "****************************** 停止 Doris **********************************"
    "${DORIS_HOME}/bin/doris.sh"         stop >> "${SERVICE_DIR}/logs/${LOG_FILE}" 2>&1   
    
    echo "****************************** 停止 Redis **********************************"
    "${REDIS_HOME}/bin/redis.sh"         stop >> "${PROJECT_DIR}/${LOG_FILE}" 2>&1
}


printf "\n"
#  匹配输入参数
case "$1" in
    # 1. 运行程序
    start)
        service_start
    ;;
    
    # 2. 停止
    stop)
        service_stop
    ;;
    
    # 3. 重启
    restart)
        service_stop
        sleep 1
        service_start
    ;;
    
    # 4. 状态查询
    status)
        service_status
    ;;
    
    # 5. 其它情况
    *)
        echo "================================================================================"
        echo "    脚本可传入一个参数，如下所示：                     "
        echo "        +-------------------------------------------+ "
        echo "        |   start  |  stop  |  restart  |  status   | "
        echo "        +-------------------------------------------+ "
        echo "        |         start      ：    启动服务         | "
        echo "        |         stop       ：    关闭服务         | "
        echo "        |         restart    ：    重启服务         | "
        echo "        |         status     ：    查看状态         | "
        echo "        +-------------------------------------------+ "
        echo "================================================================================"
    ;;
esac
printf "\n"
exit 0
