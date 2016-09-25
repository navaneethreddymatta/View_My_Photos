/*
InClass05
Chase Schelthoff, Sonal Kaulkar, Divya Ravi, Navaneeth Reddy Matta
 */

package com.example.inclass05;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements MyImage.ImageInterface {

    private ImageButton ibPrev, ibNext;
    private ImageView imageView;
    private TextView search;
    private Button buttonGo;
    private AlertDialog.Builder alertDialog;
    private String currentSelected;
    private CharSequence[] keywords;
    private static final String baseUrl = "http://dev.theappsdr.com/apis/photos/index.php?keyword=";
    private static String finalUrl;
    private int currentIndex = 1;
    private int numImages;
    private String[] urls;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if(networkInfo == null || !networkInfo.isConnected())
        {
            Toast.makeText(this, getString(R.string.toast_has_no_internet), Toast.LENGTH_SHORT).show();
            return;
        }

        progress = new ProgressDialog(MainActivity.this);
        progress.setCancelable(false);

        keywords = (CharSequence[])getResources().getStringArray(R.array.keywords);
        setAllViews();

        setButtonsEnabled(false);

        buttonGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setTitle(getString(R.string.label_choose));
                alertDialog.setCancelable(true);
                alertDialog.setItems(keywords,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                currentSelected = (String)keywords[which];
                                search.setText(currentSelected);
                                finalUrl = baseUrl + currentSelected;
                                progress.setTitle(getString(R.string.label_loading_dictionary));
                                progress.show();
                                currentIndex = 1;
                                new GetImages().execute(finalUrl);
                            }
                        }).show();

            }
        });

        ibPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentIndex == 1)
                {
                    currentIndex = numImages - 1;
                }
                else
                {
                    --currentIndex;
                }

                progress.setTitle(getString(R.string.label_loading_photo));
                progress.show();

                new MyImage(MainActivity.this).execute(urls[currentIndex]);
            }
        });

        ibNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentIndex == numImages - 1)
                {
                    currentIndex = 1;
                }
                else
                {
                    ++currentIndex;
                }

                progress.setTitle(getString(R.string.label_loading_photo));
                progress.show();

                new MyImage(MainActivity.this).execute(urls[currentIndex]);
            }
        });

    }

    private void setButtonsEnabled(boolean enabled)
    {
        ibNext.setEnabled(enabled);
        ibPrev.setEnabled(enabled);
    }

    @Override
    public void setImage(Bitmap bitmap) {
        this.imageView.setImageBitmap(bitmap);
        this.progress.cancel();

        if(numImages > 2)
        {
            setButtonsEnabled(true);
        }
    }

    private class GetImages extends AsyncTask<String, Void, String[]>
    {

        @Override
        protected void onPostExecute(String[] urls) {
            super.onPostExecute(urls);

            if(urls.length == 0 || urls.length == 1)
            {
                Toast.makeText(MainActivity.this, getString(R.string.toast_has_no_images),
                        Toast.LENGTH_SHORT).show();
                progress.cancel();
            }

            if(urls.length > 1)
            {
                progress.setTitle(getString(R.string.label_loading_photo));
                new MyImage(MainActivity.this).execute(urls[currentIndex]);
            }

        }

        @Override
        protected String[] doInBackground(String... params) {
            BufferedReader bf;
            try {
                URL url = new URL(params[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                bf = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = "";
                while ((line = bf.readLine()) != null)
                {
                    sb.append(line);
                }

                urls = sb.toString().split(";");
                numImages = urls.length;

                return urls;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }



    void setAllViews()
    {
        ibPrev =  (ImageButton)findViewById(R.id.imageButtonPrev);
        ibNext =  (ImageButton)findViewById(R.id.imageButtonNext);
        imageView =  (ImageView) findViewById(R.id.imageView);
        search = (TextView)findViewById(R.id.textViewSearch);
        buttonGo = (Button)findViewById(R.id.buttonGo);
    }
}
