package edu.tju.ina.things.util;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import edu.tju.ina.things.InfoApplication;
import edu.tju.ina.things.net.JSONCallback;
import edu.tju.ina.things.net.HttpClient;
import edu.tju.ina.things.vo.user.User;

import com.google.gson.Gson;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;

public class LoginManager {
	
	private static final String LOGIN_NAME = "login";
	private static final String USERNAME_KEY = "username";
	private static final String PASSWORD_KEY = "password";
	
	private Context mContext;
	private LoginListener mListener;

	public LoginManager(Context context) {
		super();
		this.mContext = context;
	}

	public boolean login(){
		SharedPreferences sp = mContext.getSharedPreferences(LOGIN_NAME, Context.MODE_PRIVATE);
		String username = sp.getString(USERNAME_KEY, "");
		String password = sp.getString(PASSWORD_KEY, "");
		if ("".equals(username))
			return false;
		sendPost(username, password);
		return true;
	}
	
	public void clearSp(){
		Editor editor = mContext.getSharedPreferences(LOGIN_NAME, Context.MODE_PRIVATE).edit();
		editor.remove(USERNAME_KEY);
		editor.remove(PASSWORD_KEY);
		editor.commit();
	}
	
	public void login(String username, String password){
		sendPost(username, password);
	}
	
	private void sendPost(final String username, final String password){
		RequestParams params = new RequestParams();
		params.addBodyParameter("username", username);
		params.addBodyParameter("password", password);
		HttpClient.post(URLHelper.LOGIN_URL, params, new JSONCallback(mContext) {

			@Override
			public void onSuccess(JSONObject response, ResponseInfo<String> resp) {
				int code = 0;
				try {
					code = response.getInt("errcode");
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				}
				if (code == 0 || code == 13) {
					// TODO if code is equal to 13, alert to fill personal information
					Editor editor = mContext.getSharedPreferences(LOGIN_NAME, Context.MODE_PRIVATE).edit();
					editor.putString(USERNAME_KEY, username);
					editor.putString(PASSWORD_KEY, password);
					editor.commit();
					try {
						String userJsonStr = response.getJSONObject("user").toString();
						User u = new Gson().fromJson(userJsonStr, User.class);
						InfoApplication.currentUser = u;
					} catch (JSONException e) {
						e.printStackTrace();
					}
					if (mListener != null) {
						if (code == 13) {
							mListener.onSuccess(true);
						} else {
							mListener.onSuccess(false);
						}
					}
				} else if (code == 1) {
					if (mListener != null) {
						mListener.onUsernameNotExist();
					}
				} else if (code == 2) {
					if (mListener != null) {
						mListener.onPasswordWrong();
					}
				} else if (code == 12) {
					if (mListener != null) {
						mListener.onFail();
					}
				}
			}
			
			protected void onComplete() {
				if (mListener != null) {
					mListener.onComplete();
				}
			}
		});
	}
	
	public void setmListener(LoginListener mListener) {
		this.mListener = mListener;
	}

	public interface LoginListener {
		public void onSuccess(boolean isFirst);
		public void onUsernameNotExist();
		public void onPasswordWrong();
		public void onFail();
		public void onComplete();
	}
	
//	public static class LoginListenerAdapter implements LoginListener {
//		@Override public void onSuccess() {}
//		@Override public void onUsernameNotExist() {}
//		@Override public void onPasswordWrong() {}
//		@Override public void onFail() {}
//		@Override public void onComplete() {}
//	}
}
