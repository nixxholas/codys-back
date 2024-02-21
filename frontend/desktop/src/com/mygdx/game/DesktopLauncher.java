package com.mygdx.game;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setTitle("black-jack-game");
		config.setWindowedMode(1920, 1080);
		config.useVsync(true);
		config.setForegroundFPS(60);
		config.setWindowSizeLimits(960, 540, 9999, 9999);
		// Uses Boot.java to create new application screen
		new Lwjgl3Application(new Boot(), config);
	}
}