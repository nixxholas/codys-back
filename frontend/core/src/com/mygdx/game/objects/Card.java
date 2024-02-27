package com.mygdx.game.objects;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

public class Card extends Actor {
    
    private Texture backImage = new Texture("back_card_150.png");
    private Texture frontImage;
    private Texture texture;

    public Card() {
        this.texture = new Texture("back_card_150.png");
        setBounds(0, 0, texture.getWidth(), texture.getHeight()); // setBounds should be called here
    }
    public Card(Texture texture) {
        this.texture = texture;
        setBounds(0, 0, texture.getWidth(), texture.getHeight()); // setBounds should be called here
    }

    public void setTexture(Texture t){
        this.texture = t;
    }

    public SequenceAction cardAnimation(float afterDelay, int x, int y, float delay, Texture t){
        MoveToAction moveToAction = Actions.moveTo(x, y, delay);
        //RotateByAction rotateAction = Actions.rotateBy(rotate,delay);
        SequenceAction sequence = Actions.sequence(moveToAction); // Move first, then Rotate

        SequenceAction finalSequence = Actions.sequence(Actions.delay(afterDelay), sequence,
                Actions.delay(0.5f), Actions.run(new Runnable() {
            @Override
            public void run() {
                // Change texture to frontImage
                setTexture(t);
            }
        }));

        return finalSequence;
    }

    public Actor dealHorizCards(float delay, int startXPos, int startYPos, int endXPos, int endYPos, int offset, String imagePath){
        Card newC = new Card(backImage);
        frontImage = new Texture(imagePath);
        newC.setPosition(startXPos, startYPos);
        
        SequenceAction sa = newC.cardAnimation(delay, endXPos +offset
                ,endYPos, 0.5f, frontImage);
        newC.addAction(sa);
        return newC;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(texture, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation(), 0, 0, texture.getWidth(), texture.getHeight(), false, false);
    }

}