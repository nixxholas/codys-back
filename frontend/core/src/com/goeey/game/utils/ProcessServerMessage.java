package com.goeey.game.utils;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.goeey.backend.util.SerializationUtil;
import com.goeey.game.GameManager;
import com.goeey.game.entity.GameState;
import com.goeey.game.screen.GameCreationScreen;
import com.goeey.game.screen.GameScreen;
import com.goeey.game.screen.LobbyRoomsScreen;
import com.goeey.game.socket.SocketHandler;
import com.gooey.base.Card;
import com.gooey.base.Player;
import com.gooey.base.socket.ServerEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Arrays;
import java.util.LinkedHashMap;

import static com.gooey.base.socket.ClientEvent.Type.LEAVE;


public class ProcessServerMessage {
    //Creating GSON Instance
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static ScreenAdapter gs;

    public static void setGS(ScreenAdapter gs){
        ProcessServerMessage.gs = gs;
    }

    public static void callMethod(ServerEvent<?> event, GameState gameState){
        switch (event.getType()){
            case ERROR:
                processError(event, gameState);
                break;
            case PLAYER_SAT:
                processPlayerSat(event, gameState);
                break;
            case COUNTDOWN:
                processCountdown(event, gameState);
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
            case PLAYER_HIT:
                // HIT already handled by PLAYER_DRAW
                break;
            case PLAYER_DRAW:
                processPlayerDraworHit(event);
                break;
            case PLAYER_TURN:
                processPlayerTurn(event);
                break;
            case CONNECT:
                //not implemented yet
                processConnect(event, gameState);
                break;
            case DISCONNECT:
                processDisconnect(event);
                break;
            case REGISTERED:
                processRegistered(event, gameState);
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
            case JOINED_LOBBY:
                processPlayerJoined(event, gameState);
                break;
            case PLAYER_LEFT:
                processPlayerLeft(event);
                break;
            case PLAYER_DISCONNECT:
                //not implemented yet
                processPlayerDisconnect(event);
                break;
            case PONG:
                //not implemented yet
                processPong(event);
                break;
            default:
                System.out.println(event.getType() + " not implemented yet.");
        }
    }

    private static void processError(ServerEvent<?> event, GameState gameState){
        if(event.getMessage().equals("You are already sitting.")){
            gameState.setSeated(true);

        } else if (event.getMessage().equals("Seat is already taken.")) {
            System.out.println("FAILED!!");
            if(gs instanceof  GameCreationScreen gsc){
//                gsc.setPlayerSat(false);
            }
            //GameCreationScreen.playerSat = false;

        } else if (event.getMessage().equals("Player already exists")) {
            if(gs instanceof GameCreationScreen) {
                gameState.setInLobby(true);
                Gdx.app.postRunnable(() -> {
                    ((GameCreationScreen) gs).showError("Username already exists! Please enter another name.");
                });
            }

        } else if (event.getMessage().equals("Invalid player")){
            if(gs instanceof GameCreationScreen) {
                ((GameCreationScreen) gs).showError("Username does not exist! Please register.");
            }
        }
    }

    private static void processPlayerSat(ServerEvent<?> event, GameState gameState){
        gameState.setSeated(true);
    }

    private static void processCountdown(ServerEvent<?> event, GameState gameState){
        System.out.println(event.getMessage());
        if(gs instanceof GameScreen gs1){
            int num = (int) Double.parseDouble(event.getMessage().toString());
            //Checking is this is the first countdown to print a different message
            if(gameState.isFirstCountDown()){
                Gdx.app.postRunnable(() -> gs1.updateGameState("Please place bets, game starts in : " + num));
                if(num == 1){
                    gameState.setFirstCountDown(false);
                    if(!gameState.getHasBet()){
                        Gdx.app.postRunnable(gs1::unseatPlayer);
                    }

                }
            }else{
                Gdx.app.postRunnable(() -> gs1.updateGameState("Countdown: " + num));
            }
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

    private static void processConnect(ServerEvent<?> event, GameState gameState){
        gameState.setInLobby(true);
        gameState.setSeated(true);
        System.out.println(event.getMessage());
        if(gs instanceof GameCreationScreen) {
            Gdx.app.postRunnable(() -> {
                ((GameCreationScreen) gs).goToLobbyScreen();
            });
        }
    }

    private static void processDisconnect(ServerEvent<?> event){
        System.out.println(event.getMessage());
    }

    private static void processRegistered(ServerEvent<?> event, GameState gameState){
        gameState.setRegistered(true);
        gameState.setInLobby(true);
        gameState.setSeated(true);
        if(gs instanceof GameCreationScreen) {
            Gdx.app.postRunnable(() -> {
                ((GameCreationScreen) gs).goToLobbyScreen();
            });
        }
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
            System.out.println("This Event does not return the amount and earnings");
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

    private static void processPlayerJoined(ServerEvent<?> event, GameState gameState){
        gameState.setPlayerBalance((int) Double.parseDouble(event.getMessage().toString()));
        gameState.setInRoom(true);
        gameState.setRegistered(true);
        gameState.setInLobby(true);
        gameState.setSeated(true);
        if(gs instanceof GameCreationScreen) {
            Gdx.app.postRunnable(() -> {
                ((GameCreationScreen) gs).goToLobbyScreen();
            });
        }
    }

    private static void processPlayerLeft(ServerEvent<?> event){
        System.out.println(event.getMessage());
    }

    private static void processPlayerDisconnect(ServerEvent<?> event){
        System.out.println(event.getMessage());
    }

    private static void processPong(ServerEvent<?> event){
        System.out.println(event.getMessage());
    }

}