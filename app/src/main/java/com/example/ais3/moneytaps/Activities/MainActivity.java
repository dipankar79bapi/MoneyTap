package com.example.ais3.moneytaps.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ais3.moneytaps.Models.ConnectionDetector;
import com.example.ais3.moneytaps.Models.MoneyTapAdapter;
import com.example.ais3.moneytaps.R;
import com.example.ais3.moneytaps.WebRequestAPI.Config;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ConnectionDetector cd;
    Boolean isInternetPresent = false;
    ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private ArrayList<MoneyTapAdapter> originalActors = new ArrayList<>();
    MoneyTapAdapter selectedActor;
    private ActorsAdapter adapter;
    String name;
    EditText search;
    ImageView iv_search;

    String st_item;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestStoragePermission();

        search=(EditText)findViewById(R.id.ed_search_item);
        iv_search=(ImageView)findViewById(R.id.iv_search_items);
        iv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                st_item=search.getText().toString().trim();
                int length=st_item.length();

                if(st_item.equals("")  || st_item == null || st_item.length() == 0 )
                {
                    search.setError("Please Enter First !!!");
                    search.requestFocus();
                    return;
                }else if ( length<=0 || length<4 ){
                    search.setError("Please Enter 4 Character Minimum !!!");
                    search.requestFocus();
                    return;
                }
                else {
                    cd = new ConnectionDetector(MainActivity.this);
                    isInternetPresent = cd.isConnectingToInternet();
                    if (isInternetPresent) {
                        st_item = st_item.replaceAll(" ","+");

                        getMyTripsAPIData(st_item);
                    }else{
                        cd.noInternetConectionShowToast();
                    }
                }
            }
        });

        adapter = new ActorsAdapter();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view5);

        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.smoothScrollToPosition(0);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
    private void requestStoragePermission() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.INTERNET)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {

                          /*  Intent intent = new Intent(MainActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();*/
                            Toast.makeText(getApplicationContext(), "All permissions are granted!", Toast.LENGTH_SHORT).show();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }
    private void getMyTripsAPIData(final String names)
    {
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Loading Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        String wikiAPI = Config.BaseURL+names+"+&gpslimit=10";
        StringRequest stringRequest = new StringRequest(wikiAPI,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject j = null;
                        try {
                            j = new JSONObject(response);
                            JSONObject jsonObject3 = j.getJSONObject("query");

                            JSONArray jsonArray=jsonObject3.getJSONArray("pages");

                            if (jsonArray!=null && jsonArray.length()>0){
                                processRequest(jsonArray.toString());
                            }else{
                                Toast.makeText(getApplicationContext(), "Empty Array", Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();

                            }
                            progressDialog.dismiss();


                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String json = null;
                        NetworkResponse response = error.networkResponse;
                        progressDialog.dismiss();
                        if (response != null && response.data != null) {
                            switch (response.statusCode) {
                                case 409:
                                    json = new String(response.data);
                                    json = trimMessage(json, "message");
                                    if (json != null) displayMessage(json);
                                    break;
                                case 400:
                                    json = new String(response.data);
                                    json = trimMessage(json, "message");
                                    if (json != null) displayMessage(json);
                                    break;
                                case 404:
                                    json = new String(response.data);
                                    json = trimMessage(json, "message");
                                    if (json != null) displayMessage(json);
                                    break;
                                case 500:
                                    json = new String(response.data);
                                    json = trimMessage(json, "message");
                                    if (json != null) displayMessage(json);
                                    Toast.makeText(getApplicationContext(), "Server Busy Please Check Later !!!", Toast.LENGTH_LONG).show();
                                    break;
                            }
                        }
                    }
                }) {
        };
        {
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        }
        //Creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(getApplication());

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }
    private void processRequest(String response)
    {
        if (response != null && !response.isEmpty()) {
            try {
                Gson gson = new Gson();
                ArrayList<MoneyTapAdapter> actors = gson.fromJson(response, new TypeToken<ArrayList<MoneyTapAdapter>>() {
                }.getType());
                if (actors != null) {
                    this.originalActors.clear();
                    this.originalActors.addAll(actors);
                    adapter.notifyDataSetChanged();

                }
            } catch (Exception ex) {
                ex.printStackTrace();
                Toast.makeText(getApplicationContext(), "Sorry, something went wrong. Please try again", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Sorry, something went wrong. Please try again", Toast.LENGTH_LONG).show();
        }
    }
    private class ActorsAdapter extends RecyclerView.Adapter {

        private class ActorViewHolder extends RecyclerView.ViewHolder
        {
            public TextView name,title;
            ImageView image;

            public RadioButton rad;
            public ActorViewHolder(View itemView) {
                super(itemView);
                title = (TextView) itemView.findViewById(R.id.tv_title);
                name = (TextView) itemView.findViewById(R.id.tv_name);
                image = (ImageView) itemView.findViewById(R.id.iv_image);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (getAdapterPosition() != RecyclerView.NO_POSITION)
                        {
                            selectedActor = originalActors.get(getAdapterPosition());

                            Intent intent = new Intent(MainActivity.this,WikiPage.class);
                            intent.putExtra("pageTitle",selectedActor.title);
                            startActivity(intent);
                        }
                    }
                });

            }
        }
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ActorsAdapter.ActorViewHolder actorViewHolder =
                    new ActorsAdapter.ActorViewHolder(MainActivity.this.getLayoutInflater().inflate(R.layout.row_moneytap, null));
            return actorViewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            MoneyTapAdapter actor = originalActors.get(position);
            String Posss = String.valueOf(position+1);

            ActorsAdapter.ActorViewHolder actorViewHolder = (ActorsAdapter.ActorViewHolder) holder;
            actorViewHolder.title.setText(actor.title);
            String namess= String.valueOf(actor.terms.getDescription());

            //to remove the [] brackets
            namess = namess.substring(1, namess.length() - 1);
            actorViewHolder.name.setText(namess);
            //actorViewHolder.name.setText(actor.terms.getDescription().toString());

            if (actor.thumbnail!=null){
                String path = actor.thumbnail.source;
                if(path!=""){
                    Picasso.with(MainActivity.this)
                            .load(path)
                            .into(actorViewHolder.image);
                }

            }else{
                Toast.makeText(MainActivity.this, "Image Not available", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public int getItemCount() {
            return originalActors.size();
        }

    }

    public String trimMessage(String json, String key) {
        String trimmedString = null;

        try {
            JSONObject obj = new JSONObject(json);
            trimmedString = obj.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return trimmedString;
    }

    public void displayMessage(String toastString) {
        Toast.makeText(getApplicationContext(), toastString, Toast.LENGTH_LONG).show();
        hideDialog();
    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.icon)
                .setTitle("Exit App?")
                .setMessage("Are you sure you want to close this  App?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }
}
