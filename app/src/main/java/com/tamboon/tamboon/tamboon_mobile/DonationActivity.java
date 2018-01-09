package com.tamboon.tamboon.tamboon_mobile;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import co.omise.android.models.Token;
import co.omise.android.ui.CreditCardActivity;

public class DonationActivity extends AppCompatActivity {
    private static final String OMISE_PKEY = "pkey_test_5aajhp4l3ouwpae8cg1";
    private static final int REQUEST_CC = 100;
    private static final String TAG = "DonationActivity";

    private EditText nameEditText;
    private EditText amountEditText;
    private Button creditCardButton;
    private ProgressBar progressBar;
    private View contentView;
    private int amount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation);
        contentView = findViewById(R.id.contentView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        nameEditText = (EditText) findViewById(R.id.nameEditText);

        creditCardButton = (Button) findViewById(R.id.creditCardButton);
        creditCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCreditCardForm();
            }
        });
        creditCardButton.setEnabled(false);

        amountEditText = (EditText) findViewById(R.id.amountEditText);
        amountEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    Double amount = Double.parseDouble(editable.toString());
                    creditCardButton.setEnabled(true);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    creditCardButton.setEnabled(false);
                }
            }
        });
    }

    void showCreditCardForm() {
        Intent intent = new Intent(this, CreditCardActivity.class);
        intent.putExtra(CreditCardActivity.EXTRA_PKEY, OMISE_PKEY);
        startActivityForResult(intent, REQUEST_CC);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CC:
                if (resultCode == CreditCardActivity.RESULT_CANCEL) {
                    return;
                }

                Token token = data.getParcelableExtra(CreditCardActivity.EXTRA_TOKEN_OBJECT);
                sendPostRequest(token);
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    void sendPostRequest(Token token) {
        showProgress(true);
        try {
            Double amount = Double.parseDouble(amountEditText.getText().toString());

            JSONObject object = new JSONObject();
            object.put("name", nameEditText.getText().toString());
            object.put("token", token.id);
            object.put("amount", (int)(amount * 100));
            String url = getString(R.string.server_url) + getString(R.string.donations);
            new DonatePostRequest(object).execute(url);
        } catch (JSONException e) {
            e.printStackTrace();
            showProgress(false);
        }
    }

    void showFinishPage() {
        Intent intent = new Intent(this, FinishPageActivity.class);
        startActivity(intent);
    }

    private class DonatePostRequest extends AsyncTask<String, Void, Integer> {
        private static final String REQUEST_METHOD = "POST";
        private static final int READ_TIMEOUT = 15000;
        private static final int CONNECTION_TIMEOUT = 15000;

        private JSONObject mJsonObject;

        private DonatePostRequest(JSONObject jsonObject) {
            this.mJsonObject = jsonObject;
        }

        @Override
        protected Integer doInBackground(String... strings) {
            String url = strings[0];
            int result = 0;
            try {
                URL serverUrl = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) serverUrl.openConnection();
                connection.setRequestMethod(REQUEST_METHOD);
                connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                connection.setRequestProperty("Accept","application/json");
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);
                connection.setDoInput(true);
                connection.setDoOutput(true);

                DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
                dataOutputStream.writeBytes(mJsonObject.toString());
                dataOutputStream.flush();
                dataOutputStream.close();

                int responseCode = connection.getResponseCode();
                result = responseCode;
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
                    String stringResult = stringBuilder.toString();
                } else {
                    Log.d(TAG, "doInBackground: " + responseCode);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (result == 200) {
                showFinishPage();
            }
            showProgress(false);
        }
    }

    private void showProgress(final boolean show) {
        int animTime = 200;
        try {
            animTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        contentView.setVisibility(show ? View.GONE : View.VISIBLE);
        contentView.animate().setDuration(animTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                contentView.setVisibility(show ? View.GONE : View.VISIBLE);
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
}
