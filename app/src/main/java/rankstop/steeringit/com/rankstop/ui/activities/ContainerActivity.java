package rankstop.steeringit.com.rankstop.ui.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import java.lang.ref.WeakReference;

import butterknife.ButterKnife;
import rankstop.steeringit.com.rankstop.data.model.network.RSAddReview;
import rankstop.steeringit.com.rankstop.data.model.network.RSNavigationData;
import rankstop.steeringit.com.rankstop.ui.callbacks.FragmentActionListener;
import rankstop.steeringit.com.rankstop.ui.fragments.AddItemFragment;
import rankstop.steeringit.com.rankstop.ui.fragments.AddReviewFragment;
import rankstop.steeringit.com.rankstop.ui.fragments.HistoryFragment;
import rankstop.steeringit.com.rankstop.ui.fragments.HomeFragment;
import rankstop.steeringit.com.rankstop.ui.fragments.ItemDetailsFragment;
import rankstop.steeringit.com.rankstop.ui.fragments.ListNotifFragment;
import rankstop.steeringit.com.rankstop.ui.fragments.ListingItemsFragment;
import rankstop.steeringit.com.rankstop.ui.fragments.ProfileFragment;
import rankstop.steeringit.com.rankstop.ui.fragments.MyEvaluationsFragment;
import rankstop.steeringit.com.rankstop.ui.fragments.SearchFragment;
import rankstop.steeringit.com.rankstop.ui.fragments.SignupFragment;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.session.RSSession;
import rankstop.steeringit.com.rankstop.utils.RSConstants;

public class ContainerActivity extends BaseActivity implements FragmentActionListener {

    private BottomNavigationView navigation;
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
                        replaceFragment(HomeFragment.getInstance(), RSConstants.FRAGMENT_HOME);
                    return true;
                case R.id.navigation_search:
                    if (!(fragment instanceof SearchFragment))
                        replaceFragment(SearchFragment.getInstance(), RSConstants.FRAGMENT_SEARCH);
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
        setContentView(R.layout.activity_container);
        ButterKnife.bind(this);

        activity = new WeakReference<>(this);

        isLoggedIn = RSSession.isLoggedIn();

        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        fragmentManager = getSupportFragmentManager();

        replaceFragment(HomeFragment.getInstance(), RSConstants.FRAGMENT_HOME);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void manageSession(boolean isLoggedIn, RSNavigationData rsNavigationData) {
        this.isLoggedIn = isLoggedIn;
        if (isLoggedIn) {
            rsNavigationData.setUserId(RSSession.getCurrentUser().get_id());
        }

        switch (rsNavigationData.getFrom()) {
            case RSConstants.FRAGMENT_MY_EVALS:
                navigation.setSelectedItemId(R.id.navigation_my_evals);
                break;
            case RSConstants.FRAGMENT_ADD_ITEM:
                navigation.setSelectedItemId(R.id.navigation_add_item);
                break;
            case RSConstants.FRAGMENT_PROFILE:
                navigation.setSelectedItemId(R.id.navigation_profile);
                break;
            case RSConstants.FRAGMENT_SIGN_UP:
                navigation.setSelectedItemId(R.id.navigation_profile);
                break;
            case RSConstants.FRAGMENT_HOME:
                //follow mel home
                startFragment(HomeFragment.getInstance(rsNavigationData), RSConstants.FRAGMENT_HOME);
                break;
            case RSConstants.FRAGMENT_HISTORY:
                startFragment(HistoryFragment.getInstance(), RSConstants.FRAGMENT_HISTORY);
                break;
            case RSConstants.FRAGMENT_NOTIF:
                startFragment(ListNotifFragment.getInstance(), RSConstants.FRAGMENT_NOTIF);
                break;
            case RSConstants.FRAGMENT_LISTING_ITEMS:
                //follow mel home
                startFragment(ListingItemsFragment.getInstance(rsNavigationData), RSConstants.FRAGMENT_LISTING_ITEMS);
                break;
            case RSConstants.FRAGMENT_ADD_REVIEW:
                if (rsNavigationData.getAction().equals(RSConstants.ACTION_ADD_REVIEW)) {
                    RSAddReview rsAddReview = new RSAddReview();
                    rsAddReview.setItemId(rsNavigationData.getItemId());
                    rsAddReview.setCategoryId(rsNavigationData.getCategoryId());
                    startFragment(AddReviewFragment.getInstance(rsAddReview, null, RSConstants.FRAGMENT_SIGN_UP, rsNavigationData.getSubAction()), RSConstants.FRAGMENT_ADD_REVIEW);
                } else if (rsNavigationData.getAction().equals(RSConstants.ACTION_FOLLOW)) {
                    // follow item men item details
                    startFragment(ItemDetailsFragment.getInstance(rsNavigationData), RSConstants.FRAGMENT_ADD_REVIEW);
                }
                break;
            case RSConstants.FRAGMENT_ITEM_DETAILS:
                if (rsNavigationData.getAction().equals(RSConstants.ACTION_REPORT_ABUSE) || rsNavigationData.getAction().equals(RSConstants.ACTION_SEND_REQ_OWNERSHIP)) {
                    startFragment(ItemDetailsFragment.getInstance(rsNavigationData), RSConstants.FRAGMENT_ITEM_DETAILS);
                }
                break;
        }
    }

    private void replaceFragment(Fragment fragment, String tag) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
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
            if (fragment instanceof AddItemFragment || fragment instanceof MyEvaluationsFragment || fragment instanceof ProfileFragment || fragment instanceof SearchFragment || fragment instanceof SignupFragment) {
                fragmentManager.popBackStack(0, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                navigation.setSelectedItemId(R.id.navigation_home);

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
    }

    @Override
    public void navigateTo(int fragmentID, String tag) {
        navigation.setSelectedItemId(fragmentID);
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
        if (activity != null)
            activity.clear();
        super.onDestroy();
    }
}
