package com.drizzle.carrental.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.drizzle.carrental.R;
import com.drizzle.carrental.adapters.CustomAdapterForClaimListView;
import com.drizzle.carrental.api.ApiClient;
import com.drizzle.carrental.api.ApiInterface;
import com.drizzle.carrental.enumerators.ClaimState;
import com.drizzle.carrental.enumerators.CoverageState;
import com.drizzle.carrental.globals.Constants;
import com.drizzle.carrental.globals.Globals;
import com.drizzle.carrental.globals.SharedHelper;
import com.drizzle.carrental.globals.Utils;
import com.drizzle.carrental.models.Claim;
import com.drizzle.carrental.serializers.ParseClaim;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClaimsActivity extends Activity implements View.OnClickListener, Callback<ResponseBody> {

    public static final int CLAIM_ADD_REQUEST = 1;
    /**
     * UI Control Handlers
     */
    private Button buttonFileAClaim;
    private ListView listView;
    private ImageButton buttonBack;

    private Long coverageId;

    private static CustomAdapterForClaimListView adapter;

    ProgressDialog progressDialog;

    ArrayList<Claim> dataModels;
    ArrayList<ParseClaim> parseDataModels;

    /**
     * get control handlers by id and add listeners
     */
    private void getControlHandlersAndLinkActions() {

        buttonFileAClaim = findViewById(R.id.button_file_a_claim);
        buttonBack = findViewById(R.id.button_back_to_onboarding);

        listView = findViewById(R.id.list_claims);

        dataModels = new ArrayList<>();

        //prepareTestData();

        adapter = new CustomAdapterForClaimListView(dataModels, this);

        listView.setAdapter(adapter);

        buttonFileAClaim.setOnClickListener(this);
        buttonBack.setOnClickListener(this);


    }

    private void prepareTestData() {

        for (int i = 0; i < 10; i++) {

            Claim claim = new Claim();
            claim.setAddressHappened("Independence Distreet, 37 ");
            if (i % 4 == 0) {
                claim.setClaimState(ClaimState.APPROVED);
            } else if (i % 4 == 1) {
                claim.setClaimState(ClaimState.NOT_APPROVED);
            } else if (i % 4 == 2) {
                claim.setClaimState(ClaimState.INCOMPLETE);
            } else {
                claim.setClaimState(ClaimState.EXPERT_UNDERGOING);
            }
            claim.setWhenHappened(new GregorianCalendar());
            claim.setWhatHappened("Glass Damaged");

            dataModels.add(claim);
        }
    }

    /**
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_claims);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        getControlHandlersAndLinkActions();

        coverageId = getIntent().getLongExtra(Constants.INTENT_DATA_COVERAGE_ID, 0);
        if (coverageId == 0) {
            Toast.makeText(this, getString(R.string.coverage_is_not_existing), Toast.LENGTH_SHORT).show();
        }
        else {
            fetchClaimListFromServer(coverageId);
        }


    }


    /**
     * OnClick Handlers
     *
     * @param view
     */
    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.button_file_a_claim:
                Globals.selectedClaim = new Claim();
                try {
                    if (Globals.coverage.getId() > 0 && Globals.coverage.getState() == CoverageState.COVERED) {
                        navigateToFileAClaimActivity();
                    }
                    else {
                        Toast.makeText(this, getString(R.string.coverage_is_not_existing), Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, getString(R.string.coverage_is_not_existing), Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.button_back_to_onboarding:
                finish();
                break;
        }
    }

    private void navigateToFileAClaimActivity() {

        Intent intent = new Intent(ClaimsActivity.this, AddClaimActivity.class);
        startActivityForResult(intent, CLAIM_ADD_REQUEST);
    }

    public void updateView() {

        if (dataModels != null) {
            adapter = new CustomAdapterForClaimListView(dataModels, this);
            listView.setAdapter(adapter);
        }
    }

    public void removeClaimFromModelList(Long claimId) {

        for (int i = 0; i < dataModels.size(); i++) {
            if (dataModels.get(i).getId() == claimId) {

                dataModels.remove(i);
                return;
            }
        }
    }

    private void fetchClaimListFromServer(Long coverageId) {

        //prepare restrofit2 request parameters
        JsonObject gSonObject = new JsonObject();

        //set parameters using org.JSONObject
        JSONObject paramObject = new JSONObject();
        try {

            paramObject.put("access_token", SharedHelper.getKey(this, "access_token"));
            paramObject.put("coverage_id", coverageId);
        } catch (Exception e) {

            //Utils.appendLog(System.err.toString());
                e.printStackTrace();
        }

        JsonParser jsonParser = new JsonParser();
        gSonObject = (JsonObject) jsonParser.parse(paramObject.toString());

        //get apiInterface
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        //display waiting dialog
        showWaitingScreen();
        //send request
        apiInterface.getClaimList(gSonObject).enqueue(this);
    }

    private void showWaitingScreen() {

        try {

            progressDialog.show();
        } catch (Exception e) {
            //Utils.appendLog(System.err.toString());
                e.printStackTrace();
        }
    }

    private void hideWaitingScreen() {

        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            //Utils.appendLog(System.err.toString());
                e.printStackTrace();
        }
    }

    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        hideWaitingScreen();

        String responseString = null;
        try {
            ResponseBody body = response.body();
            if (body != null) {
                responseString = body.string();
            }
        } catch (Exception e) {
            //Utils.appendLog(System.err.toString());
                e.printStackTrace();
        }

        JSONObject object = null;
        if (responseString != null) {
            try {
                object = new JSONObject(responseString);
            } catch (Exception e) {
                //Utils.appendLog(System.err.toString());
                e.printStackTrace();
            }
        } else {

            Toast.makeText(this, R.string.message_no_response, Toast.LENGTH_SHORT).show();
            return;
        }

        if (object == null) {

            Toast.makeText(this, R.string.message_no_response, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            if (object.getString("success").equals("true")) {

                JSONObject data = object.getJSONObject("data");
                JSONArray listObject = data.getJSONArray("claimList");

                dataModels.clear();
                parseDataModels = new Gson().fromJson(listObject.toString(), new TypeToken<List<ParseClaim>>() {
                }.getType());

                for (int i = 0; i < parseDataModels.size(); i++) {

                    ParseClaim parseClaim = parseDataModels.get(i);
                    Claim claim = new Claim();

                    claim.setId(parseClaim.getId());

                    if (parseClaim.getName() != null && !parseClaim.getName().isEmpty()) {
                        claim.setName(parseClaim.getName());
                    }

                    if (parseClaim.getWhatHappened() != null) {
                        claim.setWhatHappened(parseClaim.getWhatHappened());
                    }

                    if (parseClaim.getTimeHappened() != 0) {

                        GregorianCalendar calendar = new GregorianCalendar();
                        calendar.setTimeInMillis(parseClaim.getTimeHappened() * 1000);
                        claim.setWhenHappened(calendar);
                    }


                    Location location = new Location("location");
                    location.setLatitude(parseClaim.getLatitude());
                    location.setLongitude(parseClaim.getLongitude());
                    claim.setWhereHappened(location);

                    if (parseClaim.getAddress() != null) {
                        claim.setAddressHappened(parseClaim.getAddress());
                    }

                    if (parseClaim.getDamagedPart() != null) {
                        claim.setDamagedPartsFromString(parseClaim.getDamagedPart());
                    }
                    if (parseClaim.getVideo() != null && !parseClaim.getVideo().isEmpty()) {
                        claim.setVideoURL(Constants.MEDIA_PATH_URL + parseClaim.getVideo());
                    }
                    if (parseClaim.getImage() != null && !parseClaim.getImage().isEmpty()) {
                        claim.setImageURL(Constants.MEDIA_PATH_URL + parseClaim.getImage());
                    }

                    claim.setExtraDescription(parseClaim.getNote());

                    claim.setClaimState(ClaimState.values()[parseClaim.getState() - 1]);

                    dataModels.add(claim);
                }
                adapter = new CustomAdapterForClaimListView(dataModels, this);

                listView.setAdapter(adapter);

                if (data.getString("token_state").equals("valid")) {

                    Iterator<String> keys = object.getJSONObject("data").keys();

                    for (Iterator i = keys; i.hasNext(); ) {

                        if (i.next().equals("refresh_user")) {

                            try {
                                JSONObject refreshUserObject = data.getJSONObject("refresh_user");

                                String newPayload = refreshUserObject.toString();
                                String newToken = refreshUserObject.getString("access_token");

                                SharedHelper.putKey(ClaimsActivity.this, "access_token", newToken);
                                SharedHelper.putKey(ClaimsActivity.this, "payload", newPayload);
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }

                            //Utils.setAuthHabitSDK(SplashActivity.this);
                        }
                    }
                }


            } else if (object.getString("success").equals("false")) {

                JSONObject data = object.getJSONObject("data");
                Toast.makeText(this, data.getString("message"), Toast.LENGTH_SHORT).show();

                if (object.getString("token_state").equals("invalid")) {

                    Utils.logout(ClaimsActivity.this, ClaimsActivity.this);
                }


            } else {

                Toast.makeText(this, R.string.message_no_response, Toast.LENGTH_SHORT).show();
            }


        } catch (Exception e) {

            Toast.makeText(this, R.string.message_no_response, Toast.LENGTH_SHORT).show();
            //Utils.appendLog(System.err.toString());
                e.printStackTrace();
        }
    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t) {
        hideWaitingScreen();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CLAIM_ADD_REQUEST) {
            //if (resultCode == RESULT_OK) {
            try {
                fetchClaimListFromServer(Globals.coverage.getId());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            //}
        }
    }
}
