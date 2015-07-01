package com.betto.procedit.screens;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
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

public class BestScoresScreen implements Screen {

	private Stage stage;
	private Skin skin;
	private TextureAtlas atlas;
	private TweenManager tweenManager;

	private Table table;
	private List<Float> scoresList;

	private String username;
	
	public BestScoresScreen(String uname) {

		this.username = uname;
		
		scoresList = new ArrayList<Float>();
		File scoresFile = new File(Gdx.files.getLocalStoragePath() + "scores_" + username + ".txt");
		BufferedReader br = null;
		try {	
			
			if(!scoresFile.exists()){
				System.out.println(this.getClass().getName() + " || The file has just been created");
				scoresFile.createNewFile();
			}
			else 
				System.out.println(this.getClass().getName() + " || Everyting is good, this freaking thing exists");

			br = new BufferedReader(new FileReader(scoresFile));
			String text = "";

			while ((text = br.readLine()) != null){
				scoresList.add(Float.parseFloat(text));
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (br != null){
					br.close();
				}
			} catch(IOException e){
				e.printStackTrace();
			}
		}
		Collections.sort(scoresList);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		//		table.debug();
		stage.act(delta);
		stage.draw();

		tweenManager.update(delta);
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, false);
		table.invalidateHierarchy();

		//this calls the table layout to be recalculated
		table.setSize(width, height);
	}

	@Override
	public void show() {

		atlas = new TextureAtlas("ui/uiskin.atlas");
		skin = new Skin(Gdx.files.internal("ui/uiskin.json"), atlas);

		stage = new Stage();
		Gdx.input.setInputProcessor(stage);

		table = new Table(skin);
		table.setFillParent(true);

		TextButton backButton = new TextButton("Back", skin);

		backButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				((Game) Gdx.app.getApplicationListener()).setScreen(new MainScreen(username));
			}
		});
		backButton.pad(10);

		Label topScoresLabel = new Label("Your Top Scores", skin, "big");
		topScoresLabel.setFontScale(0.9f);
		topScoresLabel.setAlignment(Align.center);
		topScoresLabel.setColor(Color.ORANGE);

		List<Label> topScoresListLabel = new ArrayList<Label>(10);
		for(Float score : scoresList){
			Label scoreLabel = new Label( "> " + String.valueOf(score), skin, "big");
			scoreLabel.setFontScale(0.6f);
			topScoresListLabel.add(scoreLabel);
		}

		table.add(topScoresLabel).width(Gdx.graphics.getWidth() - 200).top().spaceBottom(25).row();
		int i = 0;
		while(!topScoresListLabel.isEmpty() && i < 10){
			Label tempLabel = topScoresListLabel.remove(topScoresListLabel.size() - 1);
			table.add(tempLabel).row();
			i++;
		}
		table.add(backButton).bottom().right();


		stage.addActor(table);

		stage.addAction(sequence(moveTo(0, stage.getHeight()), moveTo(0, 0, .5f))); //coming in from top animation

		tweenManager = new TweenManager();
		Tween.registerAccessor(Actor.class, new ActorAccessor());

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
		skin.dispose();
		atlas.dispose();
	}

}
