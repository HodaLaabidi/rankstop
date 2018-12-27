package rankstop.steeringit.com.rankstop.ui.fragments;

import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.Locale;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rankstop.steeringit.com.rankstop.MVP.model.PresenterUpdateProfileImpl;
import rankstop.steeringit.com.rankstop.MVP.presenter.RSPresenter;
import rankstop.steeringit.com.rankstop.MVP.view.RSView;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.data.model.User;
import rankstop.steeringit.com.rankstop.ui.activities.ContainerActivity;
import rankstop.steeringit.com.rankstop.ui.callbacks.FragmentActionListener;
import rankstop.steeringit.com.rankstop.utils.RSConstants;

public class UpdateProfileFragment extends Fragment implements RSView.StandardView, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private View rootView;
    private Unbinder unbinder;
    private static UpdateProfileFragment instance;
    private RSPresenter.UpdateProfilePresenter presenterUpdateProfile;
    private User currentUser;

    private Geocoder geocoder;
    private GoogleApiClient googleApiClient;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.avatar)
    SimpleDraweeView avatar;

    @BindView(R.id.input_first_name)
    TextInputEditText inputFirstNameET;
    @BindView(R.id.input_last_name)
    TextInputEditText inputLastNameET;
    @BindView(R.id.input_username)
    TextInputEditText inputUserNameET;
    @BindView(R.id.input_pays)
    TextInputEditText inputCountryET;
    @BindView(R.id.input_city)
    TextInputEditText inputCityET;
    @BindView(R.id.input_phone)
    TextInputEditText inputPhoneET;

    // save changes
    @OnClick(R.id.btn_save_changes)
    public void saveChanges(){}
    // current location
    @OnClick(R.id.btn_use_c_location)
    public void cLocation(){
        getCurrentLocation();
    }

    private void getCurrentLocation() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }else{
            onConnected(null);
        }
        googleApiClient.connect();
    }

    private FragmentActionListener fragmentActionListener;

    public void setFragmentActionListener(FragmentActionListener fragmentActionListener) {
        this.fragmentActionListener = fragmentActionListener;
    }

    public static UpdateProfileFragment getInstance(User user) {
        Bundle args = new Bundle();
        args.putSerializable(RSConstants.CURRENT_USER, user);
        if (instance == null) {
            instance = new UpdateProfileFragment();
        }
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_update_profile, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bindViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        presenterUpdateProfile = new PresenterUpdateProfileImpl(UpdateProfileFragment.this);

        currentUser = (User) getArguments().getSerializable(RSConstants.CURRENT_USER);

        bindData(currentUser);
    }

    private void bindData(User user) {
        inputFirstNameET.setText(user.getFullName());
        inputLastNameET.setText(user.getFullName());
        inputUserNameET.setText(user.getUsername());
        inputCountryET.setText(user.getAddress());
        inputCityET.setText(user.getAddress());
        inputPhoneET.setText(user.getPhone());
        setUserPic(user.getPictureProfile());
    }

    private void bindViews() {
        toolbar.setTitle("Modifier profil");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setFragmentActionListener((ContainerActivity) getActivity());
    }

    private void setUserPic(String picture) {
        Uri imageUri = Uri.parse(picture);
        avatar.setImageURI(imageUri);
    }

    @Override
    public void onDestroyView() {
        rootView = null;
        unbinder.unbind();
        instance= null;
        presenterUpdateProfile.onDestroy();
        super.onDestroyView();
    }

    @Override
    public void onSuccess(String target, Object data) {
        switch(target){
            case "":
                break;
        }
    }

    @Override
    public void onFailure(String target) {
        switch(target){
            case "":
                break;
        }
    }

    @Override
    public void showProgressBar(String target) {
        switch(target){
            case "":
                break;
        }
    }

    @Override
    public void hideProgressBar(String target) {
        switch(target){
            case "":
                break;
        }
    }

    @Override
    public void showMessage(String target, String message) {
        switch(target){
            case "":
                break;
        }
    }





    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflater.inflate(R.menu.profile_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case android.R.id.home:
                getActivity().onBackPressed();
                break;
            /*case R.id.setting:
                fragmentActionListener.startFragment(SettingsFragment.getInstance(), RSConstants.FRAGMENT_SETTINGS);
                break;
            case R.id.history:
                fragmentActionListener.startFragment(HistoryFragment.getInstance(), RSConstants.FRAGMENT_HISTORY);
                break;
            case R.id.contact:
                fragmentActionListener.startFragment(ContactFragment.getInstance(), RSConstants.FRAGMENT_CONTACT);
                break;
            case R.id.notifications:
                fragmentActionListener.startFragment(ListNotifFragment.getInstance(), RSConstants.FRAGMENT_NOTIF);
                break;*/
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else {

            Location userCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (userCurrentLocation != null) {
                LatLng currentUserLatLang = new LatLng(userCurrentLocation.getLatitude(), userCurrentLocation.getLongitude());
                inputCountryET.post(new Runnable() {
                    @Override
                    public void run() {
                        inputCountryET.setText(getCountry(currentUserLatLang));
                    }
                });

                inputCityET.post(new Runnable() {
                    @Override
                    public void run() {
                        inputCityET.setText(getCity(currentUserLatLang));
                    }
                });
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            onConnected(null);
        } else {
            // No Permitions Granted
        }
    }

    private String getCity(LatLng latLng) {
        geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            String city = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0).getLocality();
            return city != null ? city : "";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private String getCountry(LatLng latLng) {
        geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            String country = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0).getCountryName();
            return country != null ? country : "";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
