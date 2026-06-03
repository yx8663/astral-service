//package com.astral.core.config.webSocketConfig;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//
//@Component
//public class RevitWebSocketConfig {
//
//    // 从配置文件中读取Revit的地址、端口、路径和超时时间
//    @Value("${revit.address}")
//    private String address;
//
//    @Value("${revit.port}")
//    private String port;
//
//    @Value("${revit.path}")
//    private String path;
//
//    @Value("${revit.timeout}")
//    private int timeout;
//
//    private RevitWsClient revitWs;
//
//    public RevitWsClient getRevitWs() {
//        return revitWs;
//    }
//
//    public RevitWsClient createRevitWs() {
//        // 启动WebSocket连接Revit
//        RevitWsClient revitWs = new RevitWsClient(address, port, path, timeout);
//        revitWs.start();
//
//        // 设置8小时自动重连（以毫秒为单位）
//        long reconnectInterval = 8 * 60 * 60 * 1000; // 8小时
//        new Thread(() -> {
//            while (true) {
//                try {
//                    Thread.sleep(reconnectInterval);
//                    if (!revitWs.isConnected()) {
//                        revitWs.reconnect();
//                    }
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//        return revitWs;
//    }
//
//}
