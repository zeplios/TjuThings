package edu.tju.ina.things.fragments.details;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import org.parceler.Parcels;

import edu.tju.ina.things.R;
import edu.tju.ina.things.adapter.InfoPhotoAdapter;
import edu.tju.ina.things.net.HttpClient;
import edu.tju.ina.things.net.JSONCallback;
import edu.tju.ina.things.ui.BigImageActivity;
import edu.tju.ina.things.ui.DetailActivity;
import edu.tju.ina.things.util.Constants;
import edu.tju.ina.things.util.ImageLoader;
import edu.tju.ina.things.util.TimeUtils;
import edu.tju.ina.things.util.URLHelper;
import edu.tju.ina.things.vo.info.Album;
import edu.tju.ina.things.widget.NoScrollGridView;

public class DetailAlbumFragment extends BaseFragment {
	
	private Album album;
	
	@ViewInject(R.id.user_avatar) private ImageView userAvatar;
	@ViewInject(R.id.user_name) private TextView username;
	@ViewInject(R.id.current_time) private TextView addtime;
	@ViewInject(R.id.info_title) private TextView titleTv;
	@ViewInject(R.id.info_poster) private ImageView poster;
	@ViewInject(R.id.info_desc) private TextView detailContent;
	@ViewInject(R.id.photos) private NoScrollGridView photos;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_detail_album, null);
		ViewUtils.inject(this, v);
		mContext.getSupportActionBar().show();
        mContext.getSupportActionBar().setTitle(mContext.getResources().getString(R.string.album_title));

		if (mContext.getInfo() == null || !(mContext.getInfo() instanceof Album))
			return v;
		album = (Album) mContext.getInfo();

		String posterUrl = URLHelper.ROOT_URL + album.getPicture();
		String avatarUrl = URLHelper.ROOT_URL + album.getUser().getAvatar();
		ImageLoader.getInstance(null).load(posterUrl, poster);
		ImageLoader.getInstance(null).load(avatarUrl, userAvatar);
		
		username.setText(album.getUser().getUsername());
		titleTv.setText(album.getTitle());
		addtime.setText(TimeUtils.getYYYYMMDD(album.getAddTime(), null));
		detailContent.setText(album.getDesc());
		
        RequestParams params = new RequestParams();
        params.addQueryStringParameter("id", Integer.toString(album.getId()));
        params.addQueryStringParameter("type", Integer.toString(album.getType()));
        HttpClient.get(URLHelper.DETAIL_URL, params, new JSONCallback(mContext) {
            @Override
            public void onSuccess(JSONObject response, ResponseInfo<String> resp) throws JSONException {
                album = new Gson().fromJson(response.getJSONObject("info").toString(), Album.class);
                addtime.setText(album.getAddTime());
                detailContent.setText(Html.fromHtml(album.getDesc()));
                photos.setAdapter(new InfoPhotoAdapter(mContext, album.getPhotos()));
                photos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent i = new Intent(mContext, DetailActivity.class);
                        i.putExtra(Constants.DEFAULT_BUNDLE_NAME, Parcels.wrap(album.getPhotos().get(position)));
                        i.putExtra(Constants.SECOND_BUNDLE_NAME, Parcels.wrap(album.getPhotos()));
                        i.putExtra(Constants.THIRD_BUNDLE_NAME, position);
                        mContext.startActivity(i);
                    }
                });
                mContext.setInfo(album);
            }
        });
		
		return v;
	}
	
	@OnClick(R.id.info_poster)
	public void onPosterClick(View v) {
		Intent i = new Intent(mContext, BigImageActivity.class);
        i.putExtra(Constants.DEFAULT_BUNDLE_NAME, album.getPicture());
		mContext.startActivity(i);
	}
}