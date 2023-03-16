#!/usr/bin/env bash


### 1. 命令行操作
# 1.1 查看当前服务器中的所有 topic
kafka-topics.sh --bootstrap-server issac:9092 --list

# 1.2 创建 topic
kafka-topics.sh --bootstrap-server issac:9092 --create --topic first --replication-factor 2 --partitions 1
 
# 1.3 删除 topic
kafka-topics.sh --bootstrap-server issac:9092 --delete --topic first

# 1.4 发送消息
kafka-console-producer.sh --broker-list issac:9092 --topic first

# 1.5 消费消息
kafka-console-consumer.sh --bootstrap-server issac:9092 --topic first
kafka-console-consumer.sh --bootstrap-server issac:9092 --from-beginning --topic first

# 1.6 查看某个 Topic 的详情
kafka-topics.sh --bootstrap-server issac:9092 --describe --topic first

# 1.7 修改分区数（修改 topic 的配置 只能修改 分区数 而且只能往大的修改 不能往小的修改 ）
kafka-topics.sh --bootstrap-server issac:9092 --alter --topic first --partitions 3


# # 历史数据
/opt/github/maxwell/bin/maxwell-bootstrap --config ./deploy/maxwell/config.properties --database at_gui_gu --table user_info
/opt/github/maxwell/bin/maxwell-bootstrap --config ./deploy/maxwell/config.properties --database at_gui_gu --table order_info
/opt/github/maxwell/bin/maxwell-bootstrap --config ./deploy/maxwell/config.properties --database at_gui_gu --table base_province
/opt/github/maxwell/bin/maxwell-bootstrap --config ./deploy/maxwell/config.properties --database at_gui_gu --table base_region
