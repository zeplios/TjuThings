package edu.tju.ina.things.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.lidroid.xutils.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import edu.tju.ina.things.net.HttpClient;
import edu.tju.ina.things.vo.info.Info;

public class SharedPrefCache {
	
	public static final String SP_INFOLIST_KEY = "infolistcache";
	
	private Context mContext;

	public SharedPrefCache(Context context) {
		super();
		this.mContext = context;
	}

	public String get(String cachefile, String key){
		SharedPreferences sp = mContext.getSharedPreferences(cachefile, Context.MODE_PRIVATE);
		String jsonArrayStr = sp.getString(key, "");
		if ("".equals(jsonArrayStr))
			return null;
		return jsonArrayStr;
	}
	
	public void clear(String cachefile){
		Editor editor = mContext.getSharedPreferences(cachefile, Context.MODE_PRIVATE).edit();
        editor.clear();
		editor.commit();
	}
	
	public void save(String cachefile, String key, String jsonArrayStr){
		Editor editor = mContext.getSharedPreferences(cachefile, Context.MODE_PRIVATE).edit();
		editor.putString(key, jsonArrayStr);
		editor.commit();
	}
}

// save cache demo
//private void cacheCurrent() {
//    SharedPrefCache cache = new SharedPrefCache(getActivity());
//    List<Info> infos = adapter.getInfos();
//    if (infos.size() > 10) {
//        infos = infos.subList(0, 10);
//    }
//    JSONArray json = new JSONArray(infos);
//    cache.save(SharedPrefCache.SP_INFOLIST_KEY, infoNames[currentIndex], json.toString());
//}

// read cache demo
//public boolean onNavigationItemSelected(int position, long l) {
//    SharedPrefCache cache = new SharedPrefCache(getActivity());
//    String type = Integer.toString(infoTypeIds[position]);
//    try {
//        String jsonArrStr = cache.get(SharedPrefCache.SP_INFOLIST_KEY, infoNames[currentIndex]);
//        if (jsonArrStr != null) {
//            JSONArray response = new JSONArray(jsonArrStr);
//            List<Info> infos = new ArrayList<Info>();
//            for (int i = 0; i < response.length(); i++) {
//                JSONObject jsonObj = response.getJSONObject(i);
//                infos.add(InfoTypeHelper.convertJsonToInfo(jsonObj));
//            }
//            adapter.setInfos(infos);
//            adapter.notifyDataSetChanged();
//        }
//    } catch (NumberFormatException | JSONException e) {
//        e.printStackTrace();
//    }
//    ......
//    return false;
//}
