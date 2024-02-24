package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.Boot;
import com.mygdx.game.objects.Card;

import java.util.ArrayList;
import java.util.List;

public class GameScreen extends ScreenAdapter {
    final Boot game;
    private final Hud hud;
    private Texture backImage;
    private Texture frontImage;
    private SpriteBatch batch;
    private Stage stage;
    private int cWidth;
    private int cHeight;


    public GameScreen(Boot game) {
        this.game = game;
        hud = new Hud(game.batch, 65000, 10, game.getPlayerName());
    }

    public void dealHorizCards(float delay, int xPos, int yPos, int offset, int rotation){
        Card newC = new Card(backImage);
        newC.setPosition(900, 800);
        stage.addActor(newC);
        SequenceAction sa = newC.cardAnimation(delay, (int) (xPos + MathUtils.cosDeg(rotation)* offset)
                , (int) (yPos+ MathUtils.sinDeg(rotation)*offset), rotation, 0.5f, frontImage);
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
        rotatingActor.setOrigin(100, 50);
        rotatingActor.setRotation(rot);
        rotatingActor.setPosition(posX, posY);
        stage.addActor(rotatingActor);
    }

    public void createPlayerSet(){
        dealHorizCards(cHeight, cHeight, cHeight, cWidth, cHeight);
        //createButtonLabel();
    }

    @Override
    public void show() {
        backImage = new Texture("back_card_150.png");
        frontImage = new Texture("2_Clubs.png");

        cWidth = frontImage.getWidth() + 5;
        cHeight = frontImage.getHeight() + 5;

        stage = new Stage();
        stage.setViewport(game.gameViewport);

        Gdx.input.setInputProcessor(stage);

        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));

        final Card cardBACK = new Card(backImage);
        cardBACK.setPosition(900, 800);
        stage.addActor(cardBACK);

        // Creating an arc and designating points by setting the arc parameters
        float centerX = game.getscreenWidth() / 2.4f; // The x coordinate of the arc's center
        float centerY = game.getscreenHeight() / 1.1f; // The y coordinate of the arc's center
        float radius = Math.min(game.getscreenWidth(), game.getscreenHeight()) / 1.5f; // The radius of the arc
        float startAngle = -30; // The start angle of the arc in degrees
        float sweepAngle = 240; // The sweep angle of the arc in degrees
        float numPlayers = 5; // The number of sprites to generate along the arc

        // create a shape renderer
        ShapeRenderer shapeRenderer = new ShapeRenderer ();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.CLEAR);
        shapeRenderer.arc(centerX, centerY, radius, startAngle, sweepAngle);
        shapeRenderer.end();

        for (int i = 0; i < numPlayers; i++) {
            // Calculate the angle and position of the sprite
            float angle = MathUtils.lerpAngleDeg(startAngle, startAngle + sweepAngle, i / (float) (numPlayers - 1));
            // Use cosine and sine to calculate diagonal offset from center of circle
            float x = centerX + MathUtils.cosDeg(angle) * radius;
            float y = centerY + MathUtils.sinDeg(angle) * radius;
            createButtonLabel(skin, (int) x + cWidth + 10, (int) y + cHeight + 40, 0, i+1);
            for(int z = 1; z <= 5 + 1; z++){
                dealHorizCards(2.0f * z + 12.0f * i, (int)x, (int)y, (cWidth / 5) * z, 0);
            }
        }

        // Situation, server passes client a JSON file.
        // Client parses it into a list
        // Add code for that here
        // *here*
        // given the following list file of the cards in a player's hand,
        // deal out cards such that the png used is the actual card value and suit
        List<String> hand = new ArrayList<String>();
        hand.add("TWO_DIAMONDS");
        hand.add("THREE_SPADES");
        hand.add("FOUR_HEARTS");
        hand.add("KING_CLUBS");
        hand.add("ACE_DIAMONDS");
        List<String> hand2 = new ArrayList<String>();
        hand.add("TWO_DIAMONDS");
        hand.add("THREE_SPADES");
        hand.add("FOUR_HEARTS");
        hand.add("KING_CLUBS");
        hand.add("ACE_DIAMONDS");
        List<List<String>> playerHands = new ArrayList<>(5);
        playerHands.add(hand);
        playerHands.add(hand2);


    }
    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0.3f, 0, 1);
        game.batch.setProjectionMatrix(stage.getCamera().combined);

        stage.act(delta);
        stage.draw();

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
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
