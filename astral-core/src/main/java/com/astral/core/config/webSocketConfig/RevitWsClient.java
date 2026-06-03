package com.astral.core.config.webSocketConfig;

import com.astral.common.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.util.Objects;
import java.util.concurrent.*;

@Component
@ClientEndpoint
public class RevitWsClient {

    private Session session;
    private String address;
    private String port;
    private String path;
    private int timeout;

    // 使用BlockingQueue作为消息队列
    private final BlockingQueue<String> sendMsgQueue = new LinkedBlockingQueue<>();

    private final BlockingQueue<String> recvMsgQueue = new LinkedBlockingQueue<>();

    private WebSocketContainer container;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public RevitWsClient() {

    }

    public RevitWsClient(String address, String port, String path, int timeout) {
        this.address = address;
        this.port = port;
        this.path = path;
        this.timeout = timeout;
    }

    public void start() {
        try {
            URI uri = new URI("ws://" + address + ":" + port + path);
            container = ContainerProvider.getWebSocketContainer();
            container.setDefaultMaxSessionIdleTimeout(timeout);
            session = container.connectToServer(this, uri);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startHeartbeat() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                if (session != null && session.isOpen()) {
                    session.getBasicRemote().sendText("ping");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, 0, 30, TimeUnit.SECONDS); // 每 30 秒发送一次心跳
    }

    public void reconnect() {
        if (session != null && session.isOpen()) {
            try {
                session.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        start();
    }

    public boolean isConnected() {
        return session != null && session.isOpen();
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        System.out.println("Connected to Revit WebSocket server");
        startHeartbeat();
    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println("Received message from Revit: " + message);
        try {
            if(Objects.equals(message, "pong")) return;

            if(CommonUtils.isJsonValid(message)){
                recvMsgQueue.put(message);
            }
        } catch (InterruptedException e) {
            System.err.println("Failed to save message: " + e.getMessage());
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println("Disconnected from Revit WebSocket server: " + closeReason);
//        this.session = null;
        reconnect();
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("WebSocket error: " + throwable.getMessage());
    }


    /**
     * 服务器端推送消息
     */
    public void sendMsg(String message) {
        try {
            // 将消息放入队列
            sendMsgQueue.put(message);
            startMessageSender();
        } catch (InterruptedException e) {
            // 处理中断异常
            Thread.currentThread().interrupt();
            System.err.println("Failed to send message: " + e.getMessage());
        }
    }

    /**
     * 服务器端推送消息
     */
    public void pushMessage(String message) {
        if (session != null && session.isOpen()) {
            try {
                synchronized (session){
                    session.getBasicRemote().sendText(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 启动一个线程从队列中取出消息并发送
     */
    public void startMessageSender() {
        Thread senderThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    // 从队列中取出消息
                    String msg = sendMsgQueue.take();
                    pushMessage(msg);
                } catch (InterruptedException e) {
                    // 处理中断异常
                    Thread.currentThread().interrupt();
                    System.err.println("Message sender thread interrupted: " + e.getMessage());
                }
            }
        });
        senderThread.setDaemon(true); // 设置为守护线程
        senderThread.start();
    }


    public String readMsg() {
        try {
            // 从队列中取出消息，如果队列为空则阻塞
            return recvMsgQueue.take();
        } catch (InterruptedException e) {
            // 处理中断异常
            Thread.currentThread().interrupt();
            System.err.println("Failed to read message: " + e.getMessage());
            return "";
        }
    }

}
