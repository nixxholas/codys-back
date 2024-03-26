package com.goeey.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.goeey.backend.util.SerializationUtil;
import com.goeey.game.screen.GameCreationScreen;
import com.goeey.game.screen.GameScreen;
import com.gooey.base.Card;
import com.gooey.base.socket.ServerEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class ProcessServerMessage {
    //Creating GSON Instance
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static ScreenAdapter gs;
    public static void setGS(ScreenAdapter gs){
        ProcessServerMessage.gs = gs;
    }
    public static void callMethod(ServerEvent<?> event){
        switch (event.getType()){
            case ERROR:
                processError(event);
                break;
            case PLAYER_SAT:
                processPlayerSat(event);
                break;
            case COUNTDOWN:
                processCountdown(event);
                break;
            case DEAL:
                processDeal(event);
                break;
            case DEALER_DRAW:
                processDealerDraw(event);
                break;
            case DEALER_REVEAL:
                processDealerReveal(event);
                break;
            case PLAYER_DRAW:
                processPlayerDraw(event);
                break;
            case PLAYER_TURN:
                //not implemented yet
                processPlayerTurn(event);
                break;
            case CONNECT:
                //not implemented yet
                processConnect(event);
                break;
            case DISCONNECT:
                //not implemented yet
                processDisconnect(event);
                break;
            case ROOM_LIST:
                //not implemented yet
                processRoomList(event);
                break;
            case ROOM_PLAYERS:
                //not implemented yet
                processRoomPlayers(event);
                break;
            case REGISTERED:
                //not implemented yet
                processRegistered(event);
                break;
            case UPDATE:
                //not implemented yet
                processUpdate(event);
                break;
            case STOOD_UP:
                //not implemented yet
                processStoodUp(event);
                break;
            case STARTING:
                //not implemented yet
                processStarting(event);
                break;
            case PLAYER_HIT:
                //not implemented yet
                processPlayerHit(event);
                break;
            case PLAYER_WIN:
                //not implemented yet
                processPlayerWin(event);
                break;
            case PLAYER_LOSE:
                //not implemented yet
                processPlayerLose(event);
                break;
            case PLAYER_PUSH:
                //not implemented yet
                processPlayerPush(event);
                break;
            case PLAYER_BUST:
                //not implemented yet
                processPlayerBust(event);
                break;
            case PLAYER_STAND:
                //not implemented yet
                processPlayerStand(event);
                break;
            case PLAYER_BET:
                //not implemented yet
                processPlayerBet(event);
                break;
            case PLAYER_JOINED:
                //not implemented yet
                processPlayerJoined(event);
                break;
            case PLAYER_LEFT:
                //not implemented yet
                processPlayerLeft(event);
                break;
            case PLAYER_DISCONNECTED:
                //not implemented yet
                processPlayerDisconnected(event);
                break;
            case PONG:
                //not implemented yet
                processPong(event);
                break;
            default:
                System.out.println("" + event.getType() +  " not implemented yet");
        }
    }

    private static void processError(ServerEvent<?> event){
        if(event.getMessage().equals("You are already sitting.")){
            System.out.println("SUCCESS!!");
            GameCreationScreen.playerSat = true;

        } else if (event.getMessage().equals("Seat is already taken.")) {
            System.out.println("FAILED!!");
            GameCreationScreen.playerSat = false;
        }
    }

    private static void processPlayerSat(ServerEvent<?> event){
        System.out.println("SUCCESS!!");
        GameCreationScreen.playerSat = true;
    }

    private static void processCountdown(ServerEvent<?> event){
        System.out.println(event.getMessage());
    }

    private static void processDeal(ServerEvent<?> event){
        System.out.println(event.getMessage());
    }

    private static void processDealerDraw(ServerEvent<?> event){
        System.out.println(event.getMessage());
        System.out.println(event.getTarget());
        String targetPlayer = String.valueOf(event.getTarget());
        if(event.getMessage() != null){
            // Convert the LinkedHashMap to a JSON string
            String jsonString =  gson.toJson(event.getMessage());
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
    }

    private static void processDealerReveal(ServerEvent<?> event){
        System.out.println(event.getMessage());
        System.out.println(event.getTarget());
        String targetPlayer = String.valueOf(event.getTarget());
        if(event.getMessage() != null && event.getTarget() != null){
            // Convert the LinkedHashMap to a JSON string
            String jsonString =  gson.toJson(event.getMessage());
            Card card = SerializationUtil.deserializeString(jsonString, Card.class);
            System.out.println(card.getRank());
            System.out.println(card.getSuit());
            System.out.println(targetPlayer);
            if(gs instanceof GameScreen gs1){
                Gdx.app.postRunnable(() -> gs1.updateUI(card, targetPlayer, true));
            }
        }
    }

    private static void processPlayerDraw(ServerEvent<?> event){
        System.out.println(event.getMessage());
        if(event.getMessage() != null){
            // Convert the LinkedHashMap to a JSON string
            String jsonString =  gson.toJson(event.getMessage());
            Card card = SerializationUtil.deserializeString(jsonString, Card.class);
            String targetPlayer2 = String.valueOf(event.getTarget());
            System.out.println(card.getRank());
            System.out.println(card.getSuit());
            System.out.println(targetPlayer2);
            if(gs instanceof GameScreen gs1){
                Gdx.app.postRunnable(() -> gs1.updateUI(card, targetPlayer2, false));
            }
        }
    }

    private static void processPlayerTurn(ServerEvent<?> event){
        System.out.println(event.getMessage());
    }

    private static void processConnect(ServerEvent<?> event){
        System.out.println(event.getMessage());
    }

    private static void processDisconnect(ServerEvent<?> event){
        System.out.println(event.getMessage());
    }

    private static void processRoomList(ServerEvent<?> event){
        System.out.println(event.getMessage());
    }

    private static void processRoomPlayers(ServerEvent<?> event){
        System.out.println(event.getMessage());
    }

    private static void processRegistered(ServerEvent<?> event){
        System.out.println(event.getMessage());
    }

    private static void processUpdate(ServerEvent<?> event){
        System.out.println(event.getMessage());
    }

    private static void processStoodUp(ServerEvent<?> event){
        System.out.println(event.getMessage());
    }

    private static void processStarting(ServerEvent<?> event){
        System.out.println(event.getMessage());
    }

    private static void processPlayerHit(ServerEvent<?> event){
        System.out.println(event.getMessage());
    }

    private static void processPlayerWin(ServerEvent<?> event){
        System.out.println(event.getMessage());
    }

    private static void processPlayerLose(ServerEvent<?> event){
        System.out.println(event.getMessage());
    }

    private static void processPlayerPush(ServerEvent<?> event){
        System.out.println(event.getMessage());
    }

    private static void processPlayerBust(ServerEvent<?> event){
        System.out.println(event.getMessage());
    }

    private static void processPlayerStand(ServerEvent<?> event){
        System.out.println(event.getMessage());
    }

    private static void processPlayerBet(ServerEvent<?> event){
        System.out.println(event.getMessage());
    }

    private static void processPlayerJoined(ServerEvent<?> event){
        System.out.println(event.getMessage());
    }

    private static void processPlayerLeft(ServerEvent<?> event){
        System.out.println(event.getMessage());
    }

    private static void processPlayerDisconnected(ServerEvent<?> event){
        System.out.println(event.getMessage());
    }

    private static void processPong(ServerEvent<?> event){
        System.out.println(event.getMessage());
    }

}