package com.mssoftinc.jobcircular.ui.main;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
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
import com.mssoftinc.jobcircular.AD;
import com.mssoftinc.jobcircular.CategoryActivity;
import com.mssoftinc.jobcircular.MainActivity;
import com.mssoftinc.jobcircular.R;

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

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {
    SwipeRefreshLayout swipe;
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String URL_PRODUCTS =  "https://jobcirculer.com/api2.php";
String change="0";

    //a list to store all the products
    List<Product> productList;
    String search="";
    private List<Object> recyclerViewItems = new ArrayList<>();
    public static final int ITEMS_PER_AD = 8;

    //the recyclerview
    RecyclerView recyclerView;
    private PageViewModel pageViewModel;

    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);

        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
            Log.i("123321", "74:"+index);

        }
        pageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        MobileAds.initialize(getActivity(),
                "ca-app-pub-8073417854918898~6526542761");
        setHasOptionsMenu(true);
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        swipe=root.findViewById(R.id.swipe);
        final TextView textView = root.findViewById(R.id.section_label);
        pageViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
               change=s;
               loadProducts();


            }
        });
        swipe.setOnRefreshListener(() -> loadProducts());
        recyclerView = root.findViewById(R.id.recylcerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //initializing the productlist
        productList = new ArrayList<>();

        //this method will fetch and parse json
        //to display it in recyclerview

loadProducts();
        return root;
    }

    private void loadProducts() {
        swipe.setRefreshing(true);
      recyclerViewItems.clear();
        recyclerView.setVisibility(View.GONE);
        /*
         * Creating a String Request
         * The request type is GET defined by first parameter
         * The URL is defined in the second parameter
         * Then we have a Response Listener and a Error Listener
         * In response listener we will get the JSON response as a String
         * */
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_PRODUCTS,
                response -> {



                    try {
                        //converting the string to json array object
                        JSONArray array = new JSONArray(response);
                        swipe.setRefreshing(false);
                        //traversing through all the object

                        productList.clear();
                        for (int i = 0; i < array.length(); i++) {
                            //getting product object from json array
                            JSONObject product = array.getJSONObject(i);

                            //adding the product to product list
                            if (search.equals("") || product.getString("title").contains(search))

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
                            ProductsAdapter adapter = new ProductsAdapter(getActivity(), productList);
                            recyclerView.setAdapter(adapter);

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

                        JSONObject obj = new JSONObject(res);

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
                MainActivity activity = (MainActivity) getActivity();
               int myDataFromActivity = (activity.getMyData());
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

        Volley.newRequestQueue(getActivity()).add(stringRequest);
        try {
           // progressDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
  menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem mSearch = menu.findItem(R.id.appSearchBar);
        SearchView mSearchView = (SearchView) mSearch.getActionView();
        mSearchView.setQueryHint("Search");
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search = query;
                loadProducts();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search = newText;
                loadProducts();

                return true;
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                String shareBody = "Download Job Circular app \n play.google.com/store/apps/details?id="+ getActivity().getPackageName();
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share with"));
                break;

            case R.id.shar:
                String shareBody0 = "Download Job Circular app \n play.google.com/store/apps/details?id="+ getActivity().getPackageName();
                Intent sharingIntent0 = new Intent(Intent.ACTION_SEND);
                sharingIntent0.setType("text/plain");
                sharingIntent0.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
                sharingIntent0.putExtra(Intent.EXTRA_TEXT, shareBody0);
                startActivity(Intent.createChooser(sharingIntent0, "Share with"));
                break;
            case R.id.rate: {

                String appPackageName = getActivity().getPackageName(); // getPackageName() from Context or Activity object
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
                String appPackageName = getActivity().getPackageName(); // getPackageName() from Context or Activity object
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



    /**
     * Sets up and loads the banner ads.
     */


    /**
     * Loads the banner ads in the items list.
     */

}
