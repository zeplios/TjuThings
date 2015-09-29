package edu.tju.ina.things.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.io.File;
import java.util.List;

import edu.tju.ina.things.R;
import edu.tju.ina.things.util.ImageLoader;
import edu.tju.ina.things.util.URLHelper;
import edu.tju.ina.things.widget.SquareImageView;

/**
 * used to preview images selected from album or camera
 */
public class SimpleImgAdapter extends BaseAdapter {
	
	private Context mContext = null;
    private List<String> mPaths = null;
    private ImageLoader imageHelper = ImageLoader.getInstance(null);
    private LayoutParams imageLayoutParam;

    private boolean hasPlusAtLast = false;
    
    public SimpleImgAdapter(Context context, List<String> paths, boolean hasPlusAtLast, LayoutParams imageLayoutParam) {
    	this.mPaths = paths;
        this.mContext = context;
        this.hasPlusAtLast = hasPlusAtLast;
        this.imageLayoutParam = imageLayoutParam;
    }

    public SimpleImgAdapter(Context context, List<String> paths, boolean hasPlusAtLast) {
        this.mPaths = paths;
        this.mContext = context;
        this.hasPlusAtLast = hasPlusAtLast;
        this.imageLayoutParam = null;
    }

    public SimpleImgAdapter(Context context, List<String> paths, LayoutParams imageLayoutParam) {
        this.mPaths = paths;
        this.mContext = context;
        this.hasPlusAtLast = false;
        this.imageLayoutParam = imageLayoutParam;
    }

    @Override
    public int getCount() {
    	if (hasPlusAtLast) {
    		return mPaths.size() + 1;
    	} else {
    		return mPaths.size();
    	}
    }

    @Override
    public String getItem(int position) {
        if (position < 0 || position > mPaths.size()) {
            return null;
        }
        return mPaths.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

	@Override
    public View getView(int position, View view, ViewGroup parent) {
        ImageView iv;
    	if (view == null && imageLayoutParam == null) {
    		iv = new SquareImageView(mContext);
            view = iv;
    	} else if (view == null) {
            iv = new ImageView(mContext);
            iv.setLayoutParams(imageLayoutParam);
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            view = iv;
        } else {
            iv = (SquareImageView) view;
        }
    	if (hasPlusAtLast && position == mPaths.size()) {
    		iv.setImageResource(R.drawable.lost_found_plus);
    	} else {
        	String path = mPaths.get(position);
            if (!path.startsWith(File.separator)) {
                path = URLHelper.ROOT_URL + path;
            }
            imageHelper.load(path, iv);
    	}
        return view;
    }

    public List<String> getPaths() {
		return mPaths;
	}

	public void setPaths(List<String> paths) {
		this.mPaths = paths;
	}

}
