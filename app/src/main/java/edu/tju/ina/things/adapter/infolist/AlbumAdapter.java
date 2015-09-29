package edu.tju.ina.things.adapter.infolist;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import edu.tju.ina.things.R;
import edu.tju.ina.things.adapter.InfoListAdapter;
import edu.tju.ina.things.ui.BigImageActivity;
import edu.tju.ina.things.util.Constants;
import edu.tju.ina.things.util.ImageLoader;
import edu.tju.ina.things.util.URLHelper;
import edu.tju.ina.things.vo.info.Album;
import edu.tju.ina.things.vo.info.Info;

public class AlbumAdapter implements InfoListAdapter.IAdapter {

    @Override
    public View getView(final Context context, List<Info> infos, int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null || !view.getTag().getClass().equals(ViewHolder.class)) {
            view = LayoutInflater.from(context).inflate(R.layout.item_infoalbum,
                    null);
            holder = new ViewHolder();
            holder.poster = (ImageView) view.findViewById(R.id.info_poster);
            holder.userAvatar = (ImageView) view.findViewById(R.id.user_avatar);
            holder.username = (TextView) view.findViewById(R.id.user_name);
            holder.time = (TextView) view.findViewById(R.id.add_time);
            holder.favour = (TextView) view.findViewById(R.id.photo_favour);
            holder.share = (TextView) view.findViewById(R.id.photo_share);
            view.setTag(holder);
        }
        else{
            holder = (ViewHolder) view.getTag();
        }

        if (!(infos.get(position) instanceof Album)) {
            return view;
        }
        final Album info = (Album) infos.get(position);
        holder.username.setText(info.getUser().getUsername());
        holder.time.setText("创建时间" + info.getAddTime());
        holder.favour.setText(String.valueOf(info.getViewCount()));
        holder.share.setText("0");
        holder.poster.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, BigImageActivity.class);
                i.putExtra(Constants.DEFAULT_BUNDLE_NAME, info.getPicture());
                context.startActivity(i);
            }
        });
        String url = URLHelper.ROOT_URL + info.getPicture();
        String userAvatarUrl = URLHelper.ROOT_URL + info.getUser().getAvatar();

        ImageLoader.getInstance(null).load(url, holder.poster);
        ImageLoader.getInstance(null).load(userAvatarUrl, holder.userAvatar);
        return view;
    }

	static class ViewHolder {
		ImageView poster;
		ImageView userAvatar;
		TextView username;
		TextView time;
		TextView favour;
		TextView share;
	}
}
