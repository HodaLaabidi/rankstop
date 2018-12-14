package rankstop.steeringit.com.rankstop.ui.activities;

import android.os.Bundle;
import android.os.Handler;
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
import rankstop.steeringit.com.rankstop.data.model.custom.RSAddReview;
import rankstop.steeringit.com.rankstop.data.model.custom.RSNavigationData;
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
import rankstop.steeringit.com.rankstop.utils.RSConstants;

public class ContainerActivity extends AppCompatActivity implements FragmentActionListener {

    private BottomNavigationView navigation;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;
    private boolean isLoggedIn = false;
    private Handler handler;
    private Runnable runnable;

    public WeakReference<ContainerActivity> activity;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Log.d("TAG_UI", "" + Thread.currentThread().getId());
            Fragment fragment = fragmentManager.findFragmentById(R.id.container);
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    if (!(fragment instanceof HomeFragment))
                        replaceFragment(HomeFragment.getInstance(), RSConstants.FRAGMENT_HOME);
                    return true;
                case R.id.navigation_add_item:
                    if (!(fragment instanceof AddItemFragment))
                        replaceFragment(AddItemFragment.getInstance(), RSConstants.FRAGMENT_ADD_ITEM);
                    return true;
                case R.id.navigation_my_evals:
                    if (!(fragment instanceof MyEvaluationsFragment))
                        replaceFragment(MyEvaluationsFragment.getInstance(), RSConstants.FRAGMENT_MY_EVALS);
                    return true;
                case R.id.navigation_profile:
                    if (isLoggedIn) {
                        if (!(fragment instanceof ProfileFragment)) {
                            replaceFragment(ProfileFragment.getInstance(), RSConstants.FRAGMENT_PROFILE);
                            item.setTitle(getResources().getString(R.string.title_profile));
                        }
                    } else {
                        if (!(fragment instanceof SignupFragment)) {
                            replaceFragment(SignupFragment.getInstance(new RSNavigationData(RSConstants.FRAGMENT_PROFILE, "")), RSConstants.FRAGMENT_SIGN_UP);
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
                Log.i("BACK_STACK", "fragment count in back stack: " + fragmentManager.getBackStackEntryCount());
            }
        });

        replaceFragment(HomeFragment.getInstance(), RSConstants.FRAGMENT_HOME);
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

    public void manageSession(boolean isLoggedIn, RSNavigationData rsNavigationData) {
        this.isLoggedIn = isLoggedIn;
        if (isLoggedIn)
            rsNavigationData.setUserId(RSSession.getCurrentUser(activity.get()).get_id());

        switch (rsNavigationData.getFrom()) {
            case RSConstants.FRAGMENT_MY_EVALS:
                navigation.setSelectedItemId(R.id.navigation_my_evals);
                break;
            case RSConstants.FRAGMENT_PROFILE:
                navigation.setSelectedItemId(R.id.navigation_profile);
                break;
            case RSConstants.FRAGMENT_SIGN_UP:
                navigation.setSelectedItemId(R.id.navigation_profile);
                break;
            case RSConstants.FRAGMENT_HOME:
                //follow mel home
                startFragment(HomeFragment.getInstance(rsNavigationData), RSConstants.FRAGMENT_ITEM_DETAILS);
                break;
            case RSConstants.FRAGMENT_ADD_REVIEW:
                if (rsNavigationData.getAction().equals(RSConstants.ACTION_ADD_REVIEW)) {
                    RSAddReview rsAddReview = new RSAddReview();
                    rsAddReview.setItemId(rsNavigationData.getItemId());
                    rsAddReview.setCategoryId(rsNavigationData.getCategoryId());
                    startFragment(AddReviewFragment.getInstance(rsAddReview, null, RSConstants.FRAGMENT_SIGN_UP), RSConstants.FRAGMENT_ADD_REVIEW);
                } else if (rsNavigationData.getAction().equals(RSConstants.ACTION_FOLLOW)) {
                    // follow item men item details
                    startFragment(ItemDetailsFragment.getInstance(rsNavigationData), RSConstants.FRAGMENT_ITEM_DETAILS);
                }
                break;
            case RSConstants.FRAGMENT_ITEM_DETAILS:
                if (rsNavigationData.getAction().equals(RSConstants.ACTION_REPORT_ABUSE)) {
                    startFragment(ItemDetailsFragment.getInstance(rsNavigationData), RSConstants.FRAGMENT_ITEM_DETAILS);
                }
                break;
        }
    }

    public void replaceFragment(Fragment fragment, String tag) {
        /*if (handler == null)
            handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {

            }
        };
        handler.postDelayed(runnable, 200);*/
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = fragmentManager.findFragmentById(R.id.container);
        if (fragment instanceof HomeFragment) {
            fragmentManager.popBackStack(0, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            finish();
        } else {
            if (fragment instanceof AddItemFragment || fragment instanceof MyEvaluationsFragment || fragment instanceof ProfileFragment) {
                fragmentManager.popBackStack(0, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                Log.d("TAG_BACK_STACK", "" + Thread.currentThread().getId());
                if (handler == null)
                    handler = new Handler();
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        navigation.setSelectedItemId(R.id.navigation_home);
                    }
                };
                handler.postDelayed(runnable, 200);

            } else {
                // test if last
                int count = fragmentManager.getBackStackEntryCount();
                if (count > 2) {
                    FragmentManager.BackStackEntry entry = fragmentManager.getBackStackEntryAt(count - 2);
                    if (entry.getName().equals(RSConstants.FRAGMENT_SIGN_UP) || entry.getName().equals(RSConstants.FRAGMENT_ADD_REVIEW))
                        fragmentManager.popBackStack();
                }
                fragmentManager.popBackStack();
            }
        }
        //Toast.makeText(this, ""+fragmentManager.getBackStackEntryCount(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void startFragment(Fragment fragment, String tag) {
        replaceFragment(fragment, tag);
    }

    @Override
    public void pop() {
        fragmentManager.popBackStack();
    }

    @Override
    protected void onDestroy() {
        activity.clear();
        handler.removeCallbacks(runnable);
        super.onDestroy();
    }
}
