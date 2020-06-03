package com.mssoftinc.jobcircular;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.snackbar.Snackbar;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ViewActivity extends AppCompatActivity {
    String TAG = "123321";
    String image;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.last)
    TextView last;

    @BindView(R.id.photo_view)
    ImageView imageView;
    private static final String URL_PRODUCTS = "https://jobcirculer.com/details.php";
    String change = "";
    String BASE = "http://jobcirculer.com/storage/";
    @BindView(R.id.view)
    TextView view;
    @BindView(R.id.download)
    TextView download;
    @BindView(R.id.passage)
    TextView passage;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    private InterstitialAd mInterstitialAd;
    @BindView(R.id.toolbar2)
    Toolbar toolbar2;
    @BindView(R.id.LinearLayout01)
    RelativeLayout LinearLayout01;
    @BindView(R.id.source)
    TextView source;
    private LinearLayout adView, adView3;

    private long downloadID;

    private BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Fetching the download id received with the broadcast
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            //Checking if the received broadcast is for our enqueued download by matching download id
            if (downloadID == id) {
                Snackbar.make(LinearLayout01, "ফাইলটি সেভ হয়েছে দেখুন!", Snackbar.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
      com.google.android.gms.ads.AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        ButterKnife.bind(this);
        registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-2631940161649809/2742835064");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back button pressed
                ViewActivity.super.onBackPressed();
            }
        });
        loadProducts();
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the interstitial ad is closed.
                beginDownload();
            }
        });

    }


    private void loadProducts() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("loading");
        progressDialog.show();


        /*
         * Creating a String Request
         * The request type is GET defined by first parameter
         * The URL is defined in the second parameter
         * Then we have a Response Listener and a Error Listener
         * In response listener we will get the JSON response as a String
         * */
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_PRODUCTS,
                response -> {
                    progressDialog.dismiss();

                    try {
                        //converting the string to json array object
                        JSONArray array = new JSONArray(response);

                        //traversing through all the object
                        for (int i = 0; i < array.length(); i++) {
                            //getting product object from json array
                            JSONObject product = array.getJSONObject(i);
                            String slug = product.getString("subtitle");
                            String ldate = StringUtils.substringBetween(slug, "<p>আবেদনের শেষ তারিখঃ", "</p>");
                            String position = StringUtils.substringBetween(slug, "<p>পদের নামঃ", "</p>");
                            String edus = StringUtils.substringBetween(slug, "<p>শিক্ষাগত যোগ্যতাঃ", "</p>");
                            String links = StringUtils.substringBetween(slug, "<p>আবেদনের লিংকঃ", "</p>");
                            String inss = StringUtils.substringBetween(slug, "<p>প্রতিষ্ঠানঃ", "</p>");
                            String passages = StringUtils.substringBetween(slug, "passage:", ":passage");

                            Log.i("123321", "98:" + passages);
                            last.setText(ldate != null ? ldate : "na");
                            String viewS = product.getString("like") + "";
                            if (links != null) {
                                source.setText("Source:" + links);
                            }
                            last.setText("আবেদনের শেষ তারিখঃ" + last.getText().toString());
                            view.setText("Total views:" + (!viewS.equals("null") ? viewS : "0" + " times"));
                            title.setText(product.getString("body") + "\n" + product.getString("title"));
                            image = product.getString("image");
                            Log.i("123321", "314:" + BASE + image);
                            passage.setText(passages != null ? Html.fromHtml(passages) : "");
                            if (image.contains("noimage.jpg")) {
                                imageView.setImageDrawable(getResources().getDrawable(R.drawable.no));
                                download.setVisibility(View.GONE);
                            } else if (image.contains(".pdf"))
                                imageView.setImageDrawable(getResources().getDrawable(R.drawable.pdf));

                            else
                                Picasso.get().load(BASE + image).error(R.mipmap.ic_launcher_round).into(imageView, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        progressBar.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        progressBar.setVisibility(View.GONE);
                                        Snackbar.make(LinearLayout01, "Somethings want's wrong", Snackbar.LENGTH_SHORT).show();


                                    }


                                });
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.i("123321", "129:" + e.getMessage());
                    }
                },
                error -> {

                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parameters = new HashMap<>();

                parameters.put("id", getIntent().getStringExtra("id"));
                return parameters;
            }
        };

        //adding our stringrequest to queue
        Volley.newRequestQueue(this).add(stringRequest);
    }

    @OnClick(R.id.photo_view)
    public void onViewClicked() {
        Intent intent = new Intent(getApplicationContext(), FullscreenActivity.class);
        intent.putExtra("link", BASE + image);
        startActivity(intent);
    }


    @OnClick(R.id.download)
    public void onViewCglicked() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                //  Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                // SaveImage(ViewActivity.this, BASE + image);
                if (mInterstitialAd.isLoaded()) {
                 mInterstitialAd.show();
                } else {
                    //  SaveImage(ViewActivity.this, BASE + image);
                    beginDownload();
                }

            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(getApplicationContext(), "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }


        };
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem mSearch = menu.findItem(R.id.appSearchBar);
        SearchView mSearchView = (SearchView) mSearch.getActionView();
        mSearch.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                String shareBody = "Download Job Circular app \n play.google.com/store/apps/details?id=" + getPackageName();
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share with"));
                break;

            case R.id.shar:
                String shareBody0 = "Download Job Circular app \n play.google.com/store/apps/details?id=" + getPackageName();
                Intent sharingIntent0 = new Intent(Intent.ACTION_SEND);
                sharingIntent0.setType("text/plain");
                sharingIntent0.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
                sharingIntent0.putExtra(Intent.EXTRA_TEXT, shareBody0);
                startActivity(Intent.createChooser(sharingIntent0, "Share with"));
                break;
            case R.id.rate: {

                String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
            break;
            case R.id.more:
                String url = "https://play.google.com/store/apps/dev?id=6981912488855530244";
                Intent i0 = new Intent(Intent.ACTION_VIEW);
                i0.setData(Uri.parse(url));
                startActivity(i0);
                break;
            case R.id.social:
                String url0 = "https://facebook.com/mssoftinc/?_rdc=1&_rdr";
                Intent i00 = new Intent(Intent.ACTION_VIEW);
                i00.setData(Uri.parse(url0));
                startActivity(i00);
            case R.id.feed: {
                String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
            break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return false;
    }

    private void beginDownload() {
        Snackbar.make(LinearLayout01, "ডাউনলোড হচ্ছে। অপেক্ষা করুন...", Snackbar.LENGTH_SHORT).show();
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        /*
        Create a DownloadManager.Request with all the information necessary to start the download
         */
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(BASE + image))
                .setTitle(image)// Title of the Download Notification
                .setDescription("Downloading")// Description of the Download Notification
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)// Visibility of the download Notification
                .setDestinationUri(Uri.fromFile(file))// Uri of the destination file

                .setAllowedOverMetered(true)// Set if download is allowed on Mobile network
                .setAllowedOverRoaming(true);// Set if download is allowed on roaming network
        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        downloadID = downloadManager.enqueue(request);// enqueue puts the download request in the queue.
    }


}

