package com.goeey.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ScreenUtils;
import com.goeey.game.GameManager;
import com.goeey.game.animation.CardAnimation;
import com.goeey.game.utils.PlayerUtils;
import java.util.ArrayList;
import java.util.List;

public class GameScreen extends ScreenAdapter {
    final GameManager game;
    private Skin skin;
    //private Hud hud;
    private Texture backImage;
    private Texture frontImage;
    private SpriteBatch batch;
    private Stage stage;
    private int cWidth;
    private int cHeight;
    private int scrWidth = Gdx.graphics.getWidth();
    private int scrHeight= Gdx.graphics.getHeight();
    private int[][] playerArr;

    public GameScreen(GameManager game) {
        this.game = game;
        this.skin = game.getSkin();
    }

    @Override
    public void show() {

        backImage = new Texture("cards/BACK_CARD.png");
        frontImage = new Texture("cards/TWO_CLUBS.png");

        cWidth = frontImage.getWidth();
        cHeight = frontImage.getHeight();

        stage = new Stage();
        stage.setViewport(game.gameViewPort);
        //hud = new Hud(game.batch, 65000, game.getPlayerName(), skin);

        Gdx.input.setInputProcessor(stage);

        // Create dealer's card
        // cards are dealt from here
        final CardAnimation cardBACK = new CardAnimation(backImage);
        cardBACK.setPosition((scrWidth-cWidth) / 2f , scrHeight/1.2f);
        stage.addActor(cardBACK);

        // Situation: server passes client a JSON file.
        // Client parses it into a list
        // Add code for that here
        // *here*
        // given the following list file of the cards in a player's hand,
        // deal out cards such that the png used is the actual card value and suit
        
        int numPlayers = 5;
        playerArr = new int[5][3];
        // 2d array of all 5 player positional values and how many cards were dealt to each
        for (int currentPlayer = 0; currentPlayer < numPlayers; currentPlayer++) {
            // Use cosine and sine to calculate diagonal offset from center of circle
            int x = (int)PlayerUtils.calcXPos(currentPlayer, numPlayers);
            int y = (int)PlayerUtils.calcYPos(currentPlayer, numPlayers);
            playerArr[currentPlayer][0] = x;
            playerArr[currentPlayer][1] = y;
            playerArr[currentPlayer][2] = 0;
            //Create player name tag for each player position
            stage.addActor(PlayerUtils.createButtonLabel(skin, x ,  y + cHeight + 40, currentPlayer+1));
        }

        // single line code to deal card to players
        stage.addActor(deal(0, "TWO_DIAMONDS"));
        stage.addActor(deal(0, "THREE_DIAMONDS"));
        stage.addActor(deal(0, "KING_DIAMONDS"));
        stage.addActor(deal(0, "QUEEN_DIAMONDS"));
        stage.addActor(deal(0, "ACE_DIAMONDS"));
        stage.addActor(deal(0, "THREE_DIAMONDS"));
        stage.addActor(deal(1, "KING_DIAMONDS"));
        stage.addActor(deal(2, "QUEEN_DIAMONDS"));
        stage.addActor(deal(3, "ACE_DIAMONDS"));
        stage.addActor(deal(4,"THREE_DIAMONDS"));
        stage.addActor(deal(4,"THREE_CLUBS"));
        //looping through i players
        // for (int currentPlayer = 0; currentPlayer < numPlayers; currentPlayer++) {
        //     // Use cosine and sine to calculate diagonal offset from center of circle
        //     int x = playerPositionArr[currentPlayer][0];
        //     int y = playerPositionArr[currentPlayer][1];
        //     stage.addActor(PlayerUtils.createButtonLabel(skin, x ,  y + cHeight + 40, currentPlayer+1));
        //     //get hand of current player
        //     List<String> currentHand = playerHands.get(currentPlayer);
        //     for(int currentCard = 0; currentCard < currentHand.size(); currentCard++){
        //         // looping through each i player's z position card
        //         String cardImagePath = (playerHands.get(currentPlayer)).get(currentCard);
        //         stage.addActor(CardAnimation.dealCards(currentCard, x, y, cardImagePath));
        //     }
        // }

    }

    public Actor deal(int playerNum, String card){
        int x = playerArr[playerNum][0];
        int y = playerArr[playerNum][1];
        int count = playerArr[playerNum][2];
        playerArr[playerNum][2]++;
        return CardAnimation.dealCards(count, x, y, card);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0.3f, 0, 1);
        stage.getBatch().setProjectionMatrix(stage.getCamera().combined);

        stage.act(delta);
        stage.draw();

        /*
        *   1. Render the HUD details
        *   2. TODO update the balance on the HUD as it changes
        * */
        //hud.hudStage.draw();
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
        //hud.dispose();
    }
}
