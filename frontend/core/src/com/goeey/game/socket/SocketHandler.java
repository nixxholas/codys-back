package com.goeey.game.socket;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.goeey.backend.util.SerializationUtil;
import com.goeey.game.screen.GameCreationScreen;
import com.goeey.game.screen.GameScreen;
import com.gooey.base.Card;
import com.gooey.base.socket.ClientEvent;
import com.gooey.base.socket.ServerEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.java_websocket.enums.ReadyState;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.concurrent.CountDownLatch;

public class SocketHandler {
    private final WebSocket ws;

    private CountDownLatch latch = new CountDownLatch(1);

    //Creating GSON Instance
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private ScreenAdapter gs;

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
                        var serverEvent =  SerializationUtil.deserializeString(message, ServerEvent.class);
                        //System.out.println(serverEvent.getType());
                        //System.out.println(serverEvent.getMessage());
                        switch(serverEvent.getType()){
                            case ERROR:
                                if(serverEvent.getMessage().equals("You are already sitting.")){
                                    System.out.println("SUCCESS!!");
                                    GameCreationScreen.playerSat = true;
                                    this.latch.countDown();
                                } else if (serverEvent.getMessage().equals("Seat is already taken.")) {
                                    System.out.println("FAILED!!");
                                    GameCreationScreen.playerSat = false;
                                    this.latch.countDown();
                                }
                                break;
                            case PLAYER_SAT:
                                System.out.println("SUCCESS!!");
                                GameCreationScreen.playerSat = true;
                                this.latch.countDown();
                                break;
                            case COUNTDOWN:
                                System.out.println(serverEvent.getMessage());
                                break;
                            case DEAL:
                                System.out.println(serverEvent.getMessage());
                                break;
                            case DEALER_DRAW:
                                System.out.println(serverEvent.getMessage());
                                System.out.println(serverEvent.getTarget());
                                String targetPlayer = String.valueOf(serverEvent.getTarget());
                                if(serverEvent.getMessage() != null){
                                    // Convert the LinkedHashMap to a JSON string
                                    String jsonString =  gson.toJson(serverEvent.getMessage());
                                    Card card = SerializationUtil.deserializeString(jsonString, Card.class);
                                    System.out.println(card.getRank());
                                    System.out.println(card.getSuit());
                                    System.out.println(targetPlayer);
                                    if(gs instanceof GameScreen gs1){
                                        Gdx.app.postRunnable(() -> gs1.updateUI(card, targetPlayer, false));
                                    }
                                }else{
                                    if(gs instanceof GameScreen gs1){
                                        Gdx.app.postRunnable(() -> gs1.updateUI(null, targetPlayer, false));
                                    }
                                }
                                break;
                            case DEALER_REVEAL:
                                System.out.println(serverEvent.getMessage());
                                System.out.println(serverEvent.getTarget());
                                String targetPlayer1 = String.valueOf(serverEvent.getTarget());

                                if(serverEvent.getMessage() != null && serverEvent.getTarget() != null){
                                    // Convert the LinkedHashMap to a JSON string
                                    String jsonString =  gson.toJson(serverEvent.getMessage());
                                    Card card = SerializationUtil.deserializeString(jsonString, Card.class);
                                    System.out.println(card.getRank());
                                    System.out.println(card.getSuit());
                                    System.out.println(targetPlayer1);
                                    if(gs instanceof GameScreen gs1){
                                        Gdx.app.postRunnable(() -> gs1.updateUI(card, targetPlayer1, true));
                                    }
                                }
                                break;
                            case PLAYER_DRAW:
                                System.out.println(serverEvent.getMessage());
                                if(serverEvent.getMessage() != null){
                                    // Convert the LinkedHashMap to a JSON string
                                    String jsonString =  gson.toJson(serverEvent.getMessage());
                                    Card card = SerializationUtil.deserializeString(jsonString, Card.class);
                                    String targetPlayer2 = String.valueOf(serverEvent.getTarget());
                                    System.out.println(card.getRank());
                                    System.out.println(card.getSuit());
                                    System.out.println(targetPlayer2);
                                    if(gs instanceof GameScreen gs1){
                                        Gdx.app.postRunnable(() -> gs1.updateUI(card, targetPlayer2, false));
                                    }
                                }
                                break;
                            case PLAYER_TURN:
                                System.out.println(serverEvent.getMessage());
                                break;
                            default:
                                System.out.println("Not a ServerEvent object/Not implemented yet");
                        }
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        listenerThread.start();
    }

    public void setGS(ScreenAdapter gs){
        this.gs = gs;
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

    public void closeWebSocket() {
        if(ws.isOpen()){
            ws.close();
        }
    }
}
