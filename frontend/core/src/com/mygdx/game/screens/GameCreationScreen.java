package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.Boot;

public class GameCreationScreen extends ScreenAdapter{
    final Boot game;
    private Skin skin;
	private Stage stage;
    private TextField textField;
    private Label numPlayersLabel;
    private Slider slider;

    public GameCreationScreen(Boot game){
        this.game = game;
        this.skin = game.skin;
    }

	public void show() {
        stage = new Stage();
        stage.setViewport(game.gameViewport);
        Gdx.input.setInputProcessor(stage);

        // Create a text field
        textField = new TextField("", skin);

        // Label
        Label nameLabel = new Label("Username", skin);
        numPlayersLabel = new Label("Number of Players: 1", skin);

        // Create a Slider
        slider = new Slider(1, 6, 1, false, skin);

        // Add a listener to respond to changes in slider value
        slider.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                // Handle slider value change
                numPlayersLabel.setText(String.format("Number of Players: %d", (int)slider.getValue()));
            }
        });

        //TextButton
        TextButton startButton = new TextButton("Start Game", skin);
        startButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                game.setPlayerName(textField.getText());
                game.setScreen(new GameScreen(game));
            }
        });

        TextButton backButton = new TextButton("Back to Main Menu", skin);
        backButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });

        // Add text field to a table
        Table table = new Table();
        table.setFillParent(true);
        table.add(nameLabel).top().left().padBottom(20);
        table.row();
        table.add(textField).width(300).height(40).padBottom(20);
        table.row();
        table.add(numPlayersLabel).top().left();
        table.row();
        table.add(slider).width(300).height(50).left().padBottom(20);
        table.row();
        table.add(startButton).width(250).height(50).pad(20);
        table.row();
        table.add(backButton).width(250).height(50);
        stage.addActor(table);
    }

    @Override
	public void render(float delta) {
        ScreenUtils.clear(0.28f, 0.31f, 0.60f, 1);

		game.batch.setProjectionMatrix(stage.getCamera().combined);

		game.batch.begin();
        stage.act(delta);
        stage.draw();
        game.batch.end();
    }

    @Override
    public void hide() {
        dispose();
    }

	@Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

	@Override
    public void dispose() {
        stage.dispose();
    }
}
