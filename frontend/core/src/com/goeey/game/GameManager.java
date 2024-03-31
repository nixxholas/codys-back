package com.goeey.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.goeey.game.entity.GameState;
import com.goeey.game.screen.GameCreationScreen;
import com.goeey.game.screen.GameScreen;
import com.goeey.game.screen.MainMenuScreen;
import com.goeey.game.socket.SocketHandler;
import com.gooey.base.EntityTarget;

import java.net.URISyntaxException;

public class GameManager extends Game {
    private String playerName;
    public FitViewport gameViewPort;
    private Skin skin;
    public static SocketHandler socketHandler;
    public GameState gameState;


    @Override
    public void create() {
        skin = new Skin(Gdx.files.internal("skin/uiskin.json"));
        gameViewPort = new FitViewport(1920, 1080);
        setScreen(new MainMenuScreen(this));

        gameState = GameState.getGameState();
        try {
            GameManager.socketHandler = new SocketHandler("ws://10.0.0.10:8081/ws", this);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void resize(int width, int height) {
        gameViewPort.update(width, height, true);
    }

    public void render() {
        super.render();
    }

    public void setPlayerName(String playerName) {this.playerName = playerName;}

    public String getPlayerName() {return playerName;}

    public Skin getSkin() {
        return this.skin;
    }

    public void dispose() {
        GameManager.socketHandler.leaveRoom(playerName);
    }
}
