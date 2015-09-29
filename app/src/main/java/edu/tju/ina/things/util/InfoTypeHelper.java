package edu.tju.ina.things.util;

import android.content.Context;
import android.content.res.Resources;

import java.util.HashMap;
import java.util.Map;

import edu.tju.ina.things.R;

/**
 * Created by ZhangFC on 2015/2/21.
 */
public class InfoTypeHelper {

    private static InfoTypeHelper instance = new InfoTypeHelper();

    private String voClassPkg;
    private Map<String, Class> voClassCache = new HashMap<>();
    private String addClassPkg;
    private Map<String, Class> addClassCache = new HashMap<>();
    private String detailClassPkg;
    private Map<String, Class> detailClassCache = new HashMap<>();
    // id -> InfoItem
    private Map<Integer, InfoItem> items = new HashMap<>();

    private InfoTypeHelper(){}

    public static InfoTypeHelper getInstance() {
        return instance;
    }

    public void init(Context context) {
        Resources res = context.getResources();
        voClassPkg = res.getString(R.string.vo_class_package);
        addClassPkg = res.getString(R.string.add_class_package);
        detailClassPkg = res.getString(R.string.detail_class_package);

        int [] ids = res.getIntArray(R.array.info_ids);
        String [] chNames = res.getStringArray(R.array.info_ch_names);
        String [] voNames = res.getStringArray(R.array.info_vo_names);
        for (int i = 0 ; i < ids.length ; i++) {
            InfoItem iitem = new InfoItem();
            iitem.id = Integer.toString(ids[i]);
            iitem.chName = chNames[i];
            iitem.voName = voNames[i];
            items.put(ids[i], iitem);
        }
    }

    public Class getVoClass(int typeid) {
        return getVoClass(get(typeid).voName);
    }

    public Class getVoClass(String name) {
        if (voClassCache.containsKey(name)) {
            return voClassCache.get(name);
        }
        String className = String.format(voClassPkg, name);
        try {
            Class c = Class.forName(className);
            voClassCache.put(name, c);
            return c;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public Class getAddClass(int typeid) {
        return getAddClass(get(typeid).voName);
    }

    public Class getAddClass(String name) {
        if (addClassCache.containsKey(name)) {
            return addClassCache.get(name);
        }
        String className = String.format(addClassPkg, name);
        try {
            Class c = Class.forName(className);
            addClassCache.put(name, c);
            return c;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public Class getDetailClass(int typeid) {
        return getDetailClass(get(typeid).voName);
    }

    public Class getDetailClass(String name) {
        if (detailClassCache.containsKey(name)) {
            return detailClassCache.get(name);
        }
        String className = String.format(detailClassPkg, name);
        try {
            Class c = Class.forName(className);
            detailClassCache.put(name, c);
            return c;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public InfoItem [] get(int [] ids) {
        InfoItem [] temps = new InfoItem[ids.length];
        for (int i = 0 ; i < ids.length ; i++) {
            temps[i] = items.get(ids[i]);
        }
        return temps;
    }

    public InfoItem get(int id) {
        return items.get(id);
    }

    public static class InfoItem {
        public String id;
        public String voName;
        public String chName;
    }
}
