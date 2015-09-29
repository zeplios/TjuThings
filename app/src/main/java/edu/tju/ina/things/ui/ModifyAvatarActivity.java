package edu.tju.ina.things.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.edmodo.cropper.CropImageView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import edu.tju.ina.things.InfoApplication;
import edu.tju.ina.things.R;
import edu.tju.ina.things.net.HttpClient;
import edu.tju.ina.things.net.JSONCallback;
import edu.tju.ina.things.util.ImageCompressor;
import edu.tju.ina.things.util.URLHelper;

public class ModifyAvatarActivity extends ActionBarActivity {

    static final int REQUEST_PICKER_CODE = 2;
    @ViewInject(R.id.crop_imageview) private CropImageView cropImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_avatar);
        ViewUtils.inject(this);
        cropImageView.setAspectRatio(1, 1);
        cropImageView.setGuidelines(2);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICKER_CODE && resultCode == Activity.RESULT_OK) {
            if (data == null)
                return;
            List<String> newImagePaths = data.getStringArrayListExtra(ImageChooserActivity.RETURN_RESULT);
            if (newImagePaths.size() > 0) {
                Bitmap bmap = new ImageCompressor().compress(newImagePaths.get(0), 2048, 2048);
                cropImageView.setImageBitmap(bmap);
            }
        }
    }

    @OnClick(R.id.select_image)
    public void onClick(View v) {
        Intent intent = new Intent(this, ImageChooserActivity.class);
        startActivityForResult(intent, REQUEST_PICKER_CODE);
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
                RequestParams params = new RequestParams();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                cropImageView.getCroppedImage().compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte [] bytes = baos.toByteArray();
                params.addBodyParameter("avatar", new ByteArrayInputStream(bytes), bytes.length);
                HttpClient.post(URLHelper.AVATAR_URL, params, new JSONCallback(this) {
                    @Override
                    public void onSuccess(JSONObject obj, ResponseInfo<String> resp) throws JSONException {
                        InfoApplication.currentUser.setAvatar(obj.getString("path"));
                        finish();
                    }
                });
        }
        return super.onOptionsItemSelected(item);
    }
}
