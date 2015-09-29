package edu.tju.ina.things.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import edu.tju.ina.things.InfoApplication;
import edu.tju.ina.things.R;
import edu.tju.ina.things.net.HttpClient;
import edu.tju.ina.things.net.JSONCallback;
import edu.tju.ina.things.util.Constants;
import edu.tju.ina.things.util.InfoTypeHelper;
import edu.tju.ina.things.util.URLHelper;
import edu.tju.ina.things.util.Utils;
import edu.tju.ina.things.vo.info.Info;

public class DetailActivity extends ActionBarActivity {

	@ViewInject(R.id.detail_favour) protected ImageView imageThumbup;
	@ViewInject(R.id.detail_collect) protected ImageView imageCollect;
    @ViewInject(R.id.thumbup_num_tv) protected TextView thumbupNum;
    @ViewInject(R.id.comment_num_tv) protected TextView commentNum;
    @ViewInject(R.id.share_num_tv) protected TextView collectNum;

	private Info info = null;

	static final int COLLECT = 1;
	static final int THUMBUP = 2;
	static final int CANCOLLECT = 3;
	static final int CANTHUMBUP = 4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_detail);
		ViewUtils.inject(this);
        setActioinBar();

        // 如果是Photo列表，第一个参数请穿任意一个Photo对象，Photo列表传到第二个参数里
		info = Parcels.unwrap(getIntent().getParcelableExtra(Constants.DEFAULT_BUNDLE_NAME));
		updateStatusWhenInit(info);

        try {
            Fragment detailFragment = (Fragment) InfoTypeHelper.getInstance().getDetailClass(info.getType()).newInstance();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment, detailFragment)
                    .commit();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
	}

	public Info getInfo() {
		return info;
	}

	public void setInfo(Info info) {
		this.info = info;
		updateStatusWhenInit(info);
	}

	@OnClick(value = { R.id.detail_favour, R.id.detail_collect })
	public void onClick(View view) {
		if (InfoApplication.currentUser == null) {
            Utils.toast(this, "请先登录");
			return;
		}
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("info.id", Integer.toString(info.getId()));
		params.addQueryStringParameter("type", Integer.toString(info.getType()));
		if (view.getId() == R.id.detail_collect) {
			if (info.isHasCollected()) {
				HttpClient.delete(URLHelper.COLLECT_URL, params,
						new JsonForCollectOrThumbupHandler(CANCOLLECT,
								(ImageView) view, info));
			} else {
				HttpClient.post(URLHelper.COLLECT_URL, params,
						new JsonForCollectOrThumbupHandler(COLLECT,
								(ImageView) view, info));
			}
		} else if (view.getId() == R.id.detail_favour) {
			if (info.isHasThumbuped()) {
				HttpClient.delete(URLHelper.THUMBUP_URL, params,
						new JsonForCollectOrThumbupHandler(CANTHUMBUP,
								(ImageView) view, info));
			} else {
				HttpClient.post(URLHelper.THUMBUP_URL, params,
						new JsonForCollectOrThumbupHandler(THUMBUP,
								(ImageView) view, info));
			}
		}
	}

	class JsonForCollectOrThumbupHandler extends JSONCallback {

		int action;
		ImageView view;

		public JsonForCollectOrThumbupHandler(int action, ImageView view,
				Info info) {
			super(DetailActivity.this);
			this.action = action;
			this.view = view;
		}

		@Override
		public void onSuccess(JSONObject response, ResponseInfo<String> resp) throws JSONException {
            int errCode = response.getInt("errcode");
            String errMsg = response.getString("errmsg");
            if (errCode != 0) {
                Utils.toast(DetailActivity.this, errMsg);
                return;
            }

            Utils.toast(DetailActivity.this, errMsg);
			switch (action) {
			case COLLECT:
				info.setHasCollected(true);
				view.setImageResource(R.drawable.info_detail_collected);
				break;
			case CANCOLLECT:
				info.setHasCollected(false);
				view.setImageResource(R.drawable.info_detail_collect);
				break;
			case THUMBUP:
				info.setHasThumbuped(true);
				view.setImageResource(R.drawable.info_detail_thumbuped);
				break;
			case CANTHUMBUP:
				info.setHasThumbuped(false);
				view.setImageResource(R.drawable.info_detail_thumbup);
				break;
			default:
				break;
			}
		}
	}

	/**
	 * a detail json for current info is returned from the server when enter
	 * this activity, update the collected and thumbuped status if necessary
	 * 
	 * @param info
	 */
	private void updateStatusWhenInit(Info info) {
		boolean collect = info.isHasCollected();
		boolean thumbup = info.isHasThumbuped();
		if (imageCollect != null) {
			if (collect) {
				imageCollect.setImageResource(R.drawable.info_detail_collected);
			} else {
				imageCollect.setImageResource(R.drawable.info_detail_collect);
			}
		}
		if (imageThumbup != null) {
			if (thumbup) {
				imageThumbup.setImageResource(R.drawable.info_detail_thumbuped);
			} else {
				imageThumbup.setImageResource(R.drawable.info_detail_thumbup);
			}
		}
        thumbupNum.setText(Integer.toString(info.getThumbupCount()));
        commentNum.setText(Integer.toString(info.getCommentCount()));
        collectNum.setText(Integer.toString(info.getCollectCount()));
	}

    private void setActioinBar() {
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.title_back);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().hide();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
