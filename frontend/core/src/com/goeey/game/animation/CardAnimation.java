package com.goeey.game.animation;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

public class CardAnimation extends Actor {
    
    final static Texture backImage = new Texture("cards/BACK_CARD.png");
    private Texture texture;

    private static float totalDelay = 2f;

    public CardAnimation() {
        this.texture = new Texture("cards/BACK_CARD.png");
        setBounds(0, 0, texture.getWidth(), texture.getHeight()); // setBounds should be called here
    }
    public CardAnimation(Texture texture) {
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

    public static Actor dealCards(int cardNum, int scrWidth, int scrHeight, int endXPos, int endYPos, String imagePath){
        CardAnimation newC = new CardAnimation(backImage);
        Texture frontImage = new Texture("cards/" + imagePath);
        float cWidth = frontImage.getWidth();
        float cHeight = frontImage.getHeight();
        float startXPos = (scrWidth-cWidth) / 2f;
        float startYPos = scrHeight / 1.2f;
        newC.setPosition(startXPos, startYPos);
        SequenceAction sa;
        if (cardNum <=4){
            sa = newC.cardAnimation(totalDelay++, (int) (endXPos - cWidth + (cWidth / 5) * (cardNum + 1))
                    , endYPos, 0.5f, frontImage);
        }else{
            sa = newC.cardAnimation(totalDelay++, (int) (endXPos - 160 + (cWidth / 5) * (cardNum - 4)),
                    (int) (endYPos - (cHeight / 4)), 0.5f, frontImage);
        }
        newC.addAction(sa);
        return newC;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(texture, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation(), 0, 0, texture.getWidth(), texture.getHeight(), false, false);
    }

}