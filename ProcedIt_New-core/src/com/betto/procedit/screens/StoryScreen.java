package com.betto.procedit.screens;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
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
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.betto.procedit.controller.InputController;
import com.betto.procedit.tween.ActorAccessor;

public class StoryScreen implements Screen {

	private Stage stage;
	private Skin skin;
	private TextureAtlas atlas;
	private TweenManager tweenManager;

	private int clickNumber = 0;

	private Table table;
	private Table table2;
	private Table table3;
	private Table table1;

	private final String username;
	
	public StoryScreen(String username) {
		this.username = username;
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
		
		final Image imageButtonA;
		final Image imageButtonD;
		final Image imageButtonSPACE;
		final Image imageCoin;
		final Image imageCoins;
		final Image imageTome;

		atlas = new TextureAtlas("ui/atlas.pack");
		skin = new Skin(Gdx.files.internal("ui/menuSkin.json"), atlas);

		imageButtonA = new Image(new Texture(Gdx.files.internal("ui/keyA.png")));
		imageButtonD = new Image(new Texture(Gdx.files.internal("ui/keyD.png")));
		imageCoins = new Image(new Texture(Gdx.files.internal("ui/coins.png")));
		imageTome = new Image(new Texture(Gdx.files.internal("ui/tome.png")));

		imageCoins.setColor(1,1,1,0);
		imageCoins.setSize(30, 30);
		
		imageTome.setColor(1,1,1,0);
		imageTome.setSize(30,30);

		stage = new Stage();

		Gdx.input.setInputProcessor(stage);

		TextureAtlas atlas = new TextureAtlas("ui/uiskin.atlas");
		skin = new Skin(Gdx.files.internal("ui/uiskin.json"), atlas);

		table = new Table(skin);
		table1 = new Table(skin);
		table2 = new Table(skin);
		table3 = new Table(skin);

		table.setFillParent(true);

		//First Scene
		final Label introLabel = new Label("Our glorious alien race has to find new ways to survive\n and we need your help", skin, "normal");
		introLabel.setWrap(true);
		introLabel.setAlignment(Align.center);
		
		final Label pressLabel = new Label("Press any key", skin, "normal");

		//Second Scene
		final Label resourceLabel = new Label("There are only two things that we need from the earthlings...", skin, "normal");
		resourceLabel.setWrap(true);
		resourceLabel.setAlignment(Align.center);
		resourceLabel.setColor(1,1,1,0);

		final Label goldLabel = new Label("...gold and knowledge!", skin, "normal");
		goldLabel.setAlignment(Align.center);
		goldLabel.setColor(1,1,1,0);
		
		//Third Scene
		final Label collectLabel1 = new Label("Collect ", skin, "small");
		imageCoin = new Image(new Texture(Gdx.files.internal("ui/goldBonus.png")));
		final Label collectLabel2 = new Label(" to increase your score", skin, "small");
		
		final Label howGoldLabel = new Label("Gold can ALSO be used to your advantage to jump higher", skin, "small");
		
		final Label useLabel = new Label("Use ", skin, "small");
		final Label consumeLabel = new Label(" to consume gold...", skin, "small");
		imageButtonSPACE = new Image(new Texture(Gdx.files.internal("ui/keySpace2.png")));
		
		final Label rememberLabel = new Label("...but remember that you can keep a SINGLE piece of gold in your pocket at a time!", skin, "small");

		collectLabel1.setColor(1,1,1,0);
		imageCoin.setColor(1,1,1,0);
		collectLabel2.setColor(1,1,1,0);
		
		howGoldLabel.setColor(1,1,1,0);
		howGoldLabel.setAlignment(Align.center);
		rememberLabel.setAlignment(Align.center);
		
		useLabel.setColor(1,1,1,0);
		imageButtonSPACE.setColor(1,1,1,0);
		consumeLabel.setColor(1,1,1,0);

		rememberLabel.setColor(1,1,1,0);
		
		collectLabel2.setWrap(true);
		howGoldLabel.setWrap(true);
		consumeLabel.setWrap(true);
		rememberLabel.setWrap(true);
		
		//Fourth Scene
		final Label collectLabel3 = new Label("Collect Images to make LOTS of points", skin, "small");
		final Label moveLabel = new Label("to move left and right", skin, "small");
		final Label roundLabel = new Label("At each round, on the TOP LEFT corner of the screen, the current topic is displayed: ", skin, "small");
		final Label moreLabel = new Label("that's what we want to know more about", skin, "small");
		final Label shipLabel = new Label("When you collect Images, your personal transmitter will send them back to us on the mothership", skin, "small");
		final Label scanLabel = new Label("Over there, everything is scanned and compared with our database", skin, "small");
		final Label carefulLabel = new Label("BE CAREFUL! If you collect something we don't need, you might LOSE points!", skin, "small");
		
		collectLabel3.setColor(1,1,1,0);
		moveLabel.setColor(1,1,1,0);
		roundLabel.setColor(1,1,1,0);
		moreLabel.setColor(1,1,1,0);
		shipLabel.setColor(1,1,1,0);
		scanLabel.setColor(1,1,1,0);
		carefulLabel.setColor(1,1,1,0);
		
		imageButtonA.setColor(1,1,1,0);
		imageButtonD.setColor(1,1,1,0);
		
		collectLabel3.setAlignment(Align.center);
		roundLabel.setAlignment(Align.center);
		moreLabel.setAlignment(Align.center);
		shipLabel.setAlignment(Align.center);
		scanLabel.setAlignment(Align.center);
		carefulLabel.setAlignment(Align.center);
		
		collectLabel3.setWrap(true);
		roundLabel.setWrap(true);
		moreLabel.setWrap(true);
		shipLabel.setWrap(true);
		scanLabel.setWrap(true);
		carefulLabel.setWrap(true);

		TextButton back = new TextButton("Back", skin);
		back.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				((Game) Gdx.app.getApplicationListener()).setScreen(new MainScreen(username));
			}
		});
		back.pad(20);

		Gdx.input.setInputProcessor(stage);

		Gdx.input.setInputProcessor(new InputMultiplexer(new InputController() {
			@Override
			public boolean keyDown(int keycode) {
				switch(keycode){
				default:
					clickNumber++;
//					System.out.println(this.getClass().getName() + " || Pressed " + clickNumber + " times");
					switch (clickNumber) {
					case 1:
						
						Timeline.createSequence().beginSequence().
						push(Tween.to(introLabel, ActorAccessor.RGB, 0.75f).target(0)).end().start(tweenManager);
						table1.getCell(introLabel).setActor(resourceLabel).colspan(2).row();
						table1.add(imageCoins).spaceTop(30);
						table1.add(imageTome).spaceLeft(30).spaceTop(30).align(Align.bottom).row().width(Gdx.graphics.getWidth() - 200);
						table1.add(goldLabel).colspan(2).spaceTop(30);
						
						Timeline.createSequence().beginSequence().
						push(Tween.to(resourceLabel, ActorAccessor.ALPHA, 1.0f).target(1)).end().start(tweenManager);
						
						break;
					case 2:
						Timeline.createSequence().beginParallel().
							push(Tween.to(imageCoins, ActorAccessor.ALPHA, 0.5f).target(1)).
							push(Tween.to(imageTome, ActorAccessor.ALPHA, 0.5f).target(1)).
							end().start(tweenManager);
						break;
					case 3:
						Timeline.createSequence().beginSequence().
							push(Tween.to(goldLabel, ActorAccessor.ALPHA, 0.5f).target(1)).end().start(tweenManager);
						break;
					case 4:
						
						//Third Scene
						table1.clear();
						
						table1.add(collectLabel1).right();
						table1.add(imageCoin);
						table1.add(collectLabel2).left().width(200).row();
						
						table1.add(howGoldLabel).width(Gdx.graphics.getWidth() - 200).colspan(3).center().spaceBottom(10).row();
						
						table2.add(useLabel);
						table2.add(imageButtonSPACE).height(50).width(250).spaceRight(15).spaceLeft(15);
						table2.add(consumeLabel).width(250);
						table1.add(table2).width(Gdx.graphics.getWidth() - 200).colspan(3).left().row();
						
						
						table1.add(rememberLabel).colspan(3).width(Gdx.graphics.getWidth() - 200);

						Timeline.createSequence().beginParallel().
							push(Tween.to(collectLabel1, ActorAccessor.ALPHA, 0.5f).target(1)).
							push(Tween.to(imageCoin, ActorAccessor.ALPHA, 0.5f).target(1)).
							push(Tween.to(collectLabel2, ActorAccessor.ALPHA, 0.5f).target(1)).
							end().start(tweenManager);
						
						break;
					case 5:
						Timeline.createSequence().beginSequence().
							push(Tween.to(howGoldLabel, ActorAccessor.ALPHA, 1.0f).target(1)).end().start(tweenManager);
						break;
					case 6:
						Timeline.createSequence().beginParallel().
							push(Tween.to(useLabel, ActorAccessor.ALPHA, 0.5f).target(1)).
							push(Tween.to(imageButtonSPACE, ActorAccessor.ALPHA, 0.5f).target(1)).
							push(Tween.to(consumeLabel, ActorAccessor.ALPHA, 0.5f).target(1)).
							push(Tween.to(rememberLabel, ActorAccessor.ALPHA, 0.5f).target(1)).
							end().start(tweenManager);
						
						break;

					case 7:
						//Fourth Scene
						table1.clear();
						table2.clear();
						useLabel.setColor(1,1,1,0);
						
						table1.add(collectLabel3).width(Gdx.graphics.getWidth() - 200).spaceBottom(15).row();
						
						table2.add(useLabel).spaceRight(15);
						table2.add(imageButtonA).size(50).spaceRight(15);
						table2.add(imageButtonD).size(50).spaceRight(15);
						table2.add(moveLabel).left();
						
						table1.add(table2).spaceBottom(15).row();
						
						table1.add(roundLabel).width(Gdx.graphics.getWidth() - 200).spaceBottom(5).row();
						table1.add(moreLabel).width(Gdx.graphics.getWidth() - 200).spaceBottom(15).row();
						table1.add(shipLabel).width(Gdx.graphics.getWidth() - 200).spaceBottom(15).row();
						table1.add(scanLabel).width(Gdx.graphics.getWidth() - 200).spaceBottom(15).row();
						table1.add(carefulLabel).width(Gdx.graphics.getWidth() - 200).spaceBottom(15).row();
						
						Timeline.createSequence().beginParallel().
							push(Tween.to(collectLabel3, ActorAccessor.ALPHA, 0.5f).target(1)).end().start(tweenManager);
						
						break;
						
					case 8:
						
						Timeline.createSequence().beginParallel().
							push(Tween.to(useLabel, ActorAccessor.ALPHA, 0.5f).target(1)).
							push(Tween.to(imageButtonA, ActorAccessor.ALPHA, 0.5f).target(1)).
							push(Tween.to(imageButtonD, ActorAccessor.ALPHA, 0.5f).target(1)).
							push(Tween.to(moveLabel, ActorAccessor.ALPHA, 0.5f).target(1)).
							end().start(tweenManager);
						
						break;
					case 9:
						
						Timeline.createSequence().beginParallel().
							push(Tween.to(roundLabel, ActorAccessor.ALPHA, 0.5f).target(1)).
							push(Tween.to(moreLabel, ActorAccessor.ALPHA, 0.5f).target(1)).
							end().start(tweenManager);
						
						break;
					case 10:
						
						Timeline.createSequence().beginParallel().
							push(Tween.to(shipLabel, ActorAccessor.ALPHA, 0.5f).target(1)).
							push(Tween.to(scanLabel, ActorAccessor.ALPHA, 0.5f).target(1)).
							end().start(tweenManager);
						
						break;
					case 11:
						Timeline.createSequence().beginSequence().
							push(Tween.to(carefulLabel, ActorAccessor.ALPHA, 0.5f).target(1)).end().start(tweenManager);
						break;
					default:
						((Game)Gdx.app.getApplicationListener()).setScreen(new MainScreen(username));
						break;
					}
					break;
				}
				return false;
			}
		}, stage));

		pressLabel.setColor(0.3f, 0.85f, 0.3f, 1); //Alien Green =)
		
		table1.add(introLabel).width(Gdx.graphics.getWidth() - 200);

		table.add(table1).spaceBottom(30).row();

		table.add(pressLabel).spaceBottom(30).row();

		stage.addActor(table);

		stage.addAction(sequence(moveTo(0, stage.getHeight()), moveTo(0, 0, .5f))); //coming in from top animation

		tweenManager = new TweenManager();
		Tween.registerAccessor(Actor.class, new ActorAccessor());

		Timeline.createSequence().beginSequence()
			.push(Tween.to(pressLabel, ActorAccessor.ALPHA, 0.5f).target(1))
			.push(Tween.to(pressLabel, ActorAccessor.ALPHA, 0.75f).target(0))
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
		skin.dispose();
		atlas.dispose();
	}

}
