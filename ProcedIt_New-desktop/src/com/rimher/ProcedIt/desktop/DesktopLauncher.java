package com.rimher.ProcedIt.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.betto.procedit.Procedit;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = Procedit.TITLE+"v"+Procedit.VERSION;
		cfg.vSyncEnabled = true;
//		cfg.useGL30 = true;
		cfg.width = (int) (1200 / 1.5f);
		cfg.height = 780;
        new LwjglApplication(new Procedit(), cfg);
	}
}
