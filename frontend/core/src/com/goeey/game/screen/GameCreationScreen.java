package com.goeey.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.goeey.game.GameManager;
import com.goeey.game.entity.GameState;
import com.goeey.game.utils.ProcessServerMessage;

public class GameCreationScreen extends ScreenAdapter {
    private final GameManager game;
    private final GameState gameState;
    private Stage stage;
    private Table uiTable;
    private TextField nameTextfield;
    private final Skin skin;

    public GameCreationScreen (GameManager game) {
        this.game = game;
        this.skin = game.getSkin();
        this.gameState = GameState.getGameState();
        ProcessServerMessage.setGS(this);
    }

    public TextButton createRegisterButton() {
        // Register Button
        TextButton registerButton = new TextButton("Register", skin);
        registerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(nameTextfield.getText().isEmpty()) {
                    showError("Username is empty.");
                    return;
                }

                game.setPlayerName(nameTextfield.getText());
                try {
                    GameManager.socketHandler.register(game.getPlayerName());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        return registerButton;
    }

    public void goToLobbyScreen() {
        // Brings player back to lobby screen.
        game.setScreen(new LobbyRoomsScreen(game));
    }

    public TextButton createStartButton() {
        // Start Button
        TextButton startButton = new TextButton("Start Game", skin);
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(nameTextfield.getText().isEmpty()) {
                    showError("Username is empty.");
                    return;
                }
                // Setting player username
                game.setPlayerName(nameTextfield.getText());

                try {
                    GameManager.socketHandler.joinLobby(game.getPlayerName());
                } catch (InterruptedException ex){
                    ex.printStackTrace();
                }
            }
        });

        return startButton;
    }

    public TextButton createBackButton() {
        // Back Button
        TextButton backButton = new TextButton("Back to Main Menu", skin);
        backButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });

        return backButton;
    }

    public void populateUITable() {

        Label nameLabel = new Label("Username:", skin);
        nameTextfield = new TextField("", skin);
        // Prepare Table
        uiTable.setFillParent(true);
        uiTable.row().height(40);
        uiTable.add(nameLabel).padRight(10).right();
        uiTable.add(nameTextfield).width(200).height(40).left();

        Label errorLabel = new Label("TEST", game.getSkin());
        errorLabel.setName("errorLabel");
        errorLabel.setVisible(false);
        errorLabel.setColor(Color.RED);

        uiTable.add(errorLabel);
        uiTable.row().height(50);

        uiTable.add(createRegisterButton()).width(200).height(50).pad(20).padLeft(50);
        uiTable.add(createStartButton()).width(200).height(50).pad(20).padLeft(50);
        uiTable.add(createBackButton()).width(200).height(50).pad(20).padRight(50);
    }

    public void showError(String errorMsg) {
        Label errLabel = uiTable.findActor("errorLabel");
        errLabel.setText(errorMsg);
        errLabel.setVisible(true);
    }

    public void show() {
        stage = new Stage();
        uiTable = new Table();
        populateUITable();
        stage.setViewport(game.gameViewPort);
        Gdx.input.setInputProcessor(stage);

        stage.addActor(uiTable);
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
}
