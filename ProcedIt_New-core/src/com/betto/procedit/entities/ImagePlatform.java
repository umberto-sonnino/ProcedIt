package com.betto.procedit.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.betto.procedit.UserDataWrapper;
import com.betto.procedit.entities.JumpingPlatform.PlatformType;

public class ImagePlatform extends PolygonShape {

	private UserDataWrapper userData;
	
	private float imageY;
	private String imageKey;
	private boolean belowScreen;
	
	public ImagePlatform(float width, float height, float x, float y,
			Sprite platformSprite, Body environment, Texture image, String key) {
		
		this.imageY = y;
		this.imageKey = key;
		
		this.setAsBox(width / 2, height / 2, new Vector2( x + width / 2 , y + height / 2) , 0);
		
		platformSprite = new Sprite(image);
		
		userData = new ImageUserDataWrapper(platformSprite, PlatformType.IMAGE, width, height, key);
		platformSprite.setSize(width, height);
		platformSprite.setOrigin(platformSprite.getWidth() / 2, platformSprite.getHeight() / 2);
		
		Fixture imageFixture = environment.createFixture(this, 0);
		imageFixture.setUserData(userData);
		imageFixture.setSensor(true);
		
		this.dispose();
	}

	public float getY(){
		return this.imageY;
	}
	
	public String getKey(){
		return this.imageKey;
	}
	
	public boolean isBelowScreen(){
		return belowScreen;
	}
	
	public void setBelowScreen(){
		belowScreen = true;
	}
	
}
