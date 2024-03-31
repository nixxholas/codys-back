package com.goeey.game.screen;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;

import com.goeey.game.GameManager;
import com.goeey.game.animation.CardAnimation;
import com.goeey.game.entity.GameState;
import com.goeey.game.utils.PlayerXY;
import com.goeey.game.utils.ProcessServerMessage;
import com.gooey.base.Card;
import com.gooey.base.EntityTarget;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class GameScreen extends ScreenAdapter implements ApplicationListener {
    private final GameManager game;
    private final GameState gameState;
    private final Skin skin;
    private Texture backImage;
    private Texture frontImage;
    private Stage stage;
    private int cWidth;
    private int cHeight;
    private static int scrWidth = 1920;
    private static int scrHeight= 1080;
    private static Map<EntityTarget, PlayerXY> playerMap = new HashMap<>();
    private Label gameStateLabel;
    private Label playerTurnLabel;
    private TextButton hitButton;
    private TextButton standButton;
    private TextButton betButton;
    private TextButton doubleDownButton;
    private TextButton leaveButton;
    private Label lblAmt;
    private String playerMessage = null;
    private final Timer timer = new Timer();
    private int countdownSeconds = 10;

    public GameScreen(GameManager game) {
        this.game = game;
        this.gameState = GameState.getGameState();
        this.skin = game.getSkin();
        ProcessServerMessage.setGS(this);
        gameState.setGameRestCalled(false);
        gameState.setGameEnded(false);
    }

    public void unseatPlayer(){
        System.out.println("Unseat Player");
        betButton.setDisabled(true);
        gameState.setSeated(false);
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
            GameManager.socketHandler.sit(game.getPlayerName(), game.gameState.getSeatNumber());
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
            lblAmt = new Label("Amount: $" + gameState.getPlayerBalance(), skin);
            lblAmt.setFontScale(1);
            lblAmt.setColor(Color.YELLOW);

            //Hit Button
            hitButton = new TextButton("Hit", skin);
            hitButton.addListener(new ClickListener() {
                public void clicked(InputEvent event, float x, float y){
                    if(!hitButton.isDisabled()){
                        GameManager.socketHandler.hit(game.getPlayerName());
                    }
                }
            });

            //Stand Button
            standButton = new TextButton("Stand", skin);
            standButton.addListener(new ClickListener() {
                public void clicked(InputEvent event, float x, float y){
                    if(!standButton.isDisabled()){
                        standButton.setDisabled(true);
                        GameManager.socketHandler.stand(game.getPlayerName());
                    }
                }
            });

            //Double Down Button
            doubleDownButton = new TextButton("Double Down", skin);
            doubleDownButton.addListener(new ClickListener() {
                public void clicked(InputEvent event, float x, float y){
                    if(!doubleDownButton.isDisabled()){
                        doubleDownButton.setDisabled(true);
                        GameManager.socketHandler.doubleDown(game.getPlayerName(), 1.0);
                    }
                }
            });

            //Bet Button
            betButton = new TextButton("Bet", skin);
            betButton.addListener(new ClickListener() {
                public void clicked(InputEvent event, float x, float y){
                    if(!betButton.isDisabled()){
                        leaveButton.setDisabled(true);
                        gameState.setHasBet(true);
                        betButton.setDisabled(true);
                        if(!gameState.isSeated()){
                            seatPlayer();
                        }
                        GameManager.socketHandler.bet(game.getPlayerName(), 10.0);
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

    public void createDealersCard() {
        final CardAnimation cardBACK = new CardAnimation(backImage);
        cardBACK.setPosition((scrWidth-cWidth) / 2f , scrHeight/1.2f);
        stage.addActor(cardBACK);
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

        createDealersCard();

        // generate clean hashmap of all Entity targets, X and Y coords and card count
        playerMap = PlayerXY.refreshMap();

        // iterate through all players
        for (Map.Entry<EntityTarget, PlayerXY> mapElement: playerMap.entrySet()) {
            if(!(mapElement.getKey() == EntityTarget.DEALER)){
                String name = "" + mapElement.getKey();
                // Adding some bonus marks to all the students
                PlayerXY xy = mapElement.getValue();

                // Check if value equals to seatNumber and set isCurrentPlayer to true
                // Create player name tag for each player position
                // X position is fixed for all players
                // Y position differs for the current players to create room for UI elements
                // Username will be generated of Current player whereas
                // for other player it would be just PLAYER_(SEAT NUMBER)
                // Parameters skin, X coordinate, Y coordinate,
                stage.addActor(createButtonsNLabels(
                        skin,
                        xy.getPlayerX(),
                        mapElement.getKey() == gameState.getPlayerEntityTarget() ?
                                xy.getPlayerY() + cHeight + 50 : xy.getPlayerY() + cHeight + 20,
                        mapElement.getKey() == gameState.getPlayerEntityTarget() ?
                                game.getPlayerName() : name,
                        mapElement.getKey() == gameState.getPlayerEntityTarget())
                );
            }
        }
        // Display Game State
        createGameState();

        // Leave button
        stage.addActor(createLeaveGameButton());
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
                if(seatNum == game.gameState.getSeatNumber()){
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
                if(seatNum == game.gameState.getSeatNumber()){
                    disableButtons();
                }
                this.updateGameState("Turn over");
                break;
            case "PLAYER_LOSE_PLAYER_1", "PLAYER_LOSE_PLAYER_2", "PLAYER_LOSE_PLAYER_3",
                    "PLAYER_LOSE_PLAYER_4", "PLAYER_LOSE_PLAYER_5":
                if(seatNum == game.gameState.getSeatNumber()){
                    gameState.deductPlayerBalance(earnings);
                    this.playerMessage = "You lost $" + earnings;
                }
                break;
            case "PLAYER_WIN_PLAYER_1", "PLAYER_WIN_PLAYER_2", "PLAYER_WIN_PLAYER_3",
                    "PLAYER_WIN_PLAYER_4", "PLAYER_WIN_PLAYER_5":
                if(seatNum == game.gameState.getSeatNumber()){
                    gameState.addToPlayerBalance(earnings);
                    this.playerMessage = "You won $" + earnings;
                }
                break;
            case "PLAYER_PUSH_PLAYER_1", "PLAYER_PUSH_PLAYER_2", "PLAYER_PUSH_PLAYER_3",
                    "PLAYER_PUSH_PLAYER_4", "PLAYER_PUSH_PLAYER_5":
                if(seatNum == game.gameState.getSeatNumber()){
                    this.playerMessage = "It is a push";
                }
                break;
            case "UPDATE":
                updateGameState(playerMessage);
                lblAmt.setText("Amount: $" + gameState.getPlayerBalance());
                disableButtons();
                playerTurnLabel.setText("");
                betButton.setDisabled(true);
                gameState.setGameEnded(true);
                break;
            default:
                // Handle the default case if needed
                break;
        }
    }

    private void enableButtons(){
        hitButton.setDisabled(false);
        standButton.setDisabled(false);
        doubleDownButton.setDisabled(false);
    }

    private void disableButtons(){
        hitButton.setDisabled(true);
        standButton.setDisabled(true);
        doubleDownButton.setDisabled(true);
    }

    public void createGameState() {
        Table table = new Table(skin);
        table.setFillParent(true);

        // Creating tables to contain labels
        Table gsTable = new Table();
        Table ptTable = new Table();

        // Create label for the message
        gameStateLabel = new Label("Place bet to start game", skin);
        playerTurnLabel = new Label("", skin);

        // Increase font size
        gameStateLabel.setFontScale(1.5f);
        playerTurnLabel.setFontScale(1.5f);

        // Adding labels to tables
        gsTable.add(gameStateLabel).padRight(150);
        ptTable.add(playerTurnLabel);

        // Adding inner tables to the main table
        table.add(gsTable).row();
        table.add(ptTable);

        // Position the table in the top right corner of the screen with padding
        table.top().right().padTop(20).padRight(20);
        //table.setPosition(scrWidth - 300, scrHeight - 200);

        // Add the table to the stage for rendering
        stage.addActor(table);
    }

    public void updateGameState(String message) {
        gameStateLabel.setText(message);
    }

    public void updatePlayerTurnLbl(String message){
        playerTurnLabel.setText(message);
    }

    public TextButton createLeaveGameButton() {
        leaveButton = new TextButton("Leave Game", game.getSkin());
        leaveButton.setPosition(20, scrHeight - leaveButton.getHeight() - 20); // Position the button

        leaveButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if(!leaveButton.isDisabled()){
                    gameState.setPlayerLeft(true);
                    timer.cancel();
                    GameManager.socketHandler.leaveRoom(game.getPlayerName());
                    gameState.setSeated(false);
                    gameState.setInRoom(false);
                    game.setScreen(new LobbyRoomsScreen(game));
                }
            }
        });

        return leaveButton;
    }

    private void resetGame(){
        leaveButton.setDisabled(false);
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
                        if(!gameState.hasPlayerLeft()){
                            game.setScreen(new GameScreen(game));
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
    public void render(float delta) {
        ScreenUtils.clear(0, 0.3f, 0, 1);
        stage.getBatch().setProjectionMatrix(stage.getCamera().combined);

        if(gameState.hasGameEnded() && !gameState.isGameRestCalled()){
            System.out.println("Game Ended Here!!!");
            gameState.setGameRestCalled(true);
            resetGame();
        }

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void render() {
    }

    @Override
    public void dispose() {
        backImage.dispose();
        frontImage.dispose();
    }
}