package com.betto.procedit.entities;

import it.uniroma1.lcl.saga.api.ImageAnnotationData;
import it.uniroma1.lcl.saga.api.MainThreadCallback;
import it.uniroma1.lcl.saga.api.SaGaConnector;
import it.uniroma1.lcl.saga.api.exceptions.AuthenticationRequiredException;

import java.util.HashSet;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactFilter;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.betto.procedit.UserDataWrapper;
import com.betto.procedit.entities.JumpingPlatform.PlatformType;
import com.betto.procedit.screens.PlayScreen;

public class Player extends InputAdapter implements ContactFilter, ContactListener {

	private Body body;
	private Fixture fixture;
	private PlayScreen screen;
	private World world;
	
	private ImageAnnotationData annotation;
	
	public final float WIDTH, HEIGHT;
	private Vector2 velocity = new Vector2();
	private float movementForce = 200, jumpPower = 45;
	private boolean dead = false;
	private int upgrades = 1;
//	private int lives = 3;
	private float score = 0;
	
	private HashSet<String> collectedKeys;
	
	private final float IMAGE_QUALITY_GOOD = 0.7f;
	private final float IMAGE_QUALITY_BAD = 0.3f;
	
	
	public Player(World world, float x, float y, float width, PlayScreen screen) {
		this.screen = screen;
		this.WIDTH = width*0.718f;
		this.world = world;
		HEIGHT = width;

		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(x, y);
		bodyDef.fixedRotation = true;

		PolygonShape shape = new PolygonShape();
		shape.setAsBox(width / 2, HEIGHT / 2);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.restitution = 0.3f;
		fixtureDef.friction = .8f;
		fixtureDef.density = 3.5f;

		body = world.createBody(bodyDef);
		fixture = body.createFixture(fixtureDef);
		
		collectedKeys = new HashSet<String>();
	}

	public void update() {
		body.applyForceToCenter(velocity, true);
	}

	@Override
	public boolean shouldCollide(Fixture fixtureA, Fixture fixtureB) {
		UserDataWrapper data = (UserDataWrapper) fixtureB.getUserData();
		PolygonShape platform = (PolygonShape) fixtureB.getShape();
		Vector2 vector1 = body.getWorldCenter(), vector2 = new Vector2();

		platform.getVertex(2, vector2);
		if(data.type.equals(PlatformType.UPGRADE)){
			if(data.isCollected()){
				return false;
			}
			return true;
		}
		else if(data.type.equals(PlatformType.CLOUD)){
			return false;
		}
		else if(data.type.equals(PlatformType.IMAGE)){
			if(data.isCollected())
				return false;
			return true;
		}
		else if(dead){
			return false;
		}
		else if(fixtureA == fixture || fixtureB == fixture){
			return vector2.y <= (vector1.y - (this.HEIGHT / 2));
		}
		return false;
	}

	@Override
	public void beginContact(Contact contact) {
		if(contact.getFixtureB().getUserData() instanceof UserDataWrapper){
			UserDataWrapper dataPlatform = (UserDataWrapper) contact.getFixtureB().getUserData();
			PlatformType type = dataPlatform.type;

			switch(type){
			case NORMAL:
				Sprite jumpSprite = new Sprite(new Texture(Gdx.files.internal("images/playerJump.png")));
				body.setUserData(jumpSprite);
				jumpSprite.setSize(WIDTH*1.5f, HEIGHT*1.5f);
				jumpSprite.setOrigin(jumpSprite.getWidth() / 2, jumpSprite.getHeight() / 2);
				screen.getSoundJump().play();
				break;
			case BOOST:
				Sprite boostSprite = new Sprite(new Texture(Gdx.files.internal("images/playerJump.png")));
				body.setUserData(boostSprite);
				boostSprite.setSize(WIDTH*1.5f, HEIGHT*1.5f);
				boostSprite.setOrigin(boostSprite.getWidth() / 2, boostSprite.getHeight() / 2);
				screen.getSoundBoost().play();
				break;
			case SPIKED:
				break;
			case UPGRADE:
				dataPlatform.sprite = null;
				dataPlatform.setCollected(true);
				moarUpgrades();
				score += 0.2;
				screen.getSoundCoin().play();
				break;
			case IMAGE:
				dataPlatform.sprite = null;
				dataPlatform.setCollected(true);
				break;
			case CLOUD:
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		if( contact.getFixtureB() != null && contact.getFixtureB().getUserData() instanceof UserDataWrapper){
			UserDataWrapper data = (UserDataWrapper) contact.getFixtureB().getUserData();
			PlatformType type = data.type;
			switch (type) {
			case NORMAL:
				body.setLinearVelocity(0, 13);
				break;
			case SPIKED:
				setDead();
				break;
			case BOOST:
				body.applyLinearImpulse(0, jumpPower * 2f, body.getWorldCenter().x, body.getWorldCenter().y, true);
				data.sprite = new Sprite(new Texture(Gdx.files.internal("ui/boostDown2.png")));
				data.sprite.setSize(data.width, data.height);
				data.sprite.setOrigin(data.sprite.getWidth() / 2, data.sprite.getHeight() / 2);
				break;
			case CLOUD: 
				screen.getSoundCloud().play();
				break;
			case UPGRADE:
				break;
			case IMAGE:
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void endContact(Contact contact) {
		if( contact.getFixtureB().getUserData() instanceof UserDataWrapper){

			UserDataWrapper dataPlatform = (UserDataWrapper) contact.getFixtureB().getUserData();
			PlatformType type = dataPlatform.type;

			switch(type){
			case NORMAL:
			case BOOST:
				Sprite jumpSprite = new Sprite(new Texture(Gdx.files.internal("images/playerFront.png")));
				body.setUserData(jumpSprite);
				jumpSprite.setSize(WIDTH*1.5f, HEIGHT*1.5f);
				jumpSprite.setOrigin(jumpSprite.getWidth() / 2, jumpSprite.getHeight() / 2);
				break;
			case CLOUD:
				break;
			case UPGRADE:
				break;
			case IMAGE:
				String key = ((ImageUserDataWrapper)dataPlatform).getKey();
				if(annotation != null){
					sendAnnotation(key, true);
				}
				else 
					System.out.println(this.getClass().getName() + "|| GUARDA CHE NON HAI SETTATO IMAGEANNOTATIONDATA");
//				System.out.println(this.getClass().getName() + "|| Ending Contact");
				break;
			case SPIKED:
				break;
			default:
				break;
			}
		}
	}

	public void sendAnnotation(final String key, final boolean answer) {
		try {
			SaGaConnector.getInstance().sendAnnotation(annotation, key, answer, new MainThreadCallback<Float>() {

				@Override
				public void onSuccessInMainThread(Float result) {
					collectedKeys.add(key);
					if(result >= IMAGE_QUALITY_GOOD){
						score += result;
					}
					else if( result < IMAGE_QUALITY_GOOD && result > IMAGE_QUALITY_BAD ){
						
						Random random = new Random();
						float dieRoll = random.nextFloat();
						System.out.println(this.getClass().getName() + "|| Annotated " + key + " successfully\n\tDie Roll: " + dieRoll);
						
						if( dieRoll < 0.5){
							if( (score - dieRoll) >= 0)
								score -= dieRoll;
							else score = 0;
						} else {
							score += dieRoll;
						}
					}
					else {
						if( (score - 1) <= 0)
							score = 0;
						else
							score--;
					}
					System.out.println(this.getClass().getName() + " || The result on key" + key + " was " + result + " with answer " + answer);
				}

				@Override
				public void onFailureInMainThread(Throwable cause) {
					cause.printStackTrace();
					
					System.out.println(this.getClass().getName() + "|| SOMETHING WENT WRONG!");
				}
			});
		} catch (AuthenticationRequiredException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean keyDown(int keycode) {
		switch(keycode) {
		case Keys.LEFT:
		case Keys.A:
			velocity.x = -movementForce;
			break;
		case Keys.RIGHT:
		case Keys.D:
			velocity.x = movementForce;
			break;
		case Keys.SPACE:
			if( getUpgrades() > 0){
				screen.getSoundWoosh().play();
				body.applyLinearImpulse(0, jumpPower / 1.5f, body.getWorldCenter().x, body.getWorldCenter().y, true);
				lessUpgrades();
			}
			break;
		default:
			return false;
		}
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		if(keycode == Keys.A || keycode == Keys.D || keycode == Keys.LEFT || keycode == Keys.RIGHT)
			while(velocity.x != 0)
				if(velocity.x > 0)
					(velocity.x)--;
				else if( velocity.x < 0)
					(velocity.x)++;
//			velocity.x = 0;
		else
			return false;
		return true;
	}

	public int getUpgrades() {
		return upgrades;
	}

	public void moarUpgrades() {
		if(upgrades < 1)
			this.upgrades++;
//		System.out.println(this.getClass().getName() + "|| NOW I HAVE " + this.upgrades + " upgrades");
	}

	public void lessUpgrades(){
		if(this.upgrades > 0)
			this.upgrades--;
	}

	public float getRestitution() {
		return fixture.getRestitution();
	}

	public void setRestitution(float restitution) {
		fixture.setRestitution(restitution);
	}

	public Body getBody() {
		return body;
	}

	public Vector2 getVelocity() {
		return velocity;
	}

	public void setVelocity(Vector2 velocity) {
		this.velocity = velocity;
	}

	public Fixture getFixture() {
		return fixture;
	}
	
	public boolean isDead() {
		return dead;
	}

	public void setDead(boolean dead) {
		this.dead = dead;
	}

	public void setDead(){
		Sprite hurtSprite = new Sprite(new Texture(Gdx.files.internal("images/playerHurt.png")));
		body.setUserData(hurtSprite);
		hurtSprite.setSize(WIDTH*1.5f, HEIGHT*1.5f);
		hurtSprite.setOrigin(hurtSprite.getWidth() / 2, hurtSprite.getHeight() / 2);
		
		screen.getSoundDie().play(0.5f);
		
		Gdx.input.setInputProcessor(null);
		
		this.dead = true;
		body.setLinearVelocity(0, 0);
		body.applyLinearImpulse(0, jumpPower/2.5f, body.getWorldCenter().x, body.getWorldCenter().y, true);
	}

	public ImageAnnotationData getAnnotation() {
		return annotation;
	}

	public void setAnnotation(ImageAnnotationData annotation) {
		this.annotation = annotation;
	}
/*
	public int getLives() {
		return lives;
	}

	public void setLives(int lives) {
		this.lives = lives;
		if(this.lives <= 0)
			setDead();
	}
	
	*/
	
	public HashSet<String> getCollectedKeys(){
		return collectedKeys;
	}

	public float getScore() {
		return score * 10;
	}
}
