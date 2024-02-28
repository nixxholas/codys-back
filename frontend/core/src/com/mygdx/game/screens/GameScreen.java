package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.Boot;
import com.mygdx.game.objects.Card;
import com.mygdx.game.entities.Player;

import java.util.ArrayList;
import java.util.List;

public class GameScreen extends ScreenAdapter {
    final Boot game;
    private Skin skin;
    private Hud hud;
    private Texture backImage;
    private Texture frontImage;
    private SpriteBatch batch;
    private Stage stage;
    private int cWidth;
    private int cHeight;
    private int scrWidth;
    private int scrHeight;

    public GameScreen(Boot game) {
        this.game = game;
        this.skin = game.skin;
    }

    @Override
    public void show() {

        backImage = new Texture("back_card_150.png");
        frontImage = new Texture("TWO_CLUBS.png");

        cWidth = frontImage.getWidth();
        cHeight = frontImage.getHeight();

        scrWidth = game.getscreenWidth();
        scrHeight = game.getscreenHeight();

        stage = new Stage();
        stage.setViewport(game.gameViewport);
        hud = new Hud(game.batch, 65000, game.getPlayerName(), skin);

        Gdx.input.setInputProcessor(stage);

        // Create dealer's card
        // cards are dealt from here
        final Card cardBACK = new Card(backImage);
        cardBACK.setPosition((scrWidth-cWidth) / 2f , scrHeight/1.2f);
        stage.addActor(cardBACK);

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

        int numPlayers = 5;
        //looping through i players
        for (int currentPlayer = 0; currentPlayer < numPlayers; currentPlayer++) {
            // Use cosine and sine to calculate diagonal offset from center of circle
            int x = (int)Player.calcXPos(currentPlayer, numPlayers, game.getscreenWidth(), game.getscreenHeight());
            int y = (int)Player.calcYPos(currentPlayer, numPlayers, game.getscreenWidth(), game.getscreenHeight());
            stage.addActor(Player.createButtonLabel(skin, x ,  y + cHeight + 40, currentPlayer+1));
            //get hand of current player
            List<String> currentHand = playerHands.get(currentPlayer);
            for(int currentCard = 0; currentCard < currentHand.size(); currentCard++){
                // looping through each i player's z position card
                String cardImagePath = (playerHands.get(currentPlayer)).get(currentCard) + ".png";
                stage.addActor(Card.dealHorizCards(currentCard, scrWidth, scrHeight, x, y, cardImagePath));
            }
        }
    }
    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0.3f, 0, 1);
        game.batch.setProjectionMatrix(stage.getCamera().combined);

        stage.act(delta);
        stage.draw();

        /*
        *   1. Render the HUD details
        *   2. TODO update the balance on the HUD as it changes
        * */
        hud.hudStage.draw();
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
