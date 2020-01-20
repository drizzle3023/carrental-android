package com.drizzle.carrental.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.drizzle.carrental.api.ApiClient;
import com.drizzle.carrental.api.ApiInterface;
import com.drizzle.carrental.globals.Constants;
import com.drizzle.carrental.globals.SharedHelper;
import com.drizzle.carrental.globals.Utils;
import com.drizzle.carrental.models.MyProfile;
import com.drizzle.carrental.globals.Globals;
import com.drizzle.carrental.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Console;
import java.io.IOException;
import java.util.Iterator;

import io.habit.analytics.HabitStatusCodes;
import io.habit.analytics.SDK;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends Activity implements Callback<ResponseBody> {

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);



        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) &&
                (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.INTERNET, Manifest.permission.CAMERA,
                            Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_NETWORK_STATE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);

        } else {

            runMainProcess();
        }

    }

    private void runMainProcess() {
//
//        Intent newIntent = new Intent(SplashActivity.this, RecordMileActivity.class);
//        startActivity(newIntent);
//        finish();
////
//        return;
//        load saved api token
//        SharedHelper.putKey(this, "access_token", "bstohcty6u56epm09pnplrlcgpv07dj6ur6korqomx2nk0lmcy8w97anye3pxj7xoey46ckmabnp7pht3t92ssgaoy5t007ojy557aaoimc2yw25tg2ke314bdw5w6m4");
//
        String strAccessToken = SharedHelper.getKey(this, "access_token");

        if (!strAccessToken.isEmpty()) {

            fetchProfileFromServer();

        } else {

            Globals.isLoggedIn = false;

            handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    navigateToOnboardingActivity();
                }
            }, 3000);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    runMainProcess();

                } else {

                    Toast.makeText(this, "Not Granted Permissions.", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }
    }

    /**
     * fetch user profile from saved access_token
     */
    private void fetchProfileFromServer() {

        //prepare restrofit2 request parameters
        JsonObject gSonObject = new JsonObject();

        //set parameters using org.JSONObject
        JSONObject paramObject = new JSONObject();
        try {

            paramObject.put("access_token", SharedHelper.getKey(this, "access_token"));
        } catch (JSONException e) {

            e.printStackTrace();
        }

        JsonParser jsonParser = new JsonParser();
        gSonObject = (JsonObject) jsonParser.parse(paramObject.toString());

        //get apiInterface
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        //send request
        apiInterface.getUserProfile(gSonObject).enqueue(this);
    }

    //callback of success api request
    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

        String responseString = null;
        try {
            ResponseBody body = response.body();
            if (body != null) {
                responseString = body.string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject object = null;
        if (responseString != null) {
            try {
                object = new JSONObject(responseString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {

            Toast.makeText(this, R.string.message_no_response, Toast.LENGTH_SHORT).show();
            navigateToOnboardingActivity();
            return;
        }


        if (object == null) {
            navigateToOnboardingActivity();
            Toast.makeText(this, R.string.message_no_response, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            if (object.getString("success").equals("true")) {


                JSONObject data = object.getJSONObject("data");
                JSONObject profileData = data.getJSONObject("profile");
                MyProfile myProfile = new Gson().fromJson(profileData.toString(), new TypeToken<MyProfile>() {
                }.getType());

                Globals.profile = myProfile;




                if (data.getString("token_state").equals("valid")) {

                    Iterator<String> keys = object.getJSONObject("data").keys();

                    for (Iterator i = keys; i.hasNext(); ) {

                        if (i.next().equals("refresh_token")) {
                            String newPayload = data.get("refresh_token").toString();
                            String newToken = data.getString("access_token");

                            SharedHelper.putKey(SplashActivity.this, "access_token", newToken);
                            SharedHelper.putKey(SplashActivity.this, "payload", newPayload);

                            Utils.initHabitSDK(SplashActivity.this);
                        }
                    }
                }

                Utils.initHabitSDK(SplashActivity.this);


                navigateToHomeActivity();

            } else if (object.getString("success").equals("false")) {

                JSONObject data = object.getJSONObject("data");
                Toast.makeText(this, data.getString("message"), Toast.LENGTH_SHORT).show();
                navigateToOnboardingActivity();
            } else {

                navigateToOnboardingActivity();
            }
        } catch (JSONException e) {

            Toast.makeText(this, R.string.message_no_response, Toast.LENGTH_SHORT).show();
            navigateToOnboardingActivity();
            e.printStackTrace();
        }
    }

    //callback of failed api request
    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t) {

        navigateToOnboardingActivity();
    }

    private void navigateToHomeActivity() {

        Globals.isLoggedIn = true;

        Intent newIntent = new Intent(SplashActivity.this, HomeActivity.class);
        startActivity(newIntent);
        finish();
    }

    private void navigateToOnboardingActivity() {

        Intent newIntent = new Intent(SplashActivity.this, OnboardingActivity.class);
        startActivity(newIntent);
        finish();
    }




}