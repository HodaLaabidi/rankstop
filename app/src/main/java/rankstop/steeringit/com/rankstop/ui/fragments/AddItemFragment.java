package rankstop.steeringit.com.rankstop.ui.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;

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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import rankstop.steeringit.com.rankstop.MVP.model.PresenterItemImpl;
import rankstop.steeringit.com.rankstop.MVP.presenter.RSPresenter;
import rankstop.steeringit.com.rankstop.MVP.view.RSView;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.data.model.db.Category;
import rankstop.steeringit.com.rankstop.data.model.network.RSAddReview;
import rankstop.steeringit.com.rankstop.ui.activities.ContainerActivity;
import rankstop.steeringit.com.rankstop.ui.adapter.SpinnerCategoryAdapter;
import rankstop.steeringit.com.rankstop.ui.callbacks.FragmentActionListener;
import rankstop.steeringit.com.rankstop.utils.RSConstants;
import rankstop.steeringit.com.rankstop.utils.WorkaroundMapFragment;

import static android.content.Context.LOCATION_SERVICE;

public class AddItemFragment extends Fragment implements RSView.StandardView, AdapterView.OnItemSelectedListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    private static AddItemFragment instance;

    private TextInputEditText nameET, descriptionET, addressET, phoneET;
    private LinearLayout locationLayout;
    private AppCompatSpinner categorySpinner;
    private WorkaroundMapFragment mapFragment;
    private NestedScrollView scrollView;
    private MaterialButton addItemBtn;
    private Toolbar toolbar;
    private View rootView;

    private GoogleMap mMap;
    private Geocoder geocoder;
    private GoogleApiClient googleApiClient;
    private MarkerOptions currentUserLocation;
    private boolean isMapInitialized = false;
    private Category selectedCategory;

    private double currentLatitude, currentLongitude;
    private RSAddReview rsAddItem = new RSAddReview();

    private RSPresenter.ItemPresenter itemPresenter;
    private LocationManager locationManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_add_item, container, false);
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
        loadCategoriesList();

        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        addItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // pour valider l'ajout du cet item, tu dois l'évaluer
                rsAddItem.setCategoryId(selectedCategory.get_id());
                rsAddItem.setTitle(nameET.getText().toString());
                rsAddItem.setDescription(descriptionET.getText().toString());
                rsAddItem.setAddress(addressET.getText().toString());
                rsAddItem.setPhone(phoneET.getText().toString());
                rsAddItem.setLatitude(""+currentLatitude);
                rsAddItem.setLongitude(""+currentLongitude);

                fragmentActionListener.startFragment(AddReviewFragment.getInstance(rsAddItem, null, ""), RSConstants.FRAGMENT_ADD_REVIEW);
            }
        });
    }

    private void loadCategoriesList() {
        itemPresenter.loadCategoriesList();
    }

    private void bindViews() {
        itemPresenter = new PresenterItemImpl(AddItemFragment.this);

        toolbar = rootView.findViewById(R.id.toolbar);
        nameET = rootView.findViewById(R.id.input_title);
        descriptionET = rootView.findViewById(R.id.input_description);
        addressET = rootView.findViewById(R.id.input_address);
        phoneET = rootView.findViewById(R.id.input_phone);
        categorySpinner = rootView.findViewById(R.id.categories_spinner);
        scrollView = rootView.findViewById(R.id.scroll_view);
        locationLayout = rootView.findViewById(R.id.layout_location);
        addItemBtn = rootView.findViewById(R.id.btn_add_item);

        toolbar.setTitle(getResources().getString(R.string.add_item));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        setFragmentActionListener((ContainerActivity) getActivity());

        categorySpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedCategory = (Category) parent.getItemAtPosition(position);
        if (selectedCategory.isLocation()) {
            locationLayout.setVisibility(View.VISIBLE);
            if (isMapInitialized == false) {
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
        mapFragment = (WorkaroundMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapFragment.setListener(new WorkaroundMapFragment.OnTouchListener() {
            @Override
            public void onTouch() {
                scrollView.requestDisallowInterceptTouchEvent(true);
            }
        });
        checkGPS();
    }

    private void checkGPS() {
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            //Toast.makeText(getContext(), "GPS is Enabled in your device", Toast.LENGTH_LONG).show();
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
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else {
            FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations, this can be null.
                            if (location != null) {
                                addMarker(location.getLatitude(), location.getLongitude());
                            }else{
                                addMarker(RSConstants.FAKE_LATITUDE, RSConstants.FAKE_LONGITUDE);
                                //Toast.makeText(getContext(), "userCurrentLocation"+ location, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
            /*Location userCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (userCurrentLocation != null) {
                addMarker(userCurrentLocation.getLatitude(), userCurrentLocation.getLongitude());
            }else{
                Toast.makeText(getContext(), "userCurrentLocation"+ userCurrentLocation, Toast.LENGTH_LONG).show();
            }*/
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
        currentUserLocation = new MarkerOptions();
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
        inflater.inflate(R.menu.profile_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.setting:
                fragmentActionListener.startFragment(SettingsFragment.getInstance(), RSConstants.FRAGMENT_SETTINGS);
                break;
            case R.id.logout:
                /*RSSession.removeToken(getContext());
                ((ContainerActivity)getActivity()).manageSession(false);*/
                break;
            case R.id.history:
                fragmentActionListener.startFragment(HistoryFragment.getInstance(""), RSConstants.FRAGMENT_HISTORY);
                break;
            case R.id.contact:
                fragmentActionListener.startFragment(ContactFragment.getInstance(), RSConstants.FRAGMENT_CONTACT);
                break;
            case R.id.notifications:
                fragmentActionListener.startFragment(ListNotifFragment.getInstance(), RSConstants.FRAGMENT_NOTIF);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private FragmentActionListener fragmentActionListener;

    public void setFragmentActionListener(FragmentActionListener fragmentActionListener) {
        this.fragmentActionListener = fragmentActionListener;
    }

    public static AddItemFragment getInstance() {
        if (instance == null) {
            instance = new AddItemFragment();
        }
        return instance;
    }

    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Goto Settings Page To Enable GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivityForResult(callGPSSettingIntent, RSConstants.REQUEST_CODE);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        addMarker(RSConstants.FAKE_LATITUDE, RSConstants.FAKE_LONGITUDE);
                    }
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
    public void onStart() {
        super.onStart();
        //Log.i("LIFE_CYCLE", "" + TAG + " onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        //Log.i("LIFE_CYCLE", "" + TAG + " onResume");
    }

    @Override
    public void onPause() {
        //Log.i("LIFE_CYCLE", "" + TAG + " onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        //Log.i("LIFE_CYCLE", "" + TAG + " onStop");
        if (googleApiClient != null)
            googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        instance = null;
        rootView = null;
        fragmentActionListener = null;
        itemPresenter.onDestroyItem();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        //Log.i("LIFE_CYCLE", "" + TAG + " onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        //Log.i("LIFE_CYCLE", "" + TAG + " onDetach");
        super.onDetach();
    }


    @Override
    public void onSuccess(String target, Object data) {
        switch (target) {
            case RSConstants.LOAD_CATEGORIES:
                Category[] categories = new Gson().fromJson(new Gson().toJson(data), Category[].class);
                List<Category> categoryList = Arrays.asList(categories);

                SpinnerCategoryAdapter spinnerCategoryAdapter = new SpinnerCategoryAdapter(getContext(), categoryList);
                categorySpinner.setAdapter(spinnerCategoryAdapter);
                //Toast.makeText(getContext(), "size = "+((List<Criteria>)categoryList.get(0).getCriterias()).get(0).getName(), Toast.LENGTH_SHORT).show();
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

    }

    @Override
    public void hideProgressBar(String target) {

    }

    @Override
    public void showMessage(String target, String message) {

    }
}
