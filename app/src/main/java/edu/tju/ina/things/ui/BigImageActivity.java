package edu.tju.ina.things.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import edu.tju.ina.things.R;
import edu.tju.ina.things.util.Constants;
import edu.tju.ina.things.util.ImageLoader;
import edu.tju.ina.things.util.URLHelper;
import uk.co.senab.photoview.PhotoView;

public class BigImageActivity extends Activity {
	
	private List<String> urls;
	@ViewInject(R.id.view_pager)
    private ViewPager viewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.element_viewpager);
        ViewUtils.inject(this);

		Intent i = getIntent();
        Object obj = i.getExtras().get(Constants.DEFAULT_BUNDLE_NAME);
        if (obj instanceof List) {
            urls = (List)obj;
        } else {
            urls = new ArrayList<>();
            urls.add(obj.toString());
        }
        int position = i.getIntExtra(Constants.SECOND_BUNDLE_NAME, 0);
        viewPager.setAdapter(new MyPagerAdapter());
        viewPager.setCurrentItem(position);
	}

    class MyPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return urls.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            PhotoView photoView = new PhotoView(container.getContext());
            String imageUrl = URLHelper.ROOT_URL + urls.get(position);
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
