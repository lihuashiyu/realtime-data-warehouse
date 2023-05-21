#!/usr/bin/env bash

# =========================================================================================
#    FileName      ：  warehouse.sh
#    CreateTime    ：  2023-02-24 01:43
#    Author        ：  lihua shiyu
#    Email         ：  lihuashiyu@github.com
#    Description   ：  warehouse.sh 被用于 ==> 数仓中每层之间的计算
# =========================================================================================

PROJECT_DIR=$(cd "$(dirname "$0")/../" || exit; pwd)       # 项目根路径
SERVICE_DIR=$(cd "$(dirname "$0")"    || exit; pwd)        # 程序位置
LOG_FILE="warehouse-$(date +%F).log"                       # 操作日志

# 1. 创建日志目录和日志文件
mkdir -p "${PROJECT_DIR}/logs"
touch "${PROJECT_DIR}/logs/${LOG_FILE}"

function execute_job()
{
    for job_name in "$@"
    do
        "${PROJECT_DIR}/process/process-1.0.sh" start "${job_name}" >> "${SERVICE_DIR}/logs/${LOG_FILE}" 2>&1 
    done
}


# 2. Kafka ----> DWD
echo "======================================= kafka -----> ODS ========================================"
ods=


# 4. Kafka ----> DIM
echo "======================================== Kafka -----> DIM ========================================"
"${PROJECT_DIR}/warehouse/ods-dim.sh"  >> "${PROJECT_DIR}/logs/${LOG_FILE}" 2>&1

# 5. DWD ----> DWS
echo "======================================== DWD ----> DWS ========================================"
"${PROJECT_DIR}/warehouse/dwd-dws.sh"  >> "${PROJECT_DIR}/logs/${LOG_FILE}" 2>&1

# 6. DWS ----> Doris
echo "======================================== DWS ----> Doris ========================================"
"${PROJECT_DIR}/warehouse/dws-ads.sh"  >> "${PROJECT_DIR}/logs/${LOG_FILE}" 2>&1

echo "========================================== 完成退出 ==========================================="
exit 0
