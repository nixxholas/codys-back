package com.goeey.game.screen;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.goeey.game.GameManager;
import com.goeey.game.animation.CardAnimation;
import com.goeey.game.utils.PlayerXY;
import com.goeey.game.utils.ProcessServerMessage;
import com.gooey.base.Card;
import com.gooey.base.EntityTarget;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class GameScreen extends ScreenAdapter implements ApplicationListener {
    final GameManager game;
    private Skin skin;
    //private Hud hud;
    private Texture backImage;
    private Texture frontImage;
    private SpriteBatch batch;
    private Stage stage;
    private int cWidth;
    private int cHeight;
    private static int scrWidth = 1920;
    private static int scrHeight= 1080;
    private static Map<EntityTarget, PlayerXY> playerMap = new HashMap<>();

    private Label gameStateLabel;

    private Label playerTurnLabel;
    private ArrayList<Label> actionsLabelList = new ArrayList<>();
    private Table actionsTable;
    private boolean gameRunning = false;

    private TextButton hitButton;

    private TextButton standButton;

    private TextButton betButton;

    private TextButton doubleDownButton;

    private Label lblAmt;

    private int playerAmt;

    private String playerMessage = null;

    private boolean playerLeft = false;

    private Timer timer = new Timer();

    private boolean gameEnded = false;

    private boolean gameRestCalled = false;

    private int countdownSeconds = 10;

    private boolean firstCountDown = true;

    private boolean hasBetted = false;

    public GameScreen(GameManager game) {
        this.game = game;
        this.skin = game.getSkin();
        ProcessServerMessage.setGS(this);
    }

    public GameScreen(GameManager game, int playerAmt) {
        this.game = game;
        this.skin = game.getSkin();
        this.playerAmt = playerAmt;
        ProcessServerMessage.setGS(this);
    }

    public boolean hasBet() {
        return hasBetted;
    }


    public boolean isFirstCountDown() {
        return firstCountDown;
    }

    public void setFirstCountDown(boolean firstCountDown) {
        this.firstCountDown = firstCountDown;
    }

    public void unseatPlayer(){
        System.out.println("Unseat Player");
        betButton.setDisabled(true);
        GameManager.playerSeated = false;
        disableButtons();
        try {
            //Remove player from seat so that card will not be dealt to him
            GameManager.socketHandler.resetLatch(1);
            GameManager.socketHandler.leaveseat(game.getPlayerName());
            GameManager.socketHandler.awaitPlayer();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void seatPlayer(){
        System.out.println("Seat Player");
        try {
            //Remove player from seat so that card will not be dealt to him
            GameManager.socketHandler.resetLatch(1);
            GameManager.socketHandler.sit(game.getPlayerName(), game.getPlayerSeatNum());
            GameManager.socketHandler.awaitPlayer();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

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
            lblAmt = new Label("Amount: $" + playerAmt, skin);
            lblAmt.setFontScale(1);
            lblAmt.setColor(Color.YELLOW);

            //Hit Button
            hitButton = new TextButton("Hit", skin);
            hitButton.addListener(new ClickListener() {
                public void clicked(InputEvent event, float x, float y){
                    if(!hitButton.isDisabled()){
                        try {
                            GameManager.socketHandler.resetLatch(1);
                            GameManager.socketHandler.hit(game.getPlayerName());
                            GameManager.socketHandler.awaitPlayer();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            //Stand Button
            standButton = new TextButton("Stand", skin);
            standButton.addListener(new ClickListener() {
                public void clicked(InputEvent event, float x, float y){
                    if(!standButton.isDisabled()){
                        standButton.setDisabled(true);
                        try {
                            GameManager.socketHandler.resetLatch(1);
                            GameManager.socketHandler.stand(game.getPlayerName());
                            GameManager.socketHandler.awaitPlayer();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            //Double Down Button
            doubleDownButton = new TextButton("Double Down", skin);
            doubleDownButton.addListener(new ClickListener() {
                public void clicked(InputEvent event, float x, float y){
                    if(!doubleDownButton.isDisabled()){
                        doubleDownButton.setDisabled(true);
                        try {
                            GameManager.socketHandler.resetLatch(1);
                            GameManager.socketHandler.doubleDown(game.getPlayerName(), 1.0);
                            GameManager.socketHandler.awaitPlayer();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            //Bet Button
            betButton = new TextButton("Bet", skin);
            betButton.addListener(new ClickListener() {
                public void clicked(InputEvent event, float x, float y){
                    if(!betButton.isDisabled()){
                        hasBetted = true;
                        betButton.setDisabled(true);
                        if(!GameManager.playerSeated){
                            seatPlayer();
                        }
                        try {
                            GameManager.socketHandler.resetLatch(1);
                            GameManager.socketHandler.bet(game.getPlayerName(), 10.0);
                            GameManager.socketHandler.awaitPlayer();
                        }catch (InterruptedException e){
                            e.printStackTrace();
                        }
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

        buttonContainer.setPosition(posX, posY);
        return buttonContainer;
    }

    @Override
    public void show() {
        backImage = new Texture("cards/BACK_CARD.png");
        frontImage = new Texture("cards/TWO_CLUBS.png");

        cWidth = frontImage.getWidth();
        cHeight = frontImage.getHeight();

        stage = new Stage();
        stage.setViewport(game.gameViewPort);

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

        // Leave button
        createLeaveGameButton();
    }

    public static Actor deal(EntityTarget entity, String card){
        PlayerXY xy = playerMap.get(entity);
        int x = xy.getPlayerX();
        int y = xy.getPlayerY();
        int count = xy.getNumCardInHand();
        xy.setNumCardInHand(count+1);
        return CardAnimation.dealCards(count, x, y, card);
    }

    public void updateUI(Card c, String eventType, int earnings){
        String cardName;
        int seatNum = eventType.charAt(eventType.length() - 1) - '0';

        switch (eventType) {
            case "DRAW_DEALER_0":
                cardName =  (c == null) ? "BACK_CARD": c.getRank() + "_" + c.getSuit();
                stage.addActor(deal(EntityTarget.DEALER, cardName));
                disableButtons();
                playerTurnLabel.setText("");
                break;
            case "DEALER_REVEAL_DEALER_0":
                cardName = c.getRank() + "_" + c.getSuit();
                Texture cardBack = new Texture("cards/BACK_CARD.png");
                Texture cardFront = new Texture("cards/" + cardName + ".png");
                CardAnimation dReveal = new CardAnimation(cardBack);
                dReveal.setTexture(cardFront);
                int x = 870;
                int y = 650;
                dReveal.setPosition(x, y);
                stage.addActor(dReveal);
                disableButtons();
                playerTurnLabel.setText("");
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
            case "PLAYER_DOUBLE_PLAYER_1":
                disableButtons();
                cardName = c.getRank() + "_" + c.getSuit();
                stage.addActor(deal(EntityTarget.PLAYER_1, cardName));
                this.updateGameState("Turn over");
                break;
            case "PLAYER_DOUBLE_PLAYER_2":
                disableButtons();
                cardName = c.getRank() + "_" + c.getSuit();
                stage.addActor(deal(EntityTarget.PLAYER_2, cardName));
                this.updateGameState("Turn over");
                break;
            case "PLAYER_DOUBLE_PLAYER_3":
                disableButtons();
                cardName = c.getRank() + "_" + c.getSuit();
                stage.addActor(deal(EntityTarget.PLAYER_3, cardName));
                this.updateGameState("Turn over");
                break;
            case "PLAYER_DOUBLE_PLAYER_4":
                disableButtons();
                cardName = c.getRank() + "_" + c.getSuit();
                stage.addActor(deal(EntityTarget.PLAYER_4, cardName));
                this.updateGameState("Turn over");
                break;
            case "PLAYER_DOUBLE_PLAYER_5":
                disableButtons();
                cardName = c.getRank() + "_" + c.getSuit();
                stage.addActor(deal(EntityTarget.PLAYER_5, cardName));
                this.updateGameState("Turn over");
                break;
            case "PLAYER_TURN_PLAYER_1", "PLAYER_TURN_PLAYER_2", "PLAYER_TURN_PLAYER_3",
                    "PLAYER_TURN_PLAYER_4", "PLAYER_TURN_PLAYER_5":
                if(seatNum == game.getPlayerSeatNum()){
                    enableButtons();
                    Gdx.app.postRunnable(() -> this.updatePlayerTurnLbl("Your turn"));
                }else{
                    disableButtons();
                    Gdx.app.postRunnable(() -> this.updatePlayerTurnLbl("Player " + seatNum + " turn"));
                }
                break;
            case "PLAYER_STAND_PLAYER_1", "PLAYER_STAND_PLAYER_2", "PLAYER_STAND_PLAYER_3",
                    "PLAYER_STAND_PLAYER_4", "PLAYER_STAND_PLAYER_5", "PLAYER_BUST_PLAYER_1",
                    "PLAYER_BUST_PLAYER_2", "PLAYER_BUST_PLAYER_3", "PLAYER_BUST_PLAYER_4",
                    "PLAYER_BUST_PLAYER_5":
                if(seatNum == game.getPlayerSeatNum()){
                    disableButtons();
                }
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
                    playerAmt += earnings;
                    this.playerMessage = "You won $" + earnings;
                }
                break;
            case "PLAYER_PUSH_PLAYER_1", "PLAYER_PUSH_PLAYER_2", "PLAYER_PUSH_PLAYER_3",
                    "PLAYER_PUSH_PLAYER_4", "PLAYER_PUSH_PLAYER_5":
                if(seatNum == game.getPlayerSeatNum()){
                    this.playerMessage = "It is a push";
                }
                break;
            case "UPDATE":
                updateGameState(playerMessage);
                lblAmt.setText("Amount: $" + this.playerAmt);
                disableButtons();
                playerTurnLabel.setText("");
                betButton.setDisabled(true);
                this.gameEnded = true;
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
        Table table = new Table(skin);

        //Creating table to contain labels
        Table gsTable = new Table();
        table.setWidth(100);
        table.setHeight(40);

        Table ptTable = new Table();
        table.setWidth(100);
        table.setHeight(40);

        // Create label for the message
        gameStateLabel = new Label("Place bet to start game", skin);
        playerTurnLabel = new Label("", skin);

        // Increase font size
        gameStateLabel.setFontScale(1.5f);
        playerTurnLabel.setFontScale(1.5f);

        //Adding labels to tables
        gsTable.add(gameStateLabel);
        ptTable.add(playerTurnLabel);

        // Add padding around the text for the box effect
//        table.add(gameStateLabel).left().padRight(100);
//        table.add(playerTurnLabel).right();

        //Applying necessary padding and alignments
        table.add(gsTable).padRight(150);
        table.add(ptTable);

        //Setting height and width of table
        table.setHeight(50);
        table.setWidth(500);

        // Position the table at the bottom of the screen
        table.top().right().padTop(20).padRight(20);
        table.setPosition(scrWidth - table.getPrefWidth() - 200, scrHeight - table.getPrefHeight());

        // Add the table to the stage for rendering
        stage.addActor(table);
    }


    public void updateGameState(String message) {
        gameStateLabel.setText(message);
    }

    public void updatePlayerTurnLbl(String message){
        playerTurnLabel.setText(message);
    }

/*    public void displayActions(String text) {
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
    }*/
    public void createLeaveGameButton() {
        // TextButton
        TextButton leaveButton = new TextButton("Leave Game", game.getSkin());
        leaveButton.setPosition(20, scrHeight - leaveButton.getHeight() - 20); // Position the button

        leaveButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                playerLeft = true;
                timer.cancel();
                GameManager.playerSeated = false;
                GameManager.playerInRoom = false;
                try {
                    GameManager.socketHandler.resetLatch(1);
                    GameManager.socketHandler.leave(game.getPlayerName());
                    GameManager.socketHandler.awaitPlayer();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                game.setScreen(new MainMenuScreen(game));
            }
        });

        stage.addActor(leaveButton);
    }

    private void resetGame(){
        // Schedule a task to update countdown seconds and execute code after 8 seconds
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Gdx.app.postRunnable(() -> {
                    if (countdownSeconds > 0) {
                        System.out.println(countdownSeconds);
                        updateGameState("Game restarting in " + countdownSeconds + " seconds");
                        countdownSeconds--;
                    } else {
                        timer.cancel();
                        if(!playerLeft){
                            game.setScreen(new GameScreen(game, playerAmt));
                        }
                    }
                });
            }
        },5000, 1000);

    }

    @Override
    public void create() {

        // Register a shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Hello");

        }));
    }

    @Override
    public void render() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0.3f, 0, 1);
        stage.getBatch().setProjectionMatrix(stage.getCamera().combined);

        if(gameEnded && !gameRestCalled){
            System.out.println("Game Ended Here!!!");
            gameRestCalled = true;
            resetGame();
        }

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public static int getScrWidth() {
        return scrWidth;
    }

    public static int getScrHeight() {
        return scrHeight;
    }

    @Override
    public void dispose() {
        batch.dispose();
        backImage.dispose();
        frontImage.dispose();
        stage.dispose();
    }
}