package edu.tju.ina.things.fragments;

import android.support.v4.app.Fragment;

public class FragmentFactory {
//	public static SparseArray<Fragment> frags = new SparseArray<Fragment>();
	
	public static Fragment createInstance (int index){
//		if (frags.get(index) != null) {
//			return frags.get(index);
//		}
		Fragment f;
		switch (index) {
		case 0:
			f = new InfoListFragment();
			return f;
		case 1:
			f = new CollectFragment();
			return f;
		case 2:
			f = new MyAlbumFragment();
			return f;
        case 3:
            f = new MyInfoFragment();
            return f;
		default:
			return null;
		}
	}
}
