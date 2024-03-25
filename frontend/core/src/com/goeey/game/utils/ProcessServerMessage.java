package com.goeey.game.utils;

import com.goeey.backend.entity.PlayerBetData;
import com.goeey.backend.entity.PlayerResultData;
import com.goeey.backend.util.SerializationUtil;
import com.goeey.game.screen.GameScreen;
import com.gooey.base.Card;
import com.gooey.base.EntityTarget;
import com.gooey.base.socket.ServerEvent;

import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ProcessServerMessage {
    public static void callMethod(ServerEvent<String> event){
        switch (event.getType()){
            case CONNECT:

            case DISCONNECT:

            case ROOM_LIST:// most likely deprecated OR not used
                //targetType List<String>
                returnRoomsList(event);
            case ROOM_PLAYERS:

            case REGISTERED:
                //targetType String
                returnRegistered(event);
            case ERROR:

            case COUNTDOWN:
                //targetType String
                returnCountdown(event);
            case UPDATE:
                //targetType String
                returnUpdate(event);
            case STOOD_UP:
                //targetType String
                returnStoodUp(event);
            case STARTING:
                //targetType String
                returnStarting(event);
            case DEAL:
                //targetType String
                returnDeal(event);
            case DEALER_REVEAL:
                break;
            case DEALER_DRAW, PLAYER_DRAW, PLAYER_HIT:
                try {
                    dealCardMethod(event);
                } catch (InvalidObjectException e) {
                    System.out.println(e.getMessage());
                }
                break;
            //            case PLAYER_HIT:
//                //targetType Actor
//                try {
//                    return targetType.cast(dealCardMethod(event));
//                } catch (InvalidObjectException e) {
//                    System.out.println(e.getMessage());
//                }
            case PLAYER_TURN:
                //targetType EntityTarget
                playerTurn(event);
            case PLAYER_WIN, PLAYER_LOSE, PLAYER_PUSH, PLAYER_BUST:
                //typeType PlayerResultData
                playerPRD(event);
            case PLAYER_STAND:
                //typeType EntityTarget
                playerStand(event);
            case PLAYER_BET:
                //TargetType PlayerBetData
                returnBet(event);
            case PLAYER_JOINED:
                //targetType String
                returnJoined(event);
            case PLAYER_LEFT:
                //targetType String
                returnPlayerLeft(event);
            case PLAYER_SAT:
                //targetType String
                returnPlayerSat(event);
            case PLAYER_DISCONNECTED:

            case PONG:
                //targetType
            default:
                System.out.println("Not a Server Event object");
        }
    }

    private static List<String> returnRoomsList(ServerEvent<String> event){
        List<String> roomList = new ArrayList<>();
        Set<?> roomSet = SerializationUtil.deserializeString(event.getMessage(), Set.class);
        for (Object o : roomSet) {
            roomList.add(o.toString());
        }
        return roomList;
    }

    private static String returnRegistered(ServerEvent<String> event){
        return event.getMessage();
    }

    private static String returnCountdown(ServerEvent<String> event){
        return event.getMessage();
    }

    private static String returnUpdate(ServerEvent<String> event){
        return event.getMessage();
    }

    private static String returnStoodUp(ServerEvent<String> event){
        return event.getMessage();
    }

    private static String returnStarting(ServerEvent<String> event){
        return event.getMessage();
    }

    private static String returnDeal(ServerEvent<String> event){
        return event.getMessage();
    }

    private static PlayerBetData returnBet(ServerEvent<String> event){
        PlayerBetData pbd = SerializationUtil.deserializeString(event.getMessage(), PlayerBetData.class);
        // format: (amtbet, playerMoney)
        return pbd;
    }

    private static String returnJoined(ServerEvent<String> event){
        return event.getMessage();
    }

    private static String returnPlayerLeft(ServerEvent<String> event){
        //returns player ID
        return event.getMessage();
    }

    private static String returnPlayerSat(ServerEvent<String> event){
        //returns player sat message
        return event.getMessage();
    }

    private static void dealCardMethod(ServerEvent<String> event) throws InvalidObjectException {
        Card card = null;
        EntityTarget entity = event.getTarget();
        switch (event.getType()) {
            case PLAYER_DRAW:
                card = SerializationUtil.deserializeString(event.getMessage(), Card.class);
                if (card != null) {
                    GameScreen.deal(entity, card.getRank() + "_" + card.getSuit());
                }
                break;
            case DEALER_DRAW:
                card = SerializationUtil.deserializeString(event.getMessage(), Card.class);
                if (card==null) {
                    GameScreen.deal(entity, "BACK_CARD");
                } else {
                    GameScreen.deal(entity, card.getRank() + "_" + card.getSuit());
                }
                break;
            default:
                throw new InvalidObjectException("Not Player/Dealer draw event");
        }
    }

    private static EntityTarget playerTurn(ServerEvent<String> event){
        EntityTarget whichPlayerTurn = event.getTarget();
        return whichPlayerTurn;
    }

    private static PlayerResultData playerPRD(ServerEvent<String> event){
        EntityTarget target = event.getTarget();
        PlayerResultData prd = SerializationUtil.deserializeString(event.getMessage(), PlayerResultData.class);
        return null;
    }

    private static PlayerResultData playerWin(ServerEvent<String> event){
        EntityTarget target = event.getTarget();
        PlayerResultData prd = SerializationUtil.deserializeString(event.getMessage(), PlayerResultData.class);
        return null;
    }

    private static PlayerResultData playerLost(ServerEvent<String> event){
        EntityTarget target = event.getTarget();
        PlayerResultData prd = SerializationUtil.deserializeString(event.getMessage(), PlayerResultData.class);
        return null;
    }

    private static PlayerResultData playerPush(ServerEvent<String> event){
        EntityTarget target = event.getTarget();
        PlayerResultData prd = SerializationUtil.deserializeString(event.getMessage(), PlayerResultData.class);
        return null;
    }

    private static PlayerResultData playerBust(ServerEvent<String> event){
        EntityTarget target = event.getTarget();
        PlayerResultData prd = SerializationUtil.deserializeString(event.getMessage(), PlayerResultData.class);
        return null;
    }

    private static EntityTarget playerStand(ServerEvent<String> event){
        EntityTarget whichPlayerStand = event.getTarget();
        return whichPlayerStand;
    }
}