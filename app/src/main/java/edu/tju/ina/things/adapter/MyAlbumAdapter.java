package edu.tju.ina.things.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.RoundedImageView;

import org.parceler.Parcels;

import java.util.List;

import edu.tju.ina.things.R;
import edu.tju.ina.things.ui.DetailActivity;
import edu.tju.ina.things.ui.adds.AddAlbumActivity;
import edu.tju.ina.things.ui.updates.UpdateAlbumActivity;
import edu.tju.ina.things.util.Constants;
import edu.tju.ina.things.util.ImageLoader;
import edu.tju.ina.things.util.TimeUtils;
import edu.tju.ina.things.util.URLHelper;
import edu.tju.ina.things.vo.info.Album;
import edu.tju.ina.things.vo.info.Info;
import edu.tju.ina.things.widget.SquareImageView;

public class MyAlbumAdapter extends InfoListAdapter {

    /**
     * 因为我的相册最后有一个添加新项的加号，所以需要单独弄一个Adapter
     * @param context
     * @param list
     */
	public MyAlbumAdapter(Context context, List<Info> list) {
		super(context, list, false, "");
	}
	@Override
	public int getCount() {
		return infos.size() + 1;
	}

	@Override
	public Object getItem(int position) {
		return infos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if( position % 2 == 0){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_timeline_rightalbum, null);
		} else {
			convertView = LayoutInflater.from(context).inflate(R.layout.item_timeline_leftalbum, null);
		}

        SquareImageView poster = (SquareImageView)convertView.findViewById(R.id.poster);
		TextView albumTime = (TextView)convertView.findViewById(R.id.album_time);
		TextView albumDesc = (TextView)convertView.findViewById(R.id.album_desc);
		
		if(position == infos.size()){
			poster.setImageResource(R.drawable.lost_found_plus);
			albumTime.setText(TimeUtils.getCurrentYYYYMMDD(null));
			poster.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent i = new Intent(context, AddAlbumActivity.class);
					context.startActivity(i);
				}
			});
			return convertView;
		} else {
			if (!(infos.get(position) instanceof Album)) {
				return convertView;
			}
			final Album album = (Album) infos.get(position);
			albumDesc.setText(album.getTitle() + "\r\n点此更新");
            albumDesc.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, UpdateAlbumActivity.class);
                    i.putExtra(Constants.DEFAULT_BUNDLE_NAME, Parcels.wrap(album));
                    context.startActivity(i);
                }
            });
			albumTime.setText(TimeUtils.getYYYYMMDD(album.getAddTime(), null));
			String url = URLHelper.ROOT_URL + album.getPicture();
			ImageLoader.getInstance(null).load(url, poster);
			poster.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent i = new Intent(context, DetailActivity.class);
					i.putExtra(Constants.DEFAULT_BUNDLE_NAME, Parcels.wrap(album));
					context.startActivity(i);
				}
			});
		}
		return convertView;
	}
}
