package edu.tju.ina.things.util;

import java.util.Random;

import android.content.Context;
import android.view.View;

import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;

import edu.tju.ina.things.R;

public class DialogUtil {
	private Context context;
	private NiftyDialogBuilder dialogBuilder;

	private Effectstype[] effects = new Effectstype[] { Effectstype.Fadein,
			Effectstype.Slideright, Effectstype.Slideleft,
			Effectstype.Slidetop, Effectstype.SlideBottom,
			Effectstype.Newspager, Effectstype.Fall, Effectstype.Sidefill,
			Effectstype.Fliph, Effectstype.Flipv, Effectstype.RotateBottom,
			Effectstype.RotateLeft, Effectstype.Slit, Effectstype.Shake };

	public DialogUtil(Context context) {
		this.context = context;
		dialogBuilder = NiftyDialogBuilder.getInstance(context);
	}

	public void show(String title, String content,
			final OnButtonClickListener listener) {
		Random random = new Random();
		int index = random.nextInt(effects.length);

		dialogBuilder
				.withTitle(title)
				// .withTitle(null) no title
				.withTitleColor("#FFFFFF")
				// def
				.withDividerColor("#11000000")
				// def
				.withMessage(content)
				// .withMessage(null) no Msg
				.withMessageColor("#FFFFFFFF")
				// def | withMessageColor(int resid)
				.withDialogColor("#ff8c31")
				// def | withDialogColor(int resid) //def
				.withIcon(
						context.getResources().getDrawable(
								R.drawable.ic_launcher))
				.isCancelableOnTouchOutside(true) // def | isCancelable(true)
				.withDuration(700) // def
				.withEffect(effects[index]) // def Effectstype.Slidetop
				.withButton1Text("确定") // def gone
				.withButton2Text("取消") // def gone
				// .setCustomView(R.layout.custom_view,v.getContext())
				// //.setCustomView(View or ResId,context)
				.setButton1Click(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (listener != null) {
							listener.onConfirm();
						}
                        dialogBuilder.hide();
					}
				}).setButton2Click(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (listener != null) {
							listener.onCancle();
						}
                        dialogBuilder.hide();
					}
				}).show();
	}

	public interface OnButtonClickListener {
		public void onConfirm();
		public void onCancle();
	}
}
