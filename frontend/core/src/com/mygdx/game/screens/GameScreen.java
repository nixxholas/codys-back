package com.mygdx.game.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.Boot;
import com.mygdx.game.objects.*;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;

public class GameScreen extends ScreenAdapter {
    final Boot game;
    private Texture backImage;
    private Texture frontImage;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Stage stage;

    private int cWidth;
    private int cHeight;

    private BitmapFont font;

    public GameScreen(Boot game) {
        this.game = game;
        this.camera = game.camera;
    }

    @Override
    public void show() {
        backImage = new Texture("back_card_150.png");
        frontImage = new Texture("2_of_clubs.png");

        cWidth = frontImage.getWidth() + 5;
        cHeight = frontImage.getHeight() + 5;

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));

        final Card cardBACK = new Card(backImage);
        cardBACK.setPosition(900, 900);
        stage.addActor(cardBACK);

        // PLAYER 1

        Card card1 = new Card(backImage);
        card1.setPosition(900, 900);
        stage.addActor(card1);
        SequenceAction sa = card1.cardAnimation(0, 1900, 1100 - (cWidth * 2), 90, 0.5f, frontImage);
        card1.addAction(sa);

        Card card2 = new Card(backImage);
        card2.setPosition(900, 900);
        stage.addActor(card2);
        SequenceAction sa2 = card2.cardAnimation(2.0f, 1900, 1100 - (cWidth * 2) + (cWidth * 1 / 5), 90, 0.5f,
                frontImage);
        card2.addAction(sa2);

        Card card3 = new Card(backImage);
        card3.setPosition(900, 900);
        stage.addActor(card3);
        SequenceAction sa3 = card3.cardAnimation(4.0f, 1900, 1100 - (cWidth * 2) + (cWidth * 2 / 5), 90, 0.5f,
                frontImage);
        card3.addAction(sa3);

        Card card4 = new Card(backImage);
        card4.setPosition(900, 900);
        stage.addActor(card4);
        SequenceAction sa4 = card4.cardAnimation(6.0f, 1900, 1100 - (cWidth * 2) + (cWidth * 3 / 5), 90, 0.5f,
                frontImage);
        card4.addAction(sa4);

        Card card5 = new Card(backImage);
        card5.setPosition(900, 900);
        stage.addActor(card5);
        SequenceAction sa5 = card5.cardAnimation(8.0f, 1900, 1100 - (cWidth * 2) + (cWidth * 4 / 5), 90, 0.5f,
                frontImage);
        card5.addAction(sa5);

        Card card6 = new Card(backImage);
        card6.setPosition(900, 900);
        stage.addActor(card6);
        SequenceAction sa6 = card6.cardAnimation(10.0f, 1900, 1100 - (cWidth * 2) + (cWidth * 5 / 5), 90, 0.5f,
                frontImage);
        card6.addAction(sa6);

        // PLAYER 1 END

        // PLAYER 1 Labels & TextButton
        Table buttonContainer = new Table(skin);
        buttonContainer.setTransform(true);
        Label lb = new Label("Player 1", skin);
        lb.setFontScale(1);
        buttonContainer.add(lb);
        buttonContainer.row().pad(10);
        TextButton tb = new TextButton("Hit", skin);
        tb.setDisabled(true);
        buttonContainer.add(tb).size(100, 50);
        Table rotatingActor = buttonContainer;
        rotatingActor.setRotation(90);
        rotatingActor.setPosition(1600, 1100 - cWidth);
        stage.addActor(rotatingActor);

        // PLAYER 2
        Card card1P2 = new Card(backImage);
        card1P2.setPosition(900, 900);
        stage.addActor(card1P2);
        SequenceAction saP2 = card1P2.cardAnimation(12.0f, 1900, 1100 - (cWidth * 5), 90, 0.5f, frontImage);
        card1P2.addAction(saP2);

        Card card2P2 = new Card(backImage);
        card2P2.setPosition(900, 900);
        stage.addActor(card2P2);
        SequenceAction sa2P2 = card2P2.cardAnimation(14.0f, 1900, 1100 - (cWidth * 5) + (cWidth * 1 / 5), 90, 0.5f,
                frontImage);
        card2P2.addAction(sa2P2);

        Card card3P2 = new Card(backImage);
        card3P2.setPosition(900, 900);
        stage.addActor(card3P2);
        SequenceAction sa3P2 = card3P2.cardAnimation(16.0f, 1900, 1100 - (cWidth * 5) + (cWidth * 2 / 5), 90, 0.5f,
                frontImage);
        card3P2.addAction(sa3P2);

        Card card4P2 = new Card(backImage);
        card4P2.setPosition(900, 900);
        stage.addActor(card4P2);
        SequenceAction sa4P2 = card4P2.cardAnimation(18.0f, 1900, 1100 - (cWidth * 5) + (cWidth * 3 / 5), 90, 0.5f,
                frontImage);
        card4P2.addAction(sa4P2);

        Card card5P2 = new Card(backImage);
        card5P2.setPosition(900, 900);
        stage.addActor(card5P2);
        SequenceAction sa5P2 = card5P2.cardAnimation(20.0f, 1900, 1100 - (cWidth * 5) + (cWidth * 4 / 5), 90, 0.5f,
                frontImage);
        card5P2.addAction(sa5P2);

        Card card6P2 = new Card(backImage);
        card6P2.setPosition(900, 900);
        stage.addActor(card6P2);
        SequenceAction sa6P2 = card6P2.cardAnimation(22.0f, 1900, 1100 - (cWidth * 5) + (cWidth * 5 / 5), 90, 0.5f,
                frontImage);
        card6P2.addAction(sa6P2);

        // PLAYER 2 Labels & TextButton
        Table buttonContainer2 = new Table(skin);
        buttonContainer2.setTransform(true);
        Label lb2 = new Label("Player 2", skin);
        lb2.setFontScale(1);
        buttonContainer2.add(lb2);
        buttonContainer2.row().pad(10);
        TextButton tb2 = new TextButton("Hit", skin);
        tb2.setDisabled(true);
        buttonContainer2.add(tb2).size(100, 50);
        Table rotatingActor2 = buttonContainer2;
        rotatingActor2.setRotation(90);
        rotatingActor2.setPosition(1600, 1100 - (cWidth * 4));
        stage.addActor(rotatingActor2);

        // PLAYER 3
        Card card1P3 = new Card(backImage);
        card1P3.setPosition(900, 900);
        stage.addActor(card1P3);
        SequenceAction saP3 = card1P3.cardAnimation(24.0f, 1100, 50, 0, 0.5f, frontImage);
        card1P3.addAction(saP3);

        Card card2P3 = new Card(backImage);
        card2P3.setPosition(900, 900);
        stage.addActor(card2P3);
        SequenceAction sa2P3 = card2P3.cardAnimation(26.0f, 1100 + (cWidth * 1 / 5), 50, 0, 0.5f, frontImage);
        card2P3.addAction(sa2P3);

        Card card3P3 = new Card(backImage);
        card3P3.setPosition(900, 900);
        stage.addActor(card3P3);
        SequenceAction sa3P3 = card3P3.cardAnimation(28.0f, 1100 + (cWidth * 2 / 5), 50, 0, 0.5f, frontImage);
        card3P3.addAction(sa3P3);

        Card card4P3 = new Card(backImage);
        card4P3.setPosition(900, 900);
        stage.addActor(card4P3);
        SequenceAction sa4P3 = card4P3.cardAnimation(30.0f, 1100 + (cWidth * 3 / 5), 50, 0, 0.5f, frontImage);
        card4P3.addAction(sa4P3);

        Card card5P3 = new Card(backImage);
        card5P3.setPosition(900, 900);
        stage.addActor(card5P3);
        SequenceAction sa5P3 = card5P3.cardAnimation(32.0f, 1100 + (cWidth * 4 / 5), 50, 0, 0.5f, frontImage);
        card5P3.addAction(sa5P3);

        Card card6P3 = new Card(backImage);
        card6P3.setPosition(900, 900);
        stage.addActor(card6P3);
        SequenceAction sa6P3 = card6P3.cardAnimation(34.0f, 1100 + (cWidth * 5 / 5), 50, 0, 0.5f, frontImage);
        card6P3.addAction(sa6P3);

        // PLAYER 3 Labels & TextButton
        Table buttonContainer3 = new Table(skin);
        buttonContainer3.setTransform(true);
        Label lb3 = new Label("Player 3", skin);
        lb3.setFontScale(1);
        buttonContainer3.add(lb3);
        buttonContainer3.row().pad(10);
        TextButton tb3 = new TextButton("Hit", skin);
        tb3.setDisabled(true);
        buttonContainer3.add(tb3).size(100, 50);
        Table rotatingActor3 = buttonContainer3;
        rotatingActor3.setRotation(0);
        rotatingActor3.setPosition(1250, 310);
        stage.addActor(rotatingActor3);

        // Player 4
        Card card1P4 = new Card(backImage);
        card1P4.setPosition(900, 900);
        stage.addActor(card1P4);
        SequenceAction saP4 = card1P4.cardAnimation(36.0f, 500, 50, 0, 0.5f, frontImage);
        card1P4.addAction(saP4);

        Card card2P4 = new Card(backImage);
        card2P4.setPosition(900, 900);
        stage.addActor(card2P4);
        SequenceAction sa2P4 = card2P4.cardAnimation(38.0f, 500 + (cWidth * 1 / 5), 50, 0, 0.5f, frontImage);
        card2P4.addAction(sa2P4);

        Card card3P4 = new Card(backImage);
        card3P4.setPosition(900, 900);
        stage.addActor(card3P4);
        SequenceAction sa3P4 = card3P4.cardAnimation(40.0f, 500 + (cWidth * 2 / 5), 50, 0, 0.5f, frontImage);
        card3P4.addAction(sa3P4);

        Card card4P4 = new Card(backImage);
        card4P4.setPosition(900, 900);
        stage.addActor(card4P4);
        SequenceAction sa4P4 = card4P4.cardAnimation(42.0f, 500 + (cWidth * 3 / 5), 50, 0, 0.5f, frontImage);
        card4P4.addAction(sa4P4);

        Card card5P4 = new Card(backImage);
        card5P4.setPosition(900, 900);
        stage.addActor(card5P4);
        SequenceAction sa5P4 = card5P4.cardAnimation(44.0f, 500 + (cWidth * 4 / 5), 50, 0, 0.5f, frontImage);
        card5P4.addAction(sa5P4);

        Card card6P4 = new Card(backImage);
        card6P4.setPosition(900, 900);
        stage.addActor(card6P4);
        SequenceAction sa6P4 = card6P4.cardAnimation(46.0f, 500 + (cWidth * 5 / 5), 50, 0, 0.5f, frontImage);
        card6P4.addAction(sa6P4);

        // PLAYER 4 Labels & TextButton
        Table buttonContainer4 = new Table(skin);
        buttonContainer4.setTransform(true);
        Label lb4 = new Label("Player 4", skin);
        lb4.setFontScale(1);
        buttonContainer4.add(lb4);
        buttonContainer4.row().pad(10);
        TextButton tb4 = new TextButton("Hit", skin);
        tb4.setDisabled(true);
        buttonContainer4.add(tb4).size(100, 50);
        Table rotatingActor4 = buttonContainer4;
        rotatingActor4.setRotation(0);
        rotatingActor4.setPosition(650, 310);
        stage.addActor(rotatingActor4);

        // Player 5
        Card card1P5 = new Card(backImage);
        card1P5.setPosition(900, 900);
        stage.addActor(card1P5);
        SequenceAction saP5 = card1P5.cardAnimation(48.0f, 50, 600, -90, 0.5f, frontImage);
        card1P5.addAction(saP5);

        Card card2P5 = new Card(backImage);
        card2P5.setPosition(900, 900);
        stage.addActor(card2P5);
        SequenceAction sa2P5 = card2P5.cardAnimation(50.0f, 50, 600 - (cWidth * 1 / 5), -90, 0.5f, frontImage);
        card2P5.addAction(sa2P5);

        Card card3P5 = new Card(backImage);
        card3P5.setPosition(900, 900);
        stage.addActor(card3P5);
        SequenceAction sa3P5 = card3P5.cardAnimation(52.0f, 50, 600 - (cWidth * 2 / 5), -90, 0.5f, frontImage);
        card3P5.addAction(sa3P5);

        Card card4P5 = new Card(backImage);
        card4P5.setPosition(900, 900);
        stage.addActor(card4P5);
        SequenceAction sa4P5 = card4P5.cardAnimation(54.0f, 50, 600 - (cWidth * 3 / 5), -90, 0.5f, frontImage);
        card4P5.addAction(sa4P5);

        Card card5P5 = new Card(backImage);
        card5P5.setPosition(900, 900);
        stage.addActor(card5P5);
        SequenceAction sa5P5 = card5P5.cardAnimation(56.0f, 50, 600 - (cWidth * 4 / 5), -90, 0.5f, frontImage);
        card5P5.addAction(sa5P5);

        Card card6P5 = new Card(backImage);
        card6P5.setPosition(900, 900);
        stage.addActor(card6P5);
        SequenceAction sa6P5 = card6P5.cardAnimation(58.0f, 50, 600 - (cWidth * 5 / 5), -90, 0.5f, frontImage);
        card6P5.addAction(sa6P5);

        // PLAYER 5 Labels & TextButton
        Table buttonContainer5 = new Table(skin);
        buttonContainer5.setTransform(true);
        Label lb5 = new Label("Player 5", skin);
        lb5.setFontScale(1);
        buttonContainer5.add(lb5);
        buttonContainer5.row().pad(10);
        TextButton tb5 = new TextButton("Hit", skin);
        tb5.setDisabled(true);
        buttonContainer5.add(tb5).size(100, 50);
        Table rotatingActor5 = buttonContainer5;
        rotatingActor5.setRotation(-90);
        rotatingActor5.setPosition(330, 1100 - (cWidth * 4));
        stage.addActor(rotatingActor5);

        // Player 6
        Card card1P6 = new Card(backImage);
        card1P6.setPosition(900, 900);
        stage.addActor(card1P6);
        SequenceAction saP6 = card1P6.cardAnimation(60.0f, 50, 1100, -90, 0.5f, frontImage);
        card1P6.addAction(saP6);

        Card card2P6 = new Card(backImage);
        card2P6.setPosition(900, 900);
        stage.addActor(card2P6);
        SequenceAction sa2P6 = card2P6.cardAnimation(62.0f, 50, 1100 - (cWidth * 1 / 5), -90, 0.5f, frontImage);
        card2P6.addAction(sa2P6);

        Card card3P6 = new Card(backImage);
        card3P6.setPosition(900, 900);
        stage.addActor(card3P6);
        SequenceAction sa3P6 = card3P6.cardAnimation(64.0f, 50, 1100 - (cWidth * 2 / 5), -90, 0.5f, frontImage);
        card3P6.addAction(sa3P6);

        Card card4P6 = new Card(backImage);
        card4P6.setPosition(900, 900);
        stage.addActor(card4P6);
        SequenceAction sa4P6 = card4P6.cardAnimation(66.0f, 50, 1100 - (cWidth * 3 / 5), -90, 0.5f, frontImage);
        card4P6.addAction(sa4P6);

        Card card5P6 = new Card(backImage);
        card5P6.setPosition(900, 900);
        stage.addActor(card5P6);
        SequenceAction sa5P6 = card5P6.cardAnimation(68.0f, 50, 1100 - (cWidth * 4 / 5), -90, 0.5f, frontImage);
        card5P6.addAction(sa5P6);

        Card card6P6 = new Card(backImage);
        card6P6.setPosition(900, 900);
        stage.addActor(card6P6);
        SequenceAction sa6P6 = card6P6.cardAnimation(70.0f, 50, 1100 - (cWidth * 5 / 5), -90, 0.5f, frontImage);
        card6P6.addAction(sa6P6);

        // PLAYER 6 Labels & TextButton
        Table buttonContainer6 = new Table(skin);
        buttonContainer6.setTransform(true);
        Label lb6 = new Label("Player 6", skin);
        lb6.setFontScale(1);
        buttonContainer6.add(lb6);
        buttonContainer6.row().pad(10);
        TextButton tb6 = new TextButton("Hit", skin);
        tb6.setDisabled(true);
        buttonContainer6.add(tb6).size(100, 50);
        Table rotatingActor6 = buttonContainer6;
        rotatingActor6.setRotation(-90);
        rotatingActor6.setPosition(330, 1100 - cWidth);
        stage.addActor(rotatingActor6);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0.3f, 0, 1);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        batch.dispose();
        backImage.dispose();
        frontImage.dispose();
        stage.dispose();
    }

}
