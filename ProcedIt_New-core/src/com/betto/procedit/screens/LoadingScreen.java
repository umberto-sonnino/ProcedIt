package com.betto.procedit.screens;

import it.uniroma1.lcl.saga.api.ImageAnnotationData;
import it.uniroma1.lcl.saga.api.MainThreadCallback;

import java.util.HashMap;
import java.util.Set;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
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

public class LoadingScreen implements Screen {

	private Stage stage;
	private Skin skin;
	private TextureAtlas atlas;
	private TweenManager tweenManager;
	
	private ImageAnnotationData imageData;
	private String lemma, gloss;
	private Table table;
	private TextButton playButton;

	private HashMap<String, Pixmap> annotationImages;
	private Label loadingLabel;
	
	private String username;
	
	public LoadingScreen(ImageAnnotationData gottenData, String uname) {

		this.username = uname;
		
		atlas = new TextureAtlas("ui/uiskin.atlas");
		skin = new Skin(Gdx.files.internal("ui/uiskin.json"), atlas);
		
		tweenManager = new TweenManager();
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);

		this.imageData = gottenData;
		this.lemma = gottenData.getLemma().replace("_", " ");
		this.gloss = gottenData.getGloss();
		
		loadAnnotationImages();
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		stage.act(delta);
		stage.draw();
//		table.debug();
		
		tweenManager.update(delta);
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, false);

	}

	@Override
	public void show() {
		/*
		 * Vediamo di cosa abbiamo bisogno:
		 * Schermata molto semplice con tabella che mostra Lemma, Gloss 
		 * Quindi Label x 2
		 * 
		 * Dopodiche' ci serve un tasto Play
		 * Che deve apparire solo alla fine del caricamento
		 */
		
		table = new Table(skin);
		table.setFillParent(true);
		
		Label lemmaLabel = new Label(lemma, skin, "big");
		Label glossLabel = new Label(gloss, skin, "normal");
		playButton = new TextButton("PLAY!", skin);
		playButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				((Game) Gdx.app.getApplicationListener()).setScreen(new PlayScreen(imageData, annotationImages, username));
			}
		
		});
		
		float currentWidth = Gdx.graphics.getWidth();
		
		loadingLabel = new Label("Loading Data", skin);
		loadingLabel.setAlignment(Align.center);
		
		lemmaLabel.setWrap(true);
		lemmaLabel.setAlignment(Align.center);
//		lemmaLabel.setFontScale(0.7f);
		
		glossLabel.setWrap(true);
		glossLabel.setWidth(currentWidth - 50);
		glossLabel.setAlignment(Align.center);
		
		table.add(lemmaLabel).width(currentWidth - 50).spaceBottom(50).row();
		table.add(glossLabel).width(currentWidth - 50).row();
		table.add(loadingLabel).height(50).spaceTop(30).right();
		
		stage.addActor(table);

//		stage.addAction(sequence(moveTo(0, stage.getHeight()), moveTo(0, 0, .5f)));
		
		Tween.registerAccessor(Actor.class, new ActorAccessor());
		
		Timeline.createSequence().beginSequence()
		.push(Tween.to(playButton, ActorAccessor.ALPHA, 0.5f).target(1))
		.end().start(tweenManager);
		
		Timeline.createSequence().beginSequence()
		.push(Tween.to(loadingLabel, ActorAccessor.ALPHA, 0.5f).target(1))
		.push(Tween.to(loadingLabel, ActorAccessor.ALPHA, 0.5f).target(0))
		.end().repeat(Tween.INFINITY, 0).start(tweenManager);
		
	}

	private void loadAnnotationImages(){
	
			annotationImages = new HashMap<String, Pixmap>();
	
			Set<String> keySet = imageData.getKeys();
//			System.out.println("KeySet size is: " + keySet.size());
	
			for(final String key: keySet){
				imageData.getImageFromKey(key, new MainThreadCallback<Pixmap>() {
	
					@Override
					public void onSuccessInMainThread(Pixmap result) {
						annotationImages.put(key, result);
						if(annotationImages.size() == imageData.getKeys().size() ){
							table.getCell(loadingLabel).setActor(playButton);
						}
					}
	
					@Override
					public void onFailureInMainThread(Throwable cause) {
						System.out.println(this.getClass().getName() + "|| ERRORE NEL PRENDERE LE IMMAGINI DALLA CHIAVE");
						cause.printStackTrace();
					}
				});
			}
			
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
		atlas.dispose();
	}

}
