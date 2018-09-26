package rankstop.steeringit.com.rankstop.Activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;

import rankstop.steeringit.com.rankstop.Fragments.AddItemFragment;
import rankstop.steeringit.com.rankstop.Fragments.HomeFragment;
import rankstop.steeringit.com.rankstop.Fragments.ProfileFragment;
import rankstop.steeringit.com.rankstop.Fragments.MyEvaluationsFragment;
import rankstop.steeringit.com.rankstop.Fragments.SignupFragment;
import rankstop.steeringit.com.rankstop.R;

public class ContainerActivity extends AppCompatActivity {

    private BottomNavigationView navigation;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;
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
                    replaceFragment(new MyEvaluationsFragment());
                    return true;
                case R.id.navigation_profile:
                    if(!isLoggedIn)
                        replaceFragment(new ProfileFragment());
                        else
                        replaceFragment(new SignupFragment());
                    return true;
            }
            return false;
        }
    };

    private void replaceFragment(Fragment fragment) {
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        //fragmentTransaction.addToBackStack("replace "+ fragment.toString());
        fragmentTransaction.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_container);

        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        fragmentManager= getSupportFragmentManager();

        //backstack change listener
        /*fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Log.i("BACK STACK", "fragment count in back stack: "+fragmentManager.getBackStackEntryCount());
            }
        });*/

        replaceFragment(new HomeFragment());
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*Fragment fragment = fragmentManager.findFragmentById(R.id.container);
        if (fragment instanceof HomeFragment){
            super.onBackPressed();
        }else {
            navigation.setSelectedItemId(R.id.navigation_home);
        }*/
        Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = fragmentManager.findFragmentById(R.id.container);
        if (fragment instanceof HomeFragment){
            super.onBackPressed();
        }else {
            navigation.setSelectedItemId(R.id.navigation_home);
        }
    }
}
