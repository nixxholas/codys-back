package com.goeey.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ScreenUtils;
import com.goeey.game.GameManager;
import com.goeey.game.animation.CardAnimation;
import com.goeey.game.utils.PlayerXY;
import com.goeey.game.utils.PlayerUtils;
import com.gooey.base.Card;
import com.gooey.base.EntityTarget;
import com.gooey.base.socket.ServerEvent;
import com.google.gson.*;

import java.io.InvalidObjectException;
import java.util.HashMap;
import java.util.Map;

public class GameScreen extends ScreenAdapter {
    final GameManager game;
    private Skin skin;
    //private Hud hud;
    private Texture backImage;
    private Texture frontImage;
    private SpriteBatch batch;
    private Stage stage;
    private int cWidth;
    private int cHeight;
    private int scrWidth = Gdx.graphics.getWidth();
    private int scrHeight= Gdx.graphics.getHeight();
    private Map<EntityTarget, PlayerXY> playerMap = new HashMap<>();

    public GameScreen(GameManager game) {
        this.game = game;
        this.skin = game.getSkin();
    }

    @Override
    public void show() {

        backImage = new Texture("cards/BACK_CARD.png");
        frontImage = new Texture("cards/TWO_CLUBS.png");

        cWidth = frontImage.getWidth();
        cHeight = frontImage.getHeight();

        stage = new Stage();
        stage.setViewport(game.gameViewPort);
        //hud = new Hud(game.batch, 65000, game.getPlayerName(), skin);

        Gdx.input.setInputProcessor(stage);

        // Create dealer's card
        // cards are dealt from here
        final CardAnimation cardBACK = new CardAnimation(backImage);
        cardBACK.setPosition((scrWidth-cWidth) / 2f , scrHeight/1.2f);
        stage.addActor(cardBACK);



        // generate clean hashmap of all Entity targets, X and Y coords and card count
        playerMap = PlayerXY.refreshMap();

        // iterate through all players
        for (Map.Entry<EntityTarget, PlayerXY> mapElement: playerMap.entrySet()) {
            if(!(mapElement.getKey()==EntityTarget.DEALER)){
                String name = "" + mapElement.getKey();
                // Adding some bonus marks to all the students
                PlayerXY xy = mapElement.getValue();
                //Create player name tag for each player position
                stage.addActor(PlayerUtils.createLabel(skin, xy.getPlayerX(),  xy.getPlayerY() + cHeight + 40, name));
            }
        }

        // single line code to deal card to players
        stage.addActor(deal(EntityTarget.DEALER, "TWO_DIAMONDS"));
        stage.addActor(deal(EntityTarget.DEALER, "THREE_DIAMONDS"));
        stage.addActor(deal(EntityTarget.DEALER, "KING_DIAMONDS"));
        stage.addActor(deal(EntityTarget.DEALER, "QUEEN_DIAMONDS"));
        stage.addActor(deal(EntityTarget.DEALER, "ACE_DIAMONDS"));
        stage.addActor(deal(EntityTarget.DEALER, "THREE_DIAMONDS"));
        stage.addActor(deal(EntityTarget.DEALER, "KING_DIAMONDS"));
        stage.addActor(deal(EntityTarget.DEALER, "QUEEN_DIAMONDS"));
        stage.addActor(deal(EntityTarget.PLAYER_1, "THREE_DIAMONDS"));
        stage.addActor(deal(EntityTarget.PLAYER_1, "KING_DIAMONDS"));
        stage.addActor(deal(EntityTarget.PLAYER_1, "QUEEN_DIAMONDS"));
        stage.addActor(deal(EntityTarget.PLAYER_1, "ACE_DIAMONDS"));
        stage.addActor(deal(EntityTarget.PLAYER_1, "THREE_DIAMONDS"));
        stage.addActor(deal(EntityTarget.PLAYER_1, "KING_DIAMONDS"));
        stage.addActor(deal(EntityTarget.PLAYER_1, "QUEEN_DIAMONDS"));
        stage.addActor(deal(EntityTarget.PLAYER_2, "KING_DIAMONDS"));
        stage.addActor(deal(EntityTarget.PLAYER_2, "QUEEN_DIAMONDS"));
        stage.addActor(deal(EntityTarget.PLAYER_2, "THREE_DIAMONDS"));
        stage.addActor(deal(EntityTarget.PLAYER_2, "KING_DIAMONDS"));
        stage.addActor(deal(EntityTarget.PLAYER_2, "QUEEN_DIAMONDS"));
        stage.addActor(deal(EntityTarget.PLAYER_3, "QUEEN_DIAMONDS"));
        stage.addActor(deal(EntityTarget.PLAYER_4, "THREE_DIAMONDS"));
        stage.addActor(deal(EntityTarget.PLAYER_5, "KING_DIAMONDS"));
        stage.addActor(deal(EntityTarget.PLAYER_5, "QUEEN_DIAMONDS"));


    }

    public Actor deal(EntityTarget entity, String card){
        PlayerXY xy = playerMap.get(entity);
        int x = xy.getPlayerX();
        int y = xy.getPlayerY();
        int count = xy.getCount();
        xy.setCount(count+1);
        return CardAnimation.dealCards(count, x, y, card);
    }

    public Actor parseServerMsg(ServerEvent<?> event) throws InvalidObjectException {
        Card card = null;
        if(event.getMessage() instanceof Card) {
            card = (Card) event.getMessage();
        }
        EntityTarget entity = event.getTarget();
        switch (event.getType()) {
            case PLAYER_DRAW:
                if (card != null) {
                    return deal(entity, card.getRank() + "_" + card.getSuit());
                }
            case DEALER_DRAW:
                if (card==null) {
                    return deal(entity, "BACK_CARD");
                } else {
                    return deal(entity, card.getRank() + "_" + card.getSuit());
                }
            default:
                throw new InvalidObjectException("Not Player/Dealer draw event");
        }
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0.3f, 0, 1);
        stage.getBatch().setProjectionMatrix(stage.getCamera().combined);

        stage.act(delta);
        stage.draw();

        /*
        *   1. Render the HUD details
        *   2. TODO update the balance on the HUD as it changes
        * */
        //hud.hudStage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        batch.dispose();
        backImage.dispose();
        frontImage.dispose();
        stage.dispose();
        //hud.dispose();
    }
}
