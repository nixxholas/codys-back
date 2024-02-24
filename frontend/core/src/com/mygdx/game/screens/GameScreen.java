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
import com.badlogic.gdx.utils.Array;
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


    public void dealHorizCards(float delay, int xPos, int yPos, int offset, String imagePath){
        Card newC = new Card(backImage);
        newC.setPosition((game.getscreenWidth()-cWidth) / 2f , game.getscreenHeight()/1.2f);
        stage.addActor(newC);
        frontImage = new Texture(imagePath);
        SequenceAction sa = newC.cardAnimation(delay, xPos +offset
                ,yPos, 0.5f, frontImage);
        newC.addAction(sa);
    }

    public void createButtonLabel(Skin skin, int posX , int posY , int playerNum){
        //Button
        Table buttonContainer = new Table(skin);
        buttonContainer.setTransform(true);
        TextButton tb = new TextButton("Hit", skin);
        tb.setDisabled(true);

        //Label
        String lbString = "Player "+ playerNum;
        Label lb = new Label(lbString, skin);
        lb.setFontScale(1);

        //Order
        buttonContainer.add(lb);
        buttonContainer.row().pad(10);
        buttonContainer.add(tb).size(100, 50);
        buttonContainer.setOrigin(50, 25);
        buttonContainer.setPosition(posX, posY);
        stage.addActor(buttonContainer);
    }

    @Override
    public void show() {
        backImage = new Texture("back_card_150.png");
        frontImage = new Texture("TWO_CLUBS.png");

        cWidth = frontImage.getWidth();
        cHeight = frontImage.getHeight();

        stage = new Stage();
        stage.setViewport(game.gameViewport);

        Gdx.input.setInputProcessor(stage);

        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Create dealer's card
        // cards are dealt from here
        final Card cardBACK = new Card(backImage);
        cardBACK.setPosition((game.getscreenWidth()-cWidth) / 2f , game.getscreenHeight()/1.2f);
        stage.addActor(cardBACK);

        // Creating an arc and designating points by setting the arc parameters
        float centerX = game.getscreenWidth() / 2f; // The x coordinate of the arc's center
        float centerY = game.getscreenHeight() / 1.1f; // The y coordinate of the arc's center
        float radius = Math.min(game.getscreenWidth(), game.getscreenHeight()) / 1.4f; // The radius of the arc
        float startAngle = -30; // The start angle of the arc in degrees
        float sweepAngle = -120; // The sweep angle of the arc in degrees
        float numPlayers = 5; // The number of sprites to generate along the arc

        // create a shape renderer
        ShapeRenderer shapeRenderer = new ShapeRenderer ();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.CLEAR);
        shapeRenderer.arc(centerX, centerY, radius, startAngle, sweepAngle);
        shapeRenderer.end();


        // Situation: server passes client a JSON file.
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
        hand.add("QUEEN_DIAMONDS");
        hand.add("TWO_DIAMONDS");
        hand.add("THREE_SPADES");
        hand.add("FOUR_HEARTS");
        hand.add("KING_CLUBS");
        hand.add("ACE_DIAMONDS");
        List<String> hand2 = new ArrayList<String>();
        hand2.add("THREE_DIAMONDS");
        hand2.add("TEN_SPADES");
        hand2.add("ACE_HEARTS");
        hand2.add("QUEEN_CLUBS");
        hand2.add("ACE_DIAMONDS");
        List<String> hand3 = new ArrayList<String>();
        List<String> hand4 = new ArrayList<String>();
        List<String> hand5 = new ArrayList<String>();
        List<List<String>> playerHands = new ArrayList<>(5);
        playerHands.add(hand);
        playerHands.add(hand2);
        playerHands.add(hand2);
        playerHands.add(hand2);
        playerHands.add(hand2);

        //looping through i players
        for (int i = 0; i < numPlayers; i++) {
            // Calculate the angle and position of each player
            float angle = MathUtils.lerpAngleDeg(startAngle, startAngle + sweepAngle,
                    i / (float) (numPlayers - 1));
            // Use cosine and sine to calculate diagonal offset from center of circle
            float x = centerX + MathUtils.cosDeg(angle) * radius;
            float y = centerY + MathUtils.sinDeg(angle) * radius;
            createButtonLabel(skin, (int) x , (int) y + cHeight + 40, i+1);
            //get hand of current player
            List<String> currentHand = playerHands.get(i);
            for(int z = 0; z < currentHand.size(); z++){
                // looping through each i player's z position card
                String cardImagePath = (playerHands.get(i)).get(z) + ".png";
                if (z<=4){
                    dealHorizCards(2.0f * (z+1) + 12.0f * i, (int)x-cWidth, (int)y,
                            (cWidth / 5) * (z+1), cardImagePath);
                }else{
                    dealHorizCards(2.0f * (z+1) + 12.0f * i, (int)x- 160,
                            (int)y -(cHeight / 4), (cWidth / 5) * (z-4), cardImagePath);
                }
            }
        }

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
        hud.dispose();
    }
}
