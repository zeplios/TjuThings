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
import edu.tju.ina.things.vo.info.Recruit;

public class DetailRecruitFragment extends BaseFragment {
	
	private Recruit recruit;
	
	@ViewInject(R.id.info_poster) private ImageView image;
	@ViewInject(R.id.info_title) private TextView title;
	@ViewInject(R.id.info_addtime) private TextView time;
	@ViewInject(R.id.recruit_place) private TextView place;
	@ViewInject(R.id.recruit_sponsor) private TextView sponsor;
	@ViewInject(R.id.info_desc) private TextView detailContent;
	@ViewInject(R.id.detail_com_content) private TextView comContent;
	@ViewInject(R.id.detail_comwebsite_content) private TextView comWebsite;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_detail_recruit, null);
		ViewUtils.inject(this, v);
		
		if (mContext.getInfo() == null || !(mContext.getInfo() instanceof Recruit))
			return v;
		recruit = (Recruit) mContext.getInfo();
		
		String posterUrl = URLHelper.ROOT_URL + recruit.getPicture();
		ImageLoader.getInstance(null).load(posterUrl, image);
		
		title.setText(recruit.getTitle());
		time.setText(recruit.getTime());
		place.setText(recruit.getArea());
		sponsor.setText("赞助商" + recruit.getComName());
		
        RequestParams params = new RequestParams();
        params.addQueryStringParameter("id", Integer.toString(recruit.getId()));
        params.addQueryStringParameter("type", Integer.toString(recruit.getType()));
        HttpClient.get(URLHelper.DETAIL_URL, params, new JSONCallback(mContext) {
            @Override
            public void onSuccess(JSONObject response, ResponseInfo<String> resp) throws JSONException {
                recruit = new Gson().fromJson(response.getJSONObject("info").toString(), Recruit.class);
                time.setText(recruit.getTime());
                place.setText(recruit.getArea());
                sponsor.setText("赞助商：" + recruit.getComName());
                detailContent.setText(Html.fromHtml(recruit.getDesc()));
                comContent.setText(Html.fromHtml(recruit.getComIntro()));
                comWebsite.setText(recruit.getWebsite());
                mContext.setInfo(recruit);
            }
        });
		return v;
	}
	
	@OnClick(R.id.info_poster)
	public void onPosterClick(View v) {
		Intent i = new Intent(mContext, BigImageActivity.class);
        i.putExtra(Constants.DEFAULT_BUNDLE_NAME, recruit.getPicture());
		mContext.startActivity(i);
	}
}
