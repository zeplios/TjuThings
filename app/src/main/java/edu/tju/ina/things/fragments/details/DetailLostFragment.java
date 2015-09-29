package edu.tju.ina.things.fragments.details;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Gallery;
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

import java.util.ArrayList;

import edu.tju.ina.things.R;
import edu.tju.ina.things.adapter.SimpleImgAdapter;
import edu.tju.ina.things.net.HttpClient;
import edu.tju.ina.things.net.JSONCallback;
import edu.tju.ina.things.ui.BigImageActivity;
import edu.tju.ina.things.util.Constants;
import edu.tju.ina.things.util.ImageLoader;
import edu.tju.ina.things.util.URLHelper;
import edu.tju.ina.things.vo.info.Lost;

public class DetailLostFragment extends BaseFragment {

	private Lost lost;
	
	@ViewInject(R.id.info_poster) private ImageView image;
	@ViewInject(R.id.info_title) private TextView title;
	@ViewInject(R.id.info_addtime) private TextView addtime;
	@ViewInject(R.id.lost_place) private TextView place;
	@ViewInject(R.id.lost_contact) private TextView contact;
	@ViewInject(R.id.info_desc) private TextView detailContent;
	@ViewInject(R.id.gallery) private Gallery gallery;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_detail_lost, null);
		ViewUtils.inject(this, v);
		
		if (mContext.getInfo() == null || !(mContext.getInfo() instanceof Lost))
			return v;
		lost = (Lost) mContext.getInfo();
		
		String posterUrl = URLHelper.ROOT_URL + lost.getPicture();
		ImageLoader.getInstance(null).load(posterUrl, image);
		
		title.setText(lost.getTitle());
		addtime.setText(lost.getAddTime());
		place.setText(lost.getPlace());
		contact.setText(lost.getDesc());
		
        RequestParams params = new RequestParams();
        params.addQueryStringParameter("id", Integer.toString(lost.getId()));
        params.addQueryStringParameter("type", Integer.toString(lost.getType()));
        HttpClient.get(URLHelper.DETAIL_URL, params, new JSONCallback(mContext) {
            @Override
            public void onSuccess(JSONObject response, ResponseInfo<String> resp) throws JSONException {
                lost = new Gson().fromJson(response.getJSONObject("info").toString(), Lost.class);
                addtime.setText(lost.getAddTime());
                place.setText(lost.getPlace());
                contact.setText(lost.getContact());
                detailContent.setText(Html.fromHtml(lost.getDesc()));
                gallery.setAdapter(new SimpleImgAdapter(mContext, lost.getImages(), new Gallery.LayoutParams(240, 240)));
                gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent i = new Intent(mContext, BigImageActivity.class);
                        i.putStringArrayListExtra(Constants.DEFAULT_BUNDLE_NAME, (ArrayList) lost.getImages());
                        i.putExtra(Constants.SECOND_BUNDLE_NAME, position);
                        mContext.startActivity(i);
                    }
                });
                mContext.setInfo(lost);
            }
        });
		return v;
	}
	
	@OnClick(R.id.info_poster)
	public void onPosterClick(View v) {
		Intent i = new Intent(mContext, BigImageActivity.class);
		i.putExtra(Constants.DEFAULT_BUNDLE_NAME, lost.getPicture());
		mContext.startActivity(i);
	}
}