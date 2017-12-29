package com.tamboon.tamboon.tamboon_mobile;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

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

public class CharityListActivity extends AppCompatActivity implements CharityListAdapter.CharityListListener {
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charity_list);
        recyclerView = (RecyclerView) findViewById(R.id.charityList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getDatabase();
    }

    private void getDatabase() {
        String url = "http://192.168.1.3:8080/charities";
        new CharityGetRequest(this).execute(url);
    }

    @Override
    public void charitySelected(CharityObject charity) {
        Intent intent = new Intent(this, DonationActivity.class);
        startActivity(intent);
    }

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
            if (s != null) {
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
                    return;
                }
            } else {
                return;
            }
        }
    }
}
