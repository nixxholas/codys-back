package com.goeey.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.goeey.backend.util.SerializationUtil;
import com.goeey.game.GameManager;
import com.goeey.game.screen.GameCreationScreen;
import com.goeey.game.screen.GameScreen;
import com.goeey.game.socket.SocketHandler;
import com.gooey.base.Card;
import com.gooey.base.socket.ServerEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


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
                //PLAYER_HIT
                processPlayerDraworHit(event);
                break;
            case PLAYER_TURN:
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
                processRegistered(event);
                break;
            case UPDATE:
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
            case PLAYER_DOUBLE:
                processPlayerDouble(event);
                break;
            case PLAYER_WIN, PLAYER_LOSE, PLAYER_PUSH:
                processPlayerWinorLoseorPush(event);
                break;
            case PLAYER_BUST:
                processPlayerBust(event);
                break;
            case PLAYER_STAND:
                processPlayerStand(event);
                break;
            case PLAYER_BET:
                processPlayerBet(event);
                break;
            case BET:
                processBet(event);
                break;
            case PLAYER_JOINED:
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
                System.out.println(event.getType() + " not implemented yet.");
        }
    }

    private static void processError(ServerEvent<?> event){
        if(event.getMessage().equals("You are already sitting.")){
            System.out.println("SUCCESS!!");
            if(gs instanceof  GameCreationScreen gsc){
                gsc.setPlayerSat(true);
            }
            //GameCreationScreen.playerSat = true;

        } else if (event.getMessage().equals("Seat is already taken.")) {
            System.out.println("FAILED!!");
            if(gs instanceof  GameCreationScreen gsc){
                gsc.setPlayerSat(false);
            }
            //GameCreationScreen.playerSat = false;
        }
    }

    private static void processPlayerSat(ServerEvent<?> event){
        System.out.println("SUCCESS!!");
        if(gs instanceof  GameCreationScreen gsc){
            gsc.setPlayerSat(true);
        }
        //GameCreationScreen.playerSat = true;
    }

    private static void processCountdown(ServerEvent<?> event){
        System.out.println(event.getMessage());
        if(gs instanceof GameScreen gs1){
            Gdx.app.postRunnable(() -> gs1.updateGameState("Countdown: " + event.getMessage()));
        }

    }

    private static void processDeal(ServerEvent<?> event){
        System.out.println(event.getMessage());
        if(gs instanceof GameScreen gs1){
            Gdx.app.postRunnable(() -> gs1.updateGameState("Game has started"));
        }
    }

    private static void processDealerDraw(ServerEvent<?> event){
        System.out.println(event.getMessage());
        System.out.println(event.getTarget());
        String targetPlayer = String.valueOf(event.getTarget());
        if(event.getMessage() != null){
            // Convert the LinkedHashMap to a JSON string
            String jsonString = gson.toJson(event.getMessage());
            Card card = SerializationUtil.deserializeString(jsonString, Card.class);
            System.out.println(card.getRank());
            System.out.println(card.getSuit());
            System.out.println(targetPlayer);
            if(gs instanceof GameScreen gs){
                Gdx.app.postRunnable(() -> gs.updateUI(card,
                        "DRAW_" + targetPlayer + "_0",
                        0));
                Gdx.app.postRunnable(() -> gs.updateGameState("Dealing cards"));
            }
        }else{
            if(gs instanceof GameScreen gs){
                Gdx.app.postRunnable(() -> gs.updateUI(null,
                        "DRAW_" + targetPlayer + "_0",
                        0));
                Gdx.app.postRunnable(() -> gs.updateGameState("Dealing cards"));
            }
        }
    }

    private static void processDealerReveal(ServerEvent<?> event){
        System.out.println(event.getMessage());
        System.out.println(event.getTarget());
        String targetPlayer = String.valueOf(event.getTarget());
        if(event.getMessage() != null && event.getTarget() != null){
            // Convert the LinkedHashMap to a JSON string
            String jsonString = gson.toJson(event.getMessage());
            Card card = SerializationUtil.deserializeString(jsonString, Card.class);
            System.out.println(card.getRank());
            System.out.println(card.getSuit());
            System.out.println(targetPlayer);
            if(gs instanceof GameScreen gs){
                Gdx.app.postRunnable(() -> gs.updateUI(card,
                        "DEALER_REVEAL_" + targetPlayer + "_0",
                        0));
                Gdx.app.postRunnable(() -> gs.updateGameState("Dealing cards"));
            }
        }
    }

    private static void processPlayerDraworHit(ServerEvent<?> event){
        System.out.println(event.getMessage());
        if(event.getMessage() != null){
            // Convert the LinkedHashMap to a JSON string
            String jsonString = gson.toJson(event.getMessage());
            Card card = SerializationUtil.deserializeString(jsonString, Card.class);
            String targetPlayer = String.valueOf(event.getTarget());
            System.out.println(card.getRank());
            System.out.println(card.getSuit());
            System.out.println(targetPlayer);
            if(gs instanceof GameScreen gs){
                Gdx.app.postRunnable(() -> gs.updateUI(card,
                        "DRAW_" + targetPlayer,
                        0));
                Gdx.app.postRunnable(() -> gs.updateGameState("Dealing cards"));
            }
        }
    }

    private static void processPlayerTurn(ServerEvent<?> event){
        System.out.println(event.getMessage());
        if(gs instanceof  GameScreen gs){
            String target = event.getTarget().toString();
            int seatNum = target.charAt(target.length() - 1) - '0';
            Gdx.app.postRunnable(() -> gs.updateUI(null,
                    "PLAYER_TURN_" + event.getTarget(),
                    0));
        }
    }

    private static void processConnect(ServerEvent<?> event){
        System.out.println(event.getMessage());
    }

    private static void processDisconnect(ServerEvent<?> event){
        System.out.println(event.getMessage());
    }

    private static void processRoomList(ServerEvent<?> event){
        System.out.println(event.getMessage());
        if(gs instanceof  GameCreationScreen gcs){
            String roomsStr = event.getMessage().toString().substring(1,
                    event.getMessage().toString().length() - 1);
            System.out.println(roomsStr.isBlank());
            System.out.println(roomsStr.isEmpty());
            if(roomsStr.isBlank() || roomsStr.isEmpty()){
                gcs.setRoomList(null);
            }else{
                String[] roomStrArr = roomsStr.split(",");
                for (int i = 0; i < roomStrArr.length; i++) {
                    roomStrArr[i] = roomStrArr[i].strip();
                    System.out.println(roomStrArr[i]);
                }
                gcs.setRoomList(roomStrArr);
            }

            //GameManager.socketHandler.getLatch().countDown();

        }
    }

    private static void processRoomPlayers(ServerEvent<?> event){
        System.out.println(event.getMessage());
        if(gs instanceof GameCreationScreen gsc){
            gsc.setNumPlayers((int)Double.parseDouble(event.getMessage().toString()));
        }
    }

    private static void processRegistered(ServerEvent<?> event){
        System.out.println(event.getMessage());
    }

    private static void processUpdate(ServerEvent<?> event){
        System.out.println(event.getMessage());
        if(gs instanceof GameScreen gs){
            Gdx.app.postRunnable(() -> gs.updateUI(null,
                    "UPDATE",
                    0));
        }
    }

    private static void processStoodUp(ServerEvent<?> event){
        System.out.println(event.getMessage());
    }

    private static void processStarting(ServerEvent<?> event){
        System.out.println(event.getMessage());
    }

//    private static void processPlayerHit(ServerEvent<?> event){
//        System.out.println(event.getMessage());
//        if(event.getMessage() != null){
//            // Convert the LinkedHashMap to a JSON string
//            String jsonString = gson.toJson(event.getMessage());
//            Card card = SerializationUtil.deserializeString(jsonString, Card.class);
//            String targetPlayer = String.valueOf(event.getTarget());
//            System.out.println(card.getRank());
//            System.out.println(card.getSuit());
//            System.out.println(targetPlayer);
//            if(gs instanceof GameScreen gs){
//                Gdx.app.postRunnable(() -> gs.updateUI(card,
//                        "DRAW_" + targetPlayer,
//                        0));
//                Gdx.app.postRunnable(() -> gs.updateGameState("Dealing cards"));
//            }
//        }
//    }

    private static void processPlayerDouble(ServerEvent<?> event){
        System.out.println(event.getMessage());
        if(event.getMessage() != null){
            // Convert the LinkedHashMap to a JSON string
            String jsonString = gson.toJson(event.getMessage());
            Card card = SerializationUtil.deserializeString(jsonString, Card.class);
            String targetPlayer = String.valueOf(event.getTarget());
            System.out.println(card.getRank());
            System.out.println(card.getSuit());
            System.out.println(targetPlayer);
            if(gs instanceof GameScreen gs){
                Gdx.app.postRunnable(() -> gs.updateUI(card,
                        "PLAYER_DOUBLE_" + targetPlayer,
                        0));
                Gdx.app.postRunnable(() -> gs.updateGameState("Dealing cards"));
            }
        }
    }

    private static void processPlayerWinorLoseorPush(ServerEvent<?> event){
        System.out.println(event.getMessage());
        String message = event.getMessage().toString();
        JsonObject messageObject = JsonParser.parseString(message).getAsJsonObject();
        double balance = messageObject.getAsJsonPrimitive("balance").getAsDouble();
        double earnings = messageObject.getAsJsonPrimitive("earnings").getAsDouble();
        System.out.println("Balance: " + balance);
        System.out.println("Earnings: " + earnings);
        if(gs instanceof GameScreen gs){
            Gdx.app.postRunnable(() -> gs.updateUI(null,
                    event.getType().toString() +
                            "_" + event.getTarget(),
                    (int)earnings));
        }
    }

    private static void processPlayerPush(ServerEvent<?> event){
        System.out.println(event.getMessage());
    }

    private static void processPlayerBust(ServerEvent<?> event){
        System.out.println(event.getMessage());
        boolean error = false;
        String message = event.getMessage().toString();
        JsonObject messageObject = JsonParser.parseString(message).getAsJsonObject();
        try{
            double earnings = messageObject.getAsJsonPrimitive("earnings").getAsDouble();
            if(gs instanceof GameScreen gs){
                Gdx.app.postRunnable(() -> gs.updateUI(null,
                        "PLAYER_LOSE_" + event.getTarget(),
                        (int)earnings));
                Gdx.app.postRunnable(() -> gs.updateUI(null,
                        "PLAYER_BUST_" + event.getTarget(),
                        (int)earnings));
            }
        }catch (NullPointerException ex){
            //Event does not return amount and earnings
            error = true;

        }

        if(error){
            //Event returns card type
            String targetPlayer = String.valueOf(event.getTarget());
            String jsonString = gson.toJson(event.getMessage());
            Card card = SerializationUtil.deserializeString(jsonString, Card.class);
            if(gs instanceof GameScreen gs){
                Gdx.app.postRunnable(() -> gs.updateUI(card,
                        "DRAW_" + targetPlayer,
                        0));
                Gdx.app.postRunnable(() -> gs.updateGameState("Dealing cards"));
            }
        }
    }

    private static void processPlayerStand(ServerEvent<?> event){
        System.out.println(event.getMessage());
        if(gs instanceof  GameScreen gs){
            Gdx.app.postRunnable(() -> gs.updateUI(null,
                    "PLAYER_STAND_" + event.getTarget(),
                    0));
        }
    }

    private static void processPlayerBet(ServerEvent<?> event){
        System.out.println(event.getMessage());
    }

    private static void processBet(ServerEvent<?> event){
        System.out.println(event.getMessage());
    }

    private static void processPlayerJoined(ServerEvent<?> event){
        System.out.println(event.getMessage());
    }

    private static void processPlayerLeft(ServerEvent<?> event){
        System.out.println("SERVER PLAYER LEFT");
        System.out.println(event.getMessage());
    }

    private static void processPlayerDisconnected(ServerEvent<?> event){
        System.out.println(event.getMessage());
    }

    private static void processPong(ServerEvent<?> event){
        System.out.println(event.getMessage());
    }

}