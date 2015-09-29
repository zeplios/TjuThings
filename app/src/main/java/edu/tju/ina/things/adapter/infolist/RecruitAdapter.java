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
import edu.tju.ina.things.vo.info.Info;
import edu.tju.ina.things.vo.info.Recruit;

public class RecruitAdapter implements InfoListAdapter.IAdapter {
    @Override
    public View getView(final Context context, List<Info> infos, int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null || !view.getTag().getClass().equals(ViewHolder.class)) {
            view = LayoutInflater.from(context).inflate(R.layout.item_infoactivity, null);
            holder = new ViewHolder();
            holder.image = (ImageView) view.findViewById(R.id.activity_image);
            holder.name = (TextView) view.findViewById(R.id.activity_name);
            holder.place = (TextView) view.findViewById(R.id.activity_place);
            holder.time = (TextView) view.findViewById(R.id.activity_time);
            holder.favour = (TextView) view.findViewById(R.id.info_favour);
            holder.share = (TextView) view.findViewById(R.id.info_share);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        if (!(infos.get(position) instanceof Recruit)) {
            return view;
        }
        final Recruit rinfo = (Recruit) infos.get(position);
        holder.name.setText(rinfo.getTitle());
        holder.place.setText(rinfo.getArea());
        holder.time.setText("时间" + rinfo.getTime());
        holder.favour.setText(String.valueOf(rinfo.getViewCount()));
        holder.share.setText("0");

        holder.image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, BigImageActivity.class);
                i.putExtra(Constants.DEFAULT_BUNDLE_NAME, rinfo.getPicture());
                context.startActivity(i);
            }
        });
        String url = URLHelper.ROOT_URL + rinfo.getPicture();
        ImageLoader.getInstance(null).load(url, holder.image);

        return view;
    }

    static class ViewHolder {
		ImageView image;
		TextView name;
		TextView place;
		TextView time;
		TextView favour;
		TextView share;
	}
}
