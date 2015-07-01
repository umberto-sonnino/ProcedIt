package com.betto.procedit.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.betto.procedit.UserDataWrapper;
import com.betto.procedit.entities.JumpingPlatform.PlatformType;

public class CoinPlatform extends PolygonShape {
	
	private UserDataWrapper userData;

	public CoinPlatform(float width, float height, float x, float y,
			Sprite platformSprite, Body environment) {


		this.setAsBox(width / 2, height / 2, new Vector2( x + width / 2 , y + height / 2) , 0);

		platformSprite = new Sprite(new Texture(Gdx.files.internal("ui/goldBonus.png")));

		userData = new UserDataWrapper(platformSprite, PlatformType.UPGRADE, width, height);
		platformSprite.setSize(width, height);
		platformSprite.setOrigin(platformSprite.getWidth() / 2, platformSprite.getHeight() / 2);

		Fixture imageFixture = environment.createFixture(this, 0);
		imageFixture.setUserData(userData);
		imageFixture.setSensor(true);

		this.dispose();
	}
}
