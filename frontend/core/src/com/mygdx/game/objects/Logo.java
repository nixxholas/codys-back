package com.mygdx.game.objects;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.Texture;

public class Logo extends Actor {

    private Texture texture;

    public Logo(Texture texture) {
        this.texture = texture;
        setBounds(0, 0, texture.getWidth(), texture.getHeight()); // setBounds should be called here
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(texture, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation(), 0, 0, texture.getWidth(), texture.getHeight(), false, false);
    }

}