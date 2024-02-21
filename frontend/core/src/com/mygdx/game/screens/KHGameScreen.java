package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.mygdx.game.Boot;
import com.mygdx.game.objects.Card;

public class KHGameScreen extends ScreenAdapter {
    final Boot game;
    private Texture backImage;
    private Texture frontImage;
    private SpriteBatch batch;
    private Stage stage;
    private int cWidth;
    private int cHeight;
//    private int balance = 0;

    private BitmapFont font;

    public KHGameScreen(Boot game) {
        this.game = game;
    }

    public void dealVertCards(float delay, int xPos, int yPos, int offset, int rotation){
        Card newC = new Card(backImage);
        newC.setPosition(900, 600);
        stage.addActor(newC);
        SequenceAction sa = newC.cardAnimation(delay, xPos, yPos + offset, rotation, 0.5f, frontImage);
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

    @Override
    public void show() {
        // Instantiate the stage with gameViewport
        stage = new Stage();
        stage.setViewport(game.gameViewport);

//        Label balanceLabel = new Label(Integer.toString(balance));
        Table hud = new Table();
        hud.setFillParent(true);
//        hud.add()

        backImage = new Texture("back_card_150.png");
        frontImage = new Texture("2_of_clubs.png");

        cWidth = frontImage.getWidth() + 5;
        cHeight = frontImage.getHeight() + 5;

        Gdx.input.setInputProcessor(stage);

        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));

        final Card card1 = new Card(backImage);
        card1.setPosition(900, 600);
        final Card card2 = new Card(backImage);
        card2.setPosition(1000, 600);
        final Card card3 = new Card(backImage);
        card3.setPosition(1100, 600);

        stage.addActor(card1);
        stage.addActor(card2);
        stage.addActor(card3);

        // PLAYER 1

        //deal 6 cards, with starting 0 delay, coord of 1900,1100 offset by cdwidth*2 and rotated 90 degrees
//        for(int i=0; i<=5; i++){
//            dealVertCards(2.0f * i, 1900, 1000 - (cWidth*2), (cWidth * 1 / 5) * i, 90);
//        }

        // PLAYER 1 Labels & TextButton
//        createButtonLabel(skin, 1600, 1000 -cWidth, 90, 1);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0.3f, 0, 1);

        game.batch.setProjectionMatrix(stage.getCamera().combined);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
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
