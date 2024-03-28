package com.goeey.game.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.goeey.game.GameManager;
import com.goeey.game.utils.ProcessServerMessage;

import java.util.Arrays;

public class GameCreationScreen extends ScreenAdapter {
    private final GameManager game;
    private Stage stage;
    private TextField nameTextfield;
    private Skin skin;

    private String[] roomsList;

    private boolean playerSat = false;

    public int numPlayers = 0;


    public GameCreationScreen (GameManager game) {
        this.game = game;
        this.skin = game.getSkin();
        ProcessServerMessage.setGS(this);
    }

    public void setRoomList(String[] roomsList) {
        if(roomsList != null){
            this.roomsList  = Arrays.copyOf(roomsList, roomsList.length);
        }
    }

    public  void setPlayerSat(boolean playerSat) {
        this.playerSat = playerSat;
    }

    public void setNumPlayers(int numPlayers) {
        this.numPlayers = numPlayers;
    }

    public Table uiTableFactory() {

        // Label
        Label nameLabel = new Label("Username:", skin);

        // Create a text field
        nameTextfield = new TextField("", skin);

        // TextButton
        TextButton startButton = new TextButton("Start Game", skin);
        startButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {

                //Setting player username
                game.setPlayerName(nameTextfield.getText());

                //Registering Player to Server
                GameManager.socketHandler.register(game.getPlayerName());

                //Connecting Player to Server
                GameManager.socketHandler.connect(game.getPlayerName());

                //Getting all Rooms from Backend
                GameManager.socketHandler.listRooms(game.getPlayerName());

                //Checking if Rooms are Empty Or Else Loop Through Them and Get Counts
                if(roomsList == null || roomsList.length == 0){
                    System.out.println("ROOMS NULL");
                    GameManager.socketHandler.createAndJoin(game.getPlayerName());
                    GameManager.socketHandler.sit(game.getPlayerName(), 1);
                    game.setEntityType(1);
                }else{
                    System.out.println("ROOMS NOT NULL");
                    System.out.println(Arrays.toString(roomsList));
                    for (int i = 0; i < roomsList.length; i++) {
                        System.out.println(roomsList[i]);

                        //For now let all players join one room
                        GameManager.socketHandler.joinRoom(game.getPlayerName(), roomsList[i]);
                        System.out.println("I am trying to join room:" + roomsList[i]);
                        //find the available seat for each player in the room
                        int seatNum = 0;
                        do {
                            GameManager.socketHandler.sit(game.getPlayerName(), ++seatNum);
                        }while (!playerSat);
                        game.setEntityType(seatNum);
                    }
                }
                GameScreen.playerInRoom = true;
                GameScreen.playerSitting = true;
                game.setScreen(new GameScreen(game, 1000));
            }

        });

        TextButton backButton = new TextButton("Back to Main Menu", skin);
        backButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });

        // Prepare Table
        Table uiTable = new Table();
//        uiTable.setDebug(true);
        uiTable.setFillParent(true);
        uiTable.row().height(40);
        uiTable.add(nameLabel).padRight(10).right();
        uiTable.add(nameTextfield).width(200).height(40).left();
        uiTable.row().height(50);
        uiTable.add(startButton).width(200).height(50).pad(20).padLeft(50);
        uiTable.add(backButton).width(200).height(50).pad(20).padRight(50);
        return uiTable;
    }

    public void show() {
        stage = new Stage();
        stage.setViewport(game.gameViewPort);
        Gdx.input.setInputProcessor(stage);

        stage.addActor(uiTableFactory());
    }

    public void render(float delta) {
        ScreenUtils.clear(0.28f, 0.31f, 0.60f, 1);
        stage.getBatch().setProjectionMatrix(stage.getCamera().combined);
        stage.draw();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
    public void hide() {
        dispose();
    }
    public void dispose() {
        stage.dispose();
    }
}
