package com.goeey.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.goeey.game.screen.MainMenuScreen;
import com.goeey.game.socket.SocketHandler;
import com.gooey.base.EntityTarget;
import com.gooey.base.socket.ClientEvent;
import com.gooey.base.socket.ServerEvent;

import java.util.concurrent.LinkedBlockingQueue;

public class GameManager extends Game {
    public static final int SCREEN_WIDTH = 1920;
    public static final int screen_height = 1080;
    private String playerName;

    private EntityTarget currentPlayer;

    public FitViewport gameViewPort;
    private Skin skin;
    public static SocketHandler socketHandler;

    @Override
    public void create() {
        skin = new Skin(Gdx.files.internal("skin/uiskin.json"));
        gameViewPort = new FitViewport(1920, 1080);
        setScreen(new MainMenuScreen(this));
        socketHandler = new SocketHandler("ws://localhost:8080/ws");
    }

    public void render() {
        super.render();
    }

    public void setPlayerName(String playerName) {this.playerName = playerName;}
    public String getPlayerName() {return playerName;}

    public  EntityTarget getPlayerEntityType(){
        return this.currentPlayer;
    }

    public void setEntityType(int num){
        switch (num){
            case 1:
                this.currentPlayer = EntityTarget.PLAYER_1;
                break;
            case 2:
                this.currentPlayer = EntityTarget.PLAYER_2;
                break;
            case 3:
                this.currentPlayer = EntityTarget.PLAYER_3;
                break;
            case 4:
                this.currentPlayer = EntityTarget.PLAYER_4;
                break;
            case 5:
                this.currentPlayer = EntityTarget.PLAYER_5;
                break;
            case 6:
                this.currentPlayer = EntityTarget.PLAYER_6;
                break;
            default:
                break;
        }
    }

    public Skin getSkin() {
        return this.skin;
    }
    public void dispose() {
        socketHandler.closeWebSocket();
    }
}
