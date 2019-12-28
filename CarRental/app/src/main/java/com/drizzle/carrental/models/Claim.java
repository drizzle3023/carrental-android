package com.drizzle.carrental.models;

import android.location.Location;

import com.drizzle.carrental.enumerators.ClaimState;
import com.drizzle.carrental.enumerators.DamagedPart;
import com.drizzle.carrental.globals.Constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Claim {

    String whatHappened; //answer for "What happened"

    GregorianCalendar whenHappened; //answer for "When this happened"

    Location whereHappened; //answer for "Where this happened"

    String addressHappened; //address of happened location

    List<DamagedPart> damagedPart; //damaged part

    String videoURL; //url of recorded video

    String extraDescription; //extra information

    ClaimState claimState; //claim state

    public String getDateString() {

        String strDate = "";

        DateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT_FOR_CLAIM);
        strDate = df.format(whenHappened.getTime());
        return strDate;

    }
}