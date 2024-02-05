package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.RotateByAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.utils.ScreenUtils;


public class BlackJack extends ApplicationAdapter{
    private Texture backImage;
    private Texture frontImage;
    private SpriteBatch batch;
    private Sprite cardSprite;
    private Rectangle backCard;
    private OrthographicCamera camera;
    private Stage stage;

    @Override 
    public void create(){
        backImage = new Texture("back_card_150.png");
        frontImage = new Texture("2_of_clubs.png");
        // create the camera and the SpriteBatch
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1200, 980);
        batch = new SpriteBatch();

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        //rectangle to logically represent the cards
        // backCard = new Rectangle();
        // backCard.x = 800 / 2 - 64 / 2; // center the bucket horizontally
        // backCard.y = 20; // bottom left corner of the bucket is 20 pixels above the bottom screen edge
        // backCard.width = 60;
        // backCard.height = 60;


        final Card card = new Card(backImage);
        card.setPosition(800 / 2 - 64 / 2, 20);
        stage.addActor(card);

        // Animate the card
        //SequenceAction sequence = new SequenceAction();
        RotateByAction rotateAction = Actions.rotateBy(90,1f);
        MoveToAction moveToAction = Actions.moveTo(600, 600, 1f);
        //sequence.addAction(rotateAction);
        //sequence.addAction(scaleAction);
        SequenceAction sequence = Actions.sequence(rotateAction, moveToAction); // Rotate first, then move

        // Delay before starting rotation
       // DelayAction delayAction1 = Actions.delay(1f); // 2 seconds delay
        //DelayAction delayAction2 = Actions.delay(2f); // 2 seconds delay

        //SequenceAction finalSequence = Actions.sequence(delayAction, sequence);

        // Inside the create() method
        SequenceAction finalSequence = Actions.sequence(Actions.delay(2f), sequence, Actions.delay(.5f), Actions.run(new Runnable() {
        @Override
            public void run() {
                card.setTexture(frontImage); // Change texture to frontImage
            }
        }));

        card.addAction(finalSequence);
    }

    @Override
    public void render(){
        ScreenUtils.clear(0, 0.3f, 0, 1);

        camera.update();

        batch.setProjectionMatrix(camera.combined);


        // batch.begin();
        // batch.draw(backImage, backCard.x, backCard.y);
        // batch.end();

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
        
    }


    @Override
    public void dispose() {
        batch.dispose();
        backImage.dispose();
    }

    private static class Card extends Actor {
        private Texture texture;
    
        public Card(Texture texture) {
            this.texture = texture;
            setBounds(0, 0, texture.getWidth(), texture.getHeight()); // setBounds should be called here
        }

        public void setTexture(Texture t){
            this.texture = t;
        }
    
        @Override
        public void draw(Batch batch, float parentAlpha) {
            batch.draw(texture, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation(), 0, 0, texture.getWidth(), texture.getHeight(), false, false);
        }
    }


}
