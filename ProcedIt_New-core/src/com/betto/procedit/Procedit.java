package com.betto.procedit;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.betto.procedit.screens.LoginScreen;
import com.betto.procedit.screens.MainScreen;
import com.betto.procedit.screens.StoryScreen;

public class Procedit extends Game {

	public final static String TITLE = "Procedit!", VERSION = "1.0";
	
	@Override
	public void create() {
		System.out.println("Creating");
		setScreen(new LoginScreen(this));
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public void pause() {
		super.pause();
	}

	@Override
	public void resume() {
		super.resume();
	}

	@Override
	public void render() {
		super.render();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	@Override
	public void setScreen(Screen screen) {
		super.setScreen(screen);
	}

	@Override
	public Screen getScreen() {
		return super.getScreen();
	}

}
