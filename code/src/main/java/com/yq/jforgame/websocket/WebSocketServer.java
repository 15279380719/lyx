package com.yq.jforgame.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint(value = "/websocket/{nickName}")
@Component
public class WebSocketServer {
    private static int onlineCount = 0;
    //ConcurrentHashMap
    private static CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<WebSocketServer>();

    /**
     * 用来记录sessionId和该session进行绑定
     */
    private static Map<String, Session> map = new HashMap<String, Session>();
    private Session session;
    private String  nickName;
    private final Logger log = LoggerFactory.getLogger(this.getClass());


    /**
     * 连接建立成功调用的方法*/
    @OnOpen
    public void onOpen(@PathParam(value = "nickName") String nickName, Session session) {
        this.session = session;
        //加入其中
        this.nickName=nickName;
        //加入set中
        webSocketSet.add(this);
        //在线数加1
        addOnlineCount();
        //在建立连接的时候，就保存频道号（这里使用的是session.getId()作为频道号）和session之间的对应关系：
        log.info("session"+session.getId(),"session"+session);
        map.put(session.getId(),session);
        log.info("有新连接加入: "+nickName+"！当前在线人数为" + getOnlineCount());
        this.session.getAsyncRemote().sendText("欢迎昵称"+nickName+"成功连接上websocket,其频道号为:"+session.getId()+",当前人数为:"+getOnlineCount());
    }
    //连接打开时执行
    /*	@OnOpen
    	public void onOpen(@PathParam("user") String user, Session session) {
    		currentUser = user;
    		System.out.println("Connected ... " + session.getId());
    	}*/

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);  //从set中删除
        subOnlineCount();           //在线数减1
        log.info("有一连接关闭！当前在线人数为" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息*/
    @OnMessage
    public void onMessage(String message, Session session,@PathParam("nickName") String nickName) {
        log.info("来自客户端的消息:" + message);
        //处理的话用objectMapper
        ObjectMapper objectMapper = new ObjectMapper();
        SocketMsg socketMsg ;
        try {
            socketMsg = objectMapper.readValue(message, SocketMsg.class);
            if (socketMsg.getType()==1){
                //单聊的,需要找到发送者和接收者
                socketMsg.setFromUser(session.getId());
                Session fromSession  = map.get(socketMsg.getFromUser());
                Session toSession  = map.get(socketMsg.getToUser());
                if (toSession!=null){
                    //发送给发送者.
                    fromSession.getAsyncRemote().sendText(nickName+"："+socketMsg.getMsg());
                    toSession.getAsyncRemote().sendText(nickName+"："+socketMsg.getMsg());
                }else {
                    fromSession.getAsyncRemote().sendText("来自系统的消息,对方不在线或者您输入的频道号不对");
                }
            }else {
                //群发消息
                for (WebSocketServer item : webSocketSet) {
                    try {
                        //群发后,消除自己
                        if (!item.nickName.equals(nickName)){
                            item.sendMessage(socketMsg.getMsg());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误");
        error.printStackTrace();
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }


    /**
     * 群发自定义消息
     * */
    public static void sendInfo(@PathParam(value = "nickName") String sKey,String message) throws IOException {
        for (WebSocketServer item : webSocketSet) {
            try {
                if (sKey==null){
                    item.sendMessage(message);
                }else if (item.nickName.equals(sKey)){
                    item.sendMessage(message);
                }
            } catch (IOException e) {
                continue;
            }
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }


}
