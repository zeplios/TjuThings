package edu.tju.ina.things.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.tju.ina.things.vo.info.Info;

public class InfoListAdapter extends BaseAdapter {

    protected List<Info> infos;
    protected Context context;
    private Map<String, IAdapter> adapters = new HashMap<>();

    private boolean useCommonInfo = false;
    private String subPackageName = "infolist";

    /**
     * 信息的通用适配器，如果列表项中有Lost，那么该列表项的实现默认在infos.LostAdapter中，
     * 该类需要实现IAdapter接口， 如果没有该实现，则默认使用infos.InfoAdapter
     * @param context
     * @param infos
     * @param useCommonInfo 是否只使用通用的Info模板，推荐用false，即每种信息用特有的模板
     * @param subPackageName 放置具体列表项模板类的子Package名
     */
	public InfoListAdapter(Context context, List<Info> infos, boolean useCommonInfo, String subPackageName) {
		this.infos = infos;
		this.context = context;
        this.useCommonInfo = useCommonInfo;
        this.subPackageName = subPackageName;
	}

	@Override
	public int getCount() {
		return infos.size();
	}

	@Override
	public Object getItem(int arg0) {
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
        IAdapter adapter;
        if (useCommonInfo) {
            adapter = getAdapter("Info");
        } else {
            String adapterName = infos.get(position).getClass().getSimpleName();
            adapter = getAdapter(adapterName);
            if (adapter == null) {
                adapter = getAdapter("Info");
            }
        }
        if (adapter != null) {
            view = adapter.getView(context, infos, position, view, parent);
        }
		return view;
	}

    private IAdapter getAdapter(String entityName) {
        IAdapter adapter = adapters.get(entityName);
        if (adapter == null) {
            String className = String.format("%s.%s.%sAdapter",
                    this.getClass().getPackage().getName(), subPackageName, entityName);
            try {
                adapter = (IAdapter) Class.forName(className).newInstance();
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            if (adapter != null) {
                adapters.put(entityName, adapter);
            }
        }
        return adapter;
    }

	public void setInfos(List<Info> infos) {
		this.infos = infos;
	}

	public List<Info> getInfos() {
		return infos;
	}

    /**
     * @param defaultId
     * @return the newest (biggest) infoid, or defaultId when infos.size() == 0
     */
    public int getFirstId(int defaultId) {
        if (infos.size() > 0) {
            return infos.get(0).getId();
        }
        return defaultId;
    }

    /**
     * @param defaultId
     * @return the oldest (smallest) infoid, or defaultId when infos.size() == 0
     */
    public int getLastId(int defaultId) {
        if (infos.size() > 0) {
            return infos.get(infos.size() - 1).getId();
        }
        return defaultId;
    }

    /**
     * 默认为false，即Activity、Lost等各自使用自己的模板。
     * 设置为true之后都使用统一的Info模板
     * @param useCommonInfo 是否只使用通用的Info模板
     */
    public void setUseCommonInfo(boolean useCommonInfo) {
        this.useCommonInfo = useCommonInfo;
    }

    /**
     * 放置具体列表项模板类的子Package名，例如InfoListFragment的模板类都在infolist包下面，
     * MyInfoFragment的模板类都在myinfo包下面。
     * @param subPackageName
     */
    public void setSubPackageName(String subPackageName) {
        this.subPackageName = subPackageName;
    }

    public interface IAdapter {
        public View getView(Context context, List<Info> infos, int position, View view, ViewGroup parent);
    }
}
