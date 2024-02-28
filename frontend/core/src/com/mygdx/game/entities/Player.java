package com.mygdx.game.entities;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class Player {
    // Creating an arc and designating points by setting the arc parameters
    static float startAngle = -30; // The start angle of the arc in degrees
    static float sweepAngle = -120; // The sweep angle of the arc in degrees
    public static float calcXPos(float slotNum, int numPlayers, float scrWidth, float scrHeight){
        float centerX = scrWidth / 2f; // The x coordinate of the arc's center
        float angle = MathUtils.lerpAngleDeg(startAngle, startAngle + sweepAngle,
                slotNum / (float) (numPlayers-1));
        float radius = Math.min(scrWidth, scrHeight) / 1.4f; // The radius of the
        // Use cosine and sine to calculate diagonal offset from center of circle
        return centerX + MathUtils.cosDeg(angle) * radius;
    }
    public static float calcYPos(float slotNum, int numPlayers, float scrWidth, float scrHeight){
        float centerY = scrHeight / 1.1f; // The y coordinate of the arc's center
        float angle = MathUtils.lerpAngleDeg(startAngle, startAngle + sweepAngle,
                slotNum / (float) (numPlayers-1));
        float radius = Math.min(scrWidth, scrHeight) / 1.4f; // The radius of the
        // Use cosine and sine to calculate diagonal offset from center of circle
        return centerY + MathUtils.sinDeg(angle) * radius;
    }
    public static Table createButtonLabel(Skin skin, int posX , int posY , int playerNum){
        //Button
        Table buttonContainer = new Table(skin);
        buttonContainer.setTransform(true);
        TextButton tb = new TextButton("Hit", skin);
        tb.setDisabled(true);

        //Label
        String lbString = "Player "+ playerNum;
        Label lb = new Label(lbString, skin);
        lb.setFontScale(1);

        //Order
        buttonContainer.add(lb);
        buttonContainer.row().pad(10);
        buttonContainer.add(tb).size(100, 50);
        buttonContainer.setOrigin(50, 25);
        buttonContainer.setPosition(posX, posY);
        return buttonContainer;
    }
}
