package edu.tju.ina.things.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.view.ResType;
import com.lidroid.xutils.view.annotation.ResInject;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import edu.tju.ina.things.InfoApplication;
import edu.tju.ina.things.R;
import edu.tju.ina.things.adapter.MyAlbumAdapter;
import edu.tju.ina.things.net.HttpClient;
import edu.tju.ina.things.net.JSONCallback;
import edu.tju.ina.things.ui.DetailActivity;
import edu.tju.ina.things.ui.MenuMainActivity;
import edu.tju.ina.things.util.Constants;
import edu.tju.ina.things.util.DialogUtil;
import edu.tju.ina.things.util.ImageLoader;
import edu.tju.ina.things.util.ProgressBuilder;
import edu.tju.ina.things.util.TimeUtils;
import edu.tju.ina.things.util.URLHelper;
import edu.tju.ina.things.util.Utils;
import edu.tju.ina.things.vo.info.Album;
import edu.tju.ina.things.vo.info.Info;

public class MyAlbumFragment extends Fragment {

	private MenuMainActivity mContext;
	private MyAlbumAdapter adapter;
    private ProgressBuilder progress;

	@ViewInject(R.id.user_avatar) private ImageView userAvatar;
	@ViewInject(R.id.user_name) private TextView username;
	@ViewInject(R.id.add_time) private TextView addTime;
	@ViewInject(R.id.listView) private ListView listView;
    @ResInject(id = R.string.my_album_title, type = ResType.String) String title;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle saveInstanceStanceState){
		View v = inflater.inflate(R.layout.fragment_my_album, parent, false);
		ViewUtils.inject(this, v);

        mContext = (MenuMainActivity) getActivity();
        if (InfoApplication.currentUser == null) {
            Utils.toast(mContext, "请先登录");
            return v;
        }

        ActionBar actionBar = mContext.getSupportActionBar();
        actionBar.setTitle(title);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

        adapter = new MyAlbumAdapter(mContext, new ArrayList<Info>());
        listView.setDividerHeight(0);
        listView.setAdapter(adapter);

		String avatarUrl = URLHelper.ROOT_URL + InfoApplication.currentUser.getAvatar();
		ImageLoader.getInstance(null).load(avatarUrl, userAvatar);
		username.setText(InfoApplication.currentUser.getUsername());
		addTime.setText(TimeUtils.getCurrentYYYYMMDD("-"));

        progress = ProgressBuilder.getInstance(mContext);
		
        progress.show(true);
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("type", "5");
		params.addQueryStringParameter("page", "0"); // page = 0 means load all
		HttpClient.get(URLHelper.USER_INFO_URL, params, new JsonResponseHandler());
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                DialogUtil dialogUtil = new DialogUtil(mContext);
                dialogUtil.show("删除", "您确认要删除吗", new DialogUtil.OnButtonClickListener() {
                    @Override
                    public void onConfirm() {
                        final Info info = adapter.getInfos().get(position);
                        RequestParams params = new RequestParams();
                        params.addQueryStringParameter("id", Integer.toString(info.getId()));
                        params.addQueryStringParameter("type", Integer.toString(info.getType()));
                        HttpClient.delete(URLHelper.INFO_URL, params, new JSONCallback(mContext){
                            @Override
                            public void onSuccess(JSONObject obj, ResponseInfo<String> resp) throws JSONException {
                                List<Info> infos = adapter.getInfos();
                                infos.remove(info);
                                adapter.setInfos(infos);
                                adapter.notifyDataSetChanged();
                                super.onSuccess(obj, resp);
                            }
                        });
                    }

                    @Override
                    public void onCancle() {
                    }
                });
                return false;
            }
        });

		return v;
	}
	
	class HandleDataTask extends AsyncTask<JSONArray, Void, Boolean> {

		@Override
		protected Boolean doInBackground(JSONArray... params) {
			List<Info> infos = new Gson().fromJson(params[0].toString(), 
					new TypeToken<List<Album>>(){}.getType());
            if (infos.size() == 0) {
                return false;
            }
            adapter.setInfos(infos);
            return true;
		}

		@Override
		protected void onPostExecute(Boolean changed) {
			super.onPostExecute(changed);
			if (changed == true) {
				adapter.notifyDataSetChanged();
			}
		}
	}

	class JsonResponseHandler extends JSONCallback {
		
		public JsonResponseHandler() {
			super(mContext);
		}
		
		@Override
		public void onSuccess(JSONObject response, ResponseInfo<String> resp) throws JSONException {
            if (response.getInt("errcode") > 0) {
                Utils.toast(mContext, response.getString("errmsg"));
                return;
            }
            JSONArray responseArr = response.getJSONArray("infos");
            new HandleDataTask().execute(responseArr);
		}

        @Override
        protected void onComplete() {
            super.onComplete();
            progress.hide();
        }
    }

}
