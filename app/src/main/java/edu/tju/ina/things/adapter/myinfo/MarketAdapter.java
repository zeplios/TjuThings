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
import edu.tju.ina.things.util.URLHelper;
import edu.tju.ina.things.vo.info.Info;
import edu.tju.ina.things.vo.info.Market;

public class MarketAdapter implements InfoListAdapter.IAdapter {

    @Override
    public View getView(final Context context, List<Info> infos, int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null || !view.getTag().getClass().equals(ViewHolder.class)) {
            view = LayoutInflater.from(context).inflate(R.layout.item_infomarket,
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

        if (!(infos.get(position) instanceof Market)) {
            return view;
        }
        final Market minfo = (Market) infos.get(position);
        holder.name.setText(minfo.getTitle());
        holder.price.setText(Double.toString(minfo.getPrice()));
        holder.addtime.setText(minfo.getAddTime());
        holder.favour.setText(String.valueOf(minfo.getViewCount()));
        holder.share.setText("0");

        if (minfo.isComplete()) {
            holder.late.setVisibility(View.VISIBLE);
        } else {
            holder.late.setVisibility(View.GONE);
        }

        holder.image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, BigImageActivity.class);
                i.putExtra(Constants.DEFAULT_BUNDLE_NAME, minfo.getPicture());
                context.startActivity(i);
            }
        });
        String url = URLHelper.ROOT_URL + minfo.getPicture();
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
