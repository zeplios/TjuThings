package edu.tju.ina.things.adapter.myinfo;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.parceler.Parcels;

import java.util.List;

import edu.tju.ina.things.R;
import edu.tju.ina.things.adapter.InfoListAdapter;
import edu.tju.ina.things.ui.BigImageActivity;
import edu.tju.ina.things.ui.updates.UpdateLostActivity;
import edu.tju.ina.things.util.Constants;
import edu.tju.ina.things.util.ImageLoader;
import edu.tju.ina.things.util.URLHelper;
import edu.tju.ina.things.vo.info.Info;
import edu.tju.ina.things.vo.info.Lost;

public class LostAdapter implements InfoListAdapter.IAdapter {

    @Override
    public View getView(final Context context, List<Info> infos, int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null || !view.getTag().getClass().equals(ViewHolder.class)) {
            view = LayoutInflater.from(context).inflate(R.layout.item_infolost,
                    null);
            holder = new ViewHolder();
            holder.image = (ImageView) view.findViewById(R.id.market_image);
            holder.name = (TextView) view.findViewById(R.id.market_name);
            holder.price = (TextView) view.findViewById(R.id.market_price);
            holder.addtime = (TextView) view.findViewById(R.id.market_addtime);
            holder.favour = (TextView) view.findViewById(R.id.info_favour);
            holder.share = (TextView) view.findViewById(R.id.info_share);
            holder.late = (ImageView) view.findViewById(R.id.info_late);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        if (!(infos.get(position) instanceof Lost)) {
            return view;
        }
        final Lost info = (Lost) infos.get(position);
        holder.name.setText(info.getTitle());
        if (info.isOwner()) {
            holder.price.setText("寻找失物");
        } else {
            holder.price.setText("寻找失主");
        }
        holder.addtime.setText(info.getAddTime());
        holder.favour.setText(String.valueOf(info.getViewCount()));
        holder.share.setText("编辑");

        if (info.isComplete()) {
            holder.late.setVisibility(View.VISIBLE);
        } else {
            holder.late.setVisibility(View.GONE);
        }

        holder.image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, BigImageActivity.class);
                i.putExtra(Constants.DEFAULT_BUNDLE_NAME, info.getPicture());
                context.startActivity(i);
            }
        });
        holder.share.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, UpdateLostActivity.class);
                i.putExtra(Constants.DEFAULT_BUNDLE_NAME, Parcels.wrap(info));
                context.startActivity(i);
            }
        });
        String url = URLHelper.ROOT_URL + info.getPicture();
        ImageLoader.getInstance(null).load(url, holder.image);

        return view;
    }

    static class ViewHolder {
		ImageView image;
		TextView name;
		TextView price;
		TextView addtime;
		TextView favour;
		TextView share;
		
		ImageView late;
	}
}
