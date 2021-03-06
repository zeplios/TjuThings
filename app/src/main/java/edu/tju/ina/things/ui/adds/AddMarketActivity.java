package edu.tju.ina.things.ui.adds;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import edu.tju.ina.things.InfoApplication;
import edu.tju.ina.things.R;
import edu.tju.ina.things.adapter.SimpleImgAdapter;
import edu.tju.ina.things.ui.BackActionBarActivity;
import edu.tju.ina.things.ui.ImageChooserActivity;
import edu.tju.ina.things.util.Constants;
import edu.tju.ina.things.util.ImageLoader;
import edu.tju.ina.things.util.InfoUploader;
import edu.tju.ina.things.util.URLHelper;
import edu.tju.ina.things.widget.NoScrollGridView;

public class AddMarketActivity extends BackActionBarActivity implements OnItemClickListener {
	
	static final int REQUEST_PICKER_CODE = 2;

	@ViewInject(R.id.user_avatar) private ImageView userAvatarIv;
	@ViewInject(R.id.user_name) private TextView userNameEt;
	@ViewInject(R.id.add_title_et) private EditText addTitle;
	@ViewInject(R.id.add_price_et) private EditText addPrice;
	@ViewInject(R.id.add_contact_et) private EditText addContact;
	@ViewInject(R.id.add_description_et) private EditText addDescription;
	@ViewInject(R.id.add_image_btn) private NoScrollGridView addImage;
	
	private SimpleImgAdapter adapter;
	private List<String> imagePaths = new ArrayList<>();
    private InfoUploader infoUploader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_market);
		ViewUtils.inject(this);

        infoUploader = new MarketUploader(this);

        getSupportActionBar().setTitle(getResources().getString(R.string.add_title));

		addImage.setOnItemClickListener(this);
		
		adapter = new SimpleImgAdapter(this, imagePaths, true);
		addImage.setAdapter(adapter);
		
		String url = URLHelper.ROOT_URL + "/" + InfoApplication.currentUser.getAvatar();
		userNameEt.setText(InfoApplication.currentUser.getUsername());
		ImageLoader.getInstance(null).loadAvatar(url, userAvatarIv);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_PICKER_CODE && resultCode == Activity.RESULT_OK) {
			if (data == null)
				return;
			List<String> newImagePaths = data.getStringArrayListExtra(ImageChooserActivity.RETURN_RESULT);
            imagePaths = newImagePaths;
			adapter.setPaths(imagePaths);
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (position == imagePaths.size()) {
			Intent intent = new Intent(AddMarketActivity.this, ImageChooserActivity.class);
            intent.putStringArrayListExtra(Constants.DEFAULT_BUNDLE_NAME, (ArrayList<String>) adapter.getPaths());
			startActivityForResult(intent, REQUEST_PICKER_CODE);
		}
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
                infoUploader.send(false);
        }
        return super.onOptionsItemSelected(item);
    }

    class MarketUploader extends InfoUploader {

        public MarketUploader (Activity context) {
            super(context);
            addToElement("market.title", addTitle);
            addToElement("market.desc", addDescription);
            addToElement("market.price", addPrice);
            addToElement("market.contact", addContact);
        }

        @Override
        public String getMainImageKey() {
            return "market.picture";
        }

        @Override
        public String getMainImagePath() {
            if (imagePaths != null && imagePaths.size() > 0)
                return imagePaths.get(0);
            else
                return null;
        }

        @Override
        public String getSubImagesKey() {
            return "market.images";
        }

        @Override
        public List<String> getSubImagesPaths() {
            if (imagePaths != null && imagePaths.size() > 1)
                return imagePaths.subList(1, imagePaths.size());
            else
                return null;
        }
    }
}
