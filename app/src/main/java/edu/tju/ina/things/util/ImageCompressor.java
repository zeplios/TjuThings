package edu.tju.ina.things.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.lidroid.xutils.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import edu.tju.ina.things.net.HttpClient;

public class ImageCompressor {
	
	private int length;
	private InputStream is;

	private InputStream compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos); 
        int options = 100;  
        while ( baos.toByteArray().length / 1024 > 500) { 
            baos.reset();
            options -= 10;
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
        }
        byte [] bytes = baos.toByteArray();
        length = bytes.length;
        is = new ByteArrayInputStream(bytes); 
        return is;
    }

    public InputStream compress(String path) {
        // 手机上比较通用的大小是2048*2048，再大了有些图不能正常显示
        return compressImage(compress(path, 2000, 2000));
    }

    public Bitmap compress(String path, int maxWidth, int maxHeight) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path,newOpts);

        newOpts.inJustDecodeBounds = false;
        int imageWidth = newOpts.outWidth;
        int imageHeight = newOpts.outHeight;
        int scale = 1;
        if (imageWidth > imageHeight && imageWidth > maxWidth) {
            scale = (int)Math.ceil(newOpts.outWidth * 1.0 / maxWidth);
        } else if (imageWidth < imageHeight && imageHeight > maxHeight) {
            scale = (int)Math.ceil(newOpts.outHeight * 1.0 / maxHeight);
        }
        newOpts.inSampleSize = scale;
        bitmap = BitmapFactory.decodeFile(path, newOpts);
        return bitmap;
    }
	
	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public InputStream getInputStream() {
		return is;
	}
	
}
