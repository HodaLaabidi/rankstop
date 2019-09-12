package com.steeringit.rankstop.ui.activities;

import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.Layout;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;

import java.lang.ref.WeakReference;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import butterknife.ButterKnife;
import com.steeringit.rankstop.RankStop;
import com.steeringit.rankstop.customviews.RSCustomToast;
import com.steeringit.rankstop.data.model.network.RSAddReview;
import com.steeringit.rankstop.data.model.network.RSNavigationData;
import com.steeringit.rankstop.ui.callbacks.FragmentActionListener;
import com.steeringit.rankstop.ui.fragments.AddItemFragment;
import com.steeringit.rankstop.ui.fragments.AddReviewFragment;
import com.steeringit.rankstop.ui.fragments.HistoryFragment;
import com.steeringit.rankstop.ui.fragments.HomeFragment;
import com.steeringit.rankstop.ui.fragments.ItemDetailsFragment;
import com.steeringit.rankstop.ui.fragments.ListNotifFragment;
import com.steeringit.rankstop.ui.fragments.ListingItemsFragment;
import com.steeringit.rankstop.ui.fragments.ProfileFragment;
import com.steeringit.rankstop.ui.fragments.MyEvaluationsFragment;
import com.steeringit.rankstop.ui.fragments.ScannerFragment;
import com.steeringit.rankstop.ui.fragments.SearchFragment;
import com.steeringit.rankstop.ui.fragments.SignupFragment;
import com.steeringit.rankstop.R;
import com.steeringit.rankstop.session.RSSession;
import com.steeringit.rankstop.ui.fragments.UpdateProfileFragment;
import com.steeringit.rankstop.utils.RSConstants;

import static com.crashlytics.android.Crashlytics.log;

public class ContainerActivity extends BaseActivity implements FragmentActionListener  {

    private static final int MY_REQUEST_CODE = 123 ;
    private BottomNavigationView navigation;
    private FragmentManager fragmentManager;
    private boolean isLoggedIn = false;
    private boolean isFirstAskForLogoutApp = false ;
    AppUpdateManager appUpdateManager;
    Task<AppUpdateInfo> appUpdateInfoTask;
    private RSNavigationData rsNavigationD = new RSNavigationData();
    private WeakReference<ContainerActivity> activity;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment = fragmentManager.findFragmentById(R.id.container);
            if(item.getTitle().length() > 13 && RankStop.getDeviceLanguage().equalsIgnoreCase(RSConstants.GermanLanguage)){
                item.setTitle(item.getTitle().subSequence(0,14)+"...");
            } else {
                item.setTitle(item.getTitle());
            }
            SpannableString itemTitle = new SpannableString(item.getTitle());
            itemTitle.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, itemTitle.length(), 0);


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


                case R.id.navigation_scanner:
                    if (!(fragment instanceof ScannerFragment))
                        replaceFragment(ScannerFragment.getInstance(), RSConstants.FRAGMENT_SCANNER);
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
        checkingApplicationUpdates();


        // ***************** print out hash key *************************************

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.steeringit.rankstop",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }


        // ***************** end print out hash key **********************************



        activity = new WeakReference<>(this);

        isLoggedIn = RSSession.isLoggedIn();

        navigation = findViewById(R.id.navigation);

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        fragmentManager = getSupportFragmentManager();

        replaceFragment(HomeFragment.getInstance(), RSConstants.FRAGMENT_HOME);


    }





    public void manageSession(boolean isLoggedIn, RSNavigationData rsNavigationData) {
        this.isLoggedIn = isLoggedIn;
        if (isLoggedIn) {
            rsNavigationData.setUserId(RSSession.getCurrentUser().get_id());
        }
        rsNavigationD = rsNavigationData ;

        switch (rsNavigationData.getFrom()) {
            case RSConstants.FRAGMENT_MY_EVALS:
                navigation.setSelectedItemId(R.id.navigation_my_evals);
                break;
            case RSConstants.FRAGMENT_ADD_ITEM:
                navigation.setSelectedItemId(R.id.navigation_add_item);
                break;
            case RSConstants.FRAGMENT_PROFILE:
                startFragment(SignupFragment.getInstance(rsNavigationData), RSConstants.FRAGMENT_SIGN_UP);
                break;
            case RSConstants.FRAGMENT_SIGN_UP:
                startFragment(ProfileFragment.getInstance(rsNavigationData), RSConstants.FRAGMENT_PROFILE);
                break;
            case RSConstants.FRAGMENT_HOME:
                //follow mel home
                startFragment(HomeFragment.getInstance(rsNavigationData), RSConstants.FRAGMENT_HOME);
                navigation.setSelectedItemId(R.id.navigation_home);
                break;
            case RSConstants.FRAGMENT_HISTORY:
                startFragment(HistoryFragment.getInstance(), RSConstants.FRAGMENT_HISTORY);
                break;
            case RSConstants.FRAGMENT_NOTIF:
                startFragment(ListNotifFragment.getInstance(), RSConstants.FRAGMENT_NOTIF);
                break;
            case RSConstants.FRAGMENT_SCANNER:

                navigation.setSelectedItemId(R.id.navigation_scanner);
                break;
            case RSConstants.UPDATE_BARCODE:
                navigation.setSelectedItemId(R.id.navigation_home);

                break;

            case RSConstants.SEARCH_BARCODE:
                startFragment(AddItemFragment.getInstance(rsNavigationData), RSConstants.FRAGMENT_SCANNER);
                navigation.setSelectedItemId(R.id.navigation_add_item);
                break;
            case RSConstants.EXISTING_BARCODE:
                startFragment(ItemDetailsFragment.getInstance(new RSNavigationData(RSConstants.FRAGMENT_SCANNER,"")), RSConstants.FRAGMENT_SCANNER);

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
            case RSConstants.UPDATE_PROFILE:
                startFragment(ProfileFragment.getInstance(rsNavigationData), RSConstants.UPDATE_PROFILE);
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
        if (fragment instanceof HomeFragment){

                if (!isFirstAskForLogoutApp) {
                    isFirstAskForLogoutApp = true ;
                     //Toast.makeText(getBaseContext(), R.string.logout_app_request, Toast.LENGTH_LONG).show();
                    new RSCustomToast(ContainerActivity.this, getResources().getString(R.string.warning), getResources().getString(R.string.logout_app_request), R.drawable.ic_warning2, RSCustomToast.WARNING).show();
                } else {
                    fragmentManager.popBackStack(0, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    finish();
                }


        } else {
            isFirstAskForLogoutApp = false;
            if (fragment instanceof AddItemFragment || fragment instanceof MyEvaluationsFragment || fragment instanceof SearchFragment) {

                fragmentManager.popBackStack(0, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                navigation.setSelectedItemId(R.id.navigation_home);
            } else if (fragment instanceof ItemDetailsFragment || fragment instanceof ProfileFragment || fragment instanceof  AddReviewFragment) {
                int count = fragmentManager.getBackStackEntryCount();
                if (count > 2) {
                    FragmentManager.BackStackEntry entry = fragmentManager.getBackStackEntryAt(count - 2);
                    if (entry.getName().equals(RSConstants.FRAGMENT_SIGN_UP ) /*|| entry.getName().equals(RSConstants.FRAGMENT_PROFILE ) */ || entry.getName().equals(RSConstants.FRAGMENT_ADD_REVIEW)){
                        fragmentManager.popBackStack();
                    if (entry.getName().equals(RSConstants.FRAGMENT_ADD_REVIEW)){
                                fragmentManager.popBackStack(0, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                navigation.setSelectedItemId(R.id.navigation_home);

                        } else if (entry.getName().equals(RSConstants.FRAGMENT_HOME)){
                            fragmentManager.popBackStack(0, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            navigation.setSelectedItemId(R.id.navigation_home);
                        } else {
                        fragmentManager.popBackStack();
                    }
                    } else {
                        fragmentManager.popBackStack();
                    }

                } else {
                    fragmentManager.popBackStack();
                }



            } else if (fragment instanceof ScannerFragment) {

                if (rsNavigationD != null) {
                    if (rsNavigationD.getAction() == RSConstants.ACTION_ADD_ITEM) {
                        fragmentManager.popBackStack();
                        navigation.setSelectedItemId(R.id.navigation_add_item);
                    } else if (rsNavigationD.getAction() == RSConstants.ACTION_UPDATE) {
                        fragmentManager.popBackStack();
                    } else {
                        fragmentManager.popBackStack(0, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        navigation.setSelectedItemId(R.id.navigation_home);

                    }
                }
                else {
                    fragmentManager.popBackStack(0, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    navigation.setSelectedItemId(R.id.navigation_home);

                }
            }else if (fragment instanceof UpdateProfileFragment || fragment instanceof ListingItemsFragment){
                fragmentManager.popBackStack();
            }
            else {
                int count = fragmentManager.getBackStackEntryCount();


                if (count > 2) {
                    FragmentManager.BackStackEntry entry = fragmentManager.getBackStackEntryAt(count - 2);
                    if (entry.getName().equals(RSConstants.FRAGMENT_SIGN_UP ) || entry.getName().equals(RSConstants.FRAGMENT_ADD_REVIEW) || entry.getName().equals(RSConstants.FRAGMENT_PROFILE))

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
        isFirstAskForLogoutApp = false ;
        super.onDestroy();
    }


    private void checkingApplicationUpdates() {
        // Creates instance of the manager.
        appUpdateManager = AppUpdateManagerFactory.create(getBaseContext());
        // Returns an intent object that you use to check for an update.
        appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    // For a flexible update, use AppUpdateType.FLEXIBLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                // Request the update.
                Log.e("InAppUpdates" ,  "available");
                try {
                    appUpdateManager.startUpdateFlowForResult(
                            // Pass the intent that is returned by 'getAppUpdateInfo()'.
                            appUpdateInfo,
                            // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                            AppUpdateType.IMMEDIATE,
                            // The current activity making the update request.
                            this,
                            // Include a request code to later monitor this update request.
                            MY_REQUEST_CODE);
                } catch (IntentSender.SendIntentException e) {

                    Log.e("InAppUpdates" ,  "not working");
                    e.printStackTrace();
                }
            } else {
                Log.e("InAppUpdates" ,  " not available");
            }

        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                log("Update flow failed! Result code: " + resultCode);
                // If the update is cancelled or fails,
                // you can request to start the update again.
            }
        }
    }

    // Checks that the update is not stalled during 'onResume()'.
// However, you should execute this check at all entry points into the app.
    @Override
    protected void onResume() {
        super.onResume();
        //   deep linking
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        if (appLinkIntent != null) {
            String param = null;
            Uri appLinkData = appLinkIntent.getData();
            if (appLinkData != null) {
                param = appLinkData.getQueryParameter(RSConstants.DEEP_LINKING_KEY_PARAM);
                if (param != null) {
                    String email = param ;
                    param = null ;
                    replaceFragment(SignupFragment.getInstance(new RSNavigationData(RSConstants.ACTIVITY_CONTAINER, email)), RSConstants.FRAGMENT_SIGN_UP);


                }
            }
        }

        appUpdateManager
                .getAppUpdateInfo()
                .addOnSuccessListener(
                        appUpdateInfo -> {
                            if (appUpdateInfo.updateAvailability()
                                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                                // If an in-app update is already running, resume the update.
                                try {
                                    appUpdateManager.startUpdateFlowForResult(
                                            appUpdateInfo,
                                            AppUpdateType.IMMEDIATE,
                                            this,
                                            MY_REQUEST_CODE);
                                } catch (IntentSender.SendIntentException e) {
                                    Log.e("InAppUpdates" ,  " in progress not working");
                                    e.printStackTrace();
                                }
                            }
                        });
    }

}
