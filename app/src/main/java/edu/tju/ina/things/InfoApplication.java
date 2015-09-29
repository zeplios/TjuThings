package edu.tju.ina.things;

import android.app.Application;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;

import edu.tju.ina.things.net.HttpClient;
import edu.tju.ina.things.service.NoticeService;
import edu.tju.ina.things.util.ImageLoader;
import edu.tju.ina.things.util.InfoTypeHelper;
import edu.tju.ina.things.util.URLHelper;
import edu.tju.ina.things.vo.user.User;

public class InfoApplication extends Application {

	public static User currentUser = null;
    public static void setNameAndAvatar(TextView name, ImageView avatar) {
        if (currentUser == null) {
            return;
        }
        if (name != null) {
            name.setText(currentUser.getUsername());;
        }
        if (avatar != null) {
            String url = URLHelper.ROOT_URL + "/" + currentUser.getAvatar();
            ImageLoader.getInstance(null).load(url, avatar);
        }
    }
	
	@Override
	public void onCreate() {
		super.onCreate();
		ImageLoader.getInstance(getApplicationContext());
		initAsyncHttpCookie();
        InfoTypeHelper.getInstance().init(getApplicationContext());
		
		Intent intent = new Intent(getApplicationContext(), NoticeService.class);
		startService(intent);
	}

	private void initAsyncHttpCookie() {
		HttpClient.setCookieEnable(getApplicationContext());
	}

}
