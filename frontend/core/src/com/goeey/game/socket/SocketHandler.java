package com.goeey.game.socket;

import com.goeey.backend.util.SerializationUtil;
import com.goeey.game.utils.ProcessServerMessage;
import com.gooey.base.socket.ClientEvent;
import com.gooey.base.socket.ServerEvent;
import org.java_websocket.enums.ReadyState;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.concurrent.CountDownLatch;

public class SocketHandler {
    private final WebSocket ws;
    private CountDownLatch latch = new CountDownLatch(1);
    public SocketHandler(String uriStr) {

        URI uri = null;
        try {
            uri = new URI(uriStr);
        } catch (URISyntaxException e) {
            System.out.println("invalid server uri");
        }

        ws = new WebSocket(uri);
        startListening();
    }

    public void startListening(){
        System.out.println("Starting to Listen");
        Thread listenerThread = new Thread(() -> {
            try {
                System.out.println(ws.getReadyState());
                ws.connectBlocking();
                System.out.println(ws.getReadyState());

                //Listen to all socket events
                while (ws.isOpen()){
                    if(ws.getMessageQueue() != null){
                        String message = ws.getMessageQueue().take();
                        ServerEvent<?> serverEvent =  SerializationUtil.deserializeString(message, ServerEvent.class);
                        ProcessServerMessage.callMethod(serverEvent);
                        this.latch.countDown();
//                        if (serverEvent.getType() == ServerEvent.Type.ERROR ||
//                                serverEvent.getType() == ServerEvent.Type.PLAYER_SAT ||
//                                serverEvent.getType() == ServerEvent.Type.ROOM_LIST ||
//                                serverEvent.getType() == ServerEvent.Type.ROOM_PLAYERS ||
//                                serverEvent.getType() == ServerEvent.Type.JOINED ||
//                                serverEvent.getType() == ServerEvent.Type.CONNECT ||
//                                serverEvent.getType() == ServerEvent.Type.REGISTERED ||
//                                serverEvent.getType() == ServerEvent.Type.PLAYER_STAND ||
//                                serverEvent.getType() == ServerEvent.Type.LEAVE) {
//
//                        }

                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        listenerThread.start();
    }

    public ReadyState getState() {
        return ws.getReadyState();
    }

    public WebSocket getWebSocket(){
        return this.ws;
    }

    public void resetLatch(int num){
        this.latch = new CountDownLatch(num);
   }

    public CountDownLatch getLatch(){
        return this.latch;
    }

    public void awaitPlayer() throws InterruptedException {
        latch.await(); // Wait until the latch count becomes zero
    }

    public void register(String clientId){
        ClientEvent registerEvent = new ClientEvent(clientId, ClientEvent.Type.REGISTER, clientId);
        try{
            ws.send(SerializationUtil.serializeString(registerEvent));
            ws.getLatch().await();
        }catch (InterruptedException ex){
            ex.printStackTrace();
        }
    }

    public void connect(String clientId){
        ClientEvent connectEvent = new ClientEvent(clientId, ClientEvent.Type.CONNECT, clientId);
        try{
            ws.send(SerializationUtil.serializeString(connectEvent));
            ws.getLatch().await();
        }catch (InterruptedException ex){
            ex.printStackTrace();
        }
    }

    public void createAndJoin(String clientId){
        ClientEvent createAndJoinEvent = new ClientEvent(clientId, ClientEvent.Type.CREATE_AND_JOIN_ROOM, clientId);
        try{
            ws.send(SerializationUtil.serializeString(createAndJoinEvent));
            ws.getLatch().await();
        }catch (InterruptedException ex){
            ex.printStackTrace();
        }
    }

    public void sit(String clientId, int seatNum){
        ClientEvent sitEvent = new ClientEvent(clientId, ClientEvent.Type.SIT, Integer.toString(seatNum));
        try{
            ws.send(SerializationUtil.serializeString(sitEvent));
            ws.getLatch().await();
        }catch (InterruptedException ex){
            ex.printStackTrace();
        }
    }

    public void leaveSeat(String clientId, int seatNum){
        ClientEvent sitEvent = new ClientEvent(clientId, ClientEvent.Type.LEAVE_SEAT, Integer.toString(seatNum));
        try{
            ws.send(SerializationUtil.serializeString(sitEvent));
            ws.getLatch().await();
        }catch (InterruptedException ex){
            ex.printStackTrace();
        }
    }

    public void bet(String clientId, double amount){
        DecimalFormat df = new DecimalFormat("#.#");
        String amt = df.format(amount);
        ClientEvent betEvent = new ClientEvent(clientId, ClientEvent.Type.BET, amt);
        try{
            ws.send(SerializationUtil.serializeString(betEvent));
            ws.getLatch().await();
        }catch (InterruptedException ex){
            ex.printStackTrace();
        }
    }

    public void hit(String clientId){
        ClientEvent hitEvent = new ClientEvent(clientId, ClientEvent.Type.HIT, "");
        try{
            ws.send(SerializationUtil.serializeString(hitEvent));
            ws.getLatch().await();
        }catch (InterruptedException ex){
            ex.printStackTrace();
        }
    }

    public void stand(String clientId){
        ClientEvent standEvent = new ClientEvent(clientId, ClientEvent.Type.STAND, "");
        try{
            ws.send(SerializationUtil.serializeString(standEvent));
            ws.getLatch().await();
        }catch (InterruptedException ex){
            ex.printStackTrace();
        }
    }

    public void leaveseat(String clientId){
        ClientEvent leaveSeatEvent = new ClientEvent(clientId, ClientEvent.Type.LEAVE_SEAT);
        try{
            ws.send(SerializationUtil.serializeString(leaveSeatEvent));
            ws.getLatch().await();
        }catch (InterruptedException ex){
            ex.printStackTrace();
        }
    }

    public void leaveroom(String clientId){
        ClientEvent leaveEvent = new ClientEvent(clientId, ClientEvent.Type.LEAVE, "");
        try{
            ws.send(SerializationUtil.serializeString(leaveEvent));
            ws.getLatch().await();
        }catch (InterruptedException ex){
            ex.printStackTrace();
        }
    }

    public void doubleDown(String clientId, double amount){
        DecimalFormat df = new DecimalFormat("#.#");
        String amt = df.format(amount);
        ClientEvent doubleDownEvent = new ClientEvent(clientId, ClientEvent.Type.DOUBLE, amt);
        try{
            ws.send(SerializationUtil.serializeString(doubleDownEvent));
            ws.getLatch().await();
        }catch (InterruptedException ex){
            ex.printStackTrace();
        }
    }

    public void joinRoom(String clientId, String roomId){
        ClientEvent joinRoomEvent = new ClientEvent(clientId, ClientEvent.Type.JOIN, roomId);
        try{
            ws.send(SerializationUtil.serializeString(joinRoomEvent));
            ws.getLatch().await();
        }catch (InterruptedException ex){
            ex.printStackTrace();
        }
    }

    public void listRooms(String clientId){
        ClientEvent listRoomEvent = new ClientEvent(clientId, ClientEvent.Type.LIST_ROOMS, "");
        try{
            ws.send(SerializationUtil.serializeString(listRoomEvent));
            ws.getLatch().await();
        }catch (InterruptedException ex){
            ex.printStackTrace();
        }
    }

    public void roomPlayers(String clientId, String roomId){
        ClientEvent roomPlayersEvent = new ClientEvent(clientId, ClientEvent.Type.ROOM_PLAYERS, roomId);
        try{
            ws.send(SerializationUtil.serializeString(roomPlayersEvent));
            ws.getLatch().await();
        }catch (InterruptedException ex){
            ex.printStackTrace();
        }
    }

    public void closeWebSocket() {
        if(ws.isOpen()){
            ws.close();
        }
    }

    public void leave(String clientId){
        ClientEvent leaveEvent = new ClientEvent(clientId, ClientEvent.Type.LEAVE);
        try{
            ws.send(SerializationUtil.serializeString(leaveEvent));
            ws.getLatch().await();
        }catch (InterruptedException ex){
            ex.printStackTrace();
        }
    }
}
