package com.betto.procedit.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.betto.procedit.UserDataWrapper;
import com.betto.procedit.entities.JumpingPlatform.PlatformType;

public class ImageUserDataWrapper extends UserDataWrapper {

	private String key;
	
	public ImageUserDataWrapper(Sprite sprite, PlatformType type, float width,
			float height, String key) {
		super(sprite, type, width, height);
		this.key = key;
	}

	public String getKey() {
		return key;
	}

}
