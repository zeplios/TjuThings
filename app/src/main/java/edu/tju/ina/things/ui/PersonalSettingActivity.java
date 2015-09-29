package edu.tju.ina.things.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.json.JSONException;
import org.json.JSONObject;

import edu.tju.ina.things.InfoApplication;
import edu.tju.ina.things.R;
import edu.tju.ina.things.net.HttpClient;
import edu.tju.ina.things.net.JSONCallback;
import edu.tju.ina.things.util.URLHelper;
import edu.tju.ina.things.util.Utils;
import edu.tju.ina.things.vo.user.User;

public class PersonalSettingActivity extends BackActionBarActivity {
	@ViewInject(R.id.user_avatar) private ImageView userAvatar;
	@ViewInject(R.id.user_nickname) private TextView name;
	@ViewInject(R.id.user_email) private TextView email;
	@ViewInject(R.id.user_phone) private TextView phone;
	@ViewInject(R.id.user_gender) private RadioGroup gender;
	@ViewInject(R.id.gender_0) private RadioButton genderMale;
	@ViewInject(R.id.gender_1) private RadioButton genderFemale;
	@ViewInject(R.id.gender_2) private RadioButton genderUnkown;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_setting);
		ViewUtils.inject(this);

        getSupportActionBar().setTitle("个人信息");
		if (InfoApplication.currentUser == null)
			finish();
		
		User u = InfoApplication.currentUser;
		name.setText(u.getNickname());
		email.setText(u.getEmail());
		phone.setText(u.getPhone());
		if (u.getGender() == 0) {
			genderMale.setChecked(true);
		} else if (u.getGender() == 1) {
			genderFemale.setChecked(true);
		} else if (u.getGender() == 2) {
			genderUnkown.setChecked(true);
		}
	}
	
	public void onSendBtnClick() {
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("nickname", name.getText().toString());
		params.addQueryStringParameter("email", email.getText().toString());
		params.addQueryStringParameter("phone", phone.getText().toString());
		if (genderMale.isChecked())
			params.addQueryStringParameter("gender", Integer.toString(0));
		else if (genderFemale.isChecked())
			params.addQueryStringParameter("gender", Integer.toString(1));
		else if (genderUnkown.isChecked())
			params.addQueryStringParameter("gender", Integer.toString(2));
		
		HttpClient.put(URLHelper.USER_URL, params, new JSONCallback(this) {
			@Override
			public void onSuccess(JSONObject response, ResponseInfo<String> resp) throws JSONException {
                String errMsg = response.getString("errmsg");

				User u = InfoApplication.currentUser;
				u.setNickname(name.getText().toString());
				u.setEmail(email.getText().toString());
				u.setPhone(phone.getText().toString());
				if (genderMale.isChecked())
					u.setGender(0);
				else if (genderFemale.isChecked())
					u.setGender(1);
				else if (genderUnkown.isChecked())
					u.setGender(2);
				Utils.toast(PersonalSettingActivity.this, errMsg);
				PersonalSettingActivity.this.finish();
			}
		});
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.complete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_send:
                onSendBtnClick();
        }
        return super.onOptionsItemSelected(item);
    }
}
