package com.goeey.frontend;

import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowAdapter;
import com.goeey.game.GameManager;

import java.util.Arrays;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setTitle("black-jack-game");
		config.setWindowedMode(1440, 810);
		config.useVsync(true);
		config.setForegroundFPS(60);
		config.setWindowSizeLimits(960, 540, -1, -1);
		config.setResizable(true);
		GameManager gm = new GameManager(arg[0]);
		config.setWindowListener(new Lwjgl3WindowAdapter() {
			@Override
			public boolean closeRequested() {
				gm.dispose(); // Implement this method to handle cleanup before closing
				return true; // Return true to indicate the window should be closed
			}
		});

		Lwjgl3Application appInstance = new Lwjgl3Application(gm, config);
		System.exit(0);
	}
}