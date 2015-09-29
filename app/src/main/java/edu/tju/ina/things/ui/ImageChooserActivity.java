package edu.tju.ina.things.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import edu.tju.ina.things.R;
import edu.tju.ina.things.adapter.ImgChooserAdapter;
import edu.tju.ina.things.util.Constants;

public class ImageChooserActivity extends BackActionBarActivity {
	
	public static final String RETURN_RESULT = "result";
    static final int REQUEST_CAMERA_CODE = 1;

	@ViewInject(R.id.images_gv) private GridView imagesGv;
    private ArrayList<String> images = new ArrayList<>();
    private ArrayList<String> selected = new ArrayList<>();
    private ImgChooserAdapter imageAdapter = null;
    
    private GetImgPathTask mLoadTask = null;

    private File cameraImage;
    private String cameraFilename;

    @SuppressLint("NewApi")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_image);
        ViewUtils.inject(this);

        Intent intent = getIntent();
        if (intent.hasExtra(Constants.DEFAULT_BUNDLE_NAME)) {
            selected = intent.getStringArrayListExtra(Constants.DEFAULT_BUNDLE_NAME);
        }

        mLoadTask = new GetImgPathTask();
        if (Build.VERSION.SDK_INT >= 11) {
        	mLoadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
        	mLoadTask.execute();
        }
    }

    private void setAdapter(ArrayList<String> datas) {
        imageAdapter = new ImgChooserAdapter(this, datas);
        imageAdapter.setSelectedImgs(selected);
        imagesGv.setAdapter(imageAdapter);
    }

    @OnClick(R.id.call_camera)
    public void onCameraBtnClick(View v) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File dir = Environment.getExternalStorageDirectory();
        dir = new File(dir, "DCIM/Camera");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        cameraFilename = Long.toHexString(System.nanoTime()) + ".jpg";
        cameraImage = new File(dir, cameraFilename);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraImage));
        startActivityForResult(intent, REQUEST_CAMERA_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAMERA_CODE && resultCode == Activity.RESULT_OK) {
            images.add(0, cameraImage.getAbsolutePath());
            selected.add(cameraImage.getAbsolutePath());
            imageAdapter.setSelectedImgs(selected);
            imageAdapter.setDataImgs(images);
            imageAdapter.notifyDataSetChanged();

            try {
                MediaStore.Images.Media.insertImage(getContentResolver(),
                        cameraImage.getAbsolutePath(), cameraFilename, null);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.parse("file://" + cameraImage.getAbsolutePath())));
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
                Intent intent = new Intent();
                intent.putExtra(RETURN_RESULT, imageAdapter.getSelectedImgs());
                setResult(RESULT_OK, intent);
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    class GetImgPathTask extends AsyncTask<Void, Void, Boolean> {

        private ArrayList<String> result = new ArrayList<>();

        @Override
        protected Boolean doInBackground(Void... params) {
            Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            ContentResolver mContentResolver = ImageChooserActivity.this.getContentResolver();
            StringBuilder selection = new StringBuilder();
            selection.append(MediaStore.Images.Media.MIME_TYPE).append("=?");
            selection.append(" or ");
            selection.append(MediaStore.Images.Media.MIME_TYPE).append("=?");

            Cursor mCursor = null;
            try {
                mCursor = mContentResolver.query(mImageUri, null, selection.toString(), new String[] {
                        "image/jpeg", "image/png"
                }, MediaStore.Images.Media.DATE_TAKEN);
                while (mCursor.moveToNext()) {
                    String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    result.add(0, path);
                }
            } catch (Exception e) {
                return false;
            } finally {
                if (mCursor != null && !mCursor.isClosed()) {
                    mCursor.close();
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            setAdapter(result);
        }
    }
}
