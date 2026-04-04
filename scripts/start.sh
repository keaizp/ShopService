#!/bin/bash

# 设置项目名称
PROJECT_NAME="yamaha-backend"


# 启动服务（后台运行）
nohup java -jar ${PROJECT_NAME}-1.0.0.jar 2>&1 &

# 记录PID
echo $! > pid.txt
echo "服务启动成功，PID: $(cat pid.txt)"

