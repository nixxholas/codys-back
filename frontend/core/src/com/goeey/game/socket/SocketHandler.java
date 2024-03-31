package com.goeey.game.socket;

import com.goeey.backend.util.SerializationUtil;
import com.goeey.game.GameManager;
import com.goeey.game.entity.GameState;
import com.gooey.base.socket.ClientEvent;
import com.gooey.base.socket.ServerEvent;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


public class SocketHandler {
    private static WebSocket ws;
    private final GameManager game;
    private final GameState gameState;
    private CountDownLatch latch = new CountDownLatch(1);

    public SocketHandler(String uriStr, GameManager game) throws URISyntaxException {
        this.game = game;
        this.gameState = GameState.getGameState();

        if(ws == null || ws.isClosed()) {
            ws = new WebSocket(new URI(uriStr));
        }
    }

    public void establishConnection(){
        if (!gameState.isConnected()) {
            try {
                if(ws.isClosed()) {
                    ws.reconnectBlocking();

                } else {
                    ws.connectBlocking(5, TimeUnit.SECONDS);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void resetLatch(int num){
        this.latch = new CountDownLatch(num);
   }

    public void awaitPlayer() throws InterruptedException {
        latch.await(); // Wait until the latch count becomes zero
    }

    public void register(String clientId) throws InterruptedException{
        ClientEvent registerEvent = new ClientEvent(clientId, ClientEvent.Type.REGISTER, clientId);
        ws.send(SerializationUtil.serializeString(registerEvent));
    }

    public void joinLobby(String clientId) throws InterruptedException{
        ClientEvent connectEvent = new ClientEvent(clientId, ClientEvent.Type.CONNECT, clientId);
        ws.send(SerializationUtil.serializeString(connectEvent));
    }

    public void joinRoom(String clientId, String roomId){
        ClientEvent joinRoomEvent = new ClientEvent(clientId, ClientEvent.Type.JOIN, roomId);
        CompletableFuture<ServerEvent<?>> futureMessage = ws.sendAsyncMessage(SerializationUtil.serializeString(joinRoomEvent));
        while(!futureMessage.isDone()) {}
        gameState.setInRoom(true);
    }

    public String createAndJoin(String clientId) throws ExecutionException, InterruptedException {
        ClientEvent createAndJoinEvent = new ClientEvent(clientId, ClientEvent.Type.CREATE_AND_JOIN_ROOM, clientId);
        CompletableFuture<ServerEvent<?>> futureMessage = ws.sendAsyncMessage(SerializationUtil.serializeString(createAndJoinEvent));
        while(!futureMessage.isDone()) {

        }
        gameState.setInRoom(true);
        return futureMessage.get().getMessage().toString().split(" ")[3];
    }

    public void sit(String clientId, int seatNum) {
        ClientEvent sitEvent = new ClientEvent(clientId, ClientEvent.Type.SIT, Integer.toString(seatNum));
        gameState.setSeatNumber(seatNum);
        CompletableFuture<ServerEvent<?>> futureMessage = ws.sendAsyncMessage(SerializationUtil.serializeString(sitEvent));
        while(!futureMessage.isDone()) {

        }
        gameState.setSeated(true);
    }

    public void bet(String clientId, double amount){
        DecimalFormat df = new DecimalFormat("#.#");
        String amt = df.format(amount);
        ClientEvent betEvent = new ClientEvent(clientId, ClientEvent.Type.BET, amt);
        ws.send(SerializationUtil.serializeString(betEvent));
    }

    public void hit(String clientId){
        ClientEvent hitEvent = new ClientEvent(clientId, ClientEvent.Type.HIT, "");
        ws.send(SerializationUtil.serializeString(hitEvent));
    }

    public void stand(String clientId){
        ClientEvent standEvent = new ClientEvent(clientId, ClientEvent.Type.STAND, "");
        ws.send(SerializationUtil.serializeString(standEvent));
    }


    public void doubleDown(String clientId, double amount){
        DecimalFormat df = new DecimalFormat("#.#");
        String amt = df.format(amount);
        ClientEvent doubleDownEvent = new ClientEvent(clientId, ClientEvent.Type.DOUBLE, amt);
        ws.send(SerializationUtil.serializeString(doubleDownEvent));
    }

    public ArrayList<String> listRooms(String clientId) throws InterruptedException, ExecutionException {
        ClientEvent listRoomEvent = new ClientEvent(clientId, ClientEvent.Type.LIST_ROOMS, "");

        CompletableFuture<ServerEvent<?>> futureMessage = ws.sendAsyncMessage(SerializationUtil.serializeString(listRoomEvent));
        while(!futureMessage.isDone()) {

        }
        ArrayList<String> roomIds = new ArrayList<>();
        System.out.println(futureMessage.get().getMessage());
        for(Object roomId: (ArrayList<?>)futureMessage.get().getMessage()) {
            roomIds.add(roomId.toString());
        }

        return roomIds;
    }

    public int getNumPlayersInRoom(String clientId, String roomId) throws ExecutionException, InterruptedException {
        ClientEvent roomPlayersEvent = new ClientEvent(clientId, ClientEvent.Type.ROOM_PLAYERS, roomId);

        CompletableFuture<ServerEvent<?>> futureMessage = ws.sendAsyncMessage(SerializationUtil.serializeString(roomPlayersEvent));
        while(!futureMessage.isDone()) {

        }
        return (int) Double.parseDouble(futureMessage.get().getMessage().toString());
    }

    public void leaveseat(String clientId){
        ClientEvent leaveSeatEvent = new ClientEvent(clientId, ClientEvent.Type.LEAVE_SEAT);
        ws.send(SerializationUtil.serializeString(leaveSeatEvent));
    }

    public void leaveRoom(String clientId){
        if(gameState.isInRoom()) {

            ClientEvent leaveEvent = new ClientEvent(clientId, ClientEvent.Type.LEAVE);
            ws.send(SerializationUtil.serializeString(leaveEvent));
        }
    }

    public void disconnect(String clientId){
        ClientEvent disconnectEvent = new ClientEvent(clientId, ClientEvent.Type.DISCONNECT);
        try{
            ws.send(SerializationUtil.serializeString(disconnectEvent));
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
}
