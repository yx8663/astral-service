//package com.astral.core.config.webSocketConfig;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import javax.websocket.Session;
//import java.util.Collections;
//import java.util.Iterator;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.concurrent.BlockingQueue;
//import java.util.concurrent.LinkedBlockingQueue;
//import java.util.concurrent.TimeUnit;
//
//public class ChatRoomWebSocket {
//
//    private static final ObjectMapper objectMapper = new ObjectMapper();
//
//    // Message types
//    public static final String TYPE_ERROR = "error";
//    public static final String TYPE_BIM2GLTF = "bim2gltf";
//    public static final String TYPE_CAD = "cad";
//    public static final String TYPE_CHATROOM_JOIN = "chatroom-join";
//    public static final String TYPE_CHATROOM_LEAVE = "chatroom-leave";
//
//    // Message class
//    public static class Message {
//        private String name;
//        private ResponseMessage message;
//
//        public Message() {}
//
//        public Message(String name, ResponseMessage message) {
//            this.name = name;
//            this.message = message;
//        }
//
//        // Getters and setters
//        public String getName() {
//            return name;
//        }
//
//        public void setName(String name) {
//            this.name = name;
//        }
//
//        public ResponseMessage getMessage() {
//            return message;
//        }
//
//        public void setMessage(ResponseMessage message) {
//            this.message = message;
//        }
//    }
//
//    // ResponseMessage class
//    public static class ResponseMessage {
//        private String type;
//        private String subscriber;
//        private Object data;
//
//        public ResponseMessage() {}
//
//        public ResponseMessage(String type, String subscriber, Object data) {
//            this.type = type;
//            this.subscriber = subscriber;
//            this.data = data;
//        }
//
//        // Getters and setters
//        public String getType() {
//            return type;
//        }
//
//        public void setType(String type) {
//            this.type = type;
//        }
//
//        public String getSubscriber() {
//            return subscriber;
//        }
//
//        public void setSubscriber(String subscriber) {
//            this.subscriber = subscriber;
//        }
//
//        public Object getData() {
//            return data;
//        }
//
//        public void setData(Object data) {
//            this.data = data;
//        }
//    }
//
//    // Connection class
//    public static class Connection {
//        private String uname;
//        private Session wsConn;
//
//        public Connection(String uname, Session wsConn) {
//            this.uname = uname;
//            this.wsConn = wsConn;
//        }
//
//        public String getUname() {
//            return uname;
//        }
//
//        public Session getWsConn() {
//            return wsConn;
//        }
//
//        public void close() {
//            try {
//                if (wsConn != null && wsConn.isOpen()) {
//                    wsConn.close();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    // Channels
//    private static final BlockingQueue<Connection> subscribeQueue = new LinkedBlockingQueue<>(10);
//    private static final BlockingQueue<String> unsubscribeQueue = new LinkedBlockingQueue<>(10);
//    private static final BlockingQueue<Message> publishQueue = new LinkedBlockingQueue<>(10);
//
//    // Subscribers list
//    private static final List<Connection> subscribers = Collections.synchronizedList(new LinkedList<>());
//
//    static {
//        // Start the chatroom thread
//        new Thread(ChatRoomWebSocket::chatroom).start();
//    }
//
//    public static void join(Connection conn) {
//        subscribeQueue.offer(conn);
//    }
//
//    public static void leave(String user) {
//        unsubscribeQueue.offer(user);
//    }
//
//    public static void publish(Message message) {
//        publishQueue.offer(message);
//    }
//
//    public static Connection getConnByUName(String uname) {
//        synchronized (subscribers) {
//            for (Connection conn : subscribers) {
//                if (conn.getUname().equals(uname)) {
//                    return conn;
//                }
//            }
//        }
//        return null;
//    }
//
//    private static boolean isUserExist(String user) {
//        synchronized (subscribers) {
//            for (Connection conn : subscribers) {
//                if (conn.getUname().equals(user)) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
//
//    private static void broadcastWebSocket(Message message) {
//        synchronized (subscribers) {
//            Iterator<Connection> iterator = subscribers.iterator();
//            while (iterator.hasNext()) {
//                Connection conn = iterator.next();
//                Session ws = conn.getWsConn();
//                if (ws != null && ws.isOpen()) {
//                    try {
//                        ResponseMessage msg = message.getMessage();
//                        if (msg.getSubscriber() == null || msg.getSubscriber().isEmpty()) {
//                            msg.setSubscriber(message.getName());
//                        }
//
//                        String msgJson = objectMapper.writeValueAsString(msg);
//                        ws.getBasicRemote().sendText(msgJson);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        iterator.remove();
//                        unsubscribeQueue.offer(conn.getUname());
//                    }
//                }
//            }
//        }
//    }
//
//    private static void chatroom() {
//        while (true) {
//            try {
//                Connection subConn = subscribeQueue.poll(100, TimeUnit.MILLISECONDS);
//                if (subConn != null) {
//                    if (!isUserExist(subConn.getUname())) {
//                        synchronized (subscribers) {
//                            subscribers.add(subConn);
//                        }
//
//                        ResponseMessage msgJson = new ResponseMessage(
//                                TYPE_CHATROOM_JOIN,
//                                subConn.getUname(),
//                                subConn.getUname() + " has joined the room."
//                        );
//                        publishQueue.offer(new Message(subConn.getUname(), msgJson));
//                        System.out.println("User {} join the room." + subConn.getUname());
//                    }
//                }
//
//                // Check publish queue
//                Message message = publishQueue.poll(100, TimeUnit.MILLISECONDS);
//                if (message != null) {
//                    broadcastWebSocket(message);
//                }
//
//                // Check unsubscribe queue
//                String unsub = unsubscribeQueue.poll(100, TimeUnit.MILLISECONDS);
//                if (unsub != null) {
//                    synchronized (subscribers) {
//                        Iterator<Connection> iterator = subscribers.iterator();
//                        while (iterator.hasNext()) {
//                            Connection conn = iterator.next();
//                            if (conn.getUname().equals(unsub)) {
//                                iterator.remove();
//                                conn.close();
//
//                                // Broadcast leave message
//                                ResponseMessage msgJson = new ResponseMessage(
//                                        TYPE_CHATROOM_LEAVE,
//                                        unsub,
//                                        unsub + " exit the room."
//                                );
//                                publishQueue.offer(new Message(unsub, msgJson));
//                                break;
//                            }
//                        }
//                    }
//                }
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//                Thread.currentThread().interrupt();
//                break;
//            }
//        }
//    }
//
//}
