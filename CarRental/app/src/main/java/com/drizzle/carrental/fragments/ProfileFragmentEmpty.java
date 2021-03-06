package com.drizzle.carrental.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.drizzle.carrental.R;
import com.drizzle.carrental.activities.AddCoverageActivity;
import com.drizzle.carrental.activities.SignUpLoginActivity;
import com.drizzle.carrental.globals.Constants;
import com.drizzle.carrental.globals.Globals;

public class ProfileFragmentEmpty extends Fragment implements View.OnClickListener {

    private TextView textViewLogin;
    private TextView textViewSignup;
    private TextView textViewFAQs;
    private TextView textViewAbout;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile_empty, container, false);

        textViewLogin = (TextView) view.findViewById(R.id.link_login);
        textViewSignup = (TextView) view.findViewById(R.id.link_signup);
        textViewFAQs = (TextView) view.findViewById(R.id.link_faqs);
        textViewAbout = (TextView) view.findViewById(R.id.link_about);

        textViewLogin.setOnClickListener(this);
        textViewSignup.setOnClickListener(this);
        textViewFAQs.setOnClickListener(this);
        textViewAbout.setOnClickListener(this);


        return view;

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.link_login:
                navigateToLoginActivity();
                break;
            case R.id.link_signup:
                navigateToSignUpActivity();
                break;

            case R.id.link_faqs:
            case R.id.link_about:
            default:
                break;
        }
    }

    private void navigateToSignUpActivity() {

        Constants.isNavigateToSignupOrLogin = true;
        Intent intent = new Intent(getActivity(), SignUpLoginActivity.class);
        startActivity(intent);
    }

    private void navigateToLoginActivity() {

        Constants.isNavigateToSignupOrLogin = false;
        Intent intent = new Intent(getActivity(), SignUpLoginActivity.class);
        startActivity(intent);
    }
}
