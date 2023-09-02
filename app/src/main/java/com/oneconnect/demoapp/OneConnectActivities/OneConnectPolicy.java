package com.oneconnect.demoapp.OneConnectActivities;


/**
 * https://developer.oneconnect.top/
 * @package Oneconnect SDK Project
 * @author oneconnect.top
 * @copyright May 2022
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.oneconnect.demoapp.R;

public class OneConnectPolicy extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.oneconnect_policy);

        TextView wc_msg = findViewById(R.id.welcome_msg);
        TextView accept_pp = findViewById(R.id.tv_accept_privacy_policy);
        TextView btnAccept = findViewById(R.id.btnAccept);

        Resources res = getResources();
        String msg = String.format(res.getString(R.string.welcome_message), getResources().getString(R.string.app_name));
        wc_msg.setText(msg);

        SpannableString myString = new SpannableString(getResources().getString(R.string.privacy_policy_msg));

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(getResources().getColor(R.color.tab_background_selected));
                ds.setUnderlineText(false);
                ds.setTypeface(Typeface.DEFAULT_BOLD);
            }

            @Override
            public void onClick(View textView) {

                Intent intent = new Intent(OneConnectPolicy.this, OneConnectPolicyDetails.class);
                startActivity(intent);
            }
        };

        myString.setSpan(clickableSpan,43,67, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        accept_pp.setMovementMethod(LinkMovementMethod.getInstance());
        accept_pp.setText(myString);

        btnAccept.setOnClickListener(view -> {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime", false);
            editor.apply();
            finish();
        });
    }
}