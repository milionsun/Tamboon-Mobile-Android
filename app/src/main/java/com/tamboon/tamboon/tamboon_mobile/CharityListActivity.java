package com.tamboon.tamboon.tamboon_mobile;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
        getDatabase();
    }

    private void getDatabase() {
        String url = "127.0.0.1:8080/charities";
        new CharityGetRequest().execute(url);
    }

    @Override
    public void charitySelected(CharityObject charity) {

    }

    public class CharityGetRequest extends AsyncTask<String, Void, String> {
        public static final String REQUEST_METHOD = "GET";
        public static final int READ_TIMEOUT = 15000;
        public static final int CONNECTION_TIMEOUT = 15000;

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

                Context context = getBaseContext();
                if (context instanceof CharityListAdapter.CharityListListener) {
                    CharityListAdapter adapter = new CharityListAdapter(charityArray, (CharityListAdapter.CharityListListener) context);
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
