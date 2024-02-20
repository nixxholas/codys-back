package com.mygdx.game.screens;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.*;
import com.mygdx.game.objects.*;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class MainMenuScreen extends ScreenAdapter{
    final Boot game;
    private OrthographicCamera camera;
	private Stage stage;
    private int proceed = 0;

    /*
     *  Viewports are used to change how the screen behaves when the window is resized.
     *  Main Menu should be using ScreenViewport because we want the blackjack image, start button and Exit button
     *  to always be centered in the middle of the screen.
     *
     *  The screenViewport is instantiated in the constructor.
     *  The screenViewport is passed into stage as a parameter when constructing it.
     *  The screenViewport.update() resizes the viewport whenever resize() is called (aka a window resize happens).
     */
    private final ScreenViewport mainMenuViewport;

    public MainMenuScreen(Boot boot){
        this.game = boot;
        this.camera = boot.camera;
        mainMenuViewport = new ScreenViewport(camera);
    }

	public void show() {
        stage = new Stage(mainMenuViewport);
        Gdx.input.setInputProcessor(stage);

        Skin skin = new Skin(Gdx.files.internal("uiskin.json")); // You can use a different skin
        
        //TextButton
        TextButton startButton = new TextButton("Start Game", skin);
        TextButton exitButton = new TextButton("Exit to Desktop", skin);
        startButton.addListener(new ClickListener() {
            public void touchUp(InputEvent event, float x, float y, int point, int button) {
                // Handle slider value change
                proceed = 1;
            }
        });
        exitButton.addListener(new ClickListener() {
            public void touchUp(InputEvent event, float x, float y, int point, int button) {
                // Handle slider value change
                proceed = -1;
            }
        });

        // Create the bjImage as a scene2d.ui Image because table.add() only accepts Actors as input,
        // and Image is a subtype of Actors.
        Image bjImage = new Image(new Texture("images/decor/blackjacklogo.png"));

        /*
        *   Table is used to control layout of UI elements and widgets in a scene2d.ui
        *   The code below has 3 rows:
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

        /*
         *   Uncomment the following line to enable lines of the tables for debugging.
         */
        // table.setDebug(true);

        stage.addActor(table);
    }

    @Override
	public void render(float delta) {
		ScreenUtils.clear(0.28f, 0.31f, 0.60f, 1);
		camera.update();
		game.batch.setProjectionMatrix(camera.combined);

		game.batch.begin();
        stage.act(delta);
        stage.draw();
        game.batch.end();

        switch (proceed) {
            case 1:
                game.setScreen(new GameCreationScreen(game));
                break;
            case -1:
                Gdx.app.exit();
                System.exit(-1);
        }
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
