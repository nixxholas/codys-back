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

    public MainMenuScreen(Boot boot){
        this.game = boot;
        this.camera = boot.camera;
    }

	public void show() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        Skin skin = new Skin(Gdx.files.internal("uiskin.json")); // You can use a different skin
        
        //TextButton
        TextButton btnStart = new TextButton("Start Game", skin);
        TextButton btnLeave = new TextButton("Exit to Desktop", skin);
        //btnStart.setSize(200, 100);
        btnStart.addListener(new ClickListener() {
            public void touchUp(InputEvent event, float x, float y, int point, int button) {
                // Handle slider value change
                proceed = 1;
            }
        });
        btnLeave.addListener(new ClickListener() {
            public void touchUp(InputEvent event, float x, float y, int point, int button) {
                // Handle slider value change
                proceed = -1;
            }
        });


        // Add text field to a table
        Table table = new Table();
        table.setFillParent(true);
        table.add(btnStart).width(250).height(50);
        table.row();
        table.add(btnLeave).width(250).height(50).pad(50);
        // get the current position of the table
        float x = table.getX ();
        float y = table.getY ();
        // shift the table 50 pixels down
        table.setPosition (x, y - 100);
        stage.addActor(table);

        // BlackJack logo
        Texture bjLogo = new Texture("images/decor/blackjacklogo.png");
        Logo bj = new Logo(bjLogo);
        //Not working, need to relook
        //bj.setOrigin((float) bjLogo.getWidth()/2, (float) bjLogo.getHeight()/2);
        //temporary fix
        bj.setPosition((float) game.getscreenWidth()/2 - (float) bjLogo.getWidth() /2
                , (float) game.getscreenHeight()/2 +50);
        stage.addActor(bj);

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
