package edu.tju.ina.things.ui.updates;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import edu.tju.ina.things.R;
import edu.tju.ina.things.adapter.InfoListAdapter;
import edu.tju.ina.things.adapter.SimpleImgAdapter;
import edu.tju.ina.things.net.HttpClient;
import edu.tju.ina.things.net.JSONCallback;
import edu.tju.ina.things.ui.BackActionBarActivity;
import edu.tju.ina.things.ui.ImageChooserActivity;
import edu.tju.ina.things.util.Constants;
import edu.tju.ina.things.util.ImageCompressor;
import edu.tju.ina.things.util.InfoUploader;
import edu.tju.ina.things.util.ProgressBuilder;
import edu.tju.ina.things.util.URLHelper;
import edu.tju.ina.things.util.Utils;
import edu.tju.ina.things.vo.info.Album;
import edu.tju.ina.things.vo.info.Info;
import edu.tju.ina.things.vo.info.Photo;
import edu.tju.ina.things.widget.NoScrollGridView;

public class UpdateAlbumActivity extends BackActionBarActivity {

    static final int REQUEST_PICKER_CODE = 2;
	
	@ViewInject(R.id.add_title_et) private EditText addTitle;
	@ViewInject(R.id.add_description_et) private EditText addDescription;
    @ViewInject(R.id.raw_listView) private ListView rawPhotosList;
    @ViewInject(R.id.add_image_btn) private NoScrollGridView newPhotosList;

    private InfoListAdapter rawAdapter;
    private SimpleImgAdapter newAdapter;
    private ArrayList<String> newImagePaths = new ArrayList<>();

    private Album album;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_album);
		ViewUtils.inject(this);

        album = Parcels.unwrap(getIntent().getParcelableExtra(Constants.DEFAULT_BUNDLE_NAME));

        getSupportActionBar().setTitle(getResources().getString(R.string.update_title));
        newAdapter = new SimpleImgAdapter(this, newImagePaths, false);
        newPhotosList.setAdapter(newAdapter);

        RequestParams params = new RequestParams();
        params.addQueryStringParameter("id", Integer.toString(album.getId()));
        params.addQueryStringParameter("type", Integer.toString(album.getType()));
        HttpClient.get(URLHelper.DETAIL_URL, params, new JSONCallback(this) {

            @Override
            public void onSuccess(JSONObject response, ResponseInfo<String> resp) throws JSONException {
                album = new Gson().fromJson(response.getJSONObject("info").toString(), Album.class);
                addTitle.setText(album.getTitle());
                addDescription.setText(album.getDesc());
                List<Info> infos = new ArrayList<Info>();
                for (Photo p : album.getPhotos()) {
                    p.setAlbum(album);
                    infos.add(p);
                }
                rawAdapter = new InfoListAdapter(UpdateAlbumActivity.this, infos, false, "myinfo");
                rawPhotosList.setAdapter(rawAdapter);
            }
        });
	}

    @OnClick(R.id.update)
	public void onSendBtnClick(View v) {
        InfoUploader infoUploader = new InfoUploader.InfoUploaderAdapter(this, false);
        infoUploader.addToElement("album.id", Integer.toString(album.getId()));
        if (!addTitle.getText().toString().equals(album.getTitle())) {
            infoUploader.addToElement("album.title", addTitle);
        }
        if (!addDescription.getText().toString().equals(album.getDesc())) {
            infoUploader.addToElement("album.desc", addDescription);
        }
        infoUploader.send(true);
	}

    @OnClick(R.id.select_image)
    public void onSelectImageClick(View v) {
        Intent intent = new Intent(this, ImageChooserActivity.class);
        intent.putStringArrayListExtra(Constants.DEFAULT_BUNDLE_NAME, newImagePaths);
        startActivityForResult(intent, REQUEST_PICKER_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICKER_CODE && resultCode == Activity.RESULT_OK) {
            if (data == null)
                return;
            newImagePaths = data.getStringArrayListExtra(ImageChooserActivity.RETURN_RESULT);
            newAdapter.setPaths(newImagePaths);
            newAdapter.notifyDataSetChanged();
        }
    }

    @OnClick(R.id.upload_photos)
    public void onSendAllClick(View v) {
        final ProgressBuilder progress = ProgressBuilder.getInstance(this);
        final Activity context = this;
        final InfoUploader uploader = new InfoUploader.InfoUploaderAdapter(this);
        final Progress pUtil = new Progress();
        progress.show(false);
        pUtil.step = 360 / newImagePaths.size();
        for (final String path : newImagePaths) {
            new AsyncTask<Void, Integer, RequestParams>() {

                @Override
                protected RequestParams doInBackground(Void... args) {
                    ImageCompressor iu = new ImageCompressor();
                    RequestParams params = new RequestParams();
                    params.addQueryStringParameter("photo.album.id", Integer.toString(album.getId()));
                    params.addBodyParameter("photo.picture", uploader.uploadFile(iu.compress(path), iu.getLength()));
                    return params;
                }

                @Override
                protected void onPostExecute(final RequestParams params) {
                    JSONCallback callback = new JSONCallback(context) {
                        @Override
                        public void onSuccess(JSONObject obj, ResponseInfo<String> resp) throws JSONException {
                            pUtil.success++;
                        }

                        @Override
                        protected void onComplete() {
                            pUtil.total++;
                            pUtil.progress += pUtil.step;
                            progress.set(pUtil.progress);
                            if (pUtil.total == newImagePaths.size()) {
                                Utils.toast(context, String.format("上传%d个文件，成功%d个", newImagePaths.size(), pUtil.success));
                                context.finish();
                            }
                        }
                    };
                    HttpClient.post(URLHelper.INFO_URL, params, callback);
                }
            }.execute();
        }
    }

    class Progress {
        int total = 0;
        int success = 0;
        int progress = 0;
        int step = 0;
    }
}
