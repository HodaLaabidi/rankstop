package rankstop.steeringit.com.rankstop.Activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import rankstop.steeringit.com.rankstop.Fragments.AddItemFragment;
import rankstop.steeringit.com.rankstop.Fragments.HomeFragment;
import rankstop.steeringit.com.rankstop.Fragments.ProfileFragment;
import rankstop.steeringit.com.rankstop.Fragments.SettingsFragment;
import rankstop.steeringit.com.rankstop.Fragments.SignupFragment;
import rankstop.steeringit.com.rankstop.R;

public class ContainerActivity extends AppCompatActivity {

    FragmentTransaction fragmentTransaction;
    private boolean isLoggedIn = true;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    replaceFragment(new HomeFragment());
                    return true;
                case R.id.navigation_add_item:
                    replaceFragment(new AddItemFragment());
                    return true;
                case R.id.navigation_settings:
                    replaceFragment(new SettingsFragment());
                    return true;
                case R.id.navigation_profile:
                    if(isLoggedIn)
                        replaceFragment(new ProfileFragment());
                        else
                        replaceFragment(new SignupFragment());
                    return true;
            }
            return false;
        }
    };

    private void replaceFragment(Fragment fragment) {
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        //fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        replaceFragment(new HomeFragment());
    }

}
