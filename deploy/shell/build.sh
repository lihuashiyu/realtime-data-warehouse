#!/usr/bin/env bash

SERVICE_DIR=$(cd "$(dirname "$0")/../" || exit; pwd)       # 程序位置
SERVICE_NAME=real-time                                     # 程序名称
ALIAS_NAME=RealTime                                        # 程序别名
LOG_FILE=process-$(date +%F).log                           # 程序运行日志文件

