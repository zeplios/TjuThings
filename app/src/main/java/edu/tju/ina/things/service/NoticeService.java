package edu.tju.ina.things.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import edu.tju.ina.things.R;
import edu.tju.ina.things.net.HttpClient;
import edu.tju.ina.things.net.JSONCallback;
import edu.tju.ina.things.ui.DetailActivity;
import edu.tju.ina.things.util.Constants;
import edu.tju.ina.things.util.InfoTypeHelper;
import edu.tju.ina.things.util.URLHelper;
import edu.tju.ina.things.vo.info.Info;
import edu.tju.ina.things.vo.user.Notice;

public class NoticeService extends Service {

	Handler mH = new Handler();

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
	}

    @Override
	public int onStartCommand(Intent intent, int flags, int startId) {
        mH.postDelayed(new Runnable() {
            @Override
            public void run() {
                queryResp();
            }
        }, 10000);
		return START_REDELIVER_INTENT;
	}
	
	/**
	 * 现在建议使用Notification.Builder代替setLatestEventInfo，但是这个接口要求的API版本很高，所以不使用
     * @param notice 弹出的通知
     * @param count 一共有几个通知
	 */
	@SuppressWarnings("deprecation")
	private void callNotification(Notice notice, int count) {
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Notification n = new Notification();
        n.icon = R.drawable.menu_about;
        n.tickerText = "有个消息好像和你有关~";
        n.when = System.currentTimeMillis();
        n.defaults |= Notification.DEFAULT_SOUND;
        n.flags = Notification.FLAG_AUTO_CANCEL ;
        Intent intentNotify = new Intent(getApplicationContext(), DetailActivity.class);
        intentNotify.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intentNotify.putExtra(Constants.DEFAULT_BUNDLE_NAME, Parcels.wrap(notice.getInfo()));
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(),
                0, intentNotify, PendingIntent.FLAG_UPDATE_CURRENT);
        String content = notice.getFromUser().getUsername() + "发布的消息 " + notice.getInfo().getTitle()
                + " 和你有关, 点击查看消息详情";
        if (count > 1) {
            content += ", 还有" + count + "条消息，可到消息中心查看";
        }
        n.setLatestEventInfo(NoticeService.this, n.tickerText, content, pi);

        nm.notify(0, n);
	}
	
	public void queryResp() {
		HttpClient.get(URLHelper.NOTICE_URL, null, new JSONCallback(getApplicationContext()) {

            @Override
            public void onFailure(HttpException e, String msg) {
            }

            @Override
            public void onSuccess(JSONObject obj, ResponseInfo<String> resp) throws JSONException {
                JSONArray response = obj.getJSONArray("notices");
                List<Notice> notices = new Gson().fromJson(response.toString(),
                        new TypeToken<ArrayList<Notice>>() {
                        }.getType());
                for (int i = 0; i < response.length(); i++) {
                    JSONObject jsonObj = response.getJSONObject(i).getJSONObject("info");
                    Class clazz = InfoTypeHelper.getInstance().getVoClass(jsonObj.getInt("type"));
                    Info info = (Info) new Gson().fromJson(jsonObj.toString(), clazz);
                    notices.get(i).setInfo(info);
                }
                if (notices.size() > 0) {
                    callNotification(notices.get(0), notices.size());
                }
            }

            @Override
            protected void onComplete() {
                super.onComplete();
                mH.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        queryResp();
                    }
                }, 30000);
            }

        }, true);
	}

}
