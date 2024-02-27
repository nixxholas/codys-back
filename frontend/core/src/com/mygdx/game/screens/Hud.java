package com.mygdx.game.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
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
public class Hud {
    public Stage stage;     /* Stage is used to contain and organize the layout of playerNameTable and balanceTable below. */

    // HUD uses ScreenViewport to maintain its display size regardless of window size
    private final ScreenViewport hudViewport;
    private int balance;

    /*
    *   Set locale to Singapore and language to English.
    *   Prepare a number formatter to format balance to SG currency format.
    *   e.g. sgCurrencyFormat(2000) will return a String "$2,000.00"
    *
    * */
    Locale locale = new Locale.Builder().setLanguage("en").setRegion("SG").build();
    NumberFormat sgCurrencyFormat = NumberFormat.getCurrencyInstance(locale);

    // Labels to hold LITERAL texts
    private static final Label.LabelStyle labelStyle = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
    private static final Label playerNameTextLabel = new Label("Player Name: ", labelStyle);
    private static final Label balanceTextLabel = new Label("Balance: ", labelStyle);

    // Tables to align the labels
    private final Table playerNameTable;
    private final Table balanceTable;

    // Labels to hold the values for display
    Label balanceLabel;
    Label playerNameLabel;

    public Hud (SpriteBatch batch, int balance, String playerName, Skin skin) {
        this.balance = balance;
        playerNameLabel = new Label(playerName, labelStyle);
        balanceLabel = new Label(sgCurrencyFormat.format(balance), labelStyle);

        hudViewport = new ScreenViewport();
        stage = new Stage(hudViewport, batch);

        playerNameTable = new Table();
        playerNameTable.setSkin(skin);
        playerNameTable.top().left().pad(5); // anchor the table to the top left of the stage with 5px padding above
        playerNameTable.setFillParent(true); // set hudtable to fill the size of the stage

        playerNameTable.add(playerNameTextLabel).padRight(10);
        playerNameTable.add(playerName);


        balanceTable = new Table();
        balanceTable.setSkin(skin);
        balanceTable.top().right().pad(5); // anchor the table to the top right of the stage with 5px padding above
        balanceTable.setFillParent(true); // set hudtable to fill the size of the stage

        balanceTable.add(balanceTextLabel).padRight(10);
        balanceTable.add(sgCurrencyFormat.format(balance));

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
