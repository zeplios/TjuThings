package edu.tju.ina.things.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import java.util.ArrayList;
import java.util.List;

import edu.tju.ina.things.R;
import edu.tju.ina.things.util.ImageLoader;
import edu.tju.ina.things.widget.SquareImageView;

/**
 * used to select images
 */
public class ImgChooserAdapter extends BaseAdapter {

    private Context mContext = null;

    private ArrayList<String> mDataList = new ArrayList<>();
    private ArrayList<String> mSelectedList = new ArrayList<>();

    public ImgChooserAdapter(Context context, ArrayList<String> list) {
        mDataList = list;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public String getItem(int position) {
        if (position < 0 || position > mDataList.size()) {
            return null;
        }
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
	@Override
    public View getView(int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.image_list_item, null);
            holder.mImageIv = (SquareImageView)view.findViewById(R.id.list_item_iv);
            holder.mClickArea = view.findViewById(R.id.list_item_cb_click_area);
            holder.mSelectedCb = (CheckBox)view.findViewById(R.id.list_item_cb);
            view.setTag(holder);
        } else {
            holder = (ViewHolder)view.getTag();
        }

        final String path = getItem(position);
        ImageLoader.getInstance(null).load(path, holder.mImageIv);

        holder.mSelectedCb.setChecked(false);
        // 该图片是否已选中
        for (String selected : mSelectedList) {
            if (selected.equals(path)) {
                holder.mSelectedCb.setChecked(true);
            }
        }

        // 可点区域单击事件
        holder.mClickArea.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = holder.mSelectedCb.isChecked();
                holder.mSelectedCb.setChecked(!checked);
                if (!checked) {
                    addImage(path);
                } else {
                    deleteImage(path);
                }
            }
        });

        return view;
    }

    /**
     * 将图片地址添加到已选择列表
     * @param path
     */
    private void addImage(String path) {
        if (mSelectedList.contains(path)) {
            return;
        }
        mSelectedList.add(path);
    }

    /**
     * 将图片地址从已选择列表中删除
     * @param path
     */
    private void deleteImage(String path) {
        mSelectedList.remove(path);
    }

    /**
     * 获取已选中的图片列表
     * @return
     */
    public ArrayList<String> getSelectedImgs() {
        return mSelectedList;
    }

    public void setSelectedImgs(ArrayList<String> selectedImgs) {
        this.mSelectedList = selectedImgs;
    }

    /**
     * 获取所有待选择的图片列表
     * @return
     */
    public ArrayList<String> getDataImgs() {
        return mDataList;
    }

    public void setDataImgs(ArrayList<String> dataList) {
        this.mDataList = dataList;
    }

    static class ViewHolder {
        public SquareImageView mImageIv;
        public View mClickArea;
        public CheckBox mSelectedCb;
    }

}
