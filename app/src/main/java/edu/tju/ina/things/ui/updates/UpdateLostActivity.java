package edu.tju.ina.things.ui.updates;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.gson.Gson;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.List;

import edu.tju.ina.things.R;
import edu.tju.ina.things.net.HttpClient;
import edu.tju.ina.things.net.JSONCallback;
import edu.tju.ina.things.ui.BackActionBarActivity;
import edu.tju.ina.things.util.Constants;
import edu.tju.ina.things.util.InfoUploader;
import edu.tju.ina.things.util.URLHelper;
import edu.tju.ina.things.vo.info.Lost;

public class UpdateLostActivity extends BackActionBarActivity {
	
	@ViewInject(R.id.add_title_et) private EditText addTitle;
	@ViewInject(R.id.add_place_et) private EditText addPlace;
	@ViewInject(R.id.completed_selector) private RadioGroup completeSelector;
    @ViewInject(R.id.selector_completed_y) private RadioButton completeYes;
    @ViewInject(R.id.selector_completed_n) private RadioButton completeNo;
	@ViewInject(R.id.add_contact_et) private EditText addContact;
	@ViewInject(R.id.add_ownerid_et) private EditText addOwnerid;
	@ViewInject(R.id.add_description_et) private EditText addDescription;

    private InfoUploader infoUploader;
    private Lost lost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_lost);
		ViewUtils.inject(this);

        lost = Parcels.unwrap(getIntent().getParcelableExtra(Constants.DEFAULT_BUNDLE_NAME));

        infoUploader = new LostUploader(this);
        getSupportActionBar().setTitle(getResources().getString(R.string.update_title));

        RequestParams params = new RequestParams();
        params.addQueryStringParameter("id", Integer.toString(lost.getId()));
        params.addQueryStringParameter("type", Integer.toString(lost.getType()));
        HttpClient.get(URLHelper.DETAIL_URL, params, new JSONCallback(this) {

            @Override
            public void onSuccess(JSONObject response, ResponseInfo<String> resp) throws JSONException {
                lost = new Gson().fromJson(response.getJSONObject("info").toString(), Lost.class);
                addTitle.setText(lost.getTitle());
                addPlace.setText(lost.getPlace());
                addContact.setText(lost.getContact());
                if (lost.isComplete()) {
                    completeYes.setChecked(true);
                } else {
                    completeNo.setChecked(true);
                }
                if (!lost.isOwner()) {
                    addOwnerid.setText(lost.getOwnerid());
                    addOwnerid.setVisibility(View.VISIBLE);
                }
                addDescription.setText(lost.getDesc());
            }
        });
	}

	public void onSendBtnClick() {
        infoUploader.addToElement("lost.id", lost.getId());
        if (!addTitle.getText().toString().equals(lost.getTitle())) {
            infoUploader.addToElement("lost.title", addTitle);
        }
        if (!addDescription.getText().toString().equals(lost.getDesc())) {
            infoUploader.addToElement("lost.desc", addDescription);
        }
        if (!addPlace.getText().toString().equals(lost.getPlace())) {
            infoUploader.addToElement("lost.place", addPlace);
        }
        if (!addContact.getText().toString().equals(lost.getContact())) {
            infoUploader.addToElement("lost.contact", addContact);
        }
        boolean isCompleted = (completeSelector.getCheckedRadioButtonId() == R.id.selector_completed_y);
        if (isCompleted != lost.isComplete()) {
            infoUploader.addToElement("lost.complete", isCompleted);
        }
        if (!lost.isOwner() && !addOwnerid.getText().toString().equals(lost.getOwnerid())) {
            infoUploader.addToElement("lost.ownerid", addOwnerid);
        }
        infoUploader.send(true);
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

    class LostUploader extends InfoUploader {
        public LostUploader (Activity context) {
            super(context);
        }
        @Override
        public String getMainImageKey() {
            return null;
        }
        @Override
        public String getMainImagePath() {
            return null;
        }
        @Override
        public String getSubImagesKey() {
            return null;
        }
        @Override
        public List<String> getSubImagesPaths() {
            return null;
        }
    }
}
