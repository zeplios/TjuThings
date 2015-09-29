package edu.tju.ina.things.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
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

import edu.tju.ina.things.InfoApplication;
import edu.tju.ina.things.R;
import edu.tju.ina.things.adapter.InfoListAdapter;
import edu.tju.ina.things.net.HttpClient;
import edu.tju.ina.things.net.JSONCallback;
import edu.tju.ina.things.ui.DetailActivity;
import edu.tju.ina.things.ui.MenuMainActivity;
import edu.tju.ina.things.ui.SearchActivity;
import edu.tju.ina.things.util.Constants;
import edu.tju.ina.things.util.InfoTypeHelper;
import edu.tju.ina.things.util.ProgressBuilder;
import edu.tju.ina.things.util.URLHelper;
import edu.tju.ina.things.util.Utils;
import edu.tju.ina.things.vo.info.Info;

public class InfoListFragment extends Fragment implements ActionBar.OnNavigationListener {

	@ViewInject(R.id.pull_refresh_list)
    private PullToRefreshListView refreshList;

    @ResInject(id = R.array.infoids_in_infolist, type = ResType.IntArray)
    private int [] showInfoIds; // 都有哪些信息在这个页面中显示
    private InfoTypeHelper.InfoItem[] infoItems;

    ProgressBuilder progressUtil;

	private int currentIndex = 0;
	private InfoListAdapter adapter;
    private boolean isLoading = false;

	private MenuMainActivity mContext;

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		FrameLayout layout = (FrameLayout) inflater.inflate(R.layout.element_pull_list, null);
		ViewUtils.inject(this, layout);
		mContext = (MenuMainActivity) getActivity();

        progressUtil = ProgressBuilder.getInstance(mContext);
        infoItems = InfoTypeHelper.getInstance().get(showInfoIds);
        String [] titles = new String [infoItems.length];
        for (int i = 0 ; i < infoItems.length ; i++) {
            titles[i] = infoItems[i].chName;
        }

        ArrayAdapter spinnerAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, titles);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ActionBar actionBar = mContext.getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        actionBar.setListNavigationCallbacks(spinnerAdapter, this);

        ListView listview = refreshList.getRefreshableView();
        adapter = new InfoListAdapter(mContext, new ArrayList<Info>(), false, "infolist");
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long id) {
                // 注意，pull_refresh_list对listview的封装加上了header，真正的position是从1开始
                Info info = adapter.getInfos().get(position - 1);
                Intent intent = new Intent(mContext, DetailActivity.class);
                intent.putExtra(Constants.DEFAULT_BUNDLE_NAME, Parcels.wrap(info));
                mContext.startActivity(intent);
            }
        });

		bindPullToRefreshListener();

		return layout;
	}

    @Override
    public boolean onNavigationItemSelected(int position, long itemid) {
        currentIndex = position;
        progressUtil.show(true);
        adapter.setInfos(new ArrayList<Info>());
        adapter.notifyDataSetChanged();
        isLoading = true;
        RequestParams params = new RequestParams();
        params.addQueryStringParameter("type", infoItems[currentIndex].id);
        params.addQueryStringParameter("page", "1");
        HttpClient.get(URLHelper.INFO_URL, params, new JsonResponseHandler());
        return false;
    }

	private void bindPullToRefreshListener() {
		refreshList.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				if (isLoading) {
                    return;
                }
                isLoading = true;
                RequestParams params = new RequestParams();
				params.addQueryStringParameter("type", infoItems[currentIndex].id);
				params.addQueryStringParameter("biggestid", Integer.toString(adapter.getFirstId(0)));
				HttpClient.get(URLHelper.INFO_URL, params, new JsonResponseHandler());
			}
		});
		refreshList.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {
			@Override
			public void onLastItemVisible() {
				if (isLoading || adapter.getInfos().size() <= 0) {
                    return;
                }
                isLoading = true;
				RequestParams params = new RequestParams();
				params.addQueryStringParameter("type", infoItems[currentIndex].id);
				params.addQueryStringParameter("smallestid",	Integer.toString(adapter.getLastId(0)));
				HttpClient.get(URLHelper.INFO_URL, params,	new JsonResponseHandler());
			}
		});
	}

    class HandleDataTask extends AsyncTask<JSONArray, Void, Boolean> {

		@Override
		protected Boolean doInBackground(JSONArray... params) {
			List<Info> infos = new ArrayList<>();
			JSONArray response = params[0];
            InfoTypeHelper helper = InfoTypeHelper.getInstance();
			for (int i = 0; i < response.length(); i++) {
				try {
					JSONObject jsonObj = response.getJSONObject(i);
                    Class clazz = helper.getVoClass(jsonObj.getInt("type"));
					infos.add((Info) new Gson().fromJson(jsonObj.toString(), clazz));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
            if (infos.size() <= 0) {
                return false;
            }
            if (infos.get(infos.size() - 1).getId() >= adapter.getFirstId(0)) {
                infos.addAll(adapter.getInfos());
                adapter.setInfos(infos);
            } else if (infos.get(0).getId() < adapter.getLastId(0)){
                infos.addAll(0, adapter.getInfos());
                adapter.setInfos(infos);
            }
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
            if (responseArr.length() == 0) {
                Utils.toast(mContext, "没有更多的消息了");
                return;
            }
            if (!infoItems[currentIndex].id.equals(responseArr.getJSONObject(0).getString("type"))) {
                return;
            }
            new HandleDataTask().execute(responseArr);
		}

		@Override
		protected void onComplete() {
			refreshList.onRefreshComplete();
            progressUtil.hide();
            isLoading = false;
		}
	}

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.add_search, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                if (InfoApplication.currentUser == null) {
                    Utils.toast(mContext, "请先登录");
                    return true;
                }
                Class c = InfoTypeHelper.getInstance().getAddClass(infoItems[currentIndex].voName);
                if (c == null) {
                    Utils.toast(mContext, "该功能还未开放");
                } else {
                    Intent intent = new Intent(mContext, c);
                    mContext.startActivity(intent);
                }
                return true;
            case R.id.action_search:
                Intent intent = new Intent(mContext, SearchActivity.class);
                mContext.startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setHasOptionsMenu to override setting in activity
        setHasOptionsMenu(true);
    }
}
