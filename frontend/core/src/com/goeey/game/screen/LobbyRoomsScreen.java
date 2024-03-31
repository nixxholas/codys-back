package com.goeey.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.goeey.game.GameManager;
import com.goeey.game.entity.GameState;
import com.goeey.game.utils.ProcessServerMessage;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class LobbyRoomsScreen extends ScreenAdapter {

    private final GameManager game;
    private Stage stage;
    private Table roomsTable;
    private List<String> roomsList;
    private final Map<String, TextButton> roomMap;

    public LobbyRoomsScreen(GameManager game) {
        this.game = game;
        ProcessServerMessage.setGS(this);

        roomMap = new HashMap<>();
    }

    public void refreshRooms() {
        // Updates the rooms in the server
        roomsList = getAllRooms();
        loadRooms();
        updateUITable();
    }

    public void updateUITable() {
        roomsTable.clearChildren();

        roomsTable.add("Room Ids:");
        roomsTable.add("Capacity:");
        roomsTable.add(createRefreshButton());
        roomsTable.row();

        for(String roomId : roomMap.keySet()) {
            roomsTable.add(roomId).pad(10);
            roomsTable.add("Capacity: " + getPlayersInRoom(roomId) + "/5").pad(10);
            roomsTable.add(roomMap.get(roomId)).pad(10);
            roomsTable.row();
        }

        roomsTable.row().padTop(100);
        roomsTable.add(createLeaveButton());
        roomsTable.add(createRoomButton());
    }

    public void createUITable() {
        roomsTable = new Table(game.getSkin());
        roomsTable.setFillParent(true);

        updateUITable();

        stage.addActor(roomsTable);
    }

    public TextButton createRoomButton() {
        // Create Room Button
        TextButton refreshButton = new TextButton("Create Room", game.getSkin());
        refreshButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            try {
                String roomId = GameManager.socketHandler.createAndJoin(game.getPlayerName());
                GameManager.socketHandler.sit(game.getPlayerName(), getPlayersInRoom(roomId) + 1);

            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            game.setScreen(new GameScreen(game));
            }
        });

        return refreshButton;
    }

    public TextButton createRefreshButton() {
        // Refresh Button
        TextButton refreshButton = new TextButton("Refresh", game.getSkin());
        refreshButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                refreshRooms();
            }
        });

        return refreshButton;
    }

    public TextButton createJoinButton(String roomId) {
        // Join Button
        TextButton joinButton = new TextButton("Join Room", game.getSkin());
        joinButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("joining roomid: " + roomId + " on " + Thread.currentThread());
                GameManager.socketHandler.joinRoom(game.getPlayerName(), roomId);
                GameManager.socketHandler.sit(game.getPlayerName(), getPlayersInRoom(roomId) + 1);
                game.setScreen(new GameScreen(game));
            }
        });

        return joinButton;
    }

    public TextButton createLeaveButton(){
        // Leave Button
        TextButton leaveButton = new TextButton("Return to Main Menu", game.getSkin());
        leaveButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });

        return leaveButton;
    }
    public ArrayList<String> getAllRooms() {
        try {
            return GameManager.socketHandler.listRooms(game.getPlayerName());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadRooms() {
        roomMap.clear();
        for(String roomId : roomsList) {
            roomMap.put(roomId, createJoinButton(roomId));
        }
    }

    public int getPlayersInRoom(String roomId) {
        try {
            return GameManager.socketHandler.getNumPlayersInRoom(game.getPlayerName(), roomId);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.28f, 0.31f, 0.60f, 1);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void show() {
        stage = new Stage();
        createUITable();
        refreshRooms();
        stage.setViewport(game.gameViewPort);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void hide() {
        super.hide();
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
