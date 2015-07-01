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
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.betto.procedit.tween.ActorAccessor;

/*
 * This screen is reached when a player goes through all the possible downloaded Images
 * without collecting at least 10 of them. The game is thus complete but failed
 */

public class EndScreen implements Screen{
	
	private static final int DATA_SIZE = 10;
	private Stage stage;
	private Table table;
	private Skin skin;
	private Sound soundFinal;
	private ImageAnnotationData imageData;
	private TweenManager tweenManager;
	private TextButton play;
	private float score;
	private String username;
	
	public EndScreen(float score, String uname) {
		this.score = score;
		this.username = uname;
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0,0,0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		stage.act(delta);
		stage.draw();

		tweenManager.update(delta);
	}

	@Override
	public void resize(int width, int height) {
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
					play.setVisible(true);
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

		soundFinal = Gdx.audio.newSound(Gdx.files.internal("audio/success.wav"));
		soundFinal.play();

		
		play = new TextButton("Let me try again!", skin);
		play.setVisible(false);
		play.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				LoadingScreen lScreen = new LoadingScreen(imageData, username);
				((Game) Gdx.app.getApplicationListener()).setScreen(lScreen);
			}
		});
		play.pad(15);

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
		
		Label text = new Label("You contributed to our glorious alien race!", skin, "normal");
		text.setFontScale(0.7f);
		text.setColor(Color.GREEN);
		
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(1);
		
		Label scoreText = new Label( df.format(score) + " Points", skin, "big" );
		scoreText.setColor(Color.ORANGE);
		
		table.add(text).expandX().top().spaceBottom(75).row();
		table.add(scoreText).top().spaceBottom(75).row();
		table.add(play).width(250).height(100).uniformX().spaceBottom(50).row();
		table.add(back).uniformX();

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

}
