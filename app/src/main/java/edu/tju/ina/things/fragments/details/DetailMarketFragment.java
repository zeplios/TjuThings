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
import edu.tju.ina.things.vo.info.Market;

public class DetailMarketFragment extends BaseFragment {

	private Market market;
	
	@ViewInject(R.id.info_poster) private ImageView image;
	@ViewInject(R.id.info_title) private TextView title;
	@ViewInject(R.id.info_addtime) private TextView addtime;
	@ViewInject(R.id.market_price) private TextView price;
	@ViewInject(R.id.market_contact) private TextView contact;
	@ViewInject(R.id.info_desc) private TextView detailContent;
	@ViewInject(R.id.gallery) private Gallery gallery;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_detail_market, null);
		ViewUtils.inject(this, v);
		
		if (mContext.getInfo() == null || !(mContext.getInfo() instanceof Market))
			return v;
		market = (Market) mContext.getInfo();
		
		String posterUrl = URLHelper.ROOT_URL + market.getPicture();
		ImageLoader.getInstance(null).load(posterUrl, image);
		
		title.setText(market.getTitle());
		addtime.setText(market.getAddTime());
		price.setText(Double.toString(market.getPrice()));
		contact.setText(market.getDesc());
		
        RequestParams params = new RequestParams();
        params.addQueryStringParameter("id", Integer.toString(market.getId()));
        params.addQueryStringParameter("type", Integer.toString(market.getType()));
        HttpClient.get(URLHelper.DETAIL_URL, params, new JSONCallback(mContext) {
            @Override
            public void onSuccess(JSONObject response, ResponseInfo<String> resp) throws JSONException {
                market = new Gson().fromJson(response.getJSONObject("info").toString(), Market.class);
                addtime.setText(market.getAddTime());
                price.setText(Double.toString(market.getPrice()));
                contact.setText(market.getContact());
                detailContent.setText(Html.fromHtml(market.getDesc()));
                gallery.setAdapter(new SimpleImgAdapter(mContext, market.getImages(), new Gallery.LayoutParams(240, 240)));
                gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent i = new Intent(mContext, BigImageActivity.class);
                        i.putStringArrayListExtra(Constants.DEFAULT_BUNDLE_NAME, (ArrayList) market.getImages());
                        i.putExtra(Constants.SECOND_BUNDLE_NAME, position);
                        mContext.startActivity(i);
                    }
                });
                mContext.setInfo(market);
            }
        });
		return v;
	}
	
	@OnClick(R.id.info_poster)
	public void onPosterClick(View v) {
		Intent i = new Intent(mContext, BigImageActivity.class);
        i.putExtra(Constants.DEFAULT_BUNDLE_NAME, market.getPicture());
		mContext.startActivity(i);
	}
}
