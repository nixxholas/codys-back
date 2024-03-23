package com.goeey.game.utils;

import com.badlogic.gdx.Gdx;
import com.gooey.base.EntityTarget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerXY {
    private int playerX;
    private int playerY;
    private int count;

    public PlayerXY(int playerX, int playerY) {
        this.playerX = playerX;
        this.playerY = playerY;
        this.count = 0;
    }

    public int getPlayerX() {
        return playerX;
    }
    public int getPlayerY() {
        return playerY;
    }
    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }

    // Returns a clean Map that consists of a Key (Dealer, Player_1, Player_2 etc),
    // and the values (the X and y Coordinates of everyone, and count of cards)
    public static Map<EntityTarget, PlayerXY> refreshMap(){
        Map<EntityTarget, PlayerXY> playerMap = new HashMap<>();
        List<EntityTarget> etList = new ArrayList<>();
        etList.add(EntityTarget.PLAYER_1);etList.add(EntityTarget.PLAYER_2);
        etList.add(EntityTarget.PLAYER_3);etList.add(EntityTarget.PLAYER_4);
        etList.add(EntityTarget.PLAYER_5);

        int dealX = (int) (Gdx.graphics.getWidth()/2.035f);
        int dealY = (int) (Gdx.graphics.getHeight()/1.6f);
        playerMap.put(EntityTarget.DEALER, new PlayerXY(dealX, dealY));

        //go through etList, put every person as key, and arr as values
        for (int i=0; i<etList.size(); i++ ){
            // Use cosine and sine to calculate diagonal offset from center of circle
            int cardX = (int) CalculateXY.calcXPos(i, 5);
            int cardY = (int) CalculateXY.calcYPos(i, 5);
            playerMap.put(etList.get(i), new PlayerXY(cardX, cardY));
        }
        return playerMap;
    }
}
