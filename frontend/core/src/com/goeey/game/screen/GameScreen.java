package com.goeey.game.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
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
import com.goeey.game.utils.ProcessServerMessage;
import com.gooey.base.Card;
import com.gooey.base.EntityTarget;
import com.gooey.base.socket.ServerEvent;
import com.google.gson.*;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.enums.ReadyState;
import java.io.InvalidObjectException;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

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

    private Label gameStateLabel;
    private ArrayList<Label> actionsLabelList = new ArrayList<>();
    private Table actionsTable;
    private boolean gameRunning = false;

    private TextButton hitButton;

    private TextButton standButton;

    private TextButton betButton;

    private TextButton doubleDownButton;

    private Label lblAmt;

    private int playerAmt = 1000;

    private String playerMessage = null;

    public Table createButtonsNLabels(Skin skin, int posX, int posY, String entity, boolean isCurrentPlayer) {
        //Create table
        Table buttonContainer = new Table(skin);
        buttonContainer.setTransform(true);

        //Player Name
        Label lblName = new Label(entity, skin);
        lblName.setFontScale(1);

        if(isCurrentPlayer){
            lblName.setColor(Color.YELLOW);
            //Player Amount
            lblAmt = new Label("Amount: $1000", skin);
            lblAmt.setFontScale(1);
            lblAmt.setColor(Color.YELLOW);

            //Hit Button
            hitButton = new TextButton("Hit", skin);
            hitButton.addListener(new ClickListener() {
                public void clicked(InputEvent event, float x, float y){
                    if(!hitButton.isDisabled()){
                        GameManager.socketHandler.hit(game.getPlayerName(), 1);
                    }
                }
            });

            //Stand Button
            standButton = new TextButton("Stand", skin);
            standButton.addListener(new ClickListener() {
                public void clicked(InputEvent event, float x, float y){
                    if(!standButton.isDisabled()){
                        GameManager.socketHandler.stand(game.getPlayerName(), 1);
                        standButton.setDisabled(true);
                    }
                }
            });

            //Double Down Button
            doubleDownButton = new TextButton("Double Down", skin);
            doubleDownButton.addListener(new ClickListener() {
                public void clicked(InputEvent event, float x, float y){
                    if(!doubleDownButton.isDisabled()){
                        GameManager.socketHandler.doubleDown(game.getPlayerName(), 1.0);
                        doubleDownButton.setDisabled(true);
                    }
                }
            });

            //Bet Button
            betButton = new TextButton("Bet", skin);
            betButton.addListener(new ClickListener() {
                public void clicked(InputEvent event, float x, float y){
                    if(!betButton.isDisabled()){
                        GameManager.socketHandler.bet(game.getPlayerName(), 10.0);
                        betButton.setDisabled(true);
                    }
                }
            });

            hitButton.setDisabled(true);
            standButton.setDisabled(true);
            doubleDownButton.setDisabled(true);
            betButton.setDisabled(false);

            buttonContainer.add(lblName);
            buttonContainer.add(lblAmt).padLeft(5);
            buttonContainer.row().width(110).height(30);
            buttonContainer.add(hitButton).padTop(3).left();
            buttonContainer.add(standButton).padTop(3).right();
            buttonContainer.row().width(110).height(30);
            buttonContainer.add(doubleDownButton).padTop(3).left();
            buttonContainer.add(betButton).padTop(3).right();

        }else{
            buttonContainer.add(lblName);
        }

        //buttonContainer.setOrigin(50, 25);

        buttonContainer.setPosition(posX, posY);
        return buttonContainer;
    }

    public GameScreen(GameManager game) {
        this.game = game;
        this.skin = game.getSkin();
        ProcessServerMessage.setGS(this);
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
                // X position is fixed for all players
                // Y position differs for the current players to create room for UI elements
                // Username will be generated of Current player whereas
                // for other player it would be just PLAYER_(SEAT NUMBER)
                // Parameters skin, X coordinate, Y coordinate,
                stage.addActor(createButtonsNLabels(
                        skin,
                        xy.getPlayerX(),
                        mapElement.getKey() == game.getPlayerEntityType() ?
                                xy.getPlayerY() + cHeight + 50 : xy.getPlayerY() + cHeight + 20,
                        mapElement.getKey() == game.getPlayerEntityType() ?
                                game.getPlayerName() : name,
                        mapElement.getKey() == game.getPlayerEntityType())
                );
            }
        }
        // Display Game State
        createGameState();

    }

    public static Actor deal(EntityTarget entity, String card){
        PlayerXY xy = playerMap.get(entity);
        int x = xy.getPlayerX();
        int y = xy.getPlayerY();
        int count = xy.getCount();
        xy.setCount(count+1);
        return CardAnimation.dealCards(count, x, y, card);
    }

    public void updateUI(Card c, String eventType, boolean dealerReveal, int earnings){
        String cardName;
        int seatNum = eventType.charAt(eventType.length() - 1) - '0';

        switch (eventType) {
            case "DRAW_DEALER_0":
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
                    cardName =  (c == null) ? "BACK_CARD": c.getRank() + "_" + c.getSuit();
                    stage.addActor(deal(EntityTarget.DEALER, cardName));
                }
                break;
            case "DRAW_PLAYER_1":
                doubleDownButton.setDisabled(true);
                cardName = c.getRank() + "_" + c.getSuit();
                stage.addActor(deal(EntityTarget.PLAYER_1, cardName));
                break;
            case "DRAW_PLAYER_2":
                doubleDownButton.setDisabled(true);
                cardName = c.getRank() + "_" + c.getSuit();
                stage.addActor(deal(EntityTarget.PLAYER_2, cardName));
                break;
            case "DRAW_PLAYER_3":
                doubleDownButton.setDisabled(true);
                cardName = c.getRank() + "_" + c.getSuit();
                stage.addActor(deal(EntityTarget.PLAYER_3, cardName));
                break;
            case "DRAW_PLAYER_4":
                doubleDownButton.setDisabled(true);
                cardName = c.getRank() + "_" + c.getSuit();
                stage.addActor(deal(EntityTarget.PLAYER_4, cardName));
                break;
            case "DRAW_PLAYER_5":
                doubleDownButton.setDisabled(true);
                cardName = c.getRank() + "_" + c.getSuit();
                stage.addActor(deal(EntityTarget.PLAYER_5, cardName));
                break;
            case "PLAYER_TURN_PLAYER_1", "PLAYER_TURN_PLAYER_2", "PLAYER_TURN_PLAYER_3",
                    "PLAYER_TURN_PLAYER_4", "PLAYER_TURN_PLAYER_5":
                if(seatNum == game.getPlayerSeatNum()){
                    enableButtons();
                    Gdx.app.postRunnable(() -> this.updateGameState("Your turn"));
                }else{
                    Gdx.app.postRunnable(() -> this.updateGameState("Player " + seatNum + " turn"));
                }
                break;
            case "PLAYER_STAND_PLAYER_1", "PLAYER_STAND_PLAYER_2", "PLAYER_STAND_PLAYER_3",
                    "PLAYER_STAND_PLAYER_4", "PLAYER_STAND_PLAYER_5", "PLAYER_BUST_PLAYER_1",
                    "PLAYER_BUST_PLAYER_2", "PLAYER_BUST_PLAYER_3", "PLAYER_BUST_PLAYER_4",
                    "PLAYER_BUST_PLAYER_5":
                if(seatNum == game.getPlayerSeatNum())
                    disableButtons();
                this.updateGameState("Turn over");
                break;
            case "PLAYER_LOSE_PLAYER_1", "PLAYER_LOSE_PLAYER_2", "PLAYER_LOSE_PLAYER_3",
                    "PLAYER_LOSE_PLAYER_4", "PLAYER_LOSE_PLAYER_5":
                if(seatNum == game.getPlayerSeatNum()){
                    this.playerAmt -= earnings;
                    this.playerMessage = "You lost $" + earnings;
                }
                break;
            case "PLAYER_WIN_PLAYER_1", "PLAYER_WIN_PLAYER_2", "PLAYER_WIN_PLAYER_3",
                    "PLAYER_WIN_PLAYER_4", "PLAYER_WIN_PLAYER_5":
                if(seatNum == game.getPlayerSeatNum()){
                    this.playerAmt += earnings;
                    this.playerMessage = "You won $" + earnings;
                }
                break;
            case "PLAYER_PUSH_PLAYER_1", "PLAYER_PUSH_PLAYER_2", "PLAYER_PUSH_PLAYER_3",
                    "PLAYER_PUSH_PLAYER_4", "PLAYER_PUSH_PLAYER_5":
                if(seatNum == game.getPlayerSeatNum()){
                    this.playerMessage = "It is a push";
                }
                break;
            case "COUNTDOWN":
                break;
            case "UPDATE":
                this.updateGameState(playerMessage);
                this.lblAmt.setText("Amount: $" + this.playerAmt);
                this.disableButtons();
                betButton.setDisabled(true);
                break;
            default:
                // Handle the default case if needed
                break;
        }
    }

    private void enableButtons(){
        //betButton.setDisabled(false);
        hitButton.setDisabled(false);
        standButton.setDisabled(false);
        doubleDownButton.setDisabled(false);
    }

    private void disableButtons(){
        //betButton.setDisabled(true);
        hitButton.setDisabled(true);
        standButton.setDisabled(true);
        doubleDownButton.setDisabled(true);
    }

    public void createGameState(){
        // Create label for the message
        gameStateLabel = new Label("Place bet to start game", skin);
        Table table = new Table(skin);

        table.setBackground("default-pane");
        // Add padding around the text for the box effect
        table.add(gameStateLabel).pad(10);

        // Increase font size
        gameStateLabel.setFontScale(1.5f);

        // Position the table at the bottom of the screen
        table.top().right();
        table.setPosition(scrWidth -table.getPrefWidth(), scrHeight - table.getPrefHeight());

        // Add the table to the stage for rendering
        stage.addActor(table);

    }

    public void updateGameState(String message) {
        gameStateLabel.setText(message);
    }
    public void displayActions(String text) {
        float slideDistance = 20;
        Label label = new Label(text, skin);
        actionsLabelList.add(label);

        // Create or initialize table if it's null
        if (actionsTable == null) {
            actionsTable = new Table(skin);
            actionsTable.setBackground("default-pane");
            actionsTable.setPosition((float) (scrWidth / 2.25), (float) scrHeight / 7);
            actionsTable.setWidth(stage.getWidth());
            stage.addActor(actionsTable);
        }

        // Add the new label to the table
        actionsTable.add(label).pad(10).row();

        // Limit the number of labels to 3
        if (actionsLabelList.size() > 3) {
            // Remove the oldest label from both table and labelList
            Label oldestLabel = actionsLabelList.remove(0);
            oldestLabel.remove();
            // Shift the remaining labels up
            actionsTable.getCell(actionsTable.getChildren().first()).setActor(label);
        }

        // Apply fade out and slide up animation to the new label
        label.addAction(Actions.sequence(
                Actions.alpha(0),
                Actions.delay(1),
                Actions.parallel(
                        Actions.fadeIn(1),
                        Actions.moveBy(0, slideDistance, 1)
                ),
                Actions.delay(2),
                Actions.fadeOut(1),
                Actions.run(() -> {
                    actionsLabelList.remove(label);
                    label.remove();
                })
        ));
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
