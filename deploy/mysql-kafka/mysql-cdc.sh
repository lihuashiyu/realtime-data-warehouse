#!/usr/bin/env bash

# =========================================================================================
#    FileName      ：  mysql-cdc.sh
#    CreateTime    ：  2023-02-24 01:44
#    Author        ：  lihua shiyu
#    Email         ：  lihuashiyu@github.com
#    Description   ：  mysql-cdc.sh 被用于 ==> 使用 FlinkCDC 监控 Mysql，将实时产生的 
#                                                  业务数据 同步到 kafka
# =========================================================================================
    
    
SERVICE_DIR=$(cd "$(dirname "$0")" || exit; pwd)           # 程序位置
ALIAS_NAME="Mysql-CDC-Kafka"                               # 程序别名
JAR_NAME="mysql-cdc-1.0.jar"                               # jar 包名称
CLASS_NAME="issac.FlinkCDCDataStream"                      # jar 中类路径
PROFILE="mysql.properties"                                 # 配置文件
HADOOP_HOME="/opt/apache/hadoop"                           # Hadoop 安装路径
FLINK_HOME="/opt/apache/flink"                             # Flink  安装路径
CONTAINER_COUNT=2                                          # TaskManager 的数量
SLOT_COUNT=2                                               # Slot 数量
QUEUE_NAME=default                                         # Yarn 队列
LOG_FILE=process-$(date +%F).log                           # 程序运行日志文件

USER=$(whoami)                                             # 服务运行用户
RUN_STATUS=1                                               # 服务运行状态
STOP_STATUS=0                                              # 服务停止状态


# 任务状态检测
function service_status()
{
    pid_count=$("${HADOOP_HOME}/bin/yarn" application -list | grep -iE "${USER}|${ALIAS_NAME}|${QUEUE_NAME}" | wc -l)
    if [ "${pid_count}" -eq 0 ]; then
        echo "${STOP_STATUS}"
    elif [ "${pid_count}" -eq 1 ]; then
        echo "${START_STATUS}"
    else
        echo "    程序（${ALIAS_NAME}）运行出现问题 ...... "
    fi
}

# 任务启动
function service_start()
{
    # 1. 统计正在运行程序的 pid 的个数
    status=$(service_status)
    
    # 2. 若程序运行状态为停止，则运行程序，否则打印程序正在运行
    if [ "${status}" == "${STOP_STATUS}" ]; then
        echo "    Flink on Yarn 任务（${ALIAS_NAME}）正在加载中 ...... "
        
        # 3. 加载程序，启动程序
        # -yn,  --container <arg>          ： 表示分配容器的数量，也就是 TaskManager 的数量
        # -d,   --detached                 ： 设置在后台运行
        # -yjm, --jobManagerMemory<arg>    ： 设置 JobManager 的内存（MB）
        # -ytm, --taskManagerMemory<arg>   ： 设置每个 TaskManager 的内存（MB）
        # -ynm, --name                     ： 给当前 Flink application 在 Yarn 上指定名称
        # -yq,  --query                    ： 显示 yarn 中可用的资源（内存、cpu 核数）
        # -yqu, --queue<arg>               ： 指定 yarn 资源队列
        # -ys,  --slots<arg>               ： 每个 TaskManager 使用的 Slot 数量
        # -yz,  --zookeeperNamespace<arg>  ： 针对 HA 模式在 Zookeeper 上创建 NameSpace
        # -yid, --applicationID<yarnAppId> ： 指定 Yarn 集群上的任务 ID，附着到一个后台独立运行的 Yarn Session 中
        "${FLINK_HOME}/bin/flink" run -d                                                        \
                                      -m   yarn-cluster                                         \
                                      -yqu ${QUEUE_NAME}                                        \
                                      -yn  ${CONTAINER_COUNT}                                   \
                                      -ys  ${SLOT_COUNT}                                        \
                                      -ynm ${ALIAS_NAME}                                        \
                                      -c   ${CLASS_NAME}                                        \
                                      "${SERVICE_DIR}/${JAR_NAME}" "${SERVICE_DIR}"/${PROFILE}  \
                                  >>  "${SERVICE_DIR}/logs/${LOG_FILE}" 2>&1
        sleep 1
        echo "    程序（${ALIAS_NAME}）程序启动验证中 ...... "
        sleep 2
        
        # 检查服务状态
        stat=$(service_status)
        if [ "${stat}" == "${RUN_STATUS}" ]; then
            echo "    Flink on Yarn 任务（${ALIAS_NAME}）启动成功 ...... "
        else
            echo "    Flink on Yarn 任务（${ALIAS_NAME}）启动失败 ...... "
        fi
    else
        echo "    Flink on Yarn 任务（${ALIAS_NAME}）正在运行中 ...... "
    fi
}

# 任务停止
function service_stop()
{
    # 1. 统计正在运行程序的 pid 的个数
    status=$(service_status)
    yarn application -kill application_1576832892572_0002
    # 2 判断程序状态
    if [ "${status}" == "${STOP_STATUS}" ]; then
        echo "    Flink on Yarn 任务（${ALIAS_NAME}）的 app-id 不存在，任务没有运行 ...... "
    
    # 3. 杀死进程，关闭程序
    else
        app_id=$("${HADOOP_HOME}/bin/yarn" application -list | grep -iE "${USER}|${ALIAS_NAME}|${QUEUE_NAME}" | awk '{print $1}')
        echo "    Flink on Yarn 任务（${ALIAS_NAME}）正在停止 ......"
        "${HADOOP_HOME}/bin/yarn" application -kill "${app_id}" >> "${SERVICE_DIR}/logs/${LOG_FILE}" 2>&1
        
        sleep 2
        echo "    Flink on Yarn 任务（${ALIAS_NAME}）停止验证中 ......"
        sleep 3
        
        # 4. 若还未关闭，则强制杀死进程，关闭程序
        stat=$(service_status)
        
        if [ "${pid_count}" == "${RUN_STATUS}" ]; then
            "${HADOOP_HOME}/bin/yarn" application -kill  "${app_id}"
        fi
        
        echo "    Flink on Yarn 任务（${ALIAS_NAME}）已经停止成功 ......"
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
            echo "    Flink on Yarn 任务（${ALIAS_NAME}）已经停止 ...... "
        elif [ "${status}" == "${RUN_STATUS}" ]; then
            echo "    Flink on Yarn 任务（${ALIAS_NAME}）正在运行中 ...... "
        else 
            echo "${status}"
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
echo 0
