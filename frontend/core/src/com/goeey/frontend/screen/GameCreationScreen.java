package com.goeey.frontend.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.goeey.frontend.GameManager;

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
        Label nameLabel = new Label("Username", skin);

        // Create a text field
        nameTextfield = new TextField("", skin);

        ///TextButton
        TextButton startButton = new TextButton("Start Game", skin);
        startButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                game.setPlayerName(nameTextfield.getText());
//                game.setScreen(new GameScreen(game));
            }
        });

        TextButton backButton = new TextButton("Back to Main Menu", skin);
        backButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });


        // Prepare Table
        // Add text field to a uiTable
        Table uiTable = new Table();
        uiTable.setFillParent(true);
        uiTable.add(nameLabel).top().left().padBottom(20);
        uiTable.add(nameTextfield).width(300).height(40).padBottom(20);
        uiTable.row();
        uiTable.add(startButton).width(250).height(50).pad(20).padLeft(50).right();
        uiTable.add(backButton).width(250).height(50).pad(20).padRight(50);
uiTable.setDebug(true);
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

    public void hide() {
        dispose();
    }

    public void dispose() { stage.dispose(); }
}
