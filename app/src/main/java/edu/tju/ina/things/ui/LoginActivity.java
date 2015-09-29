package edu.tju.ina.things.ui;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import edu.tju.ina.things.R;
import edu.tju.ina.things.util.DialogUtil;
import edu.tju.ina.things.util.DialogUtil.OnButtonClickListener;
import edu.tju.ina.things.util.LoginManager;
import edu.tju.ina.things.util.LoginManager.LoginListener;
import edu.tju.ina.things.util.ProgressBuilder;
import edu.tju.ina.things.util.Utils;

public class LoginActivity extends ActionBarActivity {
	
	@ViewInject(R.id.username_et) private EditText usernameEt;
	@ViewInject(R.id.password_et) private EditText passwordEt;
	@ViewInject(R.id.login_btn) private Button logBtn;

    ProgressBuilder progress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		ViewUtils.inject(this);

        progress = ProgressBuilder.getInstance(this);

		Typeface typeFace = Typeface.createFromAsset(getAssets(),"fonts/arial.ttf");
		usernameEt.setTypeface(typeFace);
		passwordEt.setTypeface(typeFace);
		logBtn.setTypeface(typeFace);
		
		logBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String username = usernameEt.getText().toString();
				String password = passwordEt.getText().toString();
				LoginManager lm = new LoginManager(getApplicationContext());
				lm.setmListener(new LoginListener() {
					
					@Override
					public void onUsernameNotExist() {
						Utils.toast(LoginActivity.this, "用户名不存在");
						usernameEt.setText("");
						passwordEt.setText("");
					}
					
					@Override
					public void onSuccess(boolean isFirst) {
						if (isFirst) {
							DialogUtil dialogUtils = new DialogUtil(LoginActivity.this);
							dialogUtils.show("登陆成功", "您是首次登陆，要去修改个人信息吗？", new OnButtonClickListener() {
								@Override
								public void onConfirm() {
									Intent intent = new Intent(LoginActivity.this, PersonalSettingActivity.class);
									LoginActivity.this.startActivity(intent);
									LoginActivity.this.finish();
								}
								
								@Override
								public void onCancle() {
									Utils.toast(LoginActivity.this, "登陆成功");
									LoginActivity.this.finish();
								}
							});
						} else {
							Utils.toast(LoginActivity.this, "登陆成功");
							LoginActivity.this.finish();
						}
					}
					
					@Override
					public void onPasswordWrong() {
						Utils.toast(LoginActivity.this, "密码错误");
						passwordEt.setText("");
					}

					@Override
					public void onComplete() {
                        progress.hide();
					}

					@Override
					public void onFail() {
						Utils.toast(LoginActivity.this, "用户名或密码错误");
						usernameEt.setText("");
						passwordEt.setText("");
					}
				});
                progress.show(true);
				lm.login(username, password);
			}
		});
		
	}
}