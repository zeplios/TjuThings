package edu.tju.ina.things.adapter.myinfo;

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
import edu.tju.ina.things.util.InfoTypeHelper;
import edu.tju.ina.things.util.URLHelper;
import edu.tju.ina.things.vo.info.Info;

public class InfoAdapter implements InfoListAdapter.IAdapter {

    @Override
    public View getView(final Context context, List<Info> infos,
                                int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null || !view.getTag().getClass().equals(ViewHolder.class)) {
            view = LayoutInflater.from(context).inflate(R.layout.item_info, null);
            holder = new ViewHolder();
            holder.image = (ImageView) view.findViewById(R.id.info_image);
            holder.name = (TextView) view.findViewById(R.id.info_name);
            holder.type = (TextView) view.findViewById(R.id.info_type);
            holder.favour = (TextView) view.findViewById(R.id.info_favour);
            holder.share = (TextView) view.findViewById(R.id.info_share);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final Info info = infos.get(position);
        holder.name.setText(info.getTitle());
        holder.type.setText("信息类型：" + InfoTypeHelper.getInstance().get(info.getType()).chName);
        holder.favour.setText(String.valueOf(info.getViewCount()));
        holder.share.setText("0");

        holder.image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, BigImageActivity.class);
                i.putExtra(Constants.DEFAULT_BUNDLE_NAME, info.getPicture());
                context.startActivity(i);
            }
        });
        if ("0".equals(info.getPicture())) {
            info.setPicture("default.jpeg");
        }
        String url = URLHelper.ROOT_URL + info.getPicture();
        ImageLoader.getInstance(null).load(url, holder.image);

        return view;
    }

    static class ViewHolder {
		ImageView image;
		TextView name;
		TextView type;
		TextView favour;
		TextView share;
	}
}
