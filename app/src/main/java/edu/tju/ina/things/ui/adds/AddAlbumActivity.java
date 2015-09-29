package edu.tju.ina.things.ui.adds;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnItemClick;

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
import edu.tju.ina.things.util.Utils;
import edu.tju.ina.things.widget.NoScrollGridView;

public class AddAlbumActivity extends BackActionBarActivity {
	static final int REQUEST_PICKER_CODE = 260;
	static final int REQUEST_PICKER_CODE_4_POSTER = 261;
	
	@ViewInject(R.id.user_avatar) private ImageView userAvatarIv;
	@ViewInject(R.id.user_name) private TextView userNameEt;
	@ViewInject(R.id.add_title_et) private EditText addTitle;
	@ViewInject(R.id.iv_poster_preview) private ImageView posterIv;
	@ViewInject(R.id.add_description_et) private EditText addDescription;
	@ViewInject(R.id.add_image_btn) private NoScrollGridView addImage;
	
	private SimpleImgAdapter adapter;
	private String posterPath = null;
	private List<String> imagePaths = new ArrayList<>();
	
    private InfoUploader infoUploader;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_album);
		ViewUtils.inject(this);

        infoUploader = new AlbumUploader(this);

        getSupportActionBar().setTitle(getResources().getString(R.string.album_title));
		
		adapter = new SimpleImgAdapter(this, imagePaths, true);
		addImage.setAdapter(adapter);
		
		String url = URLHelper.ROOT_URL + "/" + InfoApplication.currentUser.getAvatar();
		userNameEt.setText(InfoApplication.currentUser.getUsername());
		ImageLoader.getInstance(null).loadAvatar(url, userAvatarIv);
	}

	@OnClick(R.id.iv_poster_preview)
	public void onPosterIvClick(View v) {
		Intent intent = new Intent(AddAlbumActivity.this, ImageChooserActivity.class);
		startActivityForResult(intent, REQUEST_PICKER_CODE_4_POSTER);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK) {
            Utils.toast(this, "出现了一点小问题");
			return;
		}
		if (requestCode == REQUEST_PICKER_CODE) {
			if (data == null)
				return;
			List<String> newImagePaths = data.getStringArrayListExtra(ImageChooserActivity.RETURN_RESULT);
            imagePaths = newImagePaths;
			adapter.setPaths(imagePaths);
			adapter.notifyDataSetChanged();
		} else if (requestCode == REQUEST_PICKER_CODE_4_POSTER) {
			if (data == null)
				return;
			List<String> newImagePaths = data.getStringArrayListExtra(ImageChooserActivity.RETURN_RESULT);
			if (newImagePaths.size() > 0) {
				posterPath = newImagePaths.get(0);
				ImageLoader.getInstance(this).load(posterPath, posterIv);
			}
		}
	}

	@OnItemClick(R.id.add_image_btn)
	public void onGridItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (position == imagePaths.size()) {
			Intent intent = new Intent(AddAlbumActivity.this, ImageChooserActivity.class);
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
                String title = addTitle.getText().toString();
                if ("".equals(title) || posterPath == null) {
                    Utils.toast(this, "必须上传一张图作为封面并起一个好听的名字~");
                    return true;
                }
                infoUploader.send(false);
        }
        return super.onOptionsItemSelected(item);
    }

    class AlbumUploader extends InfoUploader {

        public AlbumUploader (Activity context) {
            super(context);
            addToElement("album.title", addTitle);
            addToElement("album.desc", addDescription);
        }

        @Override
        public String getMainImageKey() {
            return "album.picture";
        }

        @Override
        public String getMainImagePath() {
            return posterPath;
        }

        @Override
        public String getSubImagesKey() {
            return "photos";
        }

        @Override
        public List<String> getSubImagesPaths() {
            return imagePaths;
        }

        @Override
        public String getUrl() {
            return URLHelper.ALBUM_URL;
        }
    }
}
