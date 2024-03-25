package com.goeey.game.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.goeey.game.GameManager;
import com.goeey.game.animation.CardAnimation;
import com.goeey.game.socket.SocketHandler;
import com.goeey.game.socket.WebSocket;
import com.goeey.game.utils.PlayerXY;
import com.goeey.game.utils.PlayerUtils;
import com.gooey.base.Card;
import com.gooey.base.EntityTarget;
import com.gooey.base.socket.ServerEvent;
import com.google.gson.*;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.enums.ReadyState;
import java.io.InvalidObjectException;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Map;

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
    private static Map<EntityTarget, PlayerXY> playerMap = new HashMap<>();

    private SocketHandler socketHandler;

    private Boolean gameStarted = false;


    public Table createButtonsNLabels(Skin skin, int posX, int posY, String entity, boolean isCurrentPlayer) {
        //Create table
        Table buttonContainer = new Table(skin);
        buttonContainer.setTransform(true);

        //Player Name
        Label lbName = new Label(entity, skin);
        lbName.setFontScale(1);

        if(isCurrentPlayer){
            lbName.setColor(Color.YELLOW);
            //Player Amount
            Label lblAmt = new Label("Amount: $1000" , skin);
            lblAmt.setFontScale(1);
            lblAmt.setColor(Color.YELLOW);

            //Hit Button
            TextButton hitButton = new TextButton("Hit", skin);
            hitButton.addListener(new ClickListener() {
                public void clicked(InputEvent event, float x, float y){
                    if(!hitButton.isDisabled()){
                        System.out.println("Clicked Hit!!");
                    }
                }
            });

            //Stand Button
            TextButton standButton = new TextButton("Stand", skin);
            standButton.addListener(new ClickListener() {
                public void clicked(InputEvent event, float x, float y){
                    if(!standButton.isDisabled()){
                        System.out.println("Clicked Stand!!");
                    }

                }
            });

            //Bet Button
            TextButton betButton = new TextButton("Bet", skin);
            betButton.addListener(new ClickListener() {
                public void clicked(InputEvent event, float x, float y){
                    if(!betButton.isDisabled()){
                        System.out.println("Clicked Bet!!");
                        GameManager.socketHandler.bet(game.getPlayerName(), 1.0);
                        betButton.setDisabled(true);
                    }
                }
            });

            hitButton.setDisabled(true);
            standButton.setDisabled(true);
            betButton.setDisabled(false);

            buttonContainer.add(lbName);
            buttonContainer.add(lblAmt).padLeft(5);
            buttonContainer.row().width(150).height(40);
            buttonContainer.add(hitButton).left().padTop(10);
            buttonContainer.add(standButton).center().padTop(10);
            buttonContainer.add(betButton).right().padTop(10);
        }else{
            buttonContainer.add(lbName).center();
        }

        //buttonContainer.setOrigin(50, 25);

        buttonContainer.setPosition(posX, posY);
        return buttonContainer;
    }

    public GameScreen(GameManager game) {
        this.game = game;
        this.skin = game.getSkin();
        GameManager.socketHandler.setGS(this);
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


        // generate clean hashmap of all Entity targets, X and Y coords and card count
        playerMap = PlayerXY.refreshMap();

        // iterate through all players
        for (Map.Entry<EntityTarget, PlayerXY> mapElement: playerMap.entrySet()) {
            if(!(mapElement.getKey()==EntityTarget.DEALER)){
                String name = "" + mapElement.getKey();
                // Adding some bonus marks to all the students
                PlayerXY xy = mapElement.getValue();

                //Check if value equals to seatNumber and set isCurrentPlayer to true
                //Create player name tag for each player position
                stage.addActor(createButtonsNLabels(
                        skin, xy.getPlayerX(),
                        xy.getPlayerY() + cHeight + 40,
                         mapElement.getKey() == game.getPlayerEntityType() ? game.getPlayerName() : name,
                        mapElement.getKey() == game.getPlayerEntityType()));
            }
        }

        // single line code to deal card to players
//        stage.addActor(deal(EntityTarget.DEALER, "TWO_DIAMONDS"));
//        stage.addActor(deal(EntityTarget.DEALER, "THREE_DIAMONDS"));
//        stage.addActor(deal(EntityTarget.DEALER, "KING_DIAMONDS"));
//        stage.addActor(deal(EntityTarget.DEALER, "QUEEN_DIAMONDS"));
//        stage.addActor(deal(EntityTarget.DEALER, "ACE_DIAMONDS"));
//        stage.addActor(deal(EntityTarget.DEALER, "THREE_DIAMONDS"));
//        stage.addActor(deal(EntityTarget.DEALER, "KING_DIAMONDS"));
//        stage.addActor(deal(EntityTarget.DEALER, "QUEEN_DIAMONDS"));
//        stage.addActor(deal(EntityTarget.PLAYER_1, "THREE_DIAMONDS"));
//        stage.addActor(deal(EntityTarget.PLAYER_1, "KING_DIAMONDS"));
//        stage.addActor(deal(EntityTarget.PLAYER_1, "QUEEN_DIAMONDS"));
//        stage.addActor(deal(EntityTarget.PLAYER_1, "ACE_DIAMONDS"));
//        stage.addActor(deal(EntityTarget.PLAYER_1, "THREE_DIAMONDS"));
//        stage.addActor(deal(EntityTarget.PLAYER_1, "KING_DIAMONDS"));
//        stage.addActor(deal(EntityTarget.PLAYER_1, "QUEEN_DIAMONDS"));
//        stage.addActor(deal(EntityTarget.PLAYER_2, "KING_DIAMONDS"));
//        stage.addActor(deal(EntityTarget.PLAYER_2, "QUEEN_DIAMONDS"));
//        stage.addActor(deal(EntityTarget.PLAYER_2, "THREE_DIAMONDS"));
//        stage.addActor(deal(EntityTarget.PLAYER_2, "KING_DIAMONDS"));
//        stage.addActor(deal(EntityTarget.PLAYER_2, "QUEEN_DIAMONDS"));
//        stage.addActor(deal(EntityTarget.PLAYER_3, "QUEEN_DIAMONDS"));
//        stage.addActor(deal(EntityTarget.PLAYER_4, "THREE_DIAMONDS"));
//        stage.addActor(deal(EntityTarget.PLAYER_5, "KING_DIAMONDS"));
//        stage.addActor(deal(EntityTarget.PLAYER_5, "QUEEN_DIAMONDS"));
    }

    public static Actor deal(EntityTarget entity, String card){
        PlayerXY xy = playerMap.get(entity);
        int x = xy.getPlayerX();
        int y = xy.getPlayerY();
        int count = xy.getCount();
        xy.setCount(count+1);
        return CardAnimation.dealCards(count, x, y, card);
    }

    public Actor parseServerMsg(ServerEvent<?> event) throws InvalidObjectException {
        Card card = null;
        if(event.getMessage() instanceof Card) {
            card = (Card) event.getMessage();
        }
        EntityTarget entity = event.getTarget();
        switch (event.getType()) {
            case PLAYER_DRAW:
                if (card != null) {
                    return deal(entity, card.getRank() + "_" + card.getSuit());
                }
            case DEALER_DRAW:
                if (card==null) {
                    return deal(entity, "BACK_CARD");
                } else {
                    return deal(entity, card.getRank() + "_" + card.getSuit());
                }
            default:
                throw new InvalidObjectException("Not Player/Dealer draw event");
        }
    }

    public void updateUI(Card c, String target, boolean dealerReveal){
        String cardName;
        switch (target) {
            case "DEALER":
                if (dealerReveal) {
                    cardName = c.getRank() + "_" + c.getSuit();
                    Texture cardBack = new Texture("cards/BACK_CARD.png");
                    Texture cardFront = new Texture("cards/" + cardName + ".png");
                    CardAnimation dReveal = new CardAnimation(cardBack);
                    dReveal.setTexture(cardFront);
                    int x = 853;
                    int y = 675;
                    dReveal.setPosition(x, y);
                    stage.addActor(dReveal);
                } else {
                    if (c != null) {
                        cardName = c.getRank() + "_" + c.getSuit();
                    } else {
                        cardName = "BACK_CARD";
                    }
                    stage.addActor(deal(EntityTarget.DEALER, cardName));
                }
                break;
            case "PLAYER_1":
                cardName = c.getRank() + "_" + c.getSuit();
                stage.addActor(deal(EntityTarget.PLAYER_1, cardName));
                break;
            case "PLAYER_2":
                cardName = c.getRank() + "_" + c.getSuit();
                stage.addActor(deal(EntityTarget.PLAYER_2, cardName));
                break;
            case "PLAYER_3":
                cardName = c.getRank() + "_" + c.getSuit();
                stage.addActor(deal(EntityTarget.PLAYER_3, cardName));
                break;
            case "PLAYER_4":
                cardName = c.getRank() + "_" + c.getSuit();
                stage.addActor(deal(EntityTarget.PLAYER_4, cardName));
                break;
            case "PLAYER_5":
                cardName = c.getRank() + "_" + c.getSuit();
                stage.addActor(deal(EntityTarget.PLAYER_5, cardName));
                break;
            default:
                // Handle the default case if needed
                break;
        }

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

    public void startGame(){
        GameManager.socketHandler.bet(game.getPlayerName(), 1.0);
    }

    private boolean isWebSocketOpen() {
        WebSocket ws = GameManager.socketHandler.getWebSocket();
        return ws != null && ws.isOpen() && ws.getReadyState() == ReadyState.OPEN;
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
