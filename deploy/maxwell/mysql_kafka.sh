#!/usr/bin/env bash


MAXWELL_HOME=/opt/github/maxwell                           # MaxWell 安装路径
SERVICE_DIR=$(cd "$(dirname "$0")" || exit; pwd)           # 服务位置
PROFILE=config.properties                                  # 配置文件
ALIAS_NAME="Mysql -> MaxWell -> Kafka"                     # 程序别名
JUDGE_NAME=com.zendesk.maxwell.Maxwell                     # MaxWell jar 名字
LOG_FILE=mysql_kafka-$(date +%F).log                       # 操作日志存储
KAFKA_URL=issac:9092                                       # Kafka 连接 url
KAFKA_TOPIC=mock_db                                        # Kafka 主题

function service_status()
{
    pid_count=$(ps -aux | grep -i ${JUDGE_NAME} | grep -i "${SERVICE_DIR}/${PROFILE}" | grep -vi grep  | grep -vi "$0" | wc -l)
    echo "${pid_count}"
}


function service_start()
{
    # 1.1 统计正在运行程序的 pid 的个数
    pc=$(service_status)
    if [[ ${pc} -lt 1 ]]; then
        
        # 替换 Kafka 的连接集群地址 和 Topic
        sed -i "s#kafka.bootstrap.servers = .*#kafka.bootstrap.servers = ${KAFKA_URL}#g" "${SERVICE_DIR}/${PROFILE}"
        sed -i "s#kafka_topic = .*#kafka_topic = ${KAFKA_TOPIC}#g" "${SERVICE_DIR}/${PROFILE}"
        
        # 启动 MaxWell 
        ${MAXWELL_HOME}/bin/maxwell --config "${SERVICE_DIR}/${PROFILE}" \
                                    --daemon \
                                    >> "${SERVICE_DIR}/${LOG_FILE}" 2>&1             
        
        echo "    程序（${ALIAS_NAME}）正在启动中 ......"
        sleep 2 
        echo "    程序（${ALIAS_NAME}）启动验证中 ......"
        sleep 3 
        
        # 1.3 判断程序启动是否成功
        count=$(service_status)
        if [ "${count}" -eq 1 ]; then
            echo "    程序（${ALIAS_NAME}）启动成功 ......"
        else
            echo "    程序（${ALIAS_NAME}）启动失败 ......"
        fi
    else
        echo "    程序（${ALIAS_NAME}）正在运行中 ......"
    fi
}


function service_stop()
{
    # 1 统计正在运行程序的 pid 的个数
    pc=$(service_status)
    if [ "${pc}" -eq 0 ]; then
        echo "    程序（${ALIAS_NAME}）进程不存在，未在运行 ......"
    else
        temp=$(ps -aux | grep -i ${JUDGE_NAME} | grep -i "${SERVICE_DIR}/${PROFILE}" | grep -vi grep  | grep -vi "$0" | awk '{print $2}' | xargs kill -15)
        echo "    程序（${ALIAS_NAME}）正在停止 ......"
        
        sleep 2
        echo "    程序（${ALIAS_NAME}）停止验证中 ......"
        sleep 3
        
        pcn=$(service_status)
        if [ "${pcn}" -gt 0 ]; then
           tmp=$(ps -aux | grep ${JUDGE_NAME} | grep -i "${SERVICE_DIR}/${PROFILE}" | grep -vi grep  | grep -vi "$0" | awk '{print $2}' | xargs kill -9) 
        fi 
        echo "    程序（${ALIAS_NAME}）已经停止 ......"
    fi
}


printf "\n=========================================================================\n"
#  匹配输入参数

case $1 in
    # #  1. 启动程序
    start )
        service_start
    ;;
    
    # 2. 停止程序
    stop )
        service_stop
    ;;

    #  3. 状态查询
    status)
        # 3.1 统计正在运行程序的 pid 的个数
        pc=$(service_status)

        #  3.2 判断运行状态
        if [ "${pc}" -gt 0 ]; then
            echo "    程序（${ALIAS_NAME}）正在运行中 ......"
        else
            echo "    程序（${ALIAS_NAME}）已经停止 ......"
        fi
    ;;

    # 4. 重启程序
    restart )
       service_stop
       service_start
    ;;

    # 5. 其它情况
    *)
        echo "    脚本可传入一个参数，如下所示：            "
        echo "        +---------------------------------+ "
        echo "        | start | stop | restart | status | "
        echo "        +---------------------------------+ "
        echo "        |      start    ：  启动服务      |  "
        echo "        |      stop     ：  关闭服务      |  "
        echo "        |      restart  ：  重启服务      |  "
        echo "        |      status   ：  查看状态      |  "
        echo "        +---------------------------------+ "
    ;;
esac
printf "=========================================================================\n\n"
    
