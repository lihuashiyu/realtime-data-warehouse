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
DATA_BASE=at_gui_gu                                        # 需要同步的数据库
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
    if [[ ${pc} -lt 1 ]]; then
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
    else
        echo "    程序（${ALIAS_NAME}）正在运行中 ......"
    fi
}

# 服务停止
function service_stop()
{
    # 1 统计正在运行程序的 pid 的个数
    pc=$(service_status)
    if [ "${pc}" -eq 0 ]; then
        echo "    程序（${ALIAS_NAME}）进程不存在，未在运行 ......"
    else
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
    fi
}

# 同步全量数据
function import_data()
{
    
    # 1. 获取 pid 个数
    pid_count=$(ps -aux | grep -i "${USER}" | grep -i "${SERVICE_NAME}" | grep "${SERVICE_DIR}/${PROFILE}" | grep -v grep  | grep -v "$0" | wc -l)
    
    # 2. 判断程 MaxWell 运行状态
    if [ "${pid_count}" -le 1 ]; then
         # 2.1 启动 MaxWell
        "${MAXWELL_HOME}/bin/maxwell" --config "${SERVICE_DIR}/${PROFILE}" \
                                      --daemon                             \
                                      >> "${SERVICE_DIR}/logs/${LOG_FILE}" 2>&1
        sleep 5
    else
        echo "    MaxWell 已经在运行中 ......"
    fi
    
    # 3. 开启全量头部数据
    echo "    开始同步表： $1 ...... "
    "${MAXWELL_HOME}/bin/maxwell-bootstrap" --database ${DATA_BASE}                     \
                                            --table "$1"                                \
                                            --config "${SERVICE_DIR}/config.properties" \
                                            >> "${SERVICE_DIR}/logs/${LOG_FILE}" 2>&1
}


printf "\n=================================== 运行开始 ===================================\n"
service_start

case $1 in
    activity_info)
        import_data activity_info 
    ;;
    
    activity_rule)
        import_data activity_rule 
    ;;
    
    activity_sku)
        import_data activity_sku 
    ;;
    
    base_category1)
        import_data base_category1 
    ;;
    
    base_category2)
        import_data base_category2 
    ;;
    
    base_category3)
        import_data base_category3 
    ;;
    
    base_province)
        import_data base_province 
    ;;
    
    base_region)
        import_data base_region 
    ;;
    
    base_trademark)
        import_data base_trademark 
    ;;
    
    coupon_info)
        import_data coupon_info 
    ;;
    
    coupon_range)
        import_data coupon_range 
    ;;
    
    financial_sku_cost)
        import_data financial_sku_cost 
    ;;
    
    sku_info)
        import_data sku_info 
    ;;
    
    spu_info)
        import_data spu_info 
    ;;
    
    user_info)
        import_data user_info 
    ;;
    
    all)
        import_data activity_info 
        import_data activity_rule 
        import_data activity_sku 
        import_data base_category1 
        import_data base_category2 
        import_data base_category3 
        import_data base_province 
        import_data base_region 
        import_data base_trademark 
        import_data coupon_info 
        import_data coupon_range 
        import_data financial_sku_cost 
        import_data sku_info 
        import_data spu_info 
        import_data user_info 
    ;;
    
    *)
        echo "    脚本可传入一个参数，使用方法：/path/$(basename $0) arg（表名）"
        echo "        +----------------------+--------------------+ "
        echo "        |        参  数        |      表 描 述      | "
        echo "        +----------------------+--------------------+ "
        echo "        |  activity_info       |  活动信息表        | "
        echo "        |  activity_rule       |  活动规则表        | "
        echo "        |  activity_sku        |  活动参与商品      | "
        echo "        |  base_category1      |  一级分类表        | "
        echo "        |  base_category2      |  二级分类表        | "
        echo "        |  base_category3      |  三级分类表        | "
        echo "        |  base_province       |  省份表            | "
        echo "        |  base_region         |  地区表            | "
        echo "        |  base_trademark      |  品牌表            | "
        echo "        |  coupon_info         |  优惠券信息        | "
        echo "        |  coupon_range        |  优惠券范围表      | "
        echo "        |  financial_sku_cost  |                    | "
        echo "        |  sku_info            |  SKU 信息表        | "
        echo "        |  spu_info            |  SPU 信息表        | "
        echo "        |  user_info           |  用户详细信息表    | "
        echo "        |  all                 |  Mysql 维度业务表  | "
        echo "        +----------------------+--------------------+ "
esac

service_stop
printf "=================================== 运行结束 ===================================\n\n"
exit 0
