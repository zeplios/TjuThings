package edu.tju.ina.things.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import edu.tju.ina.things.R;

public class LeftMenuAdapter extends BaseAdapter {
	Context context;
	String[] items;
	int[] itemImgs;

	public LeftMenuAdapter(Context context) {
		super();
		this.context = context;
		items = context.getResources().getStringArray(R.array.left_menu);
		itemImgs = context.getResources().getIntArray(R.array.left_menu_img);
		TypedArray ta = context.getResources().obtainTypedArray(R.array.left_menu_img);
		itemImgs = new int[ta.length()];
		for (int i = 0 ; i < ta.length() ; i++) {
			itemImgs[i] = ta.getResourceId(i, 0);
		}
		ta.recycle();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return items.length;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		// TODO Auto-generated method stub

		ViewHolder holder = null;
		if (view == null) {
			view = LayoutInflater.from(context).inflate(R.layout.item_leftmenu,
					null);

			holder = new ViewHolder();
			holder.image = (ImageView) view.findViewById(R.id.menu_image);
			holder.name = (TextView) view.findViewById(R.id.menu_item);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		holder.image.setImageResource(itemImgs[position]);
		holder.name.setText(items[position]);
		return view;
	}

	static class ViewHolder {
		ImageView image;
		TextView name;
	}
}
