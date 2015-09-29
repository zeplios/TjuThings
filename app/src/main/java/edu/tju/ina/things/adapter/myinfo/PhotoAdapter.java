package edu.tju.ina.things.adapter.myinfo;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import edu.tju.ina.things.R;
import edu.tju.ina.things.adapter.InfoListAdapter;
import edu.tju.ina.things.ui.BigImageActivity;
import edu.tju.ina.things.util.Constants;
import edu.tju.ina.things.util.ImageLoader;
import edu.tju.ina.things.util.InfoUploader;
import edu.tju.ina.things.util.URLHelper;
import edu.tju.ina.things.vo.info.Album;
import edu.tju.ina.things.vo.info.Info;
import edu.tju.ina.things.vo.info.Photo;

public class PhotoAdapter implements InfoListAdapter.IAdapter {

    @Override
    public View getView(final Context context, List<Info> infos, int position, View view, ViewGroup parent) {
        if (!(infos.get(position) instanceof Photo)) {
            return view;
        }
        final Photo info = (Photo) infos.get(position);
        boolean localImage = info.getPicture().startsWith(File.separator);

        ViewHolder holder;
        if (view == null || !view.getTag().getClass().equals(ViewHolder.class)) {
            view = LayoutInflater.from(context).inflate(R.layout.item_my_photo, null);
            holder = new ViewHolder();
            holder.picture = (ImageView) view.findViewById(R.id.image);
            holder.title = (EditText) view.findViewById(R.id.title);
            holder.update = (Button) view.findViewById(R.id.update);
            if (!localImage)
                holder.update.setVisibility(View.VISIBLE);
            holder.setPoster = (Button) view.findViewById(R.id.set_poster);
            if (!localImage)
                holder.setPoster.setVisibility(View.VISIBLE);
            view.setTag(holder);
        }
        else{
            holder = (ViewHolder) view.getTag();
        }

        holder.title.setText(info.getTitle());
        String url = URLHelper.ROOT_URL + info.getPicture();
        if (localImage)
            ImageLoader.getInstance(null).load(info.getPicture(), holder.picture);
        else
            ImageLoader.getInstance(null).load(url, holder.picture);

        final EditText titleEt = holder.title;
        holder.title.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                info.setTitle(titleEt.getText().toString());
            }
        });
        holder.update.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                InfoUploader infoUploader = new InfoUploader.InfoUploaderAdapter((android.app.Activity) context, false);
                infoUploader.addToElement("photo.id", Integer.toString(info.getId()));
                if (!titleEt.getText().toString().equals(info.getDesc())) {
                    infoUploader.addToElement("photo.title", titleEt);
                }
                infoUploader.send(true);
            }
        });
        holder.setPoster.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                InfoUploader infoUploader = new InfoUploader.InfoUploaderAdapter((android.app.Activity) context, false);
                infoUploader.addToElement("album.id", Integer.toString(info.getAlbum().getId()));
                infoUploader.addToElement("album.picture", info.getPicture());
                infoUploader.send(true);
            }
        });
        return view;
    }

	static class ViewHolder {
		ImageView picture;
		EditText title;
		Button update;
        Button setPoster;
	}
}
