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

public class Hud {
    public Stage stage;
    private final ScreenViewport hudViewport;
    private int balance;
    private static String playerName;

    Locale locale = new Locale.Builder().setLanguage("en").setRegion("SG").build();
    NumberFormat sgCurrencyFormat = NumberFormat.getCurrencyInstance(locale);

    private static final Label.LabelStyle labelStyle = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
    static Label playerNameTextLabel = new Label("Player Name: ", labelStyle);
    static Label balanceTextLabel = new Label("Balance: ", labelStyle);

    Label balanceLabel;
    Label playerNameLabel;

    public Hud (SpriteBatch batch, int balance, int bet, String playerName) {
        this.balance = balance;
        this.playerName = playerName;

        hudViewport = new ScreenViewport();
        stage = new Stage(hudViewport, batch);

        Table hudTable = new Table();
        hudTable.top().pad(5); // anchor the table to the top of the stage
        hudTable.setFillParent(true); // set hudtable to fill the size of the stage

        balanceLabel = new Label(sgCurrencyFormat.format(balance), labelStyle);
        playerNameLabel = new Label(playerName, labelStyle);

        hudTable.add(playerNameTextLabel).padRight(10);
        hudTable.add(playerNameLabel);
        hudTable.add(balanceLabel).expandX().right();

        stage.addActor(hudTable);
    }

    public void update(int balance) {
        this.balance = balance;
        balanceLabel.setText(sgCurrencyFormat.format(balance));
    }

    public void dispose() {
        stage.dispose();
    }
}
