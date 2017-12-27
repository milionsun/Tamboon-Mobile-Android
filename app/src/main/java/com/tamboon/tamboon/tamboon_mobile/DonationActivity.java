package com.tamboon.tamboon.tamboon_mobile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import co.omise.android.models.Token;
import co.omise.android.ui.CreditCardActivity;

public class DonationActivity extends AppCompatActivity {
    private static final String OMISE_PKEY = "pkey_test_5aajhp4l3ouwpae8cg1";
    private static final int REQUEST_CC = 100;

    private EditText nameEditText;
    private EditText amountEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation);
        nameEditText = (EditText) findViewById(R.id.nameEditText);
        amountEditText = (EditText) findViewById(R.id.amountEditText);

        Button creditCardButton = (Button) findViewById(R.id.creditCardButton);
        creditCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCreditCardForm();
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
        Intent intent = new Intent(this, FinishPageActivity.class);
        startActivity(intent);
    }
}
