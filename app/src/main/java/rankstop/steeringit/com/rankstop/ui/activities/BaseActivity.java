package com.steeringit.rankstop.ui.activities;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.steeringit.rankstop.RankStop;
import com.steeringit.rankstop.utils.Utility;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(RankStop.localeManager.setLocale(base));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.resetActivityTitle(this);
    }
}