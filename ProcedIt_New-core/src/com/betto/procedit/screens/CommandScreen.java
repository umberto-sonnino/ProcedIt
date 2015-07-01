package com.betto.procedit.screens;


import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;
import it.uniroma1.lcl.saga.api.ImageAnnotationData;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.betto.procedit.tween.ActorAccessor;

public class CommandScreen implements Screen {

	private Stage stage;
	private Skin skin;
	private TextureAtlas atlas;
	private TweenManager tweenManager;

	private Table table;
	private Table table2;
	private Table table3;
	private Table table1;
	private ImageAnnotationData imageData;
	
	private String username;
	
	public CommandScreen(String uname) {
		this.username = uname;
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

//		table.debug();
//		table1.debug();
//		table2.debug();
//		table3.debug();
		stage.act(delta);
		stage.draw();
		
		tweenManager.update(delta);

	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, false);
		table.invalidateHierarchy();
		table1.invalidateHierarchy();
		table2.invalidateHierarchy();
		table3.invalidateHierarchy();

		//this calls the table layout to be recalculated
		table.setSize(width, height);
		table1.setSize(width, height);
		table2.setSize(width, height);
		table3.setSize(width, height);
	}

	@Override
	public void show() {
		Image buttonA, buttonD, buttonSPACE, coin;

		atlas = new TextureAtlas("ui/atlas.pack");
		skin = new Skin(Gdx.files.internal("ui/menuSkin.json"), atlas);

		buttonA = new Image(new Texture(Gdx.files.internal("ui/keyA.png")));
		buttonD = new Image(new Texture(Gdx.files.internal("ui/keyD.png")));
		buttonSPACE = new Image(new Texture(Gdx.files.internal("ui/keySpace.png")));
		coin = new Image(new Texture(Gdx.files.internal("ui/goldBonus.png")));

		stage = new Stage();

		Gdx.input.setInputProcessor(stage);

		TextureAtlas atlas = new TextureAtlas("ui/atlas.pack");
		skin = new Skin(Gdx.files.internal("ui/menuSkin.json"), atlas);

		table = new Table(skin);
		table1 = new Table(skin);
		table2 = new Table(skin);
		table3 = new Table(skin);

		table.setFillParent(true);

//		Label pressLabel = new Label("Press any key to play", skin);
		
		TextButton back = new TextButton("Back", skin);
		back.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				((Game) Gdx.app.getApplicationListener()).setScreen(new MainScreen(username));
			}
		});
		back.pad(10);

		Gdx.input.setInputProcessor(stage);
		
//		Gdx.input.setInputProcessor(new InputMultiplexer(new InputController() {
//			@Override
//			public boolean keyDown(int keycode) {
//				switch(keycode){
//				default:
//					PlayScreen pScreen = new PlayScreen();
//					pScreen.setImageData(imageData);
//					System.out.println(pScreen.getImageData());
//					String s = pScreen.getImageData().getLemma();
//					System.out.println(this.getClass().getName() + "|| " + s + " sono ancora vivo!");
//					((Game)Gdx.app.getApplicationListener()).setScreen(pScreen);;
//					break;
//				}
//				return false;
//			}
//		}, stage));

		table1.add(new Label("Use ", skin));
		table1.add(buttonA).size(50, 50).spaceRight(30);
		table1.add(buttonD).size(50, 50);
		table1.add(new Label(" to move laterally", skin));

		table2.add(new Label("Collect ", skin));
		table2.add(coin);
		table2.add(new Label(" to have jump boost", skin));

		table3.add(new Label("Use ", skin));
		table3.add(buttonSPACE).height(50).width(250);
		table3.add(new Label(" to use your boost", skin)).row();

		table.add(table1).spaceBottom(30).row();
		table.add(table2).spaceBottom(30).row();
		table.add(table3).spaceBottom(150).row();
//		table.add(pressLabel).spaceBottom(30).row();
		table.add(back).bottom().right();

		stage.addActor(table);

		stage.addAction(sequence(moveTo(0, stage.getHeight()), moveTo(0, 0, .5f))); //coming in from top animation
		
		tweenManager = new TweenManager();
		Tween.registerAccessor(Actor.class, new ActorAccessor());
		
//		Timeline.createSequence().beginSequence()
//		.push(Tween.to(pressLabel, ActorAccessor.RGB, 0.5f).target(1, 1, 1))
//		.push(Tween.to(pressLabel, ActorAccessor.RGB, 0.75f).target(0, 0, 0))
//		.end().repeat(Tween.INFINITY, 0).start(tweenManager);
		
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

	public void setImageData(ImageAnnotationData imageData) {
		this.imageData = imageData;
	}

	public ImageAnnotationData getImageData() {
		return imageData;
	}

}
