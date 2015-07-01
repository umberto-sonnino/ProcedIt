package com.betto.procedit.screens;


import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;
import it.uniroma1.lcl.saga.api.DataType;
import it.uniroma1.lcl.saga.api.ImageAnnotationData;
import it.uniroma1.lcl.saga.api.MainThreadCallback;
import it.uniroma1.lcl.saga.api.SaGaConnector;
import it.uniroma1.lcl.saga.api.exceptions.AuthenticationRequiredException;

import java.text.DecimalFormat;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
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
import com.betto.procedit.tween.ActorAccessor;

public class DeathScreen implements Screen {

	private final int DATA_SIZE = 10;

	private Stage stage;
	private Table table;
	private Skin skin;
	private TweenManager tweenManager;
	private Sound soundFinal;
	private ImageAnnotationData imageData;
	private float score;

	private TextButton playButton;

	private String username;

	public DeathScreen(float playerScore, String uname) {
		this.score = playerScore;
		this.username = uname;
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0,0,0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		stage.act(delta);
		stage.draw();

//		table.debug();
		
		tweenManager.update(delta);
	}

	@Override
	public void resize(int width, int height) {
		Gdx.graphics.setDisplayMode((int) (1200 / 1.5f), 780,  false);
		stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
		table.invalidateHierarchy();
	}

	@Override
	public void show() {

		if(Gdx.app.getType() == ApplicationType.Desktop)
			Gdx.graphics.setDisplayMode((int) (1200 / 1.5f), 780, false);

		try {
			SaGaConnector.getInstance().getImageData(DataType.IMAGE, DATA_SIZE, new MainThreadCallback<ImageAnnotationData>() {

				@Override
				public void onSuccessInMainThread(ImageAnnotationData result) {
					System.out.println(this.getClass().getName() + "|| GOTTEN IMAGE DATA " + result.getLemma());
					imageData = result;
					playButton.setVisible(true);
				}

				@Override
				public void onFailureInMainThread(Throwable cause) {
					System.out.println(this.getClass().getName() + "|| FAILED GETTING IMAGE DATA");
					((Game)Gdx.app.getApplicationListener()).setScreen(new MainScreen(username));
				}
			});
		} catch (AuthenticationRequiredException e) {
			e.printStackTrace();
			System.out.println(this.getClass().getName() + "|| ******* UTENTE NON AUTENTICATO *******");
			Game game = (Game) Gdx.app.getApplicationListener();
			game.setScreen(new LoginScreen(game));
		}

		stage = new Stage();

		Gdx.input.setInputProcessor(stage);

		TextureAtlas atlas = new TextureAtlas("ui/uiskin.atlas");
		skin = new Skin(Gdx.files.internal("ui/uiskin.json"), atlas);

		table = new Table(skin);
		table.setFillParent(true);

		soundFinal = Gdx.audio.newSound(Gdx.files.internal("audio/failed.wav"));
		soundFinal.play();


		playButton = new TextButton("Wanna Try Again?", skin);
		playButton.setVisible(false);
		playButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				LoadingScreen lScreen = new LoadingScreen(imageData, username);
				((Game) Gdx.app.getApplicationListener()).setScreen(lScreen);
			}
		});
		playButton.pad(15);

		//Back Button
		TextButton back = new TextButton("Main Menu", skin);
		back.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				Timeline.createParallel().beginParallel()
				.push(Tween.to(table, ActorAccessor.ALPHA, .75f).target(0))
				.push(Tween.to(table, ActorAccessor.Y, .75f).target(table.getY() - 50)
						.setCallback(new TweenCallback() {

							@Override
							public void onEvent(int type, BaseTween<?> source) {
								((Game) Gdx.app.getApplicationListener()).setScreen(new MainScreen(username));
							}
						}))
						.end().start(tweenManager);
			}
		});
		back.pad(10);

		// creating animations
		tweenManager = new TweenManager();
		Tween.registerAccessor(Actor.class, new ActorAccessor());

		Label dyingLabel = new Label("Dying is not fun", skin, "big");
		dyingLabel.setColor(Color.ORANGE);

		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(1);
		
		Label scoreLabel = new Label ("At least you scored " + df.format(score) + " points", skin, "normal");
		Label gratefulLabel = new Label("Our glorious leaders are pleased, your death will not be in vain!", skin, "small");
		Label uselessLabel = new Label("Too bad, looks like your life has been just a waste...", skin, "small");
		
		gratefulLabel.setAlignment(Align.center);;
		uselessLabel.setAlignment(Align.center);;
		scoreLabel.setAlignment(Align.center);;
		
		gratefulLabel.setWrap(true);
		uselessLabel.setWrap(true);
		
		table.add(dyingLabel).spaceBottom(75).row();
		table.add(scoreLabel).width(Gdx.graphics.getWidth() - 200).spaceBottom(10).row();
		if(score >= 10 )
			table.add(gratefulLabel).width(Gdx.graphics.getWidth() - 200).spaceBottom(50).row();
		else
			table.add(uselessLabel).width(Gdx.graphics.getWidth() - 200).spaceBottom(50).row();
		table.add(playButton).width(250).height(100).uniformX().spaceBottom(50).row();
		
		table.add(back);

		stage.addActor(table);

		stage.addAction(sequence(moveTo(0, stage.getHeight()), moveTo(0, 0, .5f))); //coming in from top animation

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
		skin.dispose();
		soundFinal.dispose();
	}

	public ImageAnnotationData getImageData() {
		return imageData;
	}

	public void setImageData(ImageAnnotationData imageData) {
		this.imageData = imageData;
	}

}
