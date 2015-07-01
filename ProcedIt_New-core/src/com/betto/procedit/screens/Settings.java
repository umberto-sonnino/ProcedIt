package com.betto.procedit.screens;



import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.betto.procedit.Procedit;

public class Settings implements Screen{

	private Stage stage;
	private Table table;
	private Skin skin;
	public String username;
	
	public Settings(String uname){
		this.username = uname;
	}
	
	public static FileHandle levelDirectory(){
		String prefsDir = Gdx.app.getPreferences(Procedit.TITLE).getString("leveldirectory").trim();	
		if(prefsDir != null && !(prefsDir==""))
			return Gdx.files.absolute(prefsDir);
		else
			return Gdx.files.absolute((Gdx.files.external(Procedit.TITLE+"/levels").file().getAbsolutePath()));
	}
	
	public static boolean vSync(){
		return Gdx.app.getPreferences(Procedit.TITLE).getBoolean("vsync");
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		stage.act(delta);
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, false);
		table.invalidateHierarchy();
	}

	@Override
	public void show() {
		stage = new Stage();
		
		Gdx.input.setInputProcessor(stage);
		
		skin = new Skin(Gdx.files.internal("ui/menuSkin.json"), new TextureAtlas("ui/atlas.pack"));
		
		table = new Table(skin);
		table.setFillParent(true);
		
		final CheckBox vSyncCheckBox = new CheckBox("vSync", skin);
		vSyncCheckBox.setChecked(Gdx.app.getPreferences(Procedit.TITLE).getBoolean("vsync"));
		
		final TextField levelDirectoryInput = new TextField(levelDirectory().path(), skin);
		levelDirectoryInput.setMessageText("level directory");
		
		final TextButton back = new TextButton("BACK", skin, "big");
		back.pad(10);
		
		ClickListener buttonHandler = new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(event.getListenerActor() == vSyncCheckBox) {
					//save vSync
					Gdx.app.getPreferences(Procedit.TITLE).putBoolean("vsync", vSyncCheckBox.isChecked());
					
					//set vSync
					Gdx.graphics.setVSync(vSync());
					
					Gdx.app.log(Procedit.TITLE, "vSync " + (vSync() ? "enabled" : "disabled"));
				}else if(event.getListenerActor() == back ) {
					//save level directory
					String actualLevelDirectory = levelDirectoryInput.getText().trim().equals("") ? 
							Gdx.files.getExternalStoragePath() + Procedit.TITLE + "/levels" : levelDirectoryInput.getText().trim();
					Gdx.app.getPreferences(Procedit.TITLE).putString("leveldirectory", actualLevelDirectory);
					
					Gdx.app.getPreferences(Procedit.TITLE).flush();
					
					Gdx.app.log(Procedit.TITLE, "settings saved");
					
					stage.addAction(sequence(moveTo(0, stage.getHeight(), .5f), run(new Runnable() {
						
						@Override
						public void run() {
							((Game) Gdx.app.getApplicationListener()).setScreen(new MainScreen(username));
						}
					})));
				}
			}
		};
		
		vSyncCheckBox.addListener(buttonHandler);
		
		
		back.addListener(buttonHandler);
		
		
		// putting everything in the table
		table.add("SETTINGS").spaceBottom(50).colspan(3).expandX().row();
		table.add();
		table.add("leveldirectory");
		table.add().row();
		table.add(vSyncCheckBox).top().expandY();
		table.add(levelDirectoryInput).top().fillX();
		table.add(back).bottom().right();

		stage.addActor(table);
		
		stage.addAction(sequence(moveTo(0, stage.getHeight()), moveTo(0, 0, .5f)));

	}

	@Override
	public void hide() {
		dispose();
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		stage.dispose();
		skin.dispose();
	}

}
