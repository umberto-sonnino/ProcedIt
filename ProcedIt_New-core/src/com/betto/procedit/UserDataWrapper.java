package com.betto.procedit;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.betto.procedit.entities.JumpingPlatform.PlatformType;

public class UserDataWrapper {

	public Sprite sprite;
	public PlatformType type;
	public float width, height;
	private boolean collected = false;
	
	public UserDataWrapper(Sprite sprite, PlatformType type, float width, float height){
		this.sprite = sprite;
		this.type = type;
		this.width = width;
		this.height = height;
	}

	public boolean isCollected() {
		return collected;
	}

	public void setCollected(boolean collected) {
		this.collected = collected;
	}
	
}
