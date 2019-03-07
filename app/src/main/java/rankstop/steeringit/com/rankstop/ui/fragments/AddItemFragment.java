package rankstop.steeringit.com.rankstop.ui.fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import butterknife.BindInt;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rankstop.steeringit.com.rankstop.MVP.model.PresenterItemImpl;
import rankstop.steeringit.com.rankstop.MVP.presenter.RSPresenter;
import rankstop.steeringit.com.rankstop.MVP.view.RSView;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.RankStop;
import rankstop.steeringit.com.rankstop.customviews.RSETMedium;
import rankstop.steeringit.com.rankstop.customviews.RSTVMedium;
import rankstop.steeringit.com.rankstop.data.model.db.Category;
import rankstop.steeringit.com.rankstop.data.model.network.RSAddReview;
import rankstop.steeringit.com.rankstop.data.model.network.RSNavigationData;
import rankstop.steeringit.com.rankstop.session.RSSession;
import rankstop.steeringit.com.rankstop.ui.activities.ContainerActivity;
import rankstop.steeringit.com.rankstop.ui.adapter.SpinnerCategoryAdapter;
import rankstop.steeringit.com.rankstop.ui.callbacks.FragmentActionListener;
import rankstop.steeringit.com.rankstop.ui.dialogFragment.ContactDialog;
import rankstop.steeringit.com.rankstop.utils.RSConstants;
import rankstop.steeringit.com.rankstop.utils.RSNetwork;
import rankstop.steeringit.com.rankstop.utils.WorkaroundMapFragment;

import static android.content.Context.LOCATION_SERVICE;

public class AddItemFragment extends Fragment implements RSView.StandardView, AdapterView.OnItemSelectedListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    private static AddItemFragment instance;
    private String itemName, itemDescription;

    private Unbinder unbinder;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.layout_login)
    LinearLayout loginLayout;

    @BindView(R.id.tv_login)
    RSTVMedium loginTV;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.input_layout_title)
    TextInputLayout inputLayoutName;
    @BindView(R.id.input_title)
    RSETMedium nameET;

    @BindView(R.id.input_layout_description)
    TextInputLayout inputLayoutDescription;
    @BindView(R.id.input_description)
    RSETMedium descriptionET;

    @BindView(R.id.input_layout_address)
    TextInputLayout inputLayoutAddress;
    @BindView(R.id.input_address)
    RSETMedium addressET;

    @BindView(R.id.input_layout_phone)
    TextInputLayout inputLayoutPhone;
    @BindView(R.id.input_phone)
    RSETMedium phoneET;

    @BindView(R.id.categories_spinner)
    AppCompatSpinner categorySpinner;

    @BindView(R.id.scroll_view)
    NestedScrollView scrollView;

    @BindView(R.id.layout_location)
    LinearLayout locationLayout;

    @BindString(R.string.field_required)
    String fieldRequired;
    @BindString(R.string.add_item)
    String addItemTitle;
    @BindString(R.string.off_line)
    String offlineMsg;
    @BindString(R.string.gps_disabled_msg)
    String gpsDisabledMsg;
    @BindString(R.string.gps_positive_btn_msg)
    String positiveBtnText;
    @BindString(R.string.cancel)
    String negativeBtnText;

    @BindInt(R.integer.max_length_50)
    int maxLength50;

    @BindInt(R.integer.max_length_500)
    int maxLength500;

    @OnClick(R.id.btn_add_item)
    void onClick() {

        itemName = nameET.getText().toString().trim();
        itemDescription = descriptionET.getText().toString().trim();

        if (validForm()) {
            if (RSNetwork.isConnected()) {
                try {
                    // pour 'valider l'ajout du cet item, tu dois l'évaluer
                    rsAddItem.setCategoryId(selectedCategory.get_id());
                    rsAddItem.setTitle(itemName);
                    rsAddItem.setDescription(itemDescription);
                    rsAddItem.setAddress(addressET.getText().toString().trim());
                    rsAddItem.setPhone(phoneET.getText().toString().trim());
                    rsAddItem.setLatitude("" + currentLatitude);
                    rsAddItem.setLongitude("" + currentLongitude);
                    fragmentActionListener.startFragment(AddReviewFragment.getInstance(rsAddItem, null, "", RSConstants.ACTION_EVAL), RSConstants.FRAGMENT_ADD_REVIEW);
                } catch (Exception e) {

                }

            } else {
                onOffLine();
            }
        } else {
            scrollView.scrollTo(0, 0);
        }
    }

    private boolean validForm() {
        int x = 0;

        if (TextUtils.isEmpty(itemName)) {
            inputLayoutName.setError(fieldRequired);
            x++;
        }
        if (itemName.length() > maxLength50) {
            x++;
        }
        if (itemDescription.length() > maxLength500) {
            x++;
        }
        if (selectedCategory == null) {
            x++;
        }
        return x == 0;
    }

    @OnClick(R.id.btn_login)
    void goToLogin() {
        RSNavigationData rsNavigationData = new RSNavigationData(RSConstants.FRAGMENT_ADD_ITEM, RSConstants.ACTION_CONNECT, "", "", "", "", "");
        navigateToSignUp(rsNavigationData);
    }

    private View rootView;

    private GoogleMap mMap;
    private Geocoder geocoder;
    private GoogleApiClient googleApiClient;
    private boolean isMapInitialized = false;
    private Category selectedCategory;

    private double currentLatitude, currentLongitude;
    private RSAddReview rsAddItem = new RSAddReview();

    private RSPresenter.ItemPresenter itemPresenter;
    private LocationManager locationManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_add_item, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        bindViews();
    }

    private void loadCategoriesList() {
        itemPresenter.loadCategoriesList(RankStop.getDeviceLanguage());
    }

    private void bindViews() {

        toolbar.setTitle(addItemTitle);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        setFragmentActionListener((ContainerActivity) getActivity());

        if (RSNetwork.isConnected()) {
            if (RSSession.isLoggedIn()) {
                scrollView.setVisibility(View.VISIBLE);
                itemPresenter = new PresenterItemImpl(AddItemFragment.this);
                categorySpinner.setOnItemSelectedListener(this);
                loadCategoriesList();
                locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
                nameET.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.toString().trim().length() > 0)
                            inputLayoutName.setErrorEnabled(false);
                        else if (s.toString().trim().length() == 0)
                            inputLayoutName.setError(fieldRequired);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            } else {
                loginLayout.setVisibility(View.VISIBLE);
            }
        } else {
            onOffLine();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedCategory = (Category) parent.getItemAtPosition(position);
        if (selectedCategory.isLocation()) {
            locationLayout.setVisibility(View.VISIBLE);
            if (!isMapInitialized) {
                isMapInitialized = true;
            }
            initMaps();
        } else {
            locationLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void initMaps() {
        WorkaroundMapFragment mapFragment = (WorkaroundMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapFragment.setListener(() -> scrollView.requestDisallowInterceptTouchEvent(true));
        checkGPS();
    }

    private void checkGPS() {
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
        } else {
            showGPSDisabledAlertToUser();
            return;
        }

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        googleApiClient.connect();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        UiSettings uiSettings = mMap.getUiSettings();

        uiSettings.setZoomGesturesEnabled(true);
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setScrollGesturesEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                addressET.setText(getAddress(marker.getPosition()));
                rsAddItem.setCity(getCity(marker.getPosition()));
                rsAddItem.setCountry(getCountry(marker.getPosition()));
                rsAddItem.setGovernorate(getGovernorate(marker.getPosition()));
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(RankStop.getInstance(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(RankStop.getInstance(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else {
            FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(RankStop.getInstance());
            mFusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), location -> {
                // Got last known location. In some rare situations, this can be null.
                if (location != null) {
                    addMarker(location.getLatitude(), location.getLongitude());
                } else {
                    addMarker(RSConstants.FAKE_LATITUDE, RSConstants.FAKE_LONGITUDE);
                }
            });
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
            // No Permissions Granted
            addMarker(RSConstants.FAKE_LATITUDE, RSConstants.FAKE_LONGITUDE);
        }
    }

    private String getAddress(LatLng latLng) {
        geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            String adr = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0).getAddressLine(0);
            return adr != null ? adr : "";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
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

    private String getGovernorate(LatLng latLng) {
        geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            String gov = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0).getAdminArea();
            return gov != null ? gov : "";
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

    private void addMarker(double latitude, double longitude) {
        MarkerOptions currentUserLocation = new MarkerOptions();
        LatLng currentUserLatLang = new LatLng(latitude, longitude);
        currentUserLocation.position(currentUserLatLang);
        currentUserLocation.draggable(true);
        mMap.addMarker(currentUserLocation);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentUserLatLang, 12));
        addressET.setText(getAddress(currentUserLatLang));
        rsAddItem.setCity(getCity(currentUserLatLang));
        rsAddItem.setCountry(getCountry(currentUserLatLang));
        rsAddItem.setGovernorate(getGovernorate(currentUserLatLang));
        currentLatitude = latitude;
        currentLongitude = longitude;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.rs_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.setting:
                fragmentActionListener.startFragment(SettingsFragment.getInstance(), RSConstants.FRAGMENT_SETTINGS);
                break;
            case R.id.history:
                fragmentActionListener.startFragment(HistoryFragment.getInstance(), RSConstants.FRAGMENT_HISTORY);
                break;
            case R.id.contact:
                openContactDialog();
                break;
            case R.id.notifications:
                fragmentActionListener.startFragment(ListNotifFragment.getInstance(), RSConstants.FRAGMENT_NOTIF);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private FragmentActionListener fragmentActionListener;

    private void setFragmentActionListener(FragmentActionListener fragmentActionListener) {
        this.fragmentActionListener = fragmentActionListener;
    }

    public static AddItemFragment getInstance() {
        if (instance == null) {
            instance = new AddItemFragment();
        }
        return instance;
    }

    private void openContactDialog() {
        ContactDialog dialog = new ContactDialog();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        dialog.show(ft, ContactDialog.TAG);
    }

    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setMessage(gpsDisabledMsg)
                .setCancelable(false)
                .setPositiveButton(positiveBtnText,
                        (dialog, id) -> {
                            Intent callGPSSettingIntent = new Intent(
                                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(callGPSSettingIntent, RSConstants.REQUEST_CODE);
                        });
        alertDialogBuilder.setNegativeButton(negativeBtnText,
                (dialog, id) -> {
                    dialog.cancel();
                    addMarker(RSConstants.FAKE_LATITUDE, RSConstants.FAKE_LONGITUDE);
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == RSConstants.REQUEST_CODE) {
            initMaps();
        }
    }

    @Override
    public void onStop() {
        if (googleApiClient != null)
            googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        instance = null;
        rootView = null;
        fragmentActionListener = null;
        if (unbinder != null)
            unbinder.unbind();
        if (itemPresenter != null)
            itemPresenter.onDestroyItem();
        super.onDestroyView();
    }


    @Override
    public void onSuccess(String target, Object data) {
        switch (target) {
            case RSConstants.LOAD_CATEGORIES:
                Category[] categories = new Gson().fromJson(new Gson().toJson(data), Category[].class);
                List<Category> categoryList = Arrays.asList(categories);

                SpinnerCategoryAdapter spinnerCategoryAdapter = new SpinnerCategoryAdapter(getContext(), categoryList);
                categorySpinner.setAdapter(spinnerCategoryAdapter);
                break;
        }
    }

    @Override
    public void onFailure(String target) {

    }

    @Override
    public void onError(String target) {

    }

    @Override
    public void showProgressBar(String target) {
        switch (target) {
            case RSConstants.LOAD_CATEGORIES:
                progressBar.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void hideProgressBar(String target) {
        switch (target) {
            case RSConstants.LOAD_CATEGORIES:
                progressBar.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void showMessage(String target, String message) {

    }

    @Override
    public void onOffLine() {
        Toast.makeText(getContext(), offlineMsg, Toast.LENGTH_LONG).show();
    }

    private void navigateToSignUp(RSNavigationData rsNavigationData) {
        fragmentActionListener.startFragment(SignupFragment.getInstance(rsNavigationData), RSConstants.FRAGMENT_SIGN_UP);
    }
}
