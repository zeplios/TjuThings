package edu.tju.ina.things.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
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

import edu.tju.ina.things.R;
import edu.tju.ina.things.adapter.InfoListAdapter;
import edu.tju.ina.things.net.HttpClient;
import edu.tju.ina.things.net.JSONCallback;
import edu.tju.ina.things.ui.DetailActivity;
import edu.tju.ina.things.ui.MenuMainActivity;
import edu.tju.ina.things.util.Constants;
import edu.tju.ina.things.util.InfoTypeHelper;
import edu.tju.ina.things.util.ProgressBuilder;
import edu.tju.ina.things.util.URLHelper;
import edu.tju.ina.things.util.Utils;
import edu.tju.ina.things.vo.info.Info;
import edu.tju.ina.things.vo.user.Collect;

public class CollectFragment extends Fragment {

	private MenuMainActivity mContext;

	//@ViewInject(R.id.info_list) ListView infoList;
    @ViewInject(R.id.pull_refresh_list)
    private PullToRefreshListView refreshList;
	@ResInject(id = R.string.collect_title, type = ResType.String) String title;

    private List<Collect> collections = new ArrayList<>();
    private List<Info> infos = new ArrayList<>();
    private InfoListAdapter adapter;
    private ProgressBuilder progressUtil;

    private boolean isLoading = false;
	
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.element_pull_list, null);
		ViewUtils.inject(this, layout);

        mContext = (MenuMainActivity) getActivity();
		progressUtil = ProgressBuilder.getInstance(mContext);

        ActionBar actionBar = mContext.getSupportActionBar();
        actionBar.setTitle(title);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

        progressUtil.show(true);
		HttpClient.get(URLHelper.COLLECT_URL, null, new JsonResponseHandler());

        ListView listview = refreshList.getRefreshableView();
        adapter = new InfoListAdapter(mContext, infos, true, "infolist");
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long id) {
                Info info = infos.get(position - 1);
                Intent intent = new Intent(mContext, DetailActivity.class);
                intent.putExtra(Constants.DEFAULT_BUNDLE_NAME, Parcels.wrap(info));
                mContext.startActivity(intent);
            }
        });

        bindPullToRefreshListener();
		
		return layout;
	}

    private void bindPullToRefreshListener() {
        refreshList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                if (isLoading) {
                    return;
                }
                isLoading = true;
                RequestParams params = new RequestParams();
                params.addQueryStringParameter("biggestid", Integer.toString(collections.get(0).getId()));
                HttpClient.get(URLHelper.COLLECT_URL, params, new JsonResponseHandler());
            }
        });
        refreshList.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                if (isLoading || collections.size() <= 0) {
                    return;
                }
                isLoading = true;
                RequestParams params = new RequestParams();
                params.addQueryStringParameter("smallestid", Integer.toString(collections.get(collections.size() - 1).getId()));
                HttpClient.get(URLHelper.COLLECT_URL, params, new JsonResponseHandler());
            }
        });
    }
	
	class HandleDataTask extends AsyncTask<JSONArray, Void, Boolean> {

		@Override
		protected Boolean doInBackground(JSONArray... params) {
            List<Collect> newcollects = new ArrayList<>();
            List<Info> newinfos = new ArrayList<>();
			JSONArray response = params[0];
            InfoTypeHelper helper = InfoTypeHelper.getInstance();
			for (int i = 0; i < response.length(); i++) {
				try {
					JSONObject jsonObj = response.getJSONObject(i);
					Collect c = new Gson().fromJson(jsonObj.toString(), Collect.class);
                    jsonObj = jsonObj.getJSONObject("info");
                    Class clazz = helper.getVoClass(jsonObj.getInt("type"));
                    Info info = (Info) new Gson().fromJson(jsonObj.toString(), clazz);
					c.setInfo(info);
                    newcollects.add(c);
                    newinfos.add(info);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
            if (newinfos.size() <= 0) {
                return false;
            }
            if (collections.size() == 0) {
                infos = newinfos;
                collections = newcollects;
                adapter.setInfos(infos);
                return true;
            }
            if (newcollects.get(newcollects.size() - 1).getId() >= collections.get(0).getId()) {
                collections.addAll(0, newcollects);
                infos.addAll(0, newinfos);
                adapter.setInfos(infos);
            } else if (newcollects.get(0).getId() < collections.get(collections.size() - 1).getId()){
                collections.addAll(newcollects);
                infos.addAll(newinfos);
                adapter.setInfos(infos);
            }
			return true;
		}

		@Override
		protected void onPostExecute(Boolean changed) {
			super.onPostExecute(changed);
            if (changed) {
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
            int errcode = response.getInt("errcode");
            if (errcode == 0) {
                JSONArray jsonArr = response.getJSONArray("collects");
                if (jsonArr.length() == 0) {
                    Utils.toast(mContext, "没有更多收藏");
                    return;
                }
                new HandleDataTask().execute(jsonArr);
            } else {
                Utils.toast(mContext, response.getString("errmsg"));
            }
        }

        @Override
        protected void onComplete() {
            refreshList.onRefreshComplete();
            progressUtil.hide();
            isLoading = false;
        }
    }
}
