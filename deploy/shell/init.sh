#!/usr/bin/env bash

# =========================================================================================
#    FileName      ：  init.sh
#    CreateTime    ：  2023-02-24 01:42
#    Author        ：  lihua shiyu
#    Email         ：  lihuashiyu@github.com
#    Description   ：  init.sh 被用于 ==> 部署完成后，一键初始化
# =========================================================================================
    
    
SERVICE_DIR=$(cd "$(dirname "$0")"    || exit; pwd)        # 程序位置
PROJECT_DIR=$(cd "${SERVICE_DIR}/../" || exit; pwd)        # 项目根路径
MYSQL_HOME="/opt/db/mysql"                                 # Mysql 安装路径
PHOENIX_HOME="/opt/apache/phoenix"                         # Phoenix 安装路径
DORIS_HOME="/opt/apache/doris"                             # Doris 安装路径
PHOENIX_HOME="/opt/apache/phoenix"                         # Phoenix 安装路径
DORIS_HOME="/opt/apache/doris"                             # Phoenix 安装路径
MYSQL_HOST="master"                                        # Mysql 安装的节点
DORIS_HOST="master"                                        # Mysql 安装的节点
MOCK_LOG_HOST_LIST=(slaver1 slaver2 slaver3)               # 需要生成 历史 行为日志 的节点 
MAXWELL_HOST="master"                                      # 同步生成的数据库中的 历史增量数据 到 Kafka 使用的 MaxWell 所在的节点 

USER=$(whoami)                                             # 当前用户
LOG_FILE="init-$(date +%F).log"                            # 操作日志


# ============================================= 定义函数 ============================================== 
# 1. 创建各个模块的日志目录
function create_model_log()
{
    module_list=$(ls -d "${PROJECT_DIR}"/*/)
    
    for module in ${module_list}
    do
        echo "    在目录（${module}）中创建 日志目录 logs "
        mkdir -p "${module}/logs"
    done
}

# 2. 创建所有 mysql、phoenix 和 doris 表
function create_table()
{
    # 2.1 创建 mock-db 模块的业务数据库并授权
    atguigu="    drop database if exists at_gui_gu;   create database if not exists at_gui_gu;   grant all privileges on at_gui_gu.*   to 'issac'@'%'; flush privileges;"
    ${MYSQL_HOME}/bin/mysql -h${MYSQL_HOST} -P3306 -uroot -p111111 -e "${atguigu}"     >> "${SERVICE_DIR}/logs/${LOG_FILE}" 2>&1
    
    # 2.2 在 Mysql 中，将 mock-db 的数据导入到 数据库 at_gui_gu
    echo "****************************** 将 mock-db 的 sql 执行到数据库 ******************************"
    ${MYSQL_HOME}/bin/mysql -h${MYSQL_HOST} -P3306 -uissac -p111111 -Dat_gui_gu < "${PROJECT_DIR}/mock-db/table.sql" >> "${SERVICE_DIR}/logs/${LOG_FILE}" 2>&1
    ${MYSQL_HOME}/bin/mysql -h${MYSQL_HOST} -P3306 -uissac -p111111 -Dat_gui_gu < "${PROJECT_DIR}/mock-db/data.sql"  >> "${SERVICE_DIR}/logs/${LOG_FILE}" 2>&1
    
    # 2.3 在 Phoenix-Hbase 中创建所有需要的维度表
    echo "****************************** 在 Mysql 中创建 ADS 层的映射表 ******************************"
    ${MYSQL_HOME}/bin/mysql -h${MYSQL_HOST} -P3306 -uissac -p111111 -Dview_report < "${PROJECT_DIR}/sql/export.sql" >> "${SERVICE_DIR}/logs/${LOG_FILE}" 2>&1    
    
    # 2.4 在 Dois 中创建所有需要实时展示的的表
    echo "****************************** 在 Mysql 中创建 ADS 层的映射表 ******************************"
    ${MYSQL_HOME}/bin/mysql -h${MYSQL_HOST} -P3306 -uissac -p111111 -Dview_report < "${PROJECT_DIR}/sql/export.sql" >> "${SERVICE_DIR}/logs/${LOG_FILE}" 2>&1
}

# 3. 模拟生成 1 天的 用户行为 历史日志
function generate_log()
{
    echo "****************************** 模拟生成 1 天的用户行为日志 ******************************"
    for host_name in "${MOCK_LOG_HOST_LIST[@]}"
    do
        echo "    ****************************** ${host_name} ******************************    "
        ssh "${USER}@${host_name}" "source ~/.bashrc; source /etc/profile; ${PROJECT_DIR}/mock-log/cycle.sh "  >> "${SERVICE_DIR}/logs/${LOG_FILE}" 2>&1
    done
}

# 4. 监控本地 用户行为日志 并同步到 Kafka
function log_monitor()
{
    echo "****************************** 监控行为日志并同步到 Kafka ******************************"
    for host_name in "${MOCK_LOG_HOST_LIST[@]}"
    do
        echo "    ************************** ${host_name} **************************    "
        ssh "${USER}@${host_name}" "source ~/.bashrc; source /etc/profile; ${PROJECT_DIR}/file-kafka/file-kafka.sh $1" >> "${SERVICE_DIR}/logs/${LOG_FILE}" 2>&1
    done
}

# 5. 将 Mysql 中的 历史维度数据 通过 maxwell 全量同步到 kafka
function mysql_kafka()
{
    # 5.1 初始化 MaxWell 元数据
    echo "***************************** 将 maxwell 的源数据导入到 Mysql *****************************"
    ${MYSQL_HOME}/bin/mysql -h${MYSQL_HOST} -P3306 -uissac -p111111 < "${PROJECT_DIR}/mysql-kafka/meta.sql" >> "${SERVICE_DIR}/logs/${LOG_FILE}" 2>&1
    
    # 5.2 同步所有全量历史数据
    echo "***************************** 将 Mysql 的 全量数据 同步到 kafka *****************************"
    "${PROJECT_DIR}/mysql-kafka/mysql-kafka.sh" all >> "${SERVICE_DIR}/logs/${LOG_FILE}" 2>&1    
}


echo "============================================= 初始化开始 =============================================" 
# 0. 给所有 shell 脚本添加可执行权限
find "${PROJECT_DIR}/" -iname "*.sh" -type f -exec chmod a+x {} + 

# 1.创建日志存储目录
create_model_log

# 2. 将解压后的项目同步到其它节点
"${SERVICE_DIR}"/xync.sh "${PROJECT_DIR}/"

# 3. 启动大数据组件
"${SERVICE_DIR}/component.sh" start 

# 4. 创建 Mysql、Phoenix、和 Doris 表
create_table

# 5. 开启行为日志监控工具
log_monitor start

# 6. 生成 行为日志 数据
generate_log

# 7. 全量同步数据库 历史维度数据
mysql_kafka

echo "============================================= 初始化结束 ============================================="
exit 0
