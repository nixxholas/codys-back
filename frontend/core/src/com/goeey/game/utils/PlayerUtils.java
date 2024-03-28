package com.goeey.game.utils;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class PlayerUtils {
    public static Table createLabel(Skin skin, int posX , int posY , String entity, boolean isCurrentPlayer){
        //Create table
        Table buttonContainer = new Table(skin);
        buttonContainer.setTransform(true);

        //Player Name
        Label lbName = new Label(entity, skin);
        lbName.setFontScale(1);


        if(isCurrentPlayer){
            //Player Amount
            Label lblAmt = new Label("Amount: $1000" , skin);
            lblAmt.setFontScale(1);

            //Hit Button
            TextButton hitButton = new TextButton("Hit", skin);
            hitButton.addListener(new ClickListener() {
                public void clicked(InputEvent event, float x, float y){
                    System.out.println("Clicked Hit!!");
                }
            });

            //Stand Button
            TextButton standButton = new TextButton("Stand", skin);
            standButton.addListener(new ClickListener() {
                public void clicked(InputEvent event, float x, float y){
                    System.out.println("Clicked Stand!!");
                }
            });

            buttonContainer.add(lbName).left();
            buttonContainer.add(lblAmt).right();
            buttonContainer.row().width(100).height(40);
            buttonContainer.add(hitButton).left().padTop(10);
            buttonContainer.add(standButton).right().padTop(10);
        }else{
            buttonContainer.add(lbName).center();
        }

        //buttonContainer.setOrigin(50, 25);
        buttonContainer.setPosition(posX, posY);
        return buttonContainer;
    }
}
