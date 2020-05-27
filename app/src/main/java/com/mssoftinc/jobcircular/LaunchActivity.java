package com.mssoftinc.jobcircular;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LaunchActivity extends AppCompatActivity {
    String URL = "http://jobcirculer.com/getdata.php";
    RequestQueue queue;
    @BindView(R.id.imageView3)
    ImageView imageView3;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.textView2)
    TextView textView2;
    @BindView(R.id.textView3)
    TextView textView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        ButterKnife.bind(this);
        try {
            textView2.setText("Version:"+getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        FirebaseMessaging.getInstance().subscribeToTopic("global");
        FirebaseMessaging.getInstance().subscribeToTopic("testing");
        queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(response);


                    //creating a string array for listview
                    String[] heroes = new String[jsonArray.length()];

                    //looping through all the elements in json array
                    for (int i = 0; i < jsonArray.length(); i++) {

                        //getting json object from the json array
                        JSONObject obj = jsonArray.getJSONObject(i);

                        //getting the name from the json object and putting it inside string array
                        heroes[i] = obj.getString("name");

                        try {
                            if (obj.getString("slug").equals("tab")) {
                                Tab.list.add(obj.getString("name"));
                                Tab.val.add(obj.getString("id"));
                            } else {
                                Tab.draw.add(obj.getString("name"));
                                Tab.drawVal.add(obj.getString("id"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    //
                    new CountDownTimer(1000, 100) {
                        @Override
                        public void onTick(long millisUntilFinished) {
progressBar.setProgress(progressBar.getProgress()+10);
                        }

                        @Override
                        public void onFinish() {
                        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("fb");
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                AD.setBannar(dataSnapshot.child("bannar").getValue().toString());
                                AD.setIntersial(dataSnapshot.child("intersial").getValue().toString());
                                AD.setNative_bannnar(dataSnapshot.child("native_bannar").getValue().toString());
                                startActivity(new Intent(LaunchActivity.this, MainActivity.class));
                                
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        }
                    }.start();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i("123321", "123321");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error", error.toString());
                Log.i("123321", "" + error);
                Builder builder = new Builder(LaunchActivity.this);
                builder.setTitle("Something want's wrong");
                builder.setCancelable(false);
                builder.setMessage("Make sure you turn on internet connection");
                builder.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(LaunchActivity.this, LaunchActivity.class));

                    }
                });
                builder.show();
            }
        });
        queue.add(request);
    }
}

