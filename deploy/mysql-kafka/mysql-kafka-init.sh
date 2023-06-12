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
DATA_BASE=at_gui_gu                                        # 需要同步的数据库
LOG_FILE="mysql-kafka-$(date +%F).log"                     # 操作日志存储


# ============================================= 函数定义 ============================================ #
# 同步全量数据
function import_data()
{
    # 开启全量同步数据
    echo "    开始同步表： $1 ...... "
    "${MAXWELL_HOME}/bin/maxwell-bootstrap" --database ${DATA_BASE}                     \
                                            --table "$1"                                \
                                            --config "${SERVICE_DIR}/config.properties" \
                                            >> "${SERVICE_DIR}/logs/${LOG_FILE}" 2>&1
}


printf "\n=================================== 运行开始 ===================================\n"
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
printf "=================================== 运行结束 ===================================\n\n"
exit 0
