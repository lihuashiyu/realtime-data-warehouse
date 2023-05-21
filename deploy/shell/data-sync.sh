#!/usr/bin/env bash

# =========================================================================================
#    FileName      ：  data-sync.sh
#    CreateTime    ：  2023-02-24 01:43
#    Author        ：  lihua shiyu
#    Email         ：  lihuashiyu@github.com
#    Description   ：  data-sync 被用于 ==> 整个数据流向数仓的启停脚本
# =========================================================================================
    
    
PROJECT_DIR=$(cd "$(dirname "$0")/../" || exit; pwd)       # 项目根路径
MOCK_LOG_HOST_LIST=(slaver1 slaver2 slaver3)               # 需要生成 历史 行为日志 的节点
MOCK_DB_HOST_LIST=(slaver1 slaver2 slaver3)                # 需要生成 历史 业务数据 的节点 
YARN_HOST=master                                           # Hadoop Yarn 组件安装的节点 
LOG_FILE="warehouse-$(date +%F).log"                       # 操作日志
USER=$(whoami)                                             # 当前用户
    

# ============================================= 定义函数 ============================================== 
# 1. 模拟生成 用户行为日志
function generate_log()
{   
    for host_name in "${MOCK_LOG_HOST_LIST[@]}"
    do
        echo "****************************** ${host_name}：生成日志 ******************************"
        ssh "${USER}@${host_name}" "source ~/.bashrc; source /etc/profile; ${PROJECT_DIR}/mock-log/cycle.sh" >> "${SERVICE_DIR}/logs/${LOG_FILE}" 2>&1
    done
}

# 2. 监控本地 用户行为日志 并同步到 Kafka
function log_monitor()
{
    for host_name in "${MOCK_LOG_HOST_LIST[@]}"
    do
        echo "****************************** 监控（${host_name}：${PROJECT_DIR}/file-kafka/log/mock-*） 并同步到 Kafka ******************************"
        ssh "${USER}@${host_name}" "source ~/.bashrc; source /etc/profile; ${PROJECT_DIR}/file-kafka/file-kafka.sh $1" >> "${SERVICE_DIR}/logs/${LOG_FILE}" 2>&1
    done    
}

# 3. 模拟生成 实时业务数据
function generate_db()
{
    # 生成数据
    for host_name in "${MOCK_DB_HOST_LIST[@]}"
    do
        echo "****************************** ${host_name}：生成数据 ******************************"
        ssh "${USER}@${host_name}" "source ~/.bashrc; source /etc/profile; ${PROJECT_DIR}/mock-db/cycle.sh" >> "${SERVICE_DIR}/logs/${LOG_FILE}" 2>&1
    done
}

# 4. 监控数据库 实时业务数据 并同步到 Kafka
function db_monitor()
{
    echo "***************************** Flink CDC 监控 Mysql 并同步到 kafka *****************************"
    "${PROJECT_DIR}/mysql-hdfs/mysql-cdc-1.0.sh $1" >> "${SERVICE_DIR}/logs/${LOG_FILE}" 2>&1    
}
    
    
printf "\n================================================================================\n"
#  匹配输入参数
case "$1" in
    # 1. 运行程序
    start)
        log_monitor   start
        db_monitor    start
        generate_log
        generate_db
    ;;
    
    # 2. 停止
    stop)
        log_monitor stop
        db_monitor  stop
    ;;
    
    # 3. 停止
    restart)
        log_monitor   restart
        db_monitor    restart
        generate_db
        generate_log
    ;;
    
    # 4. 状态查询
    status)
        log_monitor  status
        db_monitor   status
    ;;
    
    # 4. 其它情况
    *)
        echo "    脚本可传入一个参数，如下所示：                     "
        echo "        +-------------------------------------------+ "
        echo "        |   start  |  stop  |  restart  |  status   | "
        echo "        +-------------------------------------------+ "
        echo "        |         start      ：    启动服务         | "
        echo "        |         stop       ：    关闭服务         | "
        echo "        |         restart    ：    重启服务         | "
        echo "        |         status     ：    查看状态         | "
        echo "        +-------------------------------------------+ "
    ;;
esac
printf "================================================================================\n\n"
exit 0
