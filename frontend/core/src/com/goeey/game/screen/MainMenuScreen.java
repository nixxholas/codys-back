package com.goeey.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.goeey.game.GameManager;
import com.goeey.game.entity.GameState;

public class MainMenuScreen extends ScreenAdapter {
    private final GameManager game;
    private Stage stage;

    public MainMenuScreen(GameManager game) {
        this.game = game;
    }

    public TextButton createStartButton() {
        // Start Button
        TextButton startButton = new TextButton("Start Game", game.getSkin());
        startButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                showConnectingDialog();

                // Start the server connection in a separate thread
                Thread connectToServerThread = new Thread(() -> {
                    // Perform server connection here
                    GameManager.socketHandler.establishConnection();

                    // Once the connection is established, dismiss the dialog
                    Gdx.app.postRunnable(() -> {
                        stage.getRoot().findActor("connectingDialog").remove();
                        if(GameState.getGameState().isConnected()) {
                            game.setScreen(new GameCreationScreen(game));
                        } else {
                            showConnectionFailedDialog();
                        }
                    });
                });
                connectToServerThread.start();
            }
        });

        return startButton;
    }

    public TextButton createExitButton() {
        // Exit Button
        TextButton exitButton = new TextButton("Exit to Desktop", game.getSkin());
        exitButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
                System.exit(-1);
            }
        });

        return exitButton;
    }

    public Table populateUITable() {
        // Create Logo Texture
        Image logoImage = new Image(new Texture("images/blackjacklogo.png"));

        // Add elements to table
        Table uiTable = new Table();
        uiTable.setFillParent(true);
        uiTable.add(logoImage);
        uiTable.row();
        uiTable.add(createStartButton()).width(500).height(100).padTop(50);
        uiTable.row();
        uiTable.add(createExitButton()).width(500).height(100).padTop(50);

        return uiTable;
    }

    public void showConnectingDialog() {
        Dialog connectionDialog = new Dialog("Connect", game.getSkin());
        connectionDialog.text("Connecting to server ...").center();
        connectionDialog.setName("connectingDialog");

        connectionDialog.show(stage);
    }

    private TextButton createOkAndCloseButton(String dialogName) {
        // Ok and Close Button
        TextButton okAndCloseButton = new TextButton("Ok", game.getSkin());
        okAndCloseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stage.getRoot().findActor(dialogName).remove();
            }
        });

        return okAndCloseButton;
    }

    public void showConnectionFailedDialog() {
        Dialog failedConnectionDialog = new Dialog("Error", game.getSkin());

        failedConnectionDialog.text("Failed to connect to server").center();
        failedConnectionDialog.setName("failedDialog");
        failedConnectionDialog.button(createOkAndCloseButton(failedConnectionDialog.getName()));

        failedConnectionDialog.show(stage);
    }

    public void show() {
        stage = new Stage();
        stage.setViewport(game.gameViewPort);
        Gdx.input.setInputProcessor(stage);

        stage.addActor(populateUITable());
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.28f, 0.31f, 0.60f, 1);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void hide() {
        dispose();
    }
}
