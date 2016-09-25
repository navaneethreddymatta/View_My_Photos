/*
InClass05
Chase Schelthoff, Sonal Kaulkar, Divya Ravi, Navaneeth Reddy Matta
 */

package com.example.inclass05;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by skaul on 9/19/2016.
 */
public class MyImage extends AsyncTask<String, Void, Bitmap>{
    private String url;
    private Bitmap bitmap;
    private ImageInterface activity;

    public MyImage(ImageInterface activity) {
        this.activity = activity;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        activity.setImage(bitmap);
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        URL url = null;
        try {
            url = new URL(params[0]);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            bitmap = BitmapFactory.decodeStream(httpURLConnection.getInputStream());
            return bitmap;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

            return null;

    }


    public static interface ImageInterface
    {
        void setImage(Bitmap bitmap);
    }
}
