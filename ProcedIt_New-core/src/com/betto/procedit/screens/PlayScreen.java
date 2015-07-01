package com.betto.procedit.screens;

import it.uniroma1.lcl.saga.api.ImageAnnotationData;
import it.uniroma1.lcl.saga.api.SaGaConnector;
import it.uniroma1.lcl.saga.api.exceptions.AuthenticationRequiredException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.betto.procedit.LevelGenerator;
import com.betto.procedit.UserDataWrapper;
import com.betto.procedit.controller.InputController;
import com.betto.procedit.entities.ImagePlatform;
import com.betto.procedit.entities.JumpingPlatform;
import com.betto.procedit.entities.JumpingPlatform.PlatformType;
import com.betto.procedit.entities.Player;



public class PlayScreen implements Screen {

	private Game game = ((Game)Gdx.app.getApplicationListener());

	private World world;
	private Box2DDebugRenderer debugRenderer;
	private SpriteBatch batch;
	private OrthographicCamera camera;
	private final float TIMESTEP = 1 / 60f;
	private final int VELOCITYITERATIONS = 8 , POSITIONITERATIONS = 3;
	private Stage stage;
	private Skin skin;
	private Table table;
	private Sound  soundBoost, soundCloud, soundJump, soundDie, soundWoosh, soundCoin;
	private Music backgroundMusic;
	private Image coin1, lives0, lives1, lives2, lives3, transparent;

	private LevelGenerator levelGenerator;
	private Player player;
	private float playerScore;

	private Vector3 bottomLeft, bottomRight;

	private Sprite boxSprite;
	private Array<Body> tmpBodies = new Array<Body>();
	private Body ground;
	private Label lemmaLabel, collectedLabel;

	private ImageAnnotationData imageData;

	HashMap<String, Pixmap> annotationImages;
	Set<String> annotationKeys;
	ArrayList<ImagePlatform> imagePlatforms;

	boolean dead = false;

	private boolean finishedImages = false;

	private String username;

	public PlayScreen(ImageAnnotationData imageData, HashMap<String, Pixmap> annotationImages, String uname) {
		this.imageData = imageData;
		this.annotationImages = annotationImages;
		annotationKeys = imageData.getKeys();
		this.username = uname;
		this.imagePlatforms = new ArrayList<ImagePlatform>();
	}

	@Override
	public void render(float delta) {

		Gdx.gl.glClearColor(0f, 0.65f, 1f , 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		//		paintLives();
		paintCoin();
		paintLabel();

		stage.act(delta);
		stage.draw();

		if(player.getBody().getPosition().x < bottomLeft.x)
			player.getBody().setTransform( bottomRight.x ,player.getBody().getPosition().y, player.getBody().getAngle());
		else if(player.getBody().getPosition().x > bottomRight.x)
			player.getBody().setTransform(bottomLeft.x, player.getBody().getPosition().y, player.getBody().getAngle());

		player.update();
		world.step(TIMESTEP, VELOCITYITERATIONS, POSITIONITERATIONS);

		if(!finishedImages)
			camera.position.y = player.getBody().getPosition().y > camera.position.y ? player.getBody().getPosition().y : camera.position.y;
		camera.update();

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		world.getBodies(tmpBodies);

		for(Body body: tmpBodies)
			if(body.getUserData() != null && body.getUserData() instanceof Sprite) {
				Sprite sprite = (Sprite) body.getUserData();
				sprite.setPosition(body.getPosition().x - sprite.getWidth()/2, body.getPosition().y - sprite.getHeight() / 2);
				sprite.setRotation(body.getAngle()*MathUtils.radiansToDegrees);
				sprite.draw(batch);
			}
		for(Fixture fixture : ground.getFixtureList())
			if(fixture.getUserData() != null && fixture.getUserData() instanceof UserDataWrapper && fixture.getShape() instanceof PolygonShape){
				UserDataWrapper data = (UserDataWrapper) fixture.getUserData();
				if(data.sprite != null){
					Sprite sprite = data.sprite;
					PolygonShape temp = (PolygonShape) fixture.getShape();
					Vector2 position = new Vector2();
					temp.getVertex(0, position);
					sprite.setPosition(position.x, position.y);
					sprite.draw(batch);
				}
			}
		
		for(ImagePlatform image: imagePlatforms){
			if( image.getY() < camera.position.y - camera.viewportHeight/2 && !image.isBelowScreen() ){
//				System.out.println( image.getKey() + " has finished below the screen");
				image.setBelowScreen();
				if( !(player.getCollectedKeys().contains( image.getKey() )) ){
//					System.out.println("This image is below the screen and has not been collected");
					player.sendAnnotation(image.getKey(), false);
				}
			}
		}
		
		dead = player.isDead();

		batch.end();

		levelGenerator.generate(camera.position.y + camera.viewportHeight / 2);

		if(finishedImages){
			try {
				SaGaConnector.getInstance().sendEnd(imageData);
			} catch (AuthenticationRequiredException e) {
				e.printStackTrace();
			}
			player.getBody().setLinearVelocity(0, 15);
			//get a bonus for finishing all the images
			table.clear();
			table.add(new Label("FINISHED!", skin, "big")).center();
			if(player.getBody().getPosition().y > camera.position.y + camera.viewportHeight / 2){
				playerScore += 25;
				saveScore();
				((Game) Gdx.app.getApplicationListener()).setScreen(new EndScreen(playerScore, username));
			}
		}

		if(player.getBody().getPosition().y < camera.position.y - camera.viewportHeight / 2 && !finishedImages){
			try {
				SaGaConnector.getInstance().sendEnd(imageData);
			} catch (AuthenticationRequiredException e) {
				e.printStackTrace();
			}
			saveScore();
			DeathScreen fScreen = new DeathScreen(playerScore, username);
			((Game) Gdx.app.getApplicationListener()).setScreen(fScreen);
		}

	}

	private void saveScore() {
		//This method is used to save the score in private table
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(1);;
		String formattedScore = df.format(playerScore) + "\n";

		try {
			
			File scoreFile = new File(Gdx.files.getLocalStoragePath() + "scores_" + username + ".txt");
			
			if(!scoreFile.exists()){
				scoreFile.createNewFile();
				System.out.println("Just created the new file!");
			}
			
			FileWriter fw = new FileWriter(scoreFile.getName(), true);
			BufferedWriter bw = new BufferedWriter(fw);
			
			bw.write(formattedScore);
			bw.close();
			
			System.out.println(this.getClass().getName() + " || Just finished saving this score " + formattedScore);
			
		} catch (IOException e) {
			e.printStackTrace();
		}


	}

	private void paintLabel() {
		if(dead)
			collectedLabel.setText("DEAD!");
		if( playerScore != player.getScore() ){
			playerScore = player.getScore();
			DecimalFormat df = new DecimalFormat();
			df.setMaximumFractionDigits(1);
			collectedLabel.setText("Score: " + df.format(playerScore) );
		}
	}

	private void paintCoin() {
		if(player.getUpgrades() > 0 && table.getCell(transparent) != null){
			table.getCell(transparent).setActor(coin1);
		}else if( player.getUpgrades() < 1 && table.getCell(coin1) != null){
			table.getCell(coin1).setActor(transparent);
		}
	}

	/*
	 * 
	private void paintLives() {
		switch (player.getLives()) {
		case 0:
			if( table.getCell(lives1) != null ){
				Gdx.gl.glClearColor(1, 0, 0, 1);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
				System.out.println(this.getClass().getName() + "|| Just got 0 lives, I'm dead");
				player.setDead();
				table.getCell(lives1).setActor(lives0);
			}
			break;
		case 1:
			if( table.getCell(lives2) != null ){
				Gdx.gl.glClearColor(1, 0, 0, 1);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
				System.out.println(this.getClass().getName() + "|| Just lost 1 life");
				table.getCell(lives2).setActor(lives1);
			}
			break;
		case 2:
			if( table.getCell(lives3) != null ){
				System.out.println(this.getClass().getName() + "|| Just lost 1 life");
				Gdx.gl.glClearColor(1, 0, 0, 1);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
				table.getCell(lives3).setActor(lives2);
			}
			break;
		case 3:
			if( table.getCell(lives3) != null ){
			}
			break;
		}
	}

	 */

	@Override
	public void resize(int width, int height) {
		//25 b/c we are dividing by 25 in the camera in the constructor
		camera.viewportWidth = width/25;
		camera.viewportHeight = height / 25; 
		camera.update();
	}

	@Override
	public void show() {

		if(Gdx.app.getType() == ApplicationType.Desktop)
			Gdx.graphics.setDisplayMode((int) (Gdx.graphics.getHeight() / 1.5f), Gdx.graphics.getHeight(),  false);


		loadAssets();

		if(imageData != null){
			try {
				SaGaConnector.getInstance().sendStart(imageData);
				player.setAnnotation(imageData);
			} catch (AuthenticationRequiredException e) {
				e.printStackTrace();
				System.err.println("ERROR WITH AUTHENTICATION, GOING BACK TO LOGIN");
				game.setScreen(new LoginScreen(game));
			}
		}

		Gdx.input.setInputProcessor(new InputMultiplexer(new InputController() {
			@Override
			public boolean keyDown(int keycode) {
				switch (keycode) {
				case Keys.ESCAPE:
					((Game)Gdx.app.getApplicationListener()).setScreen(new MainScreen(username));
					break;
				}
				return false;
			}

			@Override
			public boolean scrolled(int amount) {
				camera.zoom += amount / 20f;

				return true;
			}
		}, player));

		boxSprite = new Sprite(new Texture(Gdx.files.internal("images/playerFront.png")));
		boxSprite.setSize(player.WIDTH*1.5f, player.HEIGHT*1.5f);
		boxSprite.setOrigin(boxSprite.getWidth() / 2, boxSprite.getHeight() / 2);
		player.getBody().setUserData(boxSprite);

		player.getBody().applyLinearImpulse(0, 75, player.getBody().getWorldCenter().x, player.getBody().getWorldCenter().y, true);

		BodyDef bodyDef = new BodyDef();
		FixtureDef fixtureDef = new FixtureDef();

		//GROUND
		bodyDef.type = BodyType.StaticBody;
		bodyDef.position.set(0,0);

		//create the Ground Shape
		ChainShape groundShape = new ChainShape();
		bottomLeft = new Vector3(0, Gdx.graphics.getHeight(), 0); 
		bottomRight = new Vector3(Gdx.graphics.getWidth(), bottomLeft.y, 0 );
		camera.unproject(bottomLeft);
		camera.unproject(bottomRight);

		groundShape.createChain(new float[]{ bottomLeft.x, bottomLeft.y, bottomRight.x, bottomRight.y });

		//create the fixture
		fixtureDef.shape = groundShape;
		fixtureDef.friction = .5f;
		fixtureDef.restitution = 0;

		ground = world.createBody(bodyDef);
		ground.createFixture(fixtureDef); 

		groundShape.dispose();

		levelGenerator = new LevelGenerator(ground, //environment
				bottomLeft.x, //left edge
				bottomRight.x, //right edge
				player.HEIGHT * 1.75f , //minGap
				player.HEIGHT * 3, //maxGap
				player.WIDTH * 1.5f, //minWidth
				player.WIDTH * 3.5f, //maxWidth 
				player.WIDTH / 3 * 2, //height
				this); //handle to the current screen
		
		new JumpingPlatform(bottomRight.x/2, levelGenerator.getHeight() * (1.7f), -2.6f, 11, levelGenerator.getPlatformSprite(), ground, PlatformType.NORMAL);
		
		lemmaLabel = new Label(imageData.getLemma().replace("_", " "), skin, "small");
		collectedLabel = new Label("Score: 0.0", skin, "small");

		table.top();
		table.add(lemmaLabel).expandX().left().top();
		//		table.add(lives3).right().width(100).height(30).pad(5);
		table.row().expandY();
		table.add(collectedLabel).bottom().left();
		table.add(coin1).bottom().right().pad(5).width(30).height(30);
		//		table.debug();
		stage.addActor(table);


	}

	private void loadAssets() {

		world = new World(new Vector2(0,-9.81f), true);
		debugRenderer = new Box2DDebugRenderer();
		batch = new SpriteBatch();
		stage = new Stage();
		skin = new Skin(Gdx.files.internal("ui/menuSkin.json"), new TextureAtlas("ui/atlas.pack"));

		table = new Table(skin);

		camera = new OrthographicCamera(Gdx.graphics.getWidth() / 25, Gdx.graphics.getHeight() / 25);

		table.setFillParent(true);

		player = new Player(world, 0, 0, 1, this);
		world.setContactFilter(player);
		world.setContactListener(player);

		coin1 = new Image(new Texture(Gdx.files.internal("ui/goldBonus.png")));
		transparent = new Image(new Texture(Gdx.files.internal("ui/transparent.png")));

		//		lives0 = new Image(new Texture(Gdx.files.internal("ui/lives0.png")));
		//		lives1 = new Image(new Texture(Gdx.files.internal("ui/lives1.png")));
		//		lives2 = new Image(new Texture(Gdx.files.internal("ui/lives2.png")));
		//		lives3 = new Image(new Texture(Gdx.files.internal("ui/lives3.png")));

		soundBoost = Gdx.audio.newSound(Gdx.files.internal("audio/jumpBoost2.wav"));
		soundCloud = Gdx.audio.newSound(Gdx.files.internal("audio/soundCloud.wav"));
		soundDie= Gdx.audio.newSound(Gdx.files.internal("audio/nooo.wav"));
		soundJump = Gdx.audio.newSound(Gdx.files.internal("audio/soundJump.wav"));
		soundWoosh = Gdx.audio.newSound(Gdx.files.internal("audio/soundWoosh2.mp3"));
		soundCoin = Gdx.audio.newSound(Gdx.files.internal("audio/soundCoin.wav"));
		backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/backgroundMusic.mp3"));
		backgroundMusic.setLooping(true);
		backgroundMusic.play();

	}

	public HashMap<String,Pixmap> getAnnotationImages() {
		return annotationImages;
	}


	public void setAnnotationImages(HashMap<String, Pixmap> annotationImages) {
		this.annotationImages = annotationImages;
	}

	@Override
	public void dispose() {
		world.dispose();
		debugRenderer.dispose();
		boxSprite.getTexture().dispose();
		stage.dispose();
		skin.dispose();
		disposeSounds();
	}

	private void disposeSounds() {
		soundBoost.dispose(); 
		soundCloud.dispose(); 
		soundJump.dispose(); 
		soundDie.dispose();
		soundWoosh.dispose();
		backgroundMusic.dispose();
	}


	@Override
	public void hide() {
		dispose();
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {
	}

	public ImageAnnotationData getImageData() {
		return imageData;
	}

	public void setImageData(ImageAnnotationData imageData) {
		this.imageData = imageData;
	}


	public Stage getStage() {
		return stage;
	}

	public Sound getSoundBoost() {
		return soundBoost;
	}

	public Sound getSoundJump() {
		return soundJump;
	}

	public Sound getSoundCloud() {
		return soundCloud;
	}

	public Sound getSoundDie() {
		return soundDie;
	}

	public Sound getSoundCoin() {
		return soundCoin;
	}

	public Sound getSoundWoosh() {
		return soundWoosh;
	}

	public void setSoundWoosh(Sound soundWoosh) {
		this.soundWoosh = soundWoosh;
	}

	public void noMoreImages() {
		this.finishedImages = true;
	}
	
	public void addImagePlatform(ImagePlatform image){
		this.imagePlatforms.add(image);
	}
}
