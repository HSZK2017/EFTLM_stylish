# 服务器崩溃分析报告

时间: 2026-08-13 18:09:08

## 匹配签名
- **client_class_on_server** (`invalid dist DEDICATED_SERVER`): 专用服务器加载了客户端类（Nightfall 粒子 / EpicFight 动画等），检查是否已是最新版 mod 组合
- **class_init_error** (`ExceptionInInitializerError`): 类静态初始化失败，通常伴随上述客户端类问题
- **tick_loop_exception** (`Exception in server tick loop`): 服务器 tick 循环异常（通用崩溃）

## 收集的文件
- crash-2026-08-05_14.38.28-server.txt
- crash-2026-08-05_23.37.41-server.txt
- crash-2026-08-05_23.38.43-server.txt
- crash-2026-08-06_22.11.17-server.txt
- crash-2026-08-06_22.11.59-server.txt
- crash-2026-08-06_22.12.46-server.txt
- crash-2026-08-12_10.02.11-server.txt
- crash-2026-08-12_11.39.01-server.txt
- crash-2026-08-12_12.45.46-server.txt
- crash-2026-08-12_13.44.26-server.txt
- debug.log
- latest.log
- server.log.tail