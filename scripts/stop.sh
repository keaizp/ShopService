#!/bin/bash

# 设置项目名称
PROJECT_NAME="yamaha-backend"

echo "正在停止服务..."


# 尝试通过进程名查找
echo "通过进程名查找服务..."
PID=$(ps aux | grep "java.*${PROJECT_NAME}" | grep -v grep | awk '{print $2}')

if [ -n "$PID" ]; then
    echo "找到进程ID: $PID"
    echo "正在停止进程..."
    kill $PID
    sleep 2
    
    if ! ps -p $PID > /dev/null; then
        echo "服务停止成功"
        exit 0
    else
        echo "服务停止失败"
        exit 1
    fi
fi

echo "未找到运行中的服务进程"
exit 0
