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
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
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

    /*
    *   ExtendViewPort behaviour: The world is first scaled to fit within the viewport,
    *   then the shorter dimension is lengthened to fill the viewport.
    *
    *   It is used for GameScreen because we want to:
    *   1. Maintain the aspect ratio of everything (cards, players, ...)
    *   2. Allow larger screens to display larger cards, and smaller screens to display smaller cards
    *   3. Display the entire table at ALL TIMES (Do not cut off parts of the table when resizing)
    *
    *   Check out how different viewports work in libGDX here:
    *   https://raeleus.github.io/viewports-sample-project/
    */
    private final ExtendViewport gameViewport;
    private BitmapFont font;

    public GameScreen(Boot game) {
        this.game = game;
        this.camera = game.camera;
        /*
        *   gameViewport has a minimum width and height of 1920x1080 and no maximum
        */
        gameViewport = new ExtendViewport(1920, 1080);
    }

    public void dealVertCards(float delay, int xPos, int yPos, int offset, int rotation){
        Card newC = new Card(backImage);
        newC.setPosition(900, 900);
        stage.addActor(newC);
        SequenceAction sa = newC.cardAnimation(delay, xPos, yPos + offset, rotation, 0.5f, frontImage);
        newC.addAction(sa);
    }

    public void dealHorizCards(float delay, int xPos, int yPos, int offset, int rotation){
        Card newC = new Card(backImage);
        newC.setPosition(900, 900);
        stage.addActor(newC);
        SequenceAction sa = newC.cardAnimation(delay, xPos+ offset, yPos , rotation, 0.5f, frontImage);
        newC.addAction(sa);
    }

    public void createButtonLabel(Skin skin, int posX , int posY , int rot, int playerNum){
        //Button
        Table buttonContainer = new Table(skin);
        buttonContainer.setTransform(true);
        TextButton tb = new TextButton("Hit", skin);
        tb.setDisabled(true);
        Table rotatingActor = buttonContainer;

        //Label
        String lbString = "Player "+ playerNum;
        Label lb = new Label(lbString, skin);
        lb.setFontScale(1);

        //Order
        buttonContainer.add(lb);
        buttonContainer.row().pad(10);
        buttonContainer.add(tb).size(100, 50);

        rotatingActor.setRotation(rot);
        rotatingActor.setPosition(posX, posY);
        stage.addActor(rotatingActor);
    }

    public void createPlayerSet(){
        dealVertCards(cHeight, cHeight, cHeight, cWidth, cHeight);
        //createButtonLabel();

    }

    @Override
    public void show() {
        backImage = new Texture("back_card_150.png");
        frontImage = new Texture("2_of_clubs.png");

        cWidth = frontImage.getWidth() + 5;
        cHeight = frontImage.getHeight() + 5;

        stage = new Stage(gameViewport);
        Gdx.input.setInputProcessor(stage);

        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));

        final Card cardBACK = new Card(backImage);
        cardBACK.setPosition(900, 900);
        stage.addActor(cardBACK);

        // PLAYER 1

        //deal 6 cards, with starting 0 delay, coord of 1900,1100 offset by cdwidth*2 and rotated 90 degrees
        for(int i=0; i<=5; i++){
            dealVertCards(2.0f * i, 1900, 1000 - (cWidth*2), (cWidth * 1 / 5) * i, 90);
        }
        
        // Card card1 = new Card(backImage);
        // card1.setPosition(900, 900);
        // stage.addActor(card1);
        // SequenceAction sa = card1.cardAnimation(0, 1900, 1100 - (cWidth * 2), 90, 0.5f, frontImage);
        // card1.addAction(sa);

        // Card card2 = new Card(backImage);
        // card2.setPosition(900, 900);
        // stage.addActor(card2);
        // SequenceAction sa2 = card2.cardAnimation(2.0f, 1900, 1100 - (cWidth * 2) + (cWidth * 1 / 5), 90, 0.5f,
        //         frontImage);
        // card2.addAction(sa2);

        // Card card3 = new Card(backImage);
        // card3.setPosition(900, 900);
        // stage.addActor(card3);
        // SequenceAction sa3 = card3.cardAnimation(4.0f, 1900, 1100 - (cWidth * 2) + (cWidth * 2 / 5), 90, 0.5f,
        //         frontImage);
        // card3.addAction(sa3);

        // Card card4 = new Card(backImage);
        // card4.setPosition(900, 900);
        // stage.addActor(card4);
        // SequenceAction sa4 = card4.cardAnimation(6.0f, 1900, 1100 - (cWidth * 2) + (cWidth * 3 / 5), 90, 0.5f,
        //         frontImage);
        // card4.addAction(sa4);

        // Card card5 = new Card(backImage);
        // card5.setPosition(900, 900);
        // stage.addActor(card5);
        // SequenceAction sa5 = card5.cardAnimation(8.0f, 1900, 1100 - (cWidth * 2) + (cWidth * 4 / 5), 90, 0.5f,
        //         frontImage);
        // card5.addAction(sa5);

        // Card card6 = new Card(backImage);
        // card6.setPosition(900, 900);
        // stage.addActor(card6);
        // SequenceAction sa6 = card6.cardAnimation(10.0f, 1900, 1100 - (cWidth * 2) + (cWidth * 5 / 5), 90, 0.5f,
        //         frontImage);
        // card6.addAction(sa6);

        // PLAYER 1 END

        // PLAYER 1 Labels & TextButton

        createButtonLabel(skin, 1600, 1000 -cWidth, 90, 1);

        /*Table buttonContainer = new Table(skin);
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
        rotatingActor.setPosition(1600, 1000 - cWidth);
        stage.addActor(rotatingActor);*/

        // PLAYER 2
        //deal 6 cards, with starting 12.0f delay, coord of 1900,1100 offset by cdwidth*1/5 and rotated 90 degrees
        for(int i=0; i<=5; i++){
            dealVertCards(12.0f + 2.0f * i, 1900, 1100 - (cWidth*5), (cWidth * 1 / 5) * i, 90);
        }
        // PLAYER 2 Labels & TextButton
        createButtonLabel(skin, 1600, 1100 -cWidth*4, 90, 2);


        // PLAYER 3
        //deal 6 cards, with starting 24.0f delay, coord of 1100,50 offset horizontally by cdwidth*1/5 and rotated 0 degrees
        for(int i=0; i<=5; i++){
            dealHorizCards(24.0f + 2.0f * i, 1100, 50, (cWidth * 1 / 5) * i, 0);
        }
        // PLAYER 3 Labels & TextButton
        createButtonLabel(skin, 1250, 310, 0, 3);


        // Player 4
        //deal 6 cards, with starting 36.0f delay, coord of 500,50 offset horizontally by cdwidth*1/5 and rotated 0 degrees
        for(int i=0; i<=5; i++){
            dealHorizCards(36.0f + 2.0f * i, 500, 50, (cWidth * 1 / 5) * i, 0);
        }
        // PLAYER 4 Labels & TextButton
        createButtonLabel(skin, 650, 310, 0, 4);


        // Player 5
        //deal 6 cards, with starting 48.0f delay, coord of 50,600 offset by -cdwidth*1/5 and rotated -90 degrees
        for(int i=0; i<=5; i++){
            dealVertCards(48.0f + 2.0f * i, 50, 600, -(cWidth * 1 / 5) * i, -90);
        }
        // PLAYER 5 Labels & TextButton
        createButtonLabel(skin, 330, 1100 -cWidth*4, -90, 5);


        // Player 6
        //deal 6 cards, with starting 48.0f delay, coord of 50,600 offset by -cdwidth*1/5 and rotated -90 degrees
        for(int i=0; i<=5; i++){
            dealVertCards(60.0f + 2.0f * i, 50, 1000, -(cWidth * 1 / 5) * i, -90);
        }
        // PLAYER 6 Labels & TextButton
        createButtonLabel(skin, 330, 1000 -cWidth, -90, 6);

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
