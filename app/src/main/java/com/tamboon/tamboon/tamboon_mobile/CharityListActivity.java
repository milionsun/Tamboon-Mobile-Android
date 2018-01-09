package com.tamboon.tamboon.tamboon_mobile;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Show list of Charity's names
 * Press any charity's name to go to DonationActivity.java
 */

public class CharityListActivity extends AppCompatActivity implements CharityListAdapter.CharityListListener {
    RecyclerView recyclerView;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charity_list);
        recyclerView = (RecyclerView) findViewById(R.id.charityList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        getDatabase();
    }

    // Connect to server to get Charities information
    private void getDatabase() {
        String url = getString(R.string.server_url) + getString(R.string.charities);
        showProgress(true);
        new CharityGetRequest(this).execute(url);
    }

    // Show DonationActivity
    @Override
    public void charitySelected(CharityObject charity) {
        Intent intent = new Intent(this, DonationActivity.class);
        startActivity(intent);
    }

    // AsyncTask for HTTP GET Request
    public class CharityGetRequest extends AsyncTask<String, Void, String> {
        private static final String REQUEST_METHOD = "GET";
        private static final int READ_TIMEOUT = 15000;
        private static final int CONNECTION_TIMEOUT = 15000;
        private Context mContext;

        private CharityGetRequest(Context context) {
            this.mContext = context;
        }

        @Override
        protected String doInBackground(String... params) {
            String url = params[0];
            String result = null;
            try {
                URL serverUrl = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) serverUrl.openConnection();
                connection.setRequestMethod(REQUEST_METHOD);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);
                connection.connect();

                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                    BufferedReader bufferedReader = new BufferedReader(streamReader);
                    StringBuilder stringBuilder = new StringBuilder();

                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }

                    bufferedReader.close();
                    streamReader.close();
                    result = stringBuilder.toString();
                } else {
                    result = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s == null) {
                showProgress(false);
                showAlertDialog();
                return;
            }

            List<CharityObject> charityArray = new ArrayList<>();

            try {
                JSONArray jsonArray = new JSONArray(s);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    charityArray.add(new CharityObject(object));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }

            if (mContext instanceof CharityListAdapter.CharityListListener) {
                CharityListAdapter adapter = new CharityListAdapter(charityArray, (CharityListAdapter.CharityListListener) mContext);
                recyclerView.setAdapter(adapter);
            } else {
                showProgress(false);
                return;
            }
            showProgress(false);
        }
    }

    // Show/Hide Progress Spinner
    private void showProgress(final boolean show) {
        int animTime = 200;
        try {
            animTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        recyclerView.animate().setDuration(animTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        progressBar.animate().setDuration(animTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        builder.setTitle(R.string.connection_timeout_title)
                .setMessage(R.string.connection_timeout_message)
                .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getDatabase();
                    }
                })
                .show();
    }
}
