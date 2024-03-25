package com.goeey.game.utils;

import com.badlogic.gdx.scenes.scene2d.Actor;
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
    // NEED TO CHANGE Object to something less disastrous
    public static <T> T callMethod(ServerEvent<String> event, Class<T> targetType){
        switch (event.getType()){
            case CONNECT:

            case DISCONNECT:

            case ROOM_LIST:// most likely deprecated OR not used
                //targetType List<String>
                return targetType.cast(returnRoomsList(event));
            case ROOM_PLAYERS:

            case REGISTERED:
                //targetType String
                return targetType.cast(returnRegistered(event));
            case ERROR:

            case COUNTDOWN:
                //targetType String
                return targetType.cast(returnCountdown(event));
            case UPDATE:
                //targetType String
                return targetType.cast(returnUpdate(event));
            case STOOD_UP:
                //targetType String
                return targetType.cast(returnStoodUp(event));
            case STARTING:
                //targetType String
                return targetType.cast(returnStarting(event));
            case DEAL:
                //targetType String
                return targetType.cast(returnDeal(event));
            case DEALER_REVEAL:

            case DEALER_DRAW, PLAYER_DRAW, PLAYER_HIT:
                //targetType Actor
                try {
                    return targetType.cast(dealCardMethod(event));
                } catch (InvalidObjectException e) {
                    System.out.println(e.getMessage());
                }
//            case PLAYER_HIT:
//                //targetType Actor
//                try {
//                    return targetType.cast(dealCardMethod(event));
//                } catch (InvalidObjectException e) {
//                    System.out.println(e.getMessage());
//                }
            case PLAYER_TURN:
                //targetType EntityTarget
                return targetType.cast(playerTurn(event));
            case PLAYER_WIN, PLAYER_LOSE, PLAYER_PUSH, PLAYER_BUST:
                //typeCast PlayerResultData
                return targetType.cast(playerPRD(event));

            case PLAYER_STAND:

            case PLAYER_BET:
                //TargetType PlayerBetData
                return targetType.cast(returnBet(event));
            case PLAYER_JOINED:
                //targetType String
                return targetType.cast(returnJoined(event));
            case PLAYER_LEFT:
                //targetType String
                return targetType.cast(processPlayerLeft(event));
            case PLAYER_SAT:

            case PLAYER_DISCONNECT:

            case PONG:

            default:
                System.out.println("Not a Server Event object");
        }
        return null;
    }

    public static List<String> returnRoomsList(ServerEvent<String> event){
        List<String> roomList = new ArrayList<>();
        Set<?> roomSet = SerializationUtil.deserializeString(event.getMessage(), Set.class);
        for (Object o : roomSet) {
            roomList.add(o.toString());
        }
        return roomList;
    }

    public static String returnRegistered(ServerEvent<String> event){
        return event.getMessage();
    }

    public static String returnCountdown(ServerEvent<String> event){
        return event.getMessage();
    }

    public static String returnUpdate(ServerEvent<String> event){
        return event.getMessage();
    }

    public static String returnStoodUp(ServerEvent<String> event){
        return event.getMessage();
    }

    public static String returnStarting(ServerEvent<String> event){
        return event.getMessage();
    }

    public static String returnDeal(ServerEvent<String> event){
        return event.getMessage();
    }

    public static PlayerBetData returnBet(ServerEvent<String> event){
        PlayerBetData pbd = SerializationUtil.deserializeString(event.getMessage(), PlayerBetData.class);
        return pbd;
    }

    public static String returnJoined(ServerEvent<String> event){
        return event.getMessage();
    }

    public static String processPlayerLeft(ServerEvent<String> event){
        //returns player ID
        return event.getMessage();
    }

    public static Actor dealCardMethod(ServerEvent<String> event) throws InvalidObjectException {
        Card card = null;
        EntityTarget entity = event.getTarget();
        switch (event.getType()) {
            case PLAYER_DRAW:
                card = SerializationUtil.deserializeString(event.getMessage(), Card.class);
                if (card != null) {
                    return GameScreen.deal(entity, card.getRank() + "_" + card.getSuit());
                }
            case DEALER_DRAW:
                card = SerializationUtil.deserializeString(event.getMessage(), Card.class);
                if (card==null) {
                    return GameScreen.deal(entity, "BACK_CARD");
                } else {
                    return GameScreen.deal(entity, card.getRank() + "_" + card.getSuit());
                }
            default:
                throw new InvalidObjectException("Not Player/Dealer draw event");
        }
    }

    public static EntityTarget playerTurn(ServerEvent<String> event){
        EntityTarget whichPlayerTurn = event.getTarget();
        return whichPlayerTurn;
    }

    public static PlayerResultData playerPRD(ServerEvent<String> event){
        EntityTarget target = event.getTarget();
        PlayerResultData prd = SerializationUtil.deserializeString(event.getMessage(), PlayerResultData.class);
        return null;
    }

    public static PlayerResultData playerWin(ServerEvent<String> event){
        EntityTarget target = event.getTarget();
        PlayerResultData prd = SerializationUtil.deserializeString(event.getMessage(), PlayerResultData.class);
        return null;
    }

    public static PlayerResultData playerLost(ServerEvent<String> event){
        EntityTarget target = event.getTarget();
        PlayerResultData prd = SerializationUtil.deserializeString(event.getMessage(), PlayerResultData.class);
        return null;
    }

    public static PlayerResultData playerPush(ServerEvent<String> event){
        EntityTarget target = event.getTarget();
        PlayerResultData prd = SerializationUtil.deserializeString(event.getMessage(), PlayerResultData.class);
        return null;
    }

    public static PlayerResultData playerBust(ServerEvent<String> event){
        EntityTarget target = event.getTarget();
        PlayerResultData prd = SerializationUtil.deserializeString(event.getMessage(), PlayerResultData.class);
        return null;
    }

}