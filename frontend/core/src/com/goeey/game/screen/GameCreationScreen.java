package com.goeey.game.screen;

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

import java.awt.*;

public class GameCreationScreen extends ScreenAdapter {
    private final GameManager game;
    private Stage stage;
    private TextButton startButton;
    private TextButton backButton;
    private TextField nameTextfield;
    private Skin skin;

    public GameCreationScreen (GameManager game) {
        this.game = game;
        this.skin = game.getSkin();
    }

    public Table uiTableFactory() {

        // Label
        Label nameLabel = new Label("Username:", skin);

        // Create a text field
        nameTextfield = new TextField("", skin);

        ///TextButton
        TextButton startButton = new TextButton("Start Game", skin);
        startButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                game.setPlayerName(nameTextfield.getText());
                game.setScreen(new GameScreen(game));
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
        stage.setViewport(game.gameViewPort); // can i public static final this?
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
    public void dispose() { stage.dispose(); }
}
