package com.drizzle.carrental.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.drizzle.carrental.R;
import com.drizzle.carrental.globals.Globals;

public class SubscribeSuccessActivity extends Activity {

    private Button buttonGotIt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribesuccess);

        buttonGotIt = findViewById(R.id.button_subscribe);
        buttonGotIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Globals.isLoggedIn = true;
                Intent intent=new Intent(SubscribeSuccessActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }

}