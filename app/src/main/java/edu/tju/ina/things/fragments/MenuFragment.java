package edu.tju.ina.things.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import edu.tju.ina.things.InfoApplication;
import edu.tju.ina.things.R;
import edu.tju.ina.things.adapter.LeftMenuAdapter;
import edu.tju.ina.things.net.HttpClient;
import edu.tju.ina.things.ui.LoginActivity;
import edu.tju.ina.things.ui.MenuMainActivity;
import edu.tju.ina.things.ui.ModifyAvatarActivity;
import edu.tju.ina.things.ui.PersonalSettingActivity;
import edu.tju.ina.things.util.LoginManager;
import edu.tju.ina.things.util.Utils;

public class MenuFragment extends ListFragment {
	
	private int currentSelected = 0;

    private Activity context;
	@ViewInject(R.id.user_avatar) ImageView avatar;
	@ViewInject(R.id.user_name) TextView name;
	
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_menu, null);
		ViewUtils.inject(this, v);
        context = getActivity();
		
		LeftMenuAdapter adapter = new LeftMenuAdapter(getActivity());
		setListAdapter(adapter);
		return v;
	}
	
	@Override
	public void onListItemClick(ListView lv, View v, int position, long id) {
		if (position == lv.getCount() - 1) {
            Utils.toast(context, "已退出登录");
			InfoApplication.currentUser = null;
			HttpClient.clearCookie();
			LoginManager lm = new LoginManager(getActivity());
			lm.clearSp();
			onResume();
		}
		currentSelected = position;
		Fragment newContent = FragmentFactory.createInstance(position);
		if (newContent != null)
			switchFragment(newContent);
	}
	
	// the meat of switching the above fragment
	private void switchFragment(Fragment fragment) {
		if (context instanceof MenuMainActivity) {
			MenuMainActivity mma = (MenuMainActivity) context;
			mma.switchContent(fragment);
		}
	}

	public int getCurrentSelected() {
		return currentSelected;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (InfoApplication.currentUser != null) {
            InfoApplication.setNameAndAvatar(name, avatar);
			name.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context, PersonalSettingActivity.class);
					context.startActivity(intent);
				}
			});
            avatar.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ModifyAvatarActivity.class);
                    context.startActivity(intent);
                }
            });
		} else {
			name.setText(context.getResources().getString(R.string.left_menu_login_hint));
			avatar.setImageResource(R.drawable.menu_login);
			name.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(context, LoginActivity.class);
					startActivity(intent);
				}
			});
		}
	}
}
