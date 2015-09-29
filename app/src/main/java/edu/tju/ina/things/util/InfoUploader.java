package edu.tju.ina.things.util;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.tju.ina.things.net.HttpClient;
import edu.tju.ina.things.net.JSONCallback;
import edu.tju.ina.things.vo.info.Info;

/**
 * Created by ZhangFC on 2015/2/17.
 */
public abstract class InfoUploader {

    private Activity mContext = null;
    private ProgressBuilder mProgressUtil;
    private RequestParams mParams;
    private Map<String, Object> mElements = new HashMap<>();
    private boolean closeAfterFinish = true;

    public InfoUploader(Activity context) {
        mContext = context;
    }

    public InfoUploader(Activity context, boolean closeAfterFinish) {
        mContext = context;
        this.closeAfterFinish = closeAfterFinish;
    }

    public void addToElement(String key, Object obj) {
        mElements.put(key, obj);
    }
    abstract public String getMainImageKey();
    abstract public String getMainImagePath();
    abstract public String getSubImagesKey();
    abstract public List<String> getSubImagesPaths();
    // designed for Album/Photo, which has a different url from normal info
    public String getUrl() {
        return URLHelper.INFO_URL;
    }

    Handler uploadHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (mProgressUtil != null) {
                mProgressUtil.set(msg.arg1);
            }
        }
    };

    public void send(final boolean update) {
        new AsyncTask<Void, Integer, Void>() {
            @Override
            protected void onPreExecute() {
                if (mProgressUtil == null) {
                    mProgressUtil = ProgressBuilder.getInstance(mContext);
                }
                mProgressUtil.show(false);
            }

            @Override
            protected Void doInBackground(Void... args) {
                mParams = new RequestParams();
                for (String key : mElements.keySet()) {
                    Object obj = mElements.get(key);
                    if (obj instanceof TextView) {
                        mParams.addQueryStringParameter(key, ((TextView)obj).getText().toString());
                    } else if (obj instanceof Boolean) {
                        mParams.addQueryStringParameter(key, ((Boolean)obj) ? "true" : "false");
                    } else {
                        mParams.addQueryStringParameter(key, obj.toString());
                    }
                }

                ImageCompressor iu = new ImageCompressor();
                String mainImageKey = getMainImageKey();
                String mainImagePath = getMainImagePath();
                String subImagesKey = getSubImagesKey();
                List<String> subImagesPaths = getSubImagesPaths();
                int progress = 0;
                Message m;

                int imageCount = 0;
                if (mainImageKey != null && mainImagePath != null) {
                    imageCount++;
                }
                if (subImagesKey != null && subImagesPaths != null) {
                    imageCount += subImagesPaths.size();
                }
                int step = 360 / (imageCount + 1);
                if (mainImageKey != null && mainImagePath != null) {
                    mParams.addBodyParameter(mainImageKey, uploadFile(iu.compress(mainImagePath), iu.getLength()));
                    m = new Message();
                    progress += step;
                    m.arg1 = progress;
                    uploadHandler.sendMessage(m);
                }
                if (subImagesKey != null && subImagesPaths != null) {
                    for (int i = 0 ; i < subImagesPaths.size() ; i++) {
                        String path = subImagesPaths.get(i);
                        mParams.addBodyParameter(subImagesKey, uploadFile(iu.compress(path), iu.getLength()));
                        m = new Message();
                        progress += step;
                        m.arg1 = progress;
                        uploadHandler.sendMessage(m);
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                JSONCallback callback = new JSONCallback(mContext) {

                    @Override
                    public void onSuccess(JSONObject response, ResponseInfo<String> resp) throws JSONException {
                        Utils.toast(mContext, response.getString("errmsg"));
                        if (closeAfterFinish)
                            mContext.finish();
                    }

                    @Override
                    protected void onComplete() {
                        super.onComplete();
                        Message m = new Message();
                        m.arg1 = 360;
                        uploadHandler.sendMessage(m);
                        mProgressUtil.hide();
                    }
                };
                if (!update)
                    HttpClient.post(getUrl(), mParams, callback);
                else
                    HttpClient.put(getUrl(), mParams, callback);
            }
        }.execute();
    }

    /**
     * must be called in non-ui thread in android
     * @param is
     * @param length
     * @return
     */
    public String uploadFile(InputStream is, int length) {
        String filename = null;
        RequestParams params = new RequestParams();
        params.addBodyParameter("file", is, length);
        String s = HttpClient.postSync(URLHelper.UPLOAD_IMAGE_URL, params);
        try {
            JSONObject jsonObj = new JSONObject(s);
            filename = jsonObj.getString("filename");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return filename;
    }

    public static class InfoUploaderAdapter extends InfoUploader {

        public InfoUploaderAdapter(Activity context) {
            super(context);
        }

        public InfoUploaderAdapter(Activity context, boolean closeAfterFinish) {
            super(context, closeAfterFinish);
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
