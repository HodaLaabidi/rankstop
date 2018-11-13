package rankstop.steeringit.com.rankstop.ui.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;

import java.lang.ref.WeakReference;

import butterknife.ButterKnife;
import rankstop.steeringit.com.rankstop.ui.callbacks.FragmentActionListener;
import rankstop.steeringit.com.rankstop.ui.fragments.AddItemFragment;
import rankstop.steeringit.com.rankstop.ui.fragments.AddReviewFragment;
import rankstop.steeringit.com.rankstop.ui.fragments.ContactFragment;
import rankstop.steeringit.com.rankstop.ui.fragments.EditProfileFragment;
import rankstop.steeringit.com.rankstop.ui.fragments.HistoryFragment;
import rankstop.steeringit.com.rankstop.ui.fragments.HomeFragment;
import rankstop.steeringit.com.rankstop.ui.fragments.ItemCommentsFragment;
import rankstop.steeringit.com.rankstop.ui.fragments.ItemCreatedFragment;
import rankstop.steeringit.com.rankstop.ui.fragments.ItemDetailsFragment;
import rankstop.steeringit.com.rankstop.ui.fragments.ItemEvalsFragment;
import rankstop.steeringit.com.rankstop.ui.fragments.ItemFollowedFragment;
import rankstop.steeringit.com.rankstop.ui.fragments.ItemOwnedFragment;
import rankstop.steeringit.com.rankstop.ui.fragments.ItemPicsFragment;
import rankstop.steeringit.com.rankstop.ui.fragments.ListNotifFragment;
import rankstop.steeringit.com.rankstop.ui.fragments.ProfileFragment;
import rankstop.steeringit.com.rankstop.ui.fragments.MyEvaluationsFragment;
import rankstop.steeringit.com.rankstop.ui.fragments.SettingsFragment;
import rankstop.steeringit.com.rankstop.ui.fragments.SignupFragment;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.session.RSSession;

public class ContainerActivity extends AppCompatActivity implements FragmentActionListener {

    private BottomNavigationView navigation;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;
    private boolean isLoggedIn = false;

    private WeakReference<ContainerActivity> activity;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment = fragmentManager.findFragmentById(R.id.container);
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    if (!(fragment instanceof HomeFragment))
                        replaceFragment(HomeFragment.getInstance());
                    return true;
                case R.id.navigation_add_item:
                    if (!(fragment instanceof AddItemFragment))
                        replaceFragment(AddItemFragment.getInstance());
                    return true;
                case R.id.navigation_settings:
                    if (!(fragment instanceof MyEvaluationsFragment))
                        replaceFragment(MyEvaluationsFragment.getInstance());
                    return true;
                case R.id.navigation_profile:
                    if (isLoggedIn) {
                        if (!(fragment instanceof ProfileFragment)){
                            replaceFragment(ProfileFragment.getInstance());
                            item.setTitle(getResources().getString(R.string.title_profile));
                        }
                    } else {
                        if (!(fragment instanceof SignupFragment)) {
                            replaceFragment(SignupFragment.getInstance());
                            item.setTitle(getResources().getString(R.string.title_login));
                        }
                    }
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(getApplicationContext());
        setContentView(R.layout.activity_container);
        ButterKnife.bind(this);

        activity = new WeakReference<ContainerActivity>(this);

        isLoggedIn = RSSession.isLoggedIn(getApplicationContext());

        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        fragmentManager = getSupportFragmentManager();

        //backstack change listener
        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Log.i("BACK STACK", "fragment count in back stack: " + fragmentManager.getBackStackEntryCount());
                Toast.makeText(activity.get(), ""+fragmentManager.getBackStackEntryCount(), Toast.LENGTH_SHORT).show();
            }
        });

        replaceFragment(HomeFragment.getInstance());
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
        //fragmentManager.popBackStack();// supprimé la dernière transaction du back stack
        //fragmentManager.popBackStack(0,0); // supprimé tous les transaction du back stack sauf la première
        //fragmentManager.popBackStack(0,FragmentManager.POP_BACK_STACK_INCLUSIVE); Supprimé tous les transactions du back stack
        //fragmentManager.popBackStack("fragment name",0); Supprimé tous les transactions du back stack sauf la transaction qui a le nom "fragment name"
    }

    public void manageSession(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
        navigation.setSelectedItemId(R.id.navigation_profile);
    }

    public void replaceFragment(Fragment fragment) {
        if (fragment instanceof AddItemFragment)
            AddItemFragment.getInstance().setFragmentActionListener(activity.get());
        else if (fragment instanceof AddReviewFragment)
            AddReviewFragment.getInstance().setFragmentActionListener(activity.get());
        else if (fragment instanceof ContactFragment)
            ContactFragment.getInstance().setFragmentActionListener(activity.get());
        else if (fragment instanceof EditProfileFragment)
            EditProfileFragment.getInstance().setFragmentActionListener(activity.get());
        else if (fragment instanceof HistoryFragment)
            HistoryFragment.getInstance().setFragmentActionListener(activity.get());
        else if (fragment instanceof HomeFragment)
            HomeFragment.getInstance().setFragmentActionListener(activity.get());
        else if (fragment instanceof ItemCommentsFragment)
            ItemCommentsFragment.getInstance().setFragmentActionListener(activity.get());
        else if (fragment instanceof ItemCreatedFragment)
            ItemCreatedFragment.getInstance().setFragmentActionListener(activity.get());
        else if (fragment instanceof ItemDetailsFragment)
            ItemDetailsFragment.getInstance().setFragmentActionListener(activity.get());
        else if (fragment instanceof ItemEvalsFragment)
            ItemEvalsFragment.getInstance().setFragmentActionListener(activity.get());
        else if (fragment instanceof ItemFollowedFragment)
            ItemFollowedFragment.getInstance().setFragmentActionListener(activity.get());
        else if (fragment instanceof ItemOwnedFragment)
            ItemOwnedFragment.getInstance().setFragmentActionListener(activity.get());
        else if (fragment instanceof ItemPicsFragment)
            ItemPicsFragment.getInstance().setFragmentActionListener(activity.get());
        else if (fragment instanceof ListNotifFragment)
            ListNotifFragment.getInstance().setFragmentActionListener(activity.get());
        else if (fragment instanceof MyEvaluationsFragment)
            MyEvaluationsFragment.getInstance().setFragmentActionListener(activity.get());
        else if (fragment instanceof ProfileFragment)
            ProfileFragment.getInstance().setFragmentActionListener(activity.get());
        else if (fragment instanceof SettingsFragment)
            SettingsFragment.getInstance().setFragmentActionListener(activity.get());
        /*else if (fragment instanceof SignupFragment)
            SignupFragment.getInstance().setFragmentActionListener(activity.get());*/

        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void replaceAndSaveFragment(Fragment fragment) {
        if (fragment instanceof AddItemFragment)
            AddItemFragment.getInstance().setFragmentActionListener(activity.get());
        else if (fragment instanceof AddReviewFragment)
            AddReviewFragment.getInstance().setFragmentActionListener(activity.get());
        else if (fragment instanceof ContactFragment)
            ContactFragment.getInstance().setFragmentActionListener(activity.get());
        else if (fragment instanceof EditProfileFragment)
            EditProfileFragment.getInstance().setFragmentActionListener(activity.get());
        else if (fragment instanceof HistoryFragment)
            HistoryFragment.getInstance().setFragmentActionListener(activity.get());
        else if (fragment instanceof HomeFragment)
            HomeFragment.getInstance().setFragmentActionListener(activity.get());
        else if (fragment instanceof ItemCommentsFragment)
            ItemCommentsFragment.getInstance().setFragmentActionListener(activity.get());
        else if (fragment instanceof ItemCreatedFragment)
            ItemCreatedFragment.getInstance().setFragmentActionListener(activity.get());
        else if (fragment instanceof ItemDetailsFragment)
            ItemDetailsFragment.getInstance().setFragmentActionListener(activity.get());
        else if (fragment instanceof ItemEvalsFragment)
            ItemEvalsFragment.getInstance().setFragmentActionListener(activity.get());
        else if (fragment instanceof ItemFollowedFragment)
            ItemFollowedFragment.getInstance().setFragmentActionListener(activity.get());
        else if (fragment instanceof ItemOwnedFragment)
            ItemOwnedFragment.getInstance().setFragmentActionListener(activity.get());
        else if (fragment instanceof ItemPicsFragment)
            ItemPicsFragment.getInstance().setFragmentActionListener(activity.get());
        else if (fragment instanceof ListNotifFragment)
            ListNotifFragment.getInstance().setFragmentActionListener(activity.get());
        else if (fragment instanceof MyEvaluationsFragment)
            MyEvaluationsFragment.getInstance().setFragmentActionListener(activity.get());
        else if (fragment instanceof ProfileFragment)
            ProfileFragment.getInstance().setFragmentActionListener(activity.get());
        else if (fragment instanceof SettingsFragment)
            SettingsFragment.getInstance().setFragmentActionListener(activity.get());
        /*else if (fragment instanceof SignupFragment)
            SignupFragment.getInstance().setFragmentActionListener(activity.get());*/

        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.addToBackStack("replace " + fragment.toString());
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = fragmentManager.findFragmentById(R.id.container);
        if (fragment instanceof HomeFragment){
            fragmentManager.popBackStack(0,FragmentManager.POP_BACK_STACK_INCLUSIVE);
            finish();
        }else {
            if (fragment instanceof AddItemFragment || fragment instanceof MyEvaluationsFragment || fragment instanceof ProfileFragment) {
                fragmentManager.popBackStack(0,FragmentManager.POP_BACK_STACK_INCLUSIVE);
                navigation.setSelectedItemId(R.id.navigation_home);
            }else {
                fragmentManager.popBackStack();
            }
        }
        Toast.makeText(this, ""+fragmentManager.getBackStackEntryCount(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void startFragment(Fragment fragment) {
        replaceFragment(fragment);
    }

    @Override
    protected void onDestroy() {
        activity.clear();
        super.onDestroy();
    }
}
