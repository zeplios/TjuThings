package edu.tju.ina.things.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar.LayoutParams;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.google.gson.Gson;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
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
import edu.tju.ina.things.util.Constants;
import edu.tju.ina.things.util.InfoTypeHelper;
import edu.tju.ina.things.util.ProgressBuilder;
import edu.tju.ina.things.util.URLHelper;
import edu.tju.ina.things.util.Utils;
import edu.tju.ina.things.vo.info.Info;

public class SearchActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    @ViewInject(R.id.webview) private WebView webview;
	private EditText searchTextEt;
	private InfoListAdapter adapter;
	private Handler handler = new Handler();
    private ProgressBuilder progressUtil;
	
	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.element_webview);
		ViewUtils.inject(this);

		setActioinBar();
        initWebView();
        progressUtil = new ProgressBuilder(this);
	}

	private void search() {
        String searchContent = searchTextEt.getText().toString();
        if (searchContent == null || searchContent.equals("")) {
            Utils.toast(this, "请输入关键字");
            return;
        }
        RequestParams params = new RequestParams();
        params.addQueryStringParameter("title", searchContent);
        progressUtil.show(true);
		HttpClient.get(URLHelper.SEARCH_URL, params, new JSONCallback(this) {
			@Override
			public void onSuccess(JSONObject response, ResponseInfo<String> resp) throws JSONException {
                List<Info> infos = new ArrayList<>();
				JSONArray jsonArr = response.getJSONArray("infos");
                for (int i = 0; i < jsonArr.length(); i++) {
                    JSONObject jsonObj = jsonArr.getJSONObject(i);
                    Class clazz = InfoTypeHelper.getInstance().getVoClass(jsonObj.getInt("type"));
                    Info info = (Info) new Gson().fromJson(jsonObj.toString(), clazz);
                    infos.add(info);
                }
                adapter.setInfos(infos);
                adapter.notifyDataSetChanged();
			}

			@Override
			protected void onComplete() {
                progressUtil.hide();
			}
		});
	}

    @Override
	public void onItemClick(AdapterView<?> arg0, View arg1,
			int position, long id) {
		Info info = adapter.getInfos().get(position);
		Intent intent = new Intent(this, DetailActivity.class);
		intent.putExtra(Constants.DEFAULT_BUNDLE_NAME, Parcels.wrap(info));
		SearchActivity.this.startActivity(intent);
	}

    private void replaceWebviewWithList() {
        FrameLayout layout = (FrameLayout) ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        layout.removeView(webview);
        webview = null;
        ListView infoList = new ListView(this);
        infoList.setOnItemClickListener(this);
        layout.addView(infoList, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        adapter = new InfoListAdapter(this, new ArrayList<Info>(), true, "infolist");
        infoList.setAdapter(adapter);
    }

    private void initWebView() {
        webview.getSettings().setJavaScriptEnabled(true);
        webview.requestFocus();
        webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webview.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void clickOnAndroid(final String title) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        replaceWebviewWithList();
                        searchTextEt.setText(title);
                        search();
                    }
                });
            }
        }, "android");
        webview.loadUrl("file:///android_asset/web/search_hotword.html");
    }

    private void setActioinBar() {
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.title_back);
        searchTextEt = new EditText(this);
        searchTextEt.setHint(R.string.search_hint);
        searchTextEt.setTextColor(Color.WHITE);
        getSupportActionBar().setCustomView(searchTextEt, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return false;
            case R.id.action_search:
                search();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}