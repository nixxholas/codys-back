package com.mygdx.game.screens;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.text.NumberFormat;
import java.util.Locale;

/*
*   The Hud class is responsible:
*   1. Display Player name
*   2. Display and update player balance
*   3. Display action buttons e.g. Hit, Stand, Double
*   4. Disable action buttons as necessary
*
*   you tell me what else ...
*
* */
public class Hud implements Disposable {

    public Stage hudStage;
    private Table hudTable;

    /*
    *   Set locale to Singapore and language to English.
    *   Prepare a number formatter to format balance to SG currency format.
    *   e.g. sgCurrencyFormat(2000) will return a String "$2,000.00"
    * */
    Locale locale = new Locale.Builder().setLanguage("en").setRegion("SG").build();
    NumberFormat sgCurrencyFormat = NumberFormat.getCurrencyInstance(locale);

    // Labels to hold the values for display
    Label balanceLabel;
    Label playerNameLabel;

    public Hud (SpriteBatch batch, int balance, String playerName, Skin skin) {
        playerNameLabel = new Label("Player Name: " + playerName, skin);
        balanceLabel = new Label("Balance: " + sgCurrencyFormat.format(balance), skin);

        hudStage = new Stage(new ScreenViewport(), batch);
        hudTable = new Table(skin);
        hudTable.setFillParent(true);                       // set hudtable to fill the size of the stage
        hudTable.top().padTop(5);
        hudTable.add(playerNameLabel).expandX().left();     // anchor the table to the top left of the stage
        hudTable.add(balanceLabel).expandX().right();       // anchor the table to the top right of the stage

        hudStage.addActor(hudTable);
    }

    public void update(int balance) {
        balanceLabel.setText(sgCurrencyFormat.format(balance));
    }

    public void dispose() {
        hudStage.dispose();
    }
}
