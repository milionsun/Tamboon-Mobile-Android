package com.tamboon.tamboon.tamboon_mobile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Shown after finish donation from DonationActivity.java
 * Press button to go back to first page (CharityListActivity.java)
 */

public class FinishPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_page);
        Button finishButton = (Button) findViewById(R.id.finishButton);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFirstPage();
            }
        });
    }

    private void goToFirstPage() {
        Intent intent = new Intent(getApplicationContext(), CharityListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
