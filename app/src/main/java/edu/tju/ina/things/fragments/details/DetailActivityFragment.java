package edu.tju.ina.things.fragments.details;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import org.json.JSONException;
import org.json.JSONObject;

import edu.tju.ina.things.R;
import edu.tju.ina.things.net.HttpClient;
import edu.tju.ina.things.net.JSONCallback;
import edu.tju.ina.things.ui.BigImageActivity;
import edu.tju.ina.things.util.Constants;
import edu.tju.ina.things.util.ImageLoader;
import edu.tju.ina.things.util.URLHelper;
import edu.tju.ina.things.vo.info.Activity;

public class DetailActivityFragment extends BaseFragment {

	private Activity activity;
	
	@ViewInject(R.id.info_poster) private ImageView image;
	@ViewInject(R.id.info_title) private TextView title;
	@ViewInject(R.id.info_addtime) private TextView time;
	@ViewInject(R.id.activity_place) private TextView place;
	@ViewInject(R.id.activity_sponsor) private TextView sponsor;
	@ViewInject(R.id.activity_detail_content) private TextView detailContent;
	@ViewInject(R.id.activity_org_content) private TextView orgContent;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_detail_activity, null);
		ViewUtils.inject(this, v);
		
		if (mContext.getInfo() == null || !(mContext.getInfo() instanceof Activity))
			return v;
		activity = (Activity) mContext.getInfo();
		
		String posterUrl = URLHelper.ROOT_URL + activity.getPicture();
		ImageLoader.getInstance(null).load(posterUrl, image);
		
		title.setText(activity.getTitle());
		time.setText(activity.getTime());
		place.setText(activity.getArea());
		sponsor.setText("赞助商");

        RequestParams params = new RequestParams();
        params.addQueryStringParameter("id", Integer.toString(activity.getId()));
        params.addQueryStringParameter("type", Integer.toString(activity.getType()));
        HttpClient.get(URLHelper.DETAIL_URL, params, new JSONCallback(mContext) {
            @Override
            public void onSuccess(JSONObject response, ResponseInfo<String> resp) throws JSONException {
                activity = new Gson().fromJson(response.getJSONObject("info").toString(), Activity.class);
                time.setText(activity.getTime());
                place.setText(activity.getArea());
                sponsor.setText("赞助商：" + activity.getOrgName());
                detailContent.setText(Html.fromHtml(activity.getDesc()));
                orgContent.setText(Html.fromHtml(activity.getOrgIntro()));
                mContext.setInfo(activity);
            }
        });
		return v;
	}
	
	@OnClick(R.id.info_poster)
	public void onPosterClick(View v) {
		Intent i = new Intent(mContext, BigImageActivity.class);
        i.putExtra(Constants.DEFAULT_BUNDLE_NAME, activity.getPicture());
		mContext.startActivity(i);
	}
}