#!/usr/bin/env bash

MVN_HOME=/opt/apache/maven                                 # Maven 安装路径
SHELL_DIR=$(cd "$(dirname "$0")" || exit; pwd)             # Shell 脚本程序位置
ROOT_DIR=$(cd "${SHELL_DIR}/../../" || exit; pwd)          # 项目根路径
PROCESS_DIR="${ROOT_DIR}/process"                          # process 子项目路径
PROCESS_DEPLOY_DIR="${ROOT_DIR}/deploy/process"            # process 子项目打包输出路径

ALIAS_NAME=Process                                         # 项目别名
COMPILE_JAR_NAME=process-1.0-jar-with-dependencies.jar     # 编译后 jar 包名字
TARGET_JAR_NAME=process-1.0.jar                            # 打 tar 包时的 jar 包名字
TAR_NAME=process-1.0.tar.gz                                # 最终打出的 tar 包的名称
LOG_FILE=proces-build-$(date +%F).log                      # 操作日志存储

# 1. 切换到 process 子项目路径
echo "=====================================> 项目（${ALIAS_NAME}）构建开始 <====================================="
cd "${PROCESS_DIR}" || exit

# 2. 使用 Maven 进行 clean package
echo "========================================> Maven 编 译 开 始 <========================================"
"${MVN_HOME}/bin/mvn" clean package -Dmaven.test.skip=true  >> "${ROOT_DIR}/logs/${LOG_FILE}" 2>&1

# 3. 判断文件夹是否存在，不存在就创建
echo "=========================================> 创 建 打 包 路 径 <========================================="
mkdir -p "${PROCESS_DEPLOY_DIR}/bin"             >> "${ROOT_DIR}/logs/${LOG_FILE}" 2>&1
mkdir -p "${PROCESS_DEPLOY_DIR}/lib"             >> "${ROOT_DIR}/logs/${LOG_FILE}" 2>&1
mkdir -p "${PROCESS_DEPLOY_DIR}/conf"            >> "${ROOT_DIR}/logs/${LOG_FILE}" 2>&1
mkdir -p "${PROCESS_DEPLOY_DIR}/logs"            >> "${ROOT_DIR}/logs/${LOG_FILE}" 2>&1

# 4. 清除包存放路径，上次打包的缓存
echo "=========================================> 检 查 打 包 路 径 <========================================="
if [ -z "${PROCESS_DEPLOY_DIR}" ]; then
    exit 0
else
    rm -rf "${PROCESS_DEPLOY_DIR}"/*/*           >> "${ROOT_DIR}/logs/${LOG_FILE}" 2>&1
fi

# 5. 将相关的 jar 和 配置文件、执行脚本 复制到相关目录
echo "=========================================> 复  制  文  件 <=========================================="
cp "${SHELL_DIR}/process-1.0.sh"                 "${PROCESS_DEPLOY_DIR}/bin"                    >> "${ROOT_DIR}/logs/${LOG_FILE}" 2>&1
cp "${PROCESS_DIR}/target/${COMPILE_JAR_NAME}"   "${PROCESS_DEPLOY_DIR}/lib/${TARGET_JAR_NAME}" >> "${ROOT_DIR}/logs/${LOG_FILE}" 2>&1
cp "${PROCESS_DIR}"/target/classes/*.properties  "${PROCESS_DEPLOY_DIR}/conf"                   >> "${ROOT_DIR}/logs/${LOG_FILE}" 2>&1
cp "${PROCESS_DIR}"/target/classes/*.xml         "${PROCESS_DEPLOY_DIR}/conf"                   >> "${ROOT_DIR}/logs/${LOG_FILE}" 2>&1
cp "${PROCESS_DIR}"/target/classes/*.yml         "${PROCESS_DEPLOY_DIR}/conf"                   >> "${ROOT_DIR}/logs/${LOG_FILE}" 2>&1
cp "${PROCESS_DIR}"/target/classes/ReadMe.md     "${PROCESS_DEPLOY_DIR}/"                       >> "${ROOT_DIR}/logs/${LOG_FILE}" 2>&1

# 6. 对 process 子项目进行打包
echo "=========================================> 开  始  打  包 <=========================================="
cd "${PROCESS_DEPLOY_DIR}/../" || exit
tar -zcvf "${SHELL_DIR}/${TAR_NAME}"     process           >> "${ROOT_DIR}/logs/${LOG_FILE}" 2>&1
mv "${SHELL_DIR}/${TAR_NAME}" "${PROCESS_DEPLOY_DIR}"      >> "${ROOT_DIR}/logs/${LOG_FILE}" 2>&1

# 7. 退出脚本，并打印执行结果
"${MVN_HOME}/bin/mvn" clean                      >> "${ROOT_DIR}/logs/${LOG_FILE}" 2>&1
echo "========================================> 打 包 完 成 并 退 出 <========================================"
exit 0
