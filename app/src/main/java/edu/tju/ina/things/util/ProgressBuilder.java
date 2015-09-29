package edu.tju.ina.things.util;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.todddavies.components.progressbar.ProgressWheel;

import edu.tju.ina.things.R;

public class ProgressBuilder extends Dialog {
    private View mDialogView;
    private LinearLayout mLinearLayout;
    ViewGroup vg = null;
    ProgressWheel pw = null;

    private static ProgressBuilder instance;
    private static Context tmpContext;

    public ProgressBuilder(Context context) {
        super(context);
        init(context);
    }

    public ProgressBuilder(Context context, int theme) {
        super(context, theme);
        init(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.width  = ViewGroup.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(params);
    }

    public static ProgressBuilder getInstance(Context context) {
        if (instance == null || !tmpContext.equals(context)) {
            synchronized (ProgressBuilder.class) {
                if (instance == null || !tmpContext.equals(context)) {
                    instance = new ProgressBuilder(context, R.style.dialog_untran);
                }
            }
        }
        tmpContext = context;
        return instance;
    }

    private void init(Context context) {
        mDialogView = View.inflate(context, R.layout.element_progresswheel, null);

        pw = (ProgressWheel) mDialogView.findViewById(R.id.progress);
        mLinearLayout = (LinearLayout) mDialogView.findViewById(R.id.main);
        setContentView(mDialogView);
    }

    /**
     * 显示进度条
     * @param spin true表示没有进度值，始终忙旋转的状态
     */
	public void show(boolean spin) {
        super.show();
        pw.resetCount();
        pw.setText("");
        if (spin) {
            pw.spin();
        }
	}

    @Override
    public void dismiss() {
        super.dismiss();
    }

    public void set(int progress) {
        if (pw != null) {
            pw.setProgress(progress);
        }
    }
}
