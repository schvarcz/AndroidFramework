package com.framework;

import android.app.Application;

public class CApp extends Application {
	public static CApp instance = null;

	public CApp() {
		instance = this;
	}

	public static CApp getInstance() {
		return instance;
	}
}
