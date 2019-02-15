package rankstop.steeringit.com.rankstop.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


public class SplashScreenActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startActivity(new Intent(getApplicationContext(), ContainerActivity.class));
        finish();
    }
}
