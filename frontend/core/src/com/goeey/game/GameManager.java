package com.goeey.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.goeey.game.screen.MainMenuScreen;
import com.goeey.game.socket.SocketHandler;
import com.gooey.base.socket.ClientEvent;
import com.gooey.base.socket.ServerEvent;

import java.util.concurrent.LinkedBlockingQueue;

public class GameManager extends Game {
    public static final int SCREEN_WIDTH = 1920;
    public static final int screen_height = 1080;
    private String playerName;
    public FitViewport gameViewPort;
    private Skin skin;
    public static SocketHandler socketHandler;
    private static LinkedBlockingQueue<ServerEvent<?>> clientQueue;

    @Override
    public void create() {
        skin = new Skin(Gdx.files.internal("skin/uiskin.json"));
        gameViewPort = new FitViewport(1920, 1080);
        setScreen(new MainMenuScreen(this));
        socketHandler = new SocketHandler("ws://10.0.0.10:8081/ws");
        clientQueue = new LinkedBlockingQueue<>();
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
        socketHandler.closeWebSocket();
    }

    public ServerEvent<?> processEvents() throws InterruptedException {
        if(clientQueue.peek() != null) {
            return clientQueue.take();
        }

        return null;
    }
}
