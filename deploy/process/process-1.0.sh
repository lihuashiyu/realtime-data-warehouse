#!/usr/bin/env bash


SERVICE_DIR=$(cd "$(dirname "$0")" || exit; pwd)           # 程序位置
SERVICE_NAME=process-1.0.jar                               # 程序名称
ALIAS_NAME=Process                                         # 程序别名
PROFILE=application.properties                             # 配置文件
LOG_FILE=process-$(date +%F).log                           # 程序运行日志文件

USER=$(whoami)                                             # 服务运行用户
RUN_STATUS=1                                               # 服务运行状态
STOP_STATUS=0                                              # 服务停止状态


# 服务状态检测
function service_status()
{
    pid_count=$(ps -aux | grep -i "${USER}" | grep -i "${SERVICE_NAME}" | grep -vi "$0" | grep -vi grep | wc -l)
    echo "${pid_count}"
}


# 服务启动
function service_start()
{
    # 1. 统计正在运行程序的 pid 的个数
    status=$(service_status)
    
    # 2. 若程序运行状态为停止，则运行程序，否则打印程序正在运行
    if [ "${status}" == "${STOP_STATUS}" ]; then
        echo "    程序（${ALIAS_NAME}）正在加载中 ......"
        
        # 3. 加载程序，启动程序
        nohup java -jar "${SERVICE_DIR}/${SERVICE_NAME}" \
                   --spring.config.location="${SERVICE_DIR}/${PROFILE}" \
                   >> "${SERVICE_DIR}/${LOG_FILE}" 2>&1 &

        sleep 1
        echo "    程序（${ALIAS_NAME}）程序启动验证中 ...... "
        sleep 2
        
        # 检查服务状态
        stat=$(service_status)
        if [ "${stat}" == "${RUN_STATUS}" ]; then
            echo "    程序（${ALIAS_NAME}）启动成功 ...... "
        else
            echo "    程序（${ALIAS_NAME}）启动失败 ...... "
        fi
    else
        echo "    程序（${ALIAS_NAME}）正在运行中 ...... "
    fi
}

# 服务停止
function service_stop()
{
    # 1. 统计正在运行程序的 pid 的个数
    status=$(service_status)

    # 2 判断程序状态
    if [ "${status}" == "${STOP_STATUS}" ]; then
        echo "    程序（${ALIAS_NAME}）的进程不存在，程序没有运行 ...... "
    
    # 3. 杀死进程，关闭程序
    else
        echo "    程序（${ALIAS_NAME}）正在停止 ......"
        temp=$(ps -aux | grep -i "${USER}" | grep -i "${SERVICE_NAME}" | grep -vi "$0" | grep -vi grep | awk '{print $2}' | xargs kill -15)

        sleep 2
        echo "    程序（${ALIAS_NAME}）停止验证中 ......"
        sleep 3

        # 4. 若还未关闭，则强制杀死进程，关闭程序
        stat=$(service_status)
        
        if [ "${pid_count}" == "${RUN_STATUS}" ]; then
            tmp=$(ps -aux | grep -i "${USER}" | grep -i "${SERVICE_NAME}" | grep -vi "$0" | grep -vi grep | awk '{print $2}' | xargs kill -9)
        fi

        echo "    程序（${ALIAS_NAME}）已经停止成功 ......"
    fi
}


printf "\n=========================================================================\n"
#  匹配输入参数
case "$1" in
    # 1. 运行程序：running
    start)
        service_start
    ;;

    # 2. 停止
    stop)
        service_stop
    ;;

    # 3. 状态查询
    status)
        # 3.1 查看正在运行程序的 pid
        status=$(service_status)

        # 3.2 判断运行状态
        if [ "${status}" == "${STOP_STATUS}" ]; then
            echo "    程序（${ALIAS_NAME}）已经停止 ...... "
        elif [ "${status}" == "${RUN_STATUS}" ]; then
            echo "    程序（${ALIAS_NAME}）正在运行中 ...... "
        fi
    ;;

    # 4. 重启程序
    restart)
        service_stop
        sleep 1
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