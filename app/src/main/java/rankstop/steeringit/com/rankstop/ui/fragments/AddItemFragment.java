package rankstop.steeringit.com.rankstop.ui.fragments;



import android.content.Context;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
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
import rankstop.steeringit.com.rankstop.customviews.RSCustomToast;
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

public class AddItemFragment extends Fragment implements RSView.StandardView, RSView.StandardView2 ,AdapterView.OnItemSelectedListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    private static final long REFRESH_ADAPTER_TIMER = 200;
    private static final float DEFAULT_ZOOM = 12f;
    private static AddItemFragment instance;
    private String itemName, itemDescription;

    private Unbinder unbinder;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.input_barcode)
    RSETMedium inputBarcode ;

    @BindView(R.id.action_delete_barcode)
    RSTVMedium actionDeleteBarcode ;

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

    @BindView(R.id.action_scanner)
    RSTVMedium actionScanner ;


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
            if (RSNetwork.isConnected(getContext())) {
                try {
                    // pour 'valider l'ajout du cet item, tu dois l'évaluer
                    setRsAddItem();

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

    private void setRsAddItem() {
        rsAddItem = new RSAddReview();
        rsAddItem.setCategoryId(selectedCategory.get_id());
        rsAddItem.setTitle(nameET.getText().toString().trim());
        rsAddItem.setBarcode(inputBarcode.getText().toString().trim());
        rsAddItem.setDescription(descriptionET.getText().toString().trim());
        rsAddItem.setAddress(addressET.getText().toString().trim());
        rsAddItem.setPhone(phoneET.getText().toString().trim());
        rsAddItem.setLatitude("" + currentLatitude);
        rsAddItem.setLongitude("" + currentLongitude);
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);
        rsAddItem.setCity(getCity(latLng));
        rsAddItem.setGovernorate(getGovernorate(latLng));
        rsAddItem.setCountry(getCountry(latLng));
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
    private static String barcode ;

    private GoogleMap mMap;
    private Geocoder geocoder;
    private GoogleApiClient googleApiClient;
    private boolean isMapInitialized = false;
    private Category selectedCategory;

    private double currentLatitude, currentLongitude;
    private static RSAddReview rsAddItem ;
    private SpinnerCategoryAdapter spinnerCategoryAdapter ;

    private RSPresenter.ItemPresenter itemPresenter;
    private LocationManager locationManager;
    private RSNavigationData rsNavigationData = new RSNavigationData();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_add_item, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        return rootView;
    }


    public static AddItemFragment getInstance(RSAddReview rsAddView) {


        Bundle args = new Bundle();
        args.putSerializable(RSConstants.RS_ADD_REVIEW, rsAddView);
        if (instance == null)
            instance = new AddItemFragment();
        instance.setArguments(args);
        if (instance.getArguments() != null) {
            rsAddItem = (RSAddReview) instance.getArguments().get(RSConstants.RS_ADD_REVIEW);
        }
        return instance;
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

    private void loadCategoriesList(Context context) {
        itemPresenter.loadCategoriesList(RankStop.getDeviceLanguage(),context);
    }

    private void bindViews() {


        toolbar.setTitle(addItemTitle);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        setFragmentActionListener((ContainerActivity) getActivity());

        if (RSNetwork.isConnected(getContext())) {
            if (RSSession.isLoggedIn()) {
                scrollView.setVisibility(View.VISIBLE);
                itemPresenter = new PresenterItemImpl(AddItemFragment.this, AddItemFragment.this);
                categorySpinner.setOnItemSelectedListener(this);
                loadCategoriesList(getContext());
                locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

                if (rsAddItem != null) {

                    ;
                    refreshAddItemData(rsAddItem);
                }
                addressET.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
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

                if (barcode != null && barcode != "") {
                    inputBarcode.setText(barcode);
                    actionDeleteBarcode.setVisibility(View.VISIBLE);
                    actionDeleteBarcode.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            barcode = "";
                            inputBarcode.setText(barcode);
                            rsAddItem.setBarcode(barcode);
                            actionDeleteBarcode.setVisibility(View.GONE);
                        }
                    });
                }

                actionScanner.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setRsAddItem();
                        fragmentActionListener.startFragment(ScannerFragment.getInstance(rsAddItem), RSConstants.FRAGMENT_SCANNER);
                        ((ContainerActivity) getActivity()).manageSession(true, new RSNavigationData(RSConstants.FRAGMENT_SCANNER, RSConstants.ACTION_ADD_ITEM));

                    }
                });

                inputBarcode.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setRsAddItem();
                        fragmentActionListener.startFragment(ScannerFragment.getInstance(rsAddItem), RSConstants.FRAGMENT_SCANNER);
                        ((ContainerActivity) getActivity()).manageSession(true, new RSNavigationData(RSConstants.FRAGMENT_SCANNER, RSConstants.ACTION_ADD_ITEM));
                    }
                });

            } else {
                loginLayout.setVisibility(View.VISIBLE);
            }
        } else {
            onOffLine();
        }
    }



    private void refreshAddItemData(RSAddReview rsAddItem) {


        if (rsAddItem.getTitle() != null) {
            nameET.setText(rsAddItem.getTitle());
        }
        if (rsAddItem.getCategoryId() != null) {


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (spinnerCategoryAdapter != null) {
                        selectedCategory = spinnerCategoryAdapter.refreshSpinner(rsAddItem.getCategoryId());
                        if (selectedCategory.get_id() != null) {
                            if (selectedCategory.isLocation()) {
                                locationLayout.setVisibility(View.VISIBLE);
                                if (mMap != null) {
                                    mMap.clear();
                                }
                                if (!isMapInitialized) {
                                    isMapInitialized = true;
                                }
                                initMaps();
                            } else {
                                locationLayout.setVisibility(View.GONE);
                            }
                        }
                    }
                }
            }, REFRESH_ADAPTER_TIMER);


        }

        if (rsAddItem.getDescription() != null) {
            descriptionET.setText(rsAddItem.getDescription());
        }
        if (rsAddItem.getAddress() != null && rsAddItem.getLongitude() != null && rsAddItem.getLatitude() != null) {


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    LatLng latLng = new LatLng(Double.parseDouble(rsAddItem.getLatitude()), Double.parseDouble(rsAddItem.getLongitude()));
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(latLng));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
                    onMapReady(mMap);
                    addressET.setText(rsAddItem.getAddress());
                    rsAddItem.setCity(getCity(latLng));
                    rsAddItem.setCountry(getCountry(latLng));
                    rsAddItem.setGovernorate(getGovernorate(latLng));

                }
            }, 500);

        }

        if (rsAddItem.getPhone() != null) {
            phoneET.setText(rsAddItem.getPhone());
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
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                List<Address> addresses;
                String address = "";
                try {
                    addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

                    addressET.setText(address);
                    Marker marker = null;
                    mMap.clear();
                    marker = mMap.addMarker(new MarkerOptions()
                            .position(latLng));
                    if (rsAddItem == null) {
                        rsAddItem = new RSAddReview();
                    }
                    rsAddItem.setCity(getCity(marker.getPosition()));
                    rsAddItem.setCountry(getCountry(marker.getPosition()));
                    rsAddItem.setGovernorate(getGovernorate(marker.getPosition()));
                    currentLatitude = latLng.latitude;
                    currentLongitude = latLng.longitude;

                } catch (IOException e) {
                    e.printStackTrace();
                }


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
        if (rsAddItem == null) {
            rsAddItem = new RSAddReview();
        }
        rsAddItem.setCity(getCity(currentUserLatLang));
        rsAddItem.setCountry(getCountry(currentUserLatLang));
        rsAddItem.setGovernorate(getGovernorate(currentUserLatLang));


        currentLatitude = latitude;
        currentLongitude = longitude;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.rs_menu, menu);

            MenuItem item = menu.findItem(R.id.logout);
            item.setVisible(false);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.setting:
                fragmentActionListener.startFragment(SettingsFragment.getInstance(), RSConstants.FRAGMENT_SETTINGS);
                break;
            case R.id.logout:
                RSSession.cancelSession();
                ((ContainerActivity) getActivity()).manageSession(false, new RSNavigationData(RSConstants.FRAGMENT_SIGN_UP, ""));
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
            case R.id.profil:
                if(RSSession.isLoggedIn())
                    fragmentActionListener.startFragment(ProfileFragment.getInstance(), RSConstants.FRAGMENT_PROFILE);
                else
                    fragmentActionListener.startFragment(SignupFragment.getInstance(rsNavigationData), RSConstants.FRAGMENT_SIGN_UP);
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


    public static AddItemFragment getInstance(RSNavigationData data) {
        Bundle args = new Bundle();
        args.putSerializable(RSConstants.NAVIGATION_DATA, data);
        if (instance == null)
            instance = new AddItemFragment();
        instance.setArguments(args);
        barcode = data.getMessage();
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
        super.onActivityResult(requestCode, resultCode, data);
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
        if (rsAddItem != null){
            rsAddItem = null;
        }

        if (RSSession.isLoggedIn()) {
            barcode = null;

            actionDeleteBarcode.setVisibility(View.GONE);
        }




        fragmentActionListener = null;
        if (unbinder != null)
            unbinder.unbind();
        if (itemPresenter != null)
            itemPresenter.onDestroyItem(getContext());
        super.onDestroyView();
    }


    @Override
    public void onSuccess(String target, Object data) {
        switch (target) {
            case RSConstants.LOAD_CATEGORIES:
                Category[] categories = new Gson().fromJson(new Gson().toJson(data), Category[].class);
                List<Category> categoryList = Arrays.asList(categories);

                 spinnerCategoryAdapter = new SpinnerCategoryAdapter(getContext(), categoryList);
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
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onOffLine() {
        //Toast.makeText(getContext(), offlineMsg, Toast.LENGTH_LONG).show();
        new RSCustomToast(getActivity(), getResources().getString(R.string.error), offlineMsg, R.drawable.ic_error, RSCustomToast.ERROR).show();

    }

    private void navigateToSignUp(RSNavigationData rsNavigationData) {
        fragmentActionListener.startFragment(SignupFragment.getInstance(rsNavigationData), RSConstants.FRAGMENT_SIGN_UP);
    }


    @Override
    public void onSuccessRefreshItem(String target, String itemId, String message, Object data) {

    }
}
