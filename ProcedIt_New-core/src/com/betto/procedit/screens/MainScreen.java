package com.betto.procedit.screens;

import it.uniroma1.lcl.saga.api.DataType;
import it.uniroma1.lcl.saga.api.ImageAnnotationData;
import it.uniroma1.lcl.saga.api.MainThreadCallback;
import it.uniroma1.lcl.saga.api.SaGaConnector;
import it.uniroma1.lcl.saga.api.exceptions.AuthenticationRequiredException;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.betto.procedit.controller.InputController;
import com.betto.procedit.tween.ActorAccessor;

public class MainScreen implements Screen {

	private Stage stage;
	private TextureAtlas atlas;
	private Skin skin;
	private Table table;
	private TweenManager tweenManager;
	private Sprite sprite;
	private Texture texture;
	private SpriteBatch spriteBatch;
	private ImageAnnotationData imageData;
	private final int DATA_SIZE = 10;
	private TextButton playButton;
	private Label loadingLabel;
	private String username;
	
	public MainScreen(String uname){
		this.username = uname;
//		System.out.println(this.getClass().getName() + " || Currently logged in as " + username);
	}
	
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.16f, 0.24f, 0.31f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		stage.act(delta);
		stage.draw();

		spriteBatch.begin();
		sprite.draw(spriteBatch);
		spriteBatch.end();

		tweenManager.update(delta);

	}

	@Override
	public void resize(int width, int height) {
		Gdx.graphics.setDisplayMode((int) (1200 / 1.5f), 780, false);
		stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		//this calls the table layout to be recalculated
		table.invalidateHierarchy();  
		table.setSize(width, height);
	}

	@Override
	public void show() {
		Gdx.graphics.setDisplayMode((int) (1200 / 1.5f), 780, false);
		imageData = null;
		try {
			SaGaConnector.getInstance().getImageData(DataType.IMAGE, DATA_SIZE, new MainThreadCallback<ImageAnnotationData>() {

				@Override
				public void onSuccessInMainThread(ImageAnnotationData result) {
//					System.out.println(this.getClass().getName() + "|| GOTTEN IMAGE DATA " + result.getLemma());
					imageData = result;
					table.getCell(loadingLabel).setActor(playButton);
				}

				@Override
				public void onFailureInMainThread(Throwable cause) {
					System.out.println(this.getClass().getName() + "|| FAILED GETTING IMAGE DATA");
					cause.printStackTrace();
					Game proceditGame = (Game)Gdx.app.getApplicationListener();
					//TODO add dialog in saying that session has expired
//					loadingLabel.setText("NO DATA!");
					proceditGame.setScreen(new LoginScreen(proceditGame));
				}
			});
		} catch (AuthenticationRequiredException e) {
			e.printStackTrace();
			System.out.println(this.getClass().getName() + "|| ******* UTENTE NON AUTENTICATO *******");
			Game game = (Game) Gdx.app.getApplicationListener();
			game.setScreen(new LoginScreen(game));
		}

		stage = new Stage();
		Gdx.input.setInputProcessor(new InputMultiplexer(new InputController() {
			@Override
			public boolean keyDown(int keycode) {
				switch (keycode) {
				case Keys.ESCAPE:
					Gdx.app.exit();
					break;
				}
				return false;
			}
		}, stage));
		atlas = new TextureAtlas("ui/uiskin.atlas");
		skin = new Skin(Gdx.files.internal("ui/uiskin.json"), atlas);

		table = new Table(skin);
		table.setFillParent(true);

		//Adding LOGO
		spriteBatch = new SpriteBatch();
		texture = new Texture(Gdx.files.internal("logo.png"));
		sprite = new Sprite(texture);
		sprite.setSize(448, 306);
		sprite.setPosition(Gdx.graphics.getWidth()/2 - sprite.getWidth()/2, Gdx.graphics.getHeight()/4 *3 - sprite.getHeight()/2);

		//create heading 
		Label heading = new Label("", skin, "big" );
		heading.setFontScale(2);

		//create Button(s)
		playButton = new TextButton("PLAY", skin);
		playButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				LoadingScreen lScreen = new LoadingScreen(imageData, username);
				((Game) Gdx.app.getApplicationListener()).setScreen(lScreen);
			}
		});
		
		TextButton buttonSettings = new TextButton("How to Play", skin);
		buttonSettings.pad(5);
		buttonSettings.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				((Game)Gdx.app.getApplicationListener()).setScreen(new StoryScreen(username));
			}
		});

		TextButton buttonExit = new TextButton("EXIT", skin);
		buttonExit.pad(5);
		buttonExit.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				Timeline.createParallel().beginParallel()
				.push(Tween.to(table, ActorAccessor.ALPHA, .75f).target(0))
				.push(Tween.to(table, ActorAccessor.Y, .75f).target(table.getY() - 50)
						.setCallback(new TweenCallback() {

							@Override
							public void onEvent(int type, BaseTween<?> source) {
								Gdx.app.exit();
							}
						}))
						.end().start(tweenManager);
			}
		});

		TextButton buttonScores = new TextButton("Best Scores", skin);
		buttonScores.addListener(new ClickListener(){

			@Override
			public void clicked(InputEvent event, float x, float y) {
				Timeline.createParallel().beginParallel()
				.push(Tween.to(table, ActorAccessor.ALPHA, .75f).target(0))
				.push(Tween.to(table, ActorAccessor.Y, .75f).target(table.getY() - 50)
						.setCallback(new TweenCallback() {

							@Override
							public void onEvent(int type, BaseTween<?> source) {
								((Game)Gdx.app.getApplicationListener()).setScreen(new BestScoresScreen(username));
							}
						}))
						.end().start(tweenManager);
			}
		});
		
		loadingLabel = new Label("Loading Data", skin);
		loadingLabel.setAlignment(Align.center);

		//		putting stuff together
		table.add(heading);
		table.getCell(heading).spaceBottom(200);
		table.row();
		table.add(loadingLabel).width(150).height(80).fillX();
		table.getCell(loadingLabel).spaceBottom(15);
		table.row();
		table.add(buttonSettings).height(30).width(120);
		table.getCell(buttonSettings).spaceBottom(15);
		table.row();
		table.add(buttonScores).height(30).width(120);
		table.getCell(buttonScores).spaceBottom(15);
		table.row();
		table.add(buttonExit).width(120);
		stage.addActor(table);


		//		creating animations
		tweenManager = new TweenManager();
		Tween.registerAccessor(Actor.class, new ActorAccessor());

		//		heading and button fade in 
		Timeline.createSequence().beginSequence()
		.push(Tween.set(playButton, ActorAccessor.ALPHA).target(0))
		.push(Tween.set(buttonSettings, ActorAccessor.ALPHA).target(0))
		.push(Tween.set(buttonScores, ActorAccessor.ALPHA).target(0))
		.push(Tween.set(buttonExit, ActorAccessor.ALPHA).target(0))
		.push(Tween.from(heading, ActorAccessor.ALPHA, 0.25f).target(0))
		.push(Tween.to(playButton, ActorAccessor.ALPHA, 0.25f).target(1))
		.push(Tween.to(buttonSettings, ActorAccessor.ALPHA, 0.25f).target(1))
		.push(Tween.to(buttonScores, ActorAccessor.ALPHA, 0.25f).target(1))
		.push(Tween.to(buttonExit, ActorAccessor.ALPHA, 0.25f ).target(1))
		.end().start(tweenManager); 


		//table fade-in
		Tween.from(table, ActorAccessor.ALPHA, .5f).target(0).start(tweenManager);
		Tween.from(table, ActorAccessor.Y , .5f).target(Gdx.graphics.getHeight() / 8).start(tweenManager);
		
		Timeline.createSequence().beginSequence()
		.push(Tween.to(loadingLabel, ActorAccessor.ALPHA, 0.5f).target(1))
		.push(Tween.to(loadingLabel, ActorAccessor.ALPHA, 0.5f).target(0))
		.end().repeat(Tween.INFINITY, 0).start(tweenManager);

		tweenManager.update(Gdx.graphics.getDeltaTime());

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

	@Override
	public void dispose() {
		stage.dispose();
		atlas.dispose();
		skin.dispose();
		spriteBatch.dispose();
		texture.dispose();
	}

}
