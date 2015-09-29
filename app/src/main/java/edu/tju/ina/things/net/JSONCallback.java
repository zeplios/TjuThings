package edu.tju.ina.things.net;

import android.content.Context;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import edu.tju.ina.things.InfoApplication;
import edu.tju.ina.things.util.Utils;

public class JSONCallback extends RequestCallBack<String> {
	
	private Context context;
	
	public JSONCallback() {
	}
	
	public JSONCallback(Context context) {
		this.context = context;
	}

	@Override
	public void onFailure(HttpException e, String msg) {
		if (context != null) {
            Utils.toast(context, msg);
		}
		onComplete();
	}

	@Override
	public void onSuccess(ResponseInfo<String> resp) {
        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(resp.result);
            // general condition, exit if don't match
            if (jsonObj.has("errcode")) {
                int errcode = jsonObj.getInt("errcode");
                if (errcode == -1 && context != null) {
                    InfoApplication.currentUser = null;
                    Utils.toast(context, "请先登录");
                    onComplete();
                    return;
                }
            }
            // general condition end
            onSuccess(new JSONObject(resp.result), resp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        onComplete();
	}

	public void onSuccess(JSONObject obj, ResponseInfo<String> resp) throws JSONException {
	}
	
	@Override
	public void onCancelled() {
		super.onCancelled();
	}

	/**
	 * 一些不管成功失败都回调的代码，如果覆写了上面这两个参数最长的函数并且没调super，
     * 这个函数就会失效
	 * @author ZhangFC
	 */
	protected void onComplete() {
	}
}
