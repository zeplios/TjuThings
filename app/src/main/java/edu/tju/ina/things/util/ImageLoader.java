package edu.tju.ina.things.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import edu.tju.ina.things.R;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.DefaultBitmapLoadCallBack;

public class ImageLoader {
	
	private static ImageLoader instance;
	private static BitmapUtils bitmapUtils;

	private BitmapDisplayConfig config;
	private BitmapDisplayConfig configOrigin;
	BitmapLoadCallBack<ImageView> callback;
	BitmapLoadCallBack<ImageView> avatarCallback;

	private ImageLoader(Context context) {
		bitmapUtils = new BitmapUtils(context);
		config = new BitmapDisplayConfig();
		configOrigin = new BitmapDisplayConfig();

		config.setBitmapConfig(Bitmap.Config.RGB_565);
		config.setShowOriginal(false);
		configOrigin.setBitmapConfig(Bitmap.Config.ARGB_8888);
		configOrigin.setShowOriginal(true);

		callback = new DefaultBitmapLoadCallBack<ImageView>() {
			
			@Override
			public void onPreLoad(ImageView container, String uri,
					BitmapDisplayConfig config) {
				super.onPreLoad(container, uri, config);
				container.setImageResource(R.drawable.pic_thumb);
			}

			@Override
			public void onLoadFailed(ImageView container, String uri,
					Drawable drawable) {
				super.onLoadFailed(container, uri, drawable);
				container.setImageResource(R.drawable.splash);
			}
		};
		
		avatarCallback = new DefaultBitmapLoadCallBack<ImageView>() {
			
			@Override
			public void onPreLoad(ImageView container, String uri,
					BitmapDisplayConfig config) {
				super.onPreLoad(container, uri, config);
				container.setImageResource(R.drawable.menu_login);
			}

			@Override
			public void onLoadFailed(ImageView container, String uri,
					Drawable drawable) {
				super.onLoadFailed(container, uri, drawable);
				container.setImageResource(R.drawable.menu_login);
			}
		};
	}

    /**
     * 获取加载器对象
     * @param context 第一次使用的时候传入ApplicationContext
     * @return
     */
	public static ImageLoader getInstance(Context context) {
		if (instance == null) {
			instance = new ImageLoader(context);
		}
		return instance;
	}

	public void load(String url, ImageView image) {
		bitmapUtils.display(image, url, config, callback);
	}
	
	public void loadOrigin(String url, ImageView image) {
		bitmapUtils.display(image, url, configOrigin, callback);
	}

    public void loadOrigin(String url, ImageView image, BitmapLoadCallBack<ImageView> callback) {
        bitmapUtils.display(image, url, configOrigin, callback);
    }

	public void loadAvatar(String url, ImageView image) {
		bitmapUtils.display(image, url, config, avatarCallback);
	}
}
