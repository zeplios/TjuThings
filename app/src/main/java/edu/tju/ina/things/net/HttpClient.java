package edu.tju.ina.things.net;

import android.content.Context;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseStream;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.util.PreferencesCookieStore;

import org.apache.http.cookie.Cookie;

import java.io.IOException;

import edu.tju.ina.things.util.URLHelper;

public class HttpClient {

	private static HttpUtils httpUtils = new HttpUtils();
    private static PreferencesCookieStore myCookieStore;
    
    static {
    	httpUtils.configTimeout(15000);
    }
    
    public static void setCookieEnable(Context context){
    	myCookieStore = new PreferencesCookieStore(context);
    	httpUtils.configCookieStore(myCookieStore);
    }
    
    public static void get(String url, RequestParams params,RequestCallBack<?> callBack) {
        for (Cookie cookie : myCookieStore.getCookies()) {
            System.err.println(cookie.getDomain() + "--->" + cookie.getName() + "--->" + cookie.getValue());
        }
        httpUtils.send(HttpRequest.HttpMethod.GET, getAbsoluteUrl(url), params, callBack);
    }
    
    public static void get(String url, RequestParams params,RequestCallBack<?> callBack, boolean nocache) {
    	if (nocache) {
            httpUtils.configCurrentHttpCacheExpiry(0);
        }
        httpUtils.send(HttpRequest.HttpMethod.GET, getAbsoluteUrl(url), params, callBack);
    }
    
    public static void post(String url, RequestParams params,RequestCallBack<?> callBack) {
        httpUtils.send(HttpRequest.HttpMethod.POST, getAbsoluteUrl(url), params, callBack);
    }

    public static String postSync(String url, RequestParams params) {
        try {
            ResponseStream rs = httpUtils.sendSync(HttpRequest.HttpMethod.POST, getAbsoluteUrl(url), params);
            return rs.readString();
        } catch (HttpException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static void put(String url, RequestParams params,RequestCallBack<?> callBack) {
        httpUtils.send(HttpRequest.HttpMethod.PUT, getAbsoluteUrl(url), params, callBack);
    }
    
    public static void delete(String url, RequestParams params,RequestCallBack<?> callBack) {
        httpUtils.send(HttpRequest.HttpMethod.DELETE, getAbsoluteUrl(url), params, callBack);
    }
    
    public static void clearCookie(){
    	myCookieStore.clear();
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return URLHelper.BASE_URL + relativeUrl;
    }
    
}