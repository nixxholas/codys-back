package com.mygdx.game;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.screens.*;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class Boot extends Game{
    
    public static Boot INSTANCE;
    private int screenWidth, screenHeight;
    public SpriteBatch batch;
    public OrthographicCamera camera;


    public Boot(){
        INSTANCE = this;
    }
    
    public void create(){
        this.batch = new SpriteBatch();
        this.screenWidth = Gdx.graphics.getWidth();
        this.screenHeight = Gdx.graphics.getHeight();
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, screenWidth, screenHeight);
        // Sets screen to MainMenuScreen.java
        setScreen(new MainMenuScreen(INSTANCE));
    }

    public void render() {
		super.render(); // important!
	}

    public int getscreenWidth(){
        return screenWidth;
    }

    public int getscreenHeight(){
        return screenHeight;
    }

}
