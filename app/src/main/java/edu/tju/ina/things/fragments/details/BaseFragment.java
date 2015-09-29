package edu.tju.ina.things.fragments.details;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.tju.ina.things.ui.DetailActivity;

public class BaseFragment extends Fragment {
	
	protected DetailActivity mContext;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = (DetailActivity) getActivity();
		return super.onCreateView(inflater, container, savedInstanceState);
	}
}
