package com.betto.procedit.screens;

import it.uniroma1.lcl.saga.api.MainThreadCallbackVoid;
import it.uniroma1.lcl.saga.api.SaGaConnector;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class RegistrationScreen implements Screen {
	
	private Stage stage;
	private Skin skin;
	private Table table;
	private TextureAtlas atlas;
	private Game game;
	

	public RegistrationScreen(final Game game) {
		
		this.game = game;
		
		stage = new Stage();

		Gdx.input.setInputProcessor(stage);

		atlas = new TextureAtlas("ui/uiskin.atlas");
		skin = new Skin(Gdx.files.internal("ui/uiskin.json"), atlas);

		table = new Table(skin);
		table.setFillParent(true);

		//Heading
		Label heading = new Label("Choose Your Credentials", skin, "big");
		heading.setColor(Color.ORANGE);
		heading.setFontScale(.75f);

		//Login&Password Headings
		Label usernameLabel = new Label("Username", skin);
		Label passwordLabel = new Label("Password", skin);
		final Label retryLabel = new Label("", skin);

		usernameLabel.setColor(Color.LIGHT_GRAY);
		passwordLabel.setColor(Color.LIGHT_GRAY);
		retryLabel.setColor(Color.RED);
		
		//TextInputs
		final TextField usernameText = new TextField("", skin);
		usernameText.setMessageText("Enter your username");

		final TextField passwordText = new TextField("", skin);
		passwordText.setMessageText("Enter your password");
		
		//Buttons
		TextButton buttonRegister = new TextButton("Register", skin);
		buttonRegister.pad(10);
		
		TextButton buttonBack = new TextButton("Back", skin);
		buttonBack.pad(10);
		
		
		
		final Table registrationTable = new Table(skin);
		
		registrationTable.add(usernameLabel).padRight(40);
		registrationTable.add(usernameText).width(250).height(25);
		registrationTable.row().spaceTop(15);
		registrationTable.add(passwordLabel).padRight(40);
		registrationTable.add(passwordText).width(250).height(25);
		registrationTable.row().spaceTop(30);
		registrationTable.add(buttonRegister).spaceTop(30).colspan(2).spaceBottom(20).row();
		registrationTable.add(buttonBack).colspan(2).spaceBottom(20).row();
		registrationTable.add(retryLabel).colspan(2);

		table.add(heading);
		table.getCell(heading).spaceBottom(100);
		table.row();
		table.add(registrationTable);
		
		buttonRegister.addListener(new ClickListener(){
			
			@Override
			public void clicked(InputEvent event, float x, float y) {
				String uname = usernameText.getText();
				String pwd = passwordText.getText();
//				System.out.println(this.getClass().getName() + "|| Registering the current user " + uname + " with pass " + pwd);
				
				SaGaConnector.getInstance().register(uname, pwd, new MainThreadCallbackVoid() {
					
					@Override
					public void onSuccessInMainThread() {
						System.out.println(this.getClass().getName() + "|| Going back to the login");
						game.setScreen(new LoginScreen(game));
					}
					
					@Override
					public void onFailureInMainThread(Throwable cause) {
						System.out.println(this.getClass().getName() + "|| Didn't succeed \nPrinting Stack Trace: ");
						cause.printStackTrace();
						System.out.println("");
						retryLabel.setText(cause.getMessage());
					}
				});
			}
			
		}
				);
		
		buttonBack.addListener(new ClickListener(){
			
			@Override
			public void clicked(InputEvent event, float x, float y) {
					game.setScreen(new LoginScreen(game));
			}
			
		}
				);

		stage.addActor(table);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.16f, 0.24f, 0.31f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		stage.act(delta);
		stage.draw();
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

	}

	@Override
	public void hide() {

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
	}

}
