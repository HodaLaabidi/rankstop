package rankstop.steeringit.com.rankstop.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import rankstop.steeringit.com.rankstop.RankStop;
import rankstop.steeringit.com.rankstop.utils.Utility;

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