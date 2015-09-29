package edu.tju.ina.things.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by ZhangFC on 2015/2/25.
 */
public class Utils {
    public static void toast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void toast(Context context, int resid) {
        Toast.makeText(context, resid, Toast.LENGTH_SHORT).show();
    }
}
