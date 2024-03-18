package com.goeey.frontend;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.goeey.game.Blackjack;
import com.goeey.game.GameManager;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setTitle("black-jack-game");
		config.setWindowedMode(1920, 1080);
		config.useVsync(true);
		config.setForegroundFPS(60);
		config.setWindowSizeLimits(1920, 1080, 9999, 9999);
		new Lwjgl3Application(new GameManager(), config);
	}
}