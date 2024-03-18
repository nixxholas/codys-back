package com.goeey.frontend;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.goeey.frontend.screen.MainMenuScreen;

public class GameManager extends Game {
    public static final int SCREEN_WIDTH = 1920;
    public static final int screen_height = 1080;
    private String playerName;
    public FitViewport gameViewPort;
    private Skin skin;

    @Override
    public void create() {
        skin = new Skin(Gdx.files.internal("skin/uiskin.json"));
        gameViewPort = new FitViewport(1920, 1080);
        setScreen(new MainMenuScreen(this));
    }

    public void render() {
        super.render();
    }

    public void setPlayerName(String playerName) {this.playerName = playerName;}
    public String getPlayerName() {return playerName;}
    public Skin getSkin() {
        return this.skin;
    }
}
