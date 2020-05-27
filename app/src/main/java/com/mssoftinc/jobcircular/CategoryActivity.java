package com.mssoftinc.jobcircular;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.navigation.NavigationView;
import com.mssoftinc.jobcircular.ui.main.PageViewModel;
import com.mssoftinc.jobcircular.ui.main.Product;
import com.mssoftinc.jobcircular.ui.main.ProductsAdapter;
import com.mssoftinc.jobcircular.ui.main.RecyclerViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoryActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    private List<Object> recyclerViewItems = new ArrayList<>();
    public static final int ITEMS_PER_AD = 8;
    private static final String URL_PRODUCTS = "https://jobcirculer.com/api2.php";
    String change = "";
    //a list to store all the products
    List<Product> productList;
    DrawerLayout drawer;
    NavigationView navigationView;
    @BindView(R.id.banner_container)
    LinearLayout bannerContainer;
    @BindView(R.id.toolbar3)
    Toolbar toolbar3;
    @BindView(R.id.recylcerView)
    RecyclerView recylcerView;
    @BindView(R.id.swipe)
    SwipeRefreshLayout swipe;
    private LinearLayout adView;
    private PageViewModel pageViewModel;
    Toolbar toolbar;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        ButterKnife.bind(this);
        //adView = new AdView(this, "1973950939380278_2585513604890672", AdSize.BANNER_HEIGHT_50);
        MobileAds.initialize(this,
                "ca-app-pub-8073417854918898~6526542761");
        // Find the Ad Container
        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);
        com.google.android.gms.ads.AdView adView = new com.google.android.gms.ads.AdView(this);

        adView.setAdSize(AdSize.BANNER);

        adView.setAdUnitId(AD.getBannar());
        adView.loadAd(new AdRequest.Builder().build());

        // Add the ad view to your activity layout
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        adContainer.addView(adView, params);




        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        swipe.setOnRefreshListener(() -> loadProducts());
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back button pressed
                CategoryActivity.super.onBackPressed();
            }
        });
        change = getIntent().getStringExtra("id");
        getSupportActionBar().setTitle(getIntent().getStringExtra("id2"));
        productList = new ArrayList<>();
        recyclerView = findViewById(R.id.recylcerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
       loadProducts();
 }

   private void loadProducts() {
    recyclerViewItems.clear();
       swipe.setRefreshing(true);
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
                       swipe.setRefreshing(false);
                       //traversing through all the object
                       Log.i("123321", "139:"+array.length()+" for "+change);
                       productList.clear();
                       for (int i = 0; i < array.length(); i++) {
                           //getting product object from json array
                           JSONObject product = array.getJSONObject(i);

                           //adding the product to product list


                           {
                               if( false){
                                   productList.add(new Product(
                                           product.getString("id"
                                           ),
                                           product.getString("title"),
                                           product.getString("slug"),
                                           product.getString("body"),
                                           product.getString("subtitle"),
                                           product.getString("publish_date")

                                   ));
                               }
                               else {
                                   String givenDateString = product.getString("publish_date");
                                   givenDateString = givenDateString.replace("T", "-");
                                   SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
                                   try {
                                       Date mDate = sdf.parse(givenDateString);
                                       long timeInMilliseconds = mDate.getTime();
                                       Date date = new Date(timeInMilliseconds);
                                       SimpleDateFormat format = new SimpleDateFormat("dd/MMM/yyyy");
                                       Log.i("123321", "Date in milli :: " + format.format(date));

                                       if(timeInMilliseconds<System.currentTimeMillis()){
                                           productList.add(new Product(
                                                   product.getString("id"
                                                   ),
                                                   product.getString("title"),
                                                   product.getString("slug"),
                                                   product.getString("body"),
                                                   product.getString("subtitle"),
                                                   product.getString("publish_date")

                                           ));}
                                   } catch (ParseException e) {
                                       Log.i("123321", "199:"+          product.getString("title"));
                                       productList.add(new Product(
                                               product.getString("id"
                                               ),
                                               product.getString("title"),
                                               product.getString("slug"),
                                               product.getString("body"),
                                               product.getString("subtitle"),
                                               product.getString("publish_date")

                                       ));
                                       e.printStackTrace();
                                   }
                               }
                           }

                       }

                       //creating adapter object and setting it to recyclerview

                       try {
                           swipe.setRefreshing(false);
                           recyclerView.setVisibility(View.VISIBLE);
                           ProductsAdapter adapter = new ProductsAdapter(getApplicationContext(), productList);
                           recyclerView.setAdapter(adapter);

                       } catch (Exception e) {
                           Log.i("123321", ""+e.getMessage());
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
                String shareBody = "Download Job Circular app \n play.google.com/store/apps/details?id="+ getPackageName();

                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share with"));
                break;

            case R.id.shar:
                String shareBody0 = "Download Job Circular app \n play.google.com/store/apps/details?id="+ getPackageName();

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




}

