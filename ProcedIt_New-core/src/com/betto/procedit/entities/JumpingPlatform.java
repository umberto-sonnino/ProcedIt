package com.betto.procedit.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.betto.procedit.UserDataWrapper;
import com.betto.procedit.screens.PlayScreen;

public class JumpingPlatform extends PolygonShape {
	
	public enum PlatformType {
		NORMAL, SPIKED, UPGRADE, BOOST, CLOUD, IMAGE
	}
	
	private UserDataWrapper userData;
	
	public JumpingPlatform(float width, float height, float x, float y, 
			Sprite platformSprite, Body environment, PlatformType type) {
		
		this.setAsBox(width / 2, height / 2, new Vector2( x + width / 2 , y + height / 2) , 0);
		
		switch (type) {
		case NORMAL:
			platformSprite = new Sprite(new Texture(Gdx.files.internal("ui/grassHalf.png")));
			userData = new UserDataWrapper(platformSprite, PlatformType.NORMAL, width, height);
			break;
		case SPIKED:
			platformSprite = new Sprite(new Texture(Gdx.files.internal("ui/spikes.png")));
			userData = new UserDataWrapper(platformSprite, PlatformType.SPIKED, width, height);
			break;
		case BOOST:
			platformSprite = new Sprite(new Texture(Gdx.files.internal("ui/boostUp.png")));
			userData = new UserDataWrapper(platformSprite, PlatformType.BOOST, width, height);
			break;
		case CLOUD:
			platformSprite = new Sprite(new Texture(Gdx.files.internal("ui/cloud.png")));
			userData = new UserDataWrapper(platformSprite, PlatformType.CLOUD, width, height);
			break;
		case UPGRADE:
			platformSprite = new Sprite(new Texture(Gdx.files.internal("ui/goldBonus.png")));
			userData = new UserDataWrapper(platformSprite, PlatformType.UPGRADE, width, height);
			break;
		default:
			break;
		}
		platformSprite.setSize(width, height);
		platformSprite.setOrigin(platformSprite.getWidth() / 2, platformSprite.getHeight() / 2);
		
		environment.createFixture(this, 0).setUserData(userData);
		
		this.dispose();
	}


}
