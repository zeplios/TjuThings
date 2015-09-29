package edu.tju.ina.things.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import edu.tju.ina.things.util.LoginManager;
import edu.tju.ina.things.util.LoginManager.LoginListener;
import edu.tju.ina.things.R;

public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		LoginManager lm = new LoginManager(SplashActivity.this);
		lm.setmListener(new LoginListener() {
			
			@Override
			public void onUsernameNotExist() {
			}
			@Override
			public void onSuccess(boolean isFirst) {
			}
			@Override
			public void onPasswordWrong() {
			}
			@Override
			public void onComplete() {
				Intent intent = new Intent(SplashActivity.this, MenuMainActivity.class);
				startActivity(intent);
				SplashActivity.this.finish();
			}
			@Override
			public void onFail() {
			}
			
		});
		if (!lm.login()) {
			Intent intent = new Intent(SplashActivity.this, MenuMainActivity.class);
			startActivity(intent);
			SplashActivity.this.finish();
			return;
		}
	}
}
