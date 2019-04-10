package rankstop.steeringit.com.rankstop.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import rankstop.steeringit.com.rankstop.R;


public class SplashScreenActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_splash_screen);
        startActivity(new Intent(getApplicationContext(), ContainerActivity.class));
        finish();
    }
}
