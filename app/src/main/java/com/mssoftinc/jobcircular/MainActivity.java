package com.mssoftinc.jobcircular;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GestureDetectorCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AdSize;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnFailureListener;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;
import com.mssoftinc.jobcircular.ui.main.PageViewModel;
import com.mssoftinc.jobcircular.ui.main.Product;
import com.mssoftinc.jobcircular.ui.main.RecyclerViewAdapter;
import com.mssoftinc.jobcircular.ui.main.SectionsPagerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String URL_PRODUCTS = "https://jobcirculer.com/api2.php";
    String change = "";
    //a list to store all the products
    List<Product> productList;
    DrawerLayout drawer;
    NavigationView navigationView;
String TAG="123321";
    //the recyclerview
    RecyclerView recyclerView;
    private PageViewModel pageViewModel;
    Toolbar toolbar;
    String search = "";
    ProgressDialog progressDialog;
    AppUpdateManager appUpdateManager;
    int REQUEST_APP_UPDATE=888;
    int pos;
    TabLayout tabs;
    private GestureDetectorCompat gestureDetectorCompat = null;
    public static final int ITEMS_PER_AD = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        inAppUpdate();
        AdSettings.addTestDevice("435d3073-144f-4255-b68c-09a30466a6ff");
        toolbar = findViewById(R.id.toolbar);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("loading");
     //   progressDialog.show();
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
     AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener(){
            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                Log.i("123321", "error:"+i);
            }
        });

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        Log.i("123321", "54:"+AD.getIntersial());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Job Circular");
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        Menu menu = navigationView.getMenu();
        menu.add(R.id.main_group, -1, 0, "Home").setIcon(R.drawable.ic_home);
        for (int i = 0; i < Tab.draw.size(); i++) {
            menu.add(Menu.FIRST, i, i + 1, Tab.draw.get(i)).setIcon(R.drawable.ic_business_center_black_24dp);
        }
        int size = Tab.draw.size();
        menu.add(R.id.second_group, size + 1, size + 1, "চাকরির ফরম").setIcon(R.drawable.ic_business_center_black_24dp);
        menu.add(R.id.second_group, size + 2, size + 2, "বয়স ক্যালকুলেটর").setIcon(R.drawable.ic_accessibility_black_24dp);
        menu.add(R.id.third_group, size + 3, size + 3, "আরো অ্যাপস").setIcon(R.drawable.ic_android_black_24dp);
        menu.add(R.id.third_group, size + 4, size + 4, "অ্যাপ টিকে ৫ স্টার দিন").setIcon(R.drawable.ic_star_black_24dp);
        menu.add(R.id.third_group, size + 5, size + 5, "অ্যাপ ফিডব্যাক").setIcon(R.drawable.ic_mail_black_24dp);
        menu.add(R.id.third_group, size + 6, size + 6, "অ্যাপ শেয়ার করুন").setIcon(R.drawable.ic_share_black_24dp);
        menu.add(R.id.third_group, size + 7, size + 7, "সোশ্যাল মিডিয়া").setIcon(R.drawable.ic_supervisor_account_black_24dp);
        menu.add(R.id.third_group, size + 8, size + 8, "Privacy Policy").setIcon(R.drawable.ic_mail_black_24dp);


        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {}
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            public void onPageSelected(int position) {

                // Check if this is the page you want.

               // change = Tab.val.get(position-1);
                //change = Tab.val.get(tab.getPosition());
                pos=position;
            }
        });
        tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        recyclerView = findViewById(R.id.recylcerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        productList = new ArrayList<>();
        change = "0";
      //  loadProducts();
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
          //  pos=tab.getPosition();
                switch (tab.getPosition()) {

                    default:
                  //     change = Tab.val.get(tab.getPosition());

                        //   change = "SELECT id, title, slug, body FROM posts  WHERE slug='" + Tab.val.get(tab.getPosition()) + "';";
                      // loadProducts();
                }


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }


        });

        navigationView.setNavigationItemSelectedListener(menuItem -> {
            int i = menuItem.getItemId();
            if (i >= 0 && i < Tab.draw.size()) {
                drawer.closeDrawers();
                Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
                intent.putExtra("id", Tab.drawVal.get(i));
                intent.putExtra("id2", Tab.draw.get(i));
                startActivity(intent);

            } else if (i < 0) startActivity(new Intent(this, MainActivity.class));
else if(i==size+1){  drawer.closeDrawers();
                String url = "http://www.forms.gov.bd/site/view/category_content/চাকুরি%20সংক্রান্ত?fbclid=IwAR0Wh3sp9DjW3BrMzQ1eEmbIYiUmwbjnmOIMjb4PyMVIEWDvJEo0altsEM4";
                Intent i0 = new Intent(Intent.ACTION_VIEW);
                i0.setData(Uri.parse(url));
                startActivity(i0);
            }

            else if(i==size+2){  drawer.closeDrawers();
                startActivity(new Intent(this, Main2Activity.class));

            }

            else if (i == size + 3) {  drawer.closeDrawers();
                String url = "https://play.google.com/store/apps/dev?id=6981912488855530244";
                Intent i0 = new Intent(Intent.ACTION_VIEW);
                i0.setData(Uri.parse(url));
                startActivity(i0);
            }   else if (i == size + 8) {  drawer.closeDrawers();
                String url = "https://www.websitepolicies.com/policies/view/3Migt1iU";
                Intent i0 = new Intent(Intent.ACTION_VIEW);
                i0.setData(Uri.parse(url));
                startActivity(i0);
            } else if (i == size + 6) {

                String shareBody = "Download Job Circular app \n play.google.com/store/apps/details?id="+ getPackageName();
                ;
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share with"));
            } else if (i == size + 7) {  drawer.closeDrawers();
                String url = "https://facebook.com/mssoftinc/?_rdc=1&_rdr";
                Intent i0 = new Intent(Intent.ACTION_VIEW);
                i0.setData(Uri.parse(url));
                startActivity(i0);
            } else if (i == size + 4 || i == size + 5) {
                {
                    drawer.closeDrawers();
                    String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    } catch (ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    }
                }

            }

            return false;
        });

        recyclerView.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {

            public void onSwipeRight() {
                Log.i("123321", "277");
           displayMessage(pos++);
            }
            public void onSwipeLeft() {
               displayMessage(pos--);
            }

        });

    }

    private void displayMessage(int i) {
        TabLayout.Tab tab=tabs.getTabAt(i);
        tab.select();
    }


    private void loadProducts() {
        Log.i("123321", "295:"+change);
        productList.clear();
        recyclerView.setVisibility(View.GONE);
        /*
         * Creating a String Request
         * The request type is GET defined by first parameter
         * The URL is defined in the second parameter
         * Then we have a Response Listener and a Error Listener
         * In response listener we will get the JSON response as a String
         * */
        String url = change.equals("96561") ? "http://jobcirculer.com/api/post/category/notic-board" : URL_PRODUCTS;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {



                    try {
                        //converting the string to json array object
                        JSONArray array = new JSONArray(response);

                        //traversing through all the object
                        for (int i = 0; i < array.length(); i++) {
                            //getting product object from json array
                            JSONObject product = array.getJSONObject(i);

                            //adding the product to product list

                        }

                        //creating adapter object and setting it to recyclerview

                        try {

                /*            recyclerView.setVisibility(View.VISIBLE);
                            RecyclerView.Adapter<RecyclerView.ViewHolder> adapter = new RecyclerViewAdapter(this,
                                    recyclerViewItems);
                            FBNativeBannerAdapter fbAdapter= FBNativeBannerAdapter.Builder.with(AD.getNative_bannnar(), adapter)
                                    .adItemInterval(8)
                                    .build();
                            recyclerView.setAdapter(fbAdapter);*/
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                // As of f605da3 the following should work
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        // Now you can use any deserializer to make sense of data
                        Log.i("123321", "157:" + res);
                        JSONObject obj = new JSONObject(res);
                        Log.i("123321", "158:" + obj);
                    } catch (UnsupportedEncodingException e1) {
                        // Couldn't properly decode data to string
                        Log.i("123321", "161:" + e1.getMessage());
                        e1.printStackTrace();
                    } catch (JSONException e2) {
                        // returned data is not JSONObject?
                        Log.i("123321", "165:" + e2.getMessage());
                        e2.printStackTrace();
                    }
                }
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("id", change);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                // Removed this line if you dont need it or Use application/json
                // params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }

        };

        Volley.newRequestQueue(this).add(stringRequest);
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){

            drawer.closeDrawer(GravityCompat.START);
        }

        else {
            LayoutInflater layoutInflater = LayoutInflater.from(this);
            View promptView = layoutInflater.inflate(R.layout.prompt, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setView(promptView);

            RatingBar ratingBar=promptView.findViewById(R.id.ratingBar);
            ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    String appPackageName =getPackageName(); // getPackageName() from Context or Activity object
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    } catch (ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    }
                }
            });

           RelativeLayout btn_1= promptView.findViewById(R.id.exit);
            btn_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finishAffinity();
                    System.exit(0);
                    //do required function
                    // don't forget to call alertD.dismiss()

                }
            });
//
//
//            Button btn_2 = (Button)promptView.findViewById(R.id.button2);
//            btn_2.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                    //do required function
//
//                }
//            });


            alertDialogBuilder
                    .setCancelable(true);



            AlertDialog alertD = alertDialogBuilder.create();
            alertD.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertD.show();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();

    }


    public int getMyData() {

        return pos;
    }



    private void inAppUpdate() {
        // Creates instance of the manager.
        appUpdateManager = AppUpdateManagerFactory.create(this);

        // Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        Log.i("123321000", "463");
        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.i("12332100", ""+e.getMessage());
            }
        });
        appUpdateInfoTask.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {

                Log.e("123321000", appUpdateInfo.availableVersionCode() + "   496");
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        // For a flexible update, use AppUpdateType.FLEXIBLE
                        && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    // Request the update.

                    try {
                        appUpdateManager.startUpdateFlowForResult(
                                // Pass the intent that is returned by 'getAppUpdateInfo()'.
                                appUpdateInfo,
                                // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                                AppUpdateType.IMMEDIATE,
                                // The current activity making the update request.
                                MainActivity.this,
                                // Include a request code to later monitor this update request.
                                888);
                    } catch (IntentSender.SendIntentException ignored) {

                        Log.i("123321000", "" + ignored.getMessage());
                    }
                }
            }
        });

        appUpdateManager.registerListener(installStateUpdatedListener);

    }
    //lambda operation used for below listener
    InstallStateUpdatedListener installStateUpdatedListener = installState -> {
        if (installState.installStatus() == InstallStatus.DOWNLOADED) {
            popupSnackbarForCompleteUpdate();
        } else
            Log.e("UPDATE", "Not downloaded yet");
    };


    private void popupSnackbarForCompleteUpdate() {

        Snackbar snackbar =
                Snackbar.make(
                      drawer,
                        "Update almost finished!",
                        Snackbar.LENGTH_INDEFINITE);
        //lambda operation used for below action
        snackbar.setAction(this.getString(R.string.restart), view ->
                appUpdateManager.completeUpdate());
        snackbar.setActionTextColor(getResources().getColor(R.color.colorPrimary));
        snackbar.show();
    }


}

