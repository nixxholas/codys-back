package com.goeey.game.utils;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class PlayerUtils {
    public static Table createLabel(Skin skin, int posX , int posY , String entity){
        //Create table
        Table buttonContainer = new Table(skin);
        buttonContainer.setTransform(true);

        //Player Name
        Label lb = new Label(entity, skin);
        lb.setFontScale(1);

        //Order
        buttonContainer.add(lb);
        buttonContainer.row().pad(10);
        buttonContainer.setOrigin(50, 25);
        buttonContainer.setPosition(posX, posY);
        return buttonContainer;
    }
}
