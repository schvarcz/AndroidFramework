package com.framework;

import android.app.Application;

public class App extends Application {
	public static App instance = null;

	public App() {
		instance = this;
	}

	public static App getInstance() {
		return instance;
	}
}
