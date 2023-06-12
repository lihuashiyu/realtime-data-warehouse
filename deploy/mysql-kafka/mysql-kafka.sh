#!/usr/bin/env bash
    
# =========================================================================================
#    FileName      ：  mysql-kafka_init.sh
#    CreateTime    ：  2023-02-24 01:44
#    Author        ：  lihua shiyu
#    Email         ：  lihuashiyu@github.com
#    Description   ：  mysql-kafka.sh 被用于 ==> 该脚本的作用是初始化所有的业务数据，
#                                               只需在初始化时执行一次即可
# =========================================================================================
    
    
SERVICE_DIR=$(cd "$(dirname "$0")" || exit; pwd)           # 服务位置
MAXWELL_HOME=/opt/github/maxwell                           # MaxWell 安装路径
SERVICE_NAME=com.zendesk.maxwell.Maxwell                   # MaxWell jar 名字
ALIAS_NAME="Mysql -> MaxWell -> Kafka"                     # 程序别名
PROFILE=config.properties                                  # 配置文件
LOG_FILE="mysql-kafka-$(date +%F).log"                     # 操作日志存储

USER=$(whoami)                                             # 服务运行用户
RUN_STATUS=1                                               # 服务运行状态
STOP_STATUS=0                                              # 服务停止状态


# ============================================= 函数定义 ============================================ #
# 服务状态检测
function service_status()
{
    # 1. 获取 pid 个数
    pid_count=$(ps -aux | grep -i "${USER}" | grep -i "${SERVICE_NAME}" | grep "${SERVICE_DIR}/${PROFILE}" | grep -v grep  | grep -v "$0" | wc -l)
    
    # 2. 判断程序循行状态
    if [ "${pid_count}" -eq 1 ]; then
        echo "${RUN_STATUS}"
    elif [ "${pid_count}" -le 1 ]; then
        echo "${STOP_STATUS}"
    else
        echo "    查看程序是否有重复使用的状况 ......"
    fi
}

# 服务启动
function service_start()
{
    # 1. 统计正在运行程序的 pid 的个数
    pc=$(service_status)
    
    # 2. 判断程序的状态
    if [ "${pc}" == "${STOP_STATUS}" ]; then
        # 2.1 启动 MaxWell
        ${MAXWELL_HOME}/bin/maxwell --config "${SERVICE_DIR}/${PROFILE}" \
                                    --daemon >> "${SERVICE_DIR}/logs/${LOG_FILE}" 2>&1
        
        echo "    程序（${ALIAS_NAME}）正在启动中 ......"
        sleep 2 
        echo "    程序（${ALIAS_NAME}）启动验证中 ......"
        sleep 3 
        
        # 2.2 判断程序启动是否成功
        count=$(service_status)
        if [ "${count}" -eq 1 ]; then
            echo "    程序（${ALIAS_NAME}）启动成功 ......"
        else
            echo "    程序（${ALIAS_NAME}）启动失败 ......"
        fi
    elif [ "${pc}" == "${RUN_STATUS}" ]; then
        echo "    程序（${ALIAS_NAME}）正在运行中 ......"
    else    
        echo "    程序（${ALIAS_NAME}）运行出现问题 ......"
    fi
}

# 服务停止
function service_stop()
{
    # 1 统计正在运行程序的 pid 的个数
    pc=$(service_status)
    if [ "${pc}" == "${STOP_STATUS}" ]; then
        echo "    程序（${ALIAS_NAME}）进程不存在，未在运行 ......"
    elif [ "${pc}" == "${RUN_STATUS}" ]; then
        temp=$(ps -aux | grep -i "${USER}" | grep -i "${SERVICE_NAME}" | grep -i "${SERVICE_DIR}/${PROFILE}" | grep -v grep  | grep -v "$0" | awk '{print $2}' | xargs kill -15)
        echo "    程序（${ALIAS_NAME}）正在停止 ......"
        
        sleep 2
        echo "    程序（${ALIAS_NAME}）停止验证中 ......"
        sleep 3
        
        pcn=$(service_status)
        if [ "${pcn}" -gt 0 ]; then
           tmp=$(ps -aux | grep -i "${USER}" | grep -i "${SERVICE_NAME}" | grep -i "${SERVICE_DIR}/${PROFILE}" | grep -v grep  | grep -v "$0" | awk '{print $2}' | xargs kill -9) 
        fi 
        echo "    程序（${ALIAS_NAME}）已经停止 ......"
    else
        echo "    程序（${ALIAS_NAME}）运行出现问题 ......"
    fi
}


printf "\n================================================================================\n"
case $1 in
    start)
        service_start
    ;;

    stop)
        service_stop
    ;;

    restart)
        service_stop
        sleep 1
        service_start
    ;;

    status)
        status=$(service_status)
            
        if [ "${status}" == "${RUN_STATUS}" ]; then
            echo "    程序（${ALIAS_NAME}）正在运行 ......"
        elif [ "${status}" == "${STOP_STATUS}" ]; then
            echo "    程序（${ALIAS_NAME}）已经停止 ......"
        else
            echo "    程序（${ALIAS_NAME}）运行出错 ...... "
        fi
    ;;
    
    *)
        echo "    脚本可传入一个参数，使用方法：/path/$(basename $0) arg "
        echo "    arg：服务选项，必填，如下表所示："
        echo "        +-------------------+------------------+ "
        echo "        |      参   数      |      描  述      | "
        echo "        +-------------------+------------------+ "
        echo "        |      start        |     启动服务     | "
        echo "        |      stop         |     停止服务     | "
        echo "        |      status       |     服务状态     | "
        echo "        |      restart      |     重启服务     | "
        echo "        +-------------------+------------------+ "
esac
printf "================================================================================\n\n"
exit 0
