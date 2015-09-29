package edu.tju.ina.things.fragments.details;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import edu.tju.ina.things.R;
import edu.tju.ina.things.net.HttpClient;
import edu.tju.ina.things.net.JSONCallback;
import edu.tju.ina.things.util.Constants;
import edu.tju.ina.things.util.ImageLoader;
import edu.tju.ina.things.util.URLHelper;
import edu.tju.ina.things.vo.info.Photo;
import uk.co.senab.photoview.PhotoView;

public class DetailPhotoFragment extends BaseFragment {

    private List<Photo> photos;
    @ViewInject(R.id.view_pager)
    private ViewPager viewPager;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.element_viewpager, null);
		ViewUtils.inject(this, v);

        Intent i = mContext.getIntent();
        photos = Parcels.unwrap(i.getParcelableExtra(Constants.SECOND_BUNDLE_NAME));
        if (photos == null) {
            photos = new ArrayList<>();
            photos.add((Photo) mContext.getInfo());
        }
        viewPager.setAdapter(new MyPagerAdapter());
        viewPager.setCurrentItem(i.getIntExtra(Constants.THIRD_BUNDLE_NAME, 0));
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                final Photo photo = photos.get(position);
                mContext.setInfo(photo);
                RequestParams params = new RequestParams();
                params.addQueryStringParameter("id", Integer.toString(photo.getId()));
                params.addQueryStringParameter("type", Integer.toString(photo.getType()));
                HttpClient.get(URLHelper.DETAIL_URL, params, new JSONCallback(mContext) {
                    @Override
                    public void onSuccess(JSONObject response, ResponseInfo<String> resp) throws JSONException {
                        Photo p = new Gson().fromJson(response.getJSONObject("info").toString(), Photo.class);
                        if (p.getId() == mContext.getInfo().getId()) {
                            mContext.setInfo(p);
                        }
                    }
                });
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
		return v;
	}

    class MyPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return photos.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            PhotoView photoView = new PhotoView(container.getContext());
            String imageUrl = URLHelper.ROOT_URL + photos.get(position).getPicture();
            ImageLoader.getInstance(null).loadOrigin(imageUrl, photoView);
            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}