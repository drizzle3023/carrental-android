package com.drizzle.carrental.activities;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.drizzle.carrental.R;
import com.drizzle.carrental.globals.Globals;
import com.drizzle.carrental.models.Company;
import com.drizzle.carrental.models.Coverage;

import java.util.GregorianCalendar;

public class AddCoverageActivity extends Activity implements View.OnClickListener {


    static final int START_COVERAGE_ACTIVITY_REQUEST = 1;
    static final int RECORD_VEHICLE_ACTIVITY_REQUEST = 2;
    static final int RECORD_MILE_ACTIVITY_REQUEST = 3;

    /**
     * UI Control Handlers
     */
    private TextView captionStartCoverage;
    private ImageButton buttonStartCoverage;
    private TextView captionRecordCar;
    private ImageButton buttonRecordCar;
    private TextView captionRecordMile;
    private ImageButton buttonRecordMile;

    private Button buttonAdd;
    private ImageButton buttonBack;

    private ImageButton buttonEdit;

    private LinearLayout layoutCoverage;
    private LinearLayout layoutCoverageLine;
    private LinearLayout layoutCoverageContent;
    private LinearLayout layoutVideoVehicle;
    private LinearLayout layoutVideoVehicleLine;
    private LinearLayout layoutVideoVehicleContent;
    private LinearLayout layoutVideoMile;
    private LinearLayout layoutVideoMileLine;
    private LinearLayout layoutVideoMileContent;

    private ImageButton buttonDeleteCoverage;
    private ImageButton buttonDeleteVideoVehicle;
    private ImageButton buttonDeleteVideoMile;

    private Button buttonLocation;
    private Button buttonCompany;
    private Button buttonPeriod;

    private ImageButton buttonVideoVehicle;
    private ImageButton buttonVideoMile;


    /**
     * Models
     */
    private Coverage coverage;
    private String vehicleVideoURL;
    private String vehicleMileURL;

    /**
     * Temporary variables for state
     */
    private boolean isEditMode;


    /**
     * get control handlers by id and add listenres
     */
    private void getControlHandlersAndLinkActions() {

        buttonBack = findViewById(R.id.button_back);
        captionStartCoverage = (TextView) findViewById(R.id.caption_start_coverage);
        buttonStartCoverage = (ImageButton) findViewById(R.id.button_start_coverage);

        captionRecordCar = (TextView) findViewById(R.id.caption_record_car);
        buttonRecordCar = (ImageButton) findViewById(R.id.button_record_car);

        captionRecordMile = (TextView) findViewById(R.id.caption_record_mile);
        buttonRecordMile = (ImageButton) findViewById(R.id.button_record_mile);

        buttonAdd = (Button) findViewById(R.id.button_add);
        buttonEdit = (ImageButton) findViewById((R.id.button_edit));
        buttonDeleteCoverage = (ImageButton) findViewById(R.id.button_delete_coverage);
        buttonDeleteVideoVehicle = (ImageButton) findViewById(R.id.button_delete_video);
        buttonDeleteVideoMile = (ImageButton) findViewById((R.id.button_delete_miles));
        layoutCoverage = (LinearLayout) findViewById(R.id.layout_coverage);
        layoutCoverageLine = findViewById(R.id.layout_coverage_line);
        layoutCoverageContent = findViewById(R.id.layout_coverage_content);

        layoutVideoVehicle = (LinearLayout) findViewById(R.id.layout_video_vehicle);
        layoutVideoVehicleLine = findViewById(R.id.layout_video_vehicle_line);
        layoutVideoVehicleContent = findViewById(R.id.layout_video_vehicle_content);

        layoutVideoMile = findViewById(R.id.layout_video_mile);
        layoutVideoMileLine = findViewById(R.id.layout_video_mile_line);
        layoutVideoMileContent = findViewById(R.id.layout_video_mile_content);

        buttonLocation = findViewById(R.id.button_location);
        buttonCompany = findViewById(R.id.button_company);
        buttonPeriod = findViewById(R.id.button_period);

        buttonVideoVehicle = (ImageButton) findViewById(R.id.button_video_vehicle);
        buttonVideoMile = (ImageButton) findViewById(R.id.button_video_mile);

        //bind listener
        captionStartCoverage.setOnClickListener(this);
        buttonStartCoverage.setOnClickListener(this);
        captionRecordCar.setOnClickListener(this);
        buttonRecordCar.setOnClickListener(this);
        captionRecordMile.setOnClickListener(this);
        buttonRecordMile.setOnClickListener(this);

        buttonEdit.setOnClickListener(this);
        buttonDeleteCoverage.setOnClickListener(this);
        buttonDeleteVideoVehicle.setOnClickListener(this);
        buttonDeleteVideoMile.setOnClickListener(this);

        buttonBack.setOnClickListener(this);

    }

    /**
     * Init and fetch data from server
     */
    private void initVariables() {

        coverage = new Coverage();

        //fetch data from server
        Location location = new Location("CoverageLocation");
        location.setLatitude(35.6118677);
        location.setLongitude(139.6872165);

        Company company = new Company();
        company.setId((long) 1);
        company.setName("Budge Rental Car");
        company.setType("Airport");

        coverage.setLocation(location);
        coverage.setCompany(company);
        coverage.setLocationAddress("Queens, NY 11430, USA");
        coverage.setDateFrom(new GregorianCalendar(2019, 1, 1));
        coverage.setDateTo(new GregorianCalendar(2020, 1, 1));

        vehicleVideoURL = "http://i.imgur.com/DvpvklR.png";
        vehicleMileURL = "http://i.imgur.com/DvpvklR.png";

        isEditMode = false;

    }

    /**
     * Update View according to current state
     */
    private void updateView() {

        if (coverage == null) {
            layoutCoverageContent.setVisibility(View.GONE);

            ViewGroup.LayoutParams params = layoutCoverageLine.getLayoutParams();
            params.height = 100;
            layoutCoverageLine.setLayoutParams(params);

        } else {
            buttonLocation.setText(coverage.getLocationAddress());
            buttonPeriod.setText(coverage.getPeriod());
            if (coverage.getCompany() != null) {
                buttonCompany.setText(coverage.getCompany().getName());
            }
        }

        if (vehicleVideoURL == null || vehicleVideoURL.isEmpty()) {
            layoutVideoVehicleContent.setVisibility(View.GONE);
            ViewGroup.LayoutParams params = layoutVideoVehicleLine.getLayoutParams();
            params.height = 100;
            layoutVideoVehicleLine.setLayoutParams(params);
        } else {
            buttonVideoVehicle.setImageResource(R.drawable.video_vehicle);
        }

        if (vehicleMileURL == null || vehicleMileURL.isEmpty()) {
            layoutVideoMileContent.setVisibility(View.GONE);
            ViewGroup.LayoutParams params = layoutVideoMileLine.getLayoutParams();
            params.height = 100;
            layoutVideoMileLine.setLayoutParams(params);
        } else {
            buttonVideoVehicle.setImageResource(R.drawable.video_mile);
        }

        if (isEditMode) {
            buttonEdit.setVisibility(View.VISIBLE);
            buttonDeleteCoverage.setVisibility(View.VISIBLE);
            buttonDeleteVideoVehicle.setVisibility(View.VISIBLE);
            buttonDeleteVideoMile.setVisibility(View.VISIBLE);

        } else {
            buttonEdit.setVisibility(View.GONE);
            buttonDeleteCoverage.setVisibility(View.GONE);
            buttonDeleteVideoVehicle.setVisibility(View.GONE);
            buttonDeleteVideoMile.setVisibility(View.GONE);
        }

    }

    /**
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_coverage);

        getControlHandlersAndLinkActions();

        //initVariables();

        updateView();


    }


    /**
     * OnClick Handlers
     *
     * @param view
     */
    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.caption_start_coverage:
            case R.id.button_start_coverage:

                navigateToStartCoveragePage();
                break;

            case R.id.caption_record_car:
            case R.id.button_record_car:

                navigateToRecordVehicleActivity();
                break;

            case R.id.caption_record_mile:
            case R.id.button_record_mile:

                navigateToRecordMileActivity();
                break;

            case R.id.button_add:

                completeAddCoverage();
                break;

            case R.id.button_back:
                setResult(RESULT_CANCELED);
                super.onBackPressed();
        }
    }


    /**
     * navigate to add new coverage activity
     */
    private void navigateToStartCoveragePage() {

        Intent intent = new Intent(AddCoverageActivity.this, StartCoverageActivity.class);
        startActivityForResult(intent, START_COVERAGE_ACTIVITY_REQUEST);
    }

    /**
     * navigate to record vehicle activity
     */
    private void navigateToRecordVehicleActivity() {

        Intent intent = new Intent(AddCoverageActivity.this, RecordVehicleActivity.class);
        startActivityForResult(intent, RECORD_VEHICLE_ACTIVITY_REQUEST);
    }

    /**
     * navigate to record vehicle activity
     */
    private void navigateToRecordMileActivity() {

        Intent intent = new Intent(AddCoverageActivity.this, RecordMileActivity.class);
        startActivityForResult(intent, RECORD_MILE_ACTIVITY_REQUEST);
    }

    private void completeAddCoverage() {

        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == START_COVERAGE_ACTIVITY_REQUEST) {

            if (resultCode == RESULT_OK) {
                coverage = Globals.coverage;
                updateView();
            }
        }
    }

    @Override
    public void onBackPressed() {

        setResult(RESULT_OK);
        super.onBackPressed();
    }
}
