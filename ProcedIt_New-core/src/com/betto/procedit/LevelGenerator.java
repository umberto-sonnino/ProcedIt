package com.betto.procedit;

import it.uniroma1.lcl.saga.api.ImageAnnotationData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.betto.procedit.entities.CoinPlatform;
import com.betto.procedit.entities.ImagePlatform;
import com.betto.procedit.entities.JumpingPlatform;
import com.betto.procedit.entities.JumpingPlatform.PlatformType;
import com.betto.procedit.screens.PlayScreen;

public class LevelGenerator {

	private Body environment;
	private float leftEdge, rightEdge, minGap, maxGap, minWidth, maxWidth, height, y;
	Sprite platformSprite;
	private ImageAnnotationData imageData;
	PlayScreen playScreen;
	private boolean wasImage = false;
	private float lastImageHeight;
	
	private final float MAX_IMAGE_WIDTH, MAX_IMAGE_HEIGHT;
	

	public LevelGenerator(Body environment, float leftEdge, float rightEdge,
			float minGap, float maxGap, float minWidth, float maxWidth,
			float height, PlayScreen playScreen) {
		super();
		this.environment = environment;
		this.leftEdge = leftEdge;
		this.rightEdge = rightEdge;
		this.minGap = minGap;
		this.maxGap = maxGap;
		this.minWidth = minWidth;
		this.maxWidth = maxWidth;
		this.height = height;
		this.playScreen = playScreen;
		
		MAX_IMAGE_HEIGHT = MAX_IMAGE_WIDTH = maxWidth * 2;
		
//		System.out.println(MAX_IMAGE_WIDTH + " && " + MAX_IMAGE_HEIGHT);
	}

	public Sprite getPlatformSprite() {
		return platformSprite;
	}

	public void generate(float topEdge){
		
		if(wasImage && y+lastImageHeight > topEdge )
			return;
		if(y + MathUtils.random(minGap, maxGap) > topEdge )
			return;
		
		y = topEdge;
		wasImage = false;
		float width = MathUtils.random( minWidth, maxWidth );
		float x = MathUtils.random( leftEdge, rightEdge - width );
		//Throw a dice to know which is the next platform
		Random random = new Random();
		int dice = random.nextInt(23) + 1;
		float medianSize = (maxWidth + minWidth) / 2.5f;


		switch (dice) {
		case 10:
		case 11:
			if(width <= maxWidth / 2)
				width = maxWidth / 1.5f;
			new JumpingPlatform(width, medianSize, x, y, platformSprite, environment, PlatformType.CLOUD);
			break;
		case 12:
			new JumpingPlatform(medianSize, medianSize, x, y, platformSprite, environment, PlatformType.BOOST);
			break;
		case 13:
		case 14:
			new CoinPlatform( 1.2f, 1.2f, x, y, platformSprite, environment);
			break;
		case 15:
		case 16:
			//TODO this whole thing in the constructor 
			HashMap<String, Pixmap> map = playScreen.getAnnotationImages();
			
			Random rnd = new Random();
			List<String> keysAsArray = new ArrayList<String>(map.keySet());
			int randomIndex = rnd.nextInt(keysAsArray.size());
			String randomKey = keysAsArray.get(randomIndex);
			Pixmap randomPixmap = map.remove(randomKey);

			Texture texture = new Texture(randomPixmap);

			System.out.println(this.getClass().getName() + "|| Textures left: " + map.size());

			if(map.isEmpty()){
				System.out.println("Goodbye");
				playScreen.noMoreImages();
				break;
			}
			
			playScreen.setAnnotationImages(map);
			
			float imageWidth = randomPixmap.getWidth();
			float imageHeight = randomPixmap.getHeight();
			while(imageWidth > MAX_IMAGE_WIDTH || imageHeight > MAX_IMAGE_HEIGHT){
				imageWidth *= 0.9;
				imageHeight *= 0.9;
			}

			y = topEdge + (imageHeight/2);

			float newX = MathUtils.random( leftEdge, rightEdge - imageWidth );

			ImagePlatform temp = new ImagePlatform(imageWidth, imageHeight, newX, y, platformSprite, environment, texture, randomKey );
			playScreen.addImagePlatform(temp);
			
			wasImage = true;
			lastImageHeight = imageHeight;
			
			break;
		case 17:
			new JumpingPlatform(medianSize * 1.5f, height, x, y , platformSprite, environment, PlatformType.SPIKED);
			break;

		default:
		{
			new JumpingPlatform(width, height * 2 , x , y, platformSprite, environment, PlatformType.NORMAL);
			break;
		}
		}
	}

	public Body getEnvironment() {
		return environment;
	}

	public void setEnvironment(Body environment) {
		this.environment = environment;
	}

	public float getLeftEdge() {
		return leftEdge;
	}

	public void setLeftEdge(float leftEdge) {
		this.leftEdge = leftEdge;
	}

	public float getRightEdge() {
		return rightEdge;
	}

	public void setRightEdge(float rightEdge) {
		this.rightEdge = rightEdge;
	}

	public float getMinGap() {
		return minGap;
	}

	public void setMinGap(float minGap) {
		this.minGap = minGap;
	}

	public float getMaxGap() {
		return maxGap;
	}

	public void setMaxGap(float maxGap) {
		this.maxGap = maxGap;
	}

	public float getMinWidth() {
		return minWidth;
	}

	public void setMinWidth(float minWidth) {
		this.minWidth = minWidth;
	}

	public float getMaxWidth() {
		return maxWidth;
	}

	public void setMaxWidth(float maxWidth) {
		this.maxWidth = maxWidth;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public ImageAnnotationData getImageData() {
		return imageData;
	}

	public void setImageData(ImageAnnotationData imageData) {
		this.imageData = imageData;
	}



}
