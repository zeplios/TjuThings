package edu.tju.ina.things.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import edu.tju.ina.things.util.ImageLoader;
import edu.tju.ina.things.util.URLHelper;
import edu.tju.ina.things.vo.info.Photo;
import edu.tju.ina.things.widget.SquareImageView;

/**
 * used to preview images selected from album or camera
 */
public class InfoPhotoAdapter extends BaseAdapter {
	
	private Context mContext = null;
    private List<Photo> photos = null;

    public InfoPhotoAdapter(Context context, List<Photo> photos) {
    	this.photos = photos;
        this.mContext = context;
    }

    @Override
    public int getCount() {
    	return photos.size();
    }

    @Override
    public Photo getItem(int position) {
        if (position < 0 || position > photos.size()) {
            return null;
        }
        return photos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
	@Override
    public View getView(int position, View view, ViewGroup parent) {
    	if (view == null) {
    		view = new SquareImageView(mContext);
    	}
        SquareImageView iv = (SquareImageView)view;

    	String path = URLHelper.ROOT_URL + photos.get(position).getPicture();
		ImageLoader.getInstance(null).load(path, iv);

        return view;
    }

    public List<Photo> getPaths() {
		return photos;
	}

	public void setPaths(List<Photo> photos) {
		this.photos = photos;
	}

}
