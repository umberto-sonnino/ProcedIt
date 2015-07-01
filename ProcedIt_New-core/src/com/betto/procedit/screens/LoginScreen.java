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
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;


public class LoginScreen implements Screen {

	private Stage stage;
	private Skin skin;
	private Table table;
	private TextureAtlas atlas;
	private Game game;
	
	
	public LoginScreen(final Game game) {
		
		System.out.println("LoginScreen is here");
		this.game = game;

		stage = new Stage();

		Gdx.input.setInputProcessor(stage);

		atlas = new TextureAtlas("ui/uiskin.atlas");
		skin = new Skin(Gdx.files.internal("ui/uiskin.json"), atlas);

		table = new Table(skin);
		table.setFillParent(true);

		//Heading
		Label heading = new Label("Please Login First", skin, "big");
		heading.setColor(Color.ORANGE);
		heading.setFontScale(.85f);

		//Login&Password Headings
		Label usernameLabel = new Label("Username", skin, "normal");
		usernameLabel.setFontScale(0.6f);
		Label passwordLabel = new Label("Password", skin, "normal");
		passwordLabel.setFontScale(0.6f);
		final Label causeLabel = new Label("", skin, "small");
		causeLabel.setColor(Color.RED);

		usernameLabel.setColor(Color.LIGHT_GRAY);
		passwordLabel.setColor(Color.LIGHT_GRAY);

		//TextInputs
		final TextField usernameText = new TextField("", skin);
		usernameText.setMessageText("Enter your username");
		
		final TextField passwordText = new TextField("", skin);
		passwordText.setMessageText("Enter your password");
		passwordText.setPasswordCharacter('*');
		passwordText.setPasswordMode(true);
		
		TextFieldListener enterListener = new TextFieldListener()
		{
			@Override
			public void keyTyped(TextField textField, char c) 
			{
				String pwd = passwordText.getText();
				final String uname = usernameText.getText();
				if( c == '\r' || c == '\n')
				{
					SaGaConnector.getInstance().login(uname, pwd, new MainThreadCallbackVoid() {
						
						@Override
						public void onSuccessInMainThread() {
							game.setScreen(new MainScreen(uname));
						}
						
						@Override
						public void onFailureInMainThread(Throwable cause) {
							System.out.println(this.getClass().getName() + "|| FAILED: " + cause.getMessage());
							causeLabel.setText(cause.getMessage());
						}
					});
				}
			}
			
		};
		
		
		usernameText.setTextFieldListener(enterListener);
		passwordText.setTextFieldListener(enterListener);


		//Buttons
		TextButton buttonLogin = new TextButton("Login", skin);
		buttonLogin.pad(7);

		TextButton buttonRegister = new TextButton("Not Registered?", skin);
		buttonRegister.pad(7);
		

		Table loginTable = new Table(skin);
		loginTable.add(usernameLabel).padRight(30);
		loginTable.add(usernameText).width(250).height(25);
		loginTable.row().spaceTop(15);
		loginTable.add(passwordLabel).padRight(30);
		loginTable.add(passwordText).width(250).height(25);
		loginTable.row().spaceTop(15).expandX();
		loginTable.add(causeLabel).colspan(2).row().spaceTop(15);
		loginTable.add(buttonLogin).colspan(2).spaceBottom(20).row();
		loginTable.add(buttonRegister).colspan(2);

		table.add(heading);
		table.getCell(heading).spaceBottom(100);
		table.row();
		table.add(loginTable);

		buttonLogin.addListener(new ClickListener(){

			@Override
			public void clicked(InputEvent event, float x, float y) {
				final String uname = usernameText.getText();
				String pwd = passwordText.getText();

				System.out.println(this.getClass().getName() + "|| Trying to login as -" + uname + "- with password *" + pwd + "*");
				
				SaGaConnector.getInstance().login(uname, pwd, new MainThreadCallbackVoid() {
					
					@Override
					public void onSuccessInMainThread() {
//						System.out.println(this.getClass().getName() + "|| SUCCESS!");
						game.setScreen(new MainScreen(uname));
					}
					
					@Override
					public void onFailureInMainThread(Throwable cause) {
						System.out.println(this.getClass().getName() + "|| FAILED: " + cause.getMessage());
						causeLabel.setText(cause.getMessage());
					}
				});
				
				
			}

		}
				);

		buttonRegister.addListener(
				new ClickListener(){
					@Override
					public void clicked(InputEvent event, float x, float y) {
						game.setScreen(new RegistrationScreen(game));
					}
				}
				);


		stage.addActor(table);

	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.16f, 0.24f, 0.31f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

//		table.debug();

		stage.act(delta);
		stage.draw();

		//		Table.drawDebug(stage);
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
