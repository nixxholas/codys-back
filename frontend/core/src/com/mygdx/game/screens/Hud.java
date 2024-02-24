package com.mygdx.game.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.text.NumberFormat;
import java.util.Locale;

/*
*   The Hud class is responsible for displaying:
*   1. Player name
*   2. Available balance
*   3. you tell me what else ...
*
* */
public class Hud {
    public Stage stage;
    private final ScreenViewport hudViewport;
    private int balance;
    private static String playerName;

    /*
    *   Set locale to Singapore and language to English.
    *   Prepare a number formatter to format balance to SG currency format.
    *   e.g. sgCurrencyFormat(2000) will return a String "$2,000.00"
    *
    * */
    Locale locale = new Locale.Builder().setLanguage("en").setRegion("SG").build();
    NumberFormat sgCurrencyFormat = NumberFormat.getCurrencyInstance(locale);

    /*
    *   Labels to label the player name and balance.
    * */
    private static final Label.LabelStyle labelStyle = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
    static Label playerNameTextLabel = new Label("Player Name: ", labelStyle);
    static Label balanceTextLabel = new Label("Balance: ", labelStyle);

    /*
    *
    * */
    private Table playerNameTable;
    private Table balanceTable;

    Label balanceLabel;
    Label playerNameLabel;

    public Hud (SpriteBatch batch, int balance, int bet, String playerName) {
        this.balance = balance;
        this.playerName = playerName;
        playerNameLabel = new Label(playerName, labelStyle);
        balanceLabel = new Label(sgCurrencyFormat.format(balance), labelStyle);

        hudViewport = new ScreenViewport();
        stage = new Stage(hudViewport, batch);

        playerNameTable = new Table();
        playerNameTable.top().left().pad(5); // anchor the table to the top left of the stage with 5px padding above
        playerNameTable.setFillParent(true); // set hudtable to fill the size of the stage

        playerNameTable.add(playerNameTextLabel).padRight(10);
        playerNameTable.add(playerNameLabel);


        balanceTable = new Table();
        balanceTable.top().right().pad(5); // anchor the table to the top right of the stage with 5px padding above
        balanceTable.setFillParent(true); // set hudtable to fill the size of the stage

        balanceTable.add(balanceTextLabel).padRight(10);
        balanceTable.add(balanceLabel);

        stage.addActor(playerNameTable);
        stage.addActor(balanceTable);
    }

    public void update(int balance) {
        this.balance = balance;
        balanceLabel.setText(sgCurrencyFormat.format(balance));
    }

    public void dispose() {
        stage.dispose();
    }
}
