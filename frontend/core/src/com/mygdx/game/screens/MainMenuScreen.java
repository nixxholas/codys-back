package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.Boot;

public class MainMenuScreen extends ScreenAdapter{
    private final Boot game;
	private Stage stage;
    private Skin skin;

    public MainMenuScreen(Boot game){
        this.game = game;
        this.skin = game.skin;
    }

	public void show() {
        stage = new Stage();
        stage.setViewport(game.gameViewport);
        Gdx.input.setInputProcessor(stage);

        Image bjImage = new Image(new Texture("images/decor/blackjacklogo.png"));
        
        //TextButton
        TextButton startButton = new TextButton("Start Game", skin);
        TextButton exitButton = new TextButton("Exit to Desktop", skin);
        startButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameCreationScreen(game));
            }
        });
        exitButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
                System.exit(-1);
            }
        });


        /*
        *   Table is used to control layout of UI elements and widgets in a scene2d.ui
        *   The code below will generate 3 rows:
        *       1. First row contains the blackjack image
        *       2. Second row contains the "Start Game" button
        *       3. Third row contains the "Exit" button
        */
        Table table = new Table();
        table.setFillParent(true);
        table.add(bjImage);
        table.row();
        table.add(startButton).width(500).height(100).padTop(50);
        table.row();
        table.add(exitButton).width(500).height(100).padTop(50);

        stage.addActor(table);
    }

    @Override
	public void render(float delta) {
		ScreenUtils.clear(0.28f, 0.31f, 0.60f, 1);
		game.batch.setProjectionMatrix(stage.getCamera().combined);

//		game.batch.begin();
        stage.draw();
//        game.batch.end();
	}
	@Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void hide() {
        dispose();
    }

	@Override
    public void dispose() {
        stage.dispose();
    }
}
