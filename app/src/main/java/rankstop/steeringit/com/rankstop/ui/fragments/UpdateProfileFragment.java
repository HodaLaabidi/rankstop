package rankstop.steeringit.com.rankstop.ui.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
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
import android.widget.DatePicker;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rankstop.steeringit.com.rankstop.MVP.model.PresenterUpdateProfileImpl;
import rankstop.steeringit.com.rankstop.MVP.presenter.RSPresenter;
import rankstop.steeringit.com.rankstop.MVP.view.RSView;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.customviews.RSBTNMedium;
import rankstop.steeringit.com.rankstop.customviews.RSETMedium;
import rankstop.steeringit.com.rankstop.data.model.db.Country;
import rankstop.steeringit.com.rankstop.data.model.db.RSPublicUserName;
import rankstop.steeringit.com.rankstop.data.model.db.User;
import rankstop.steeringit.com.rankstop.ui.activities.ContainerActivity;
import rankstop.steeringit.com.rankstop.ui.adapter.SpinnerCountryAdapter;
import rankstop.steeringit.com.rankstop.ui.adapter.SpinnerGenderAdapter;
import rankstop.steeringit.com.rankstop.ui.adapter.SpinnerPublicNameAdapter;
import rankstop.steeringit.com.rankstop.ui.callbacks.DateListener;
import rankstop.steeringit.com.rankstop.ui.callbacks.FragmentActionListener;
import rankstop.steeringit.com.rankstop.utils.RSConstants;
import rankstop.steeringit.com.rankstop.utils.RSDateParser;

public class UpdateProfileFragment extends Fragment implements RSView.StandardView, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, AdapterView.OnItemSelectedListener {

    private View rootView;
    private Unbinder unbinder;
    private static UpdateProfileFragment instance;
    private RSPresenter.UpdateProfilePresenter presenterUpdateProfile;
    private User currentUser;
    private Country selectedCountry;
    private String selectedGender, selectedPublicName;
    private List<Country> countries;
    private String[] genderArray, publicNameArray;
    public static Context context;
    private String currentDate;

    private Geocoder geocoder;
    private GoogleApiClient googleApiClient;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.avatar)
    SimpleDraweeView avatar;

    @BindView(R.id.input_first_name)
    RSETMedium inputFirstNameET;

    @BindView(R.id.input_last_name)
    RSETMedium inputLastNameET;

    @BindView(R.id.input_username)
    RSETMedium inputUserNameET;

    @BindView(R.id.input_city)
    RSETMedium inputCityET;

    @BindView(R.id.input_phone)
    RSETMedium inputPhoneET;

    @BindView(R.id.countries_spinner)
    AppCompatSpinner countriesSpinner;

    @BindView(R.id.gender_spinner)
    AppCompatSpinner genderSpinner;

    @BindView(R.id.public_name_spinner)
    AppCompatSpinner publicNameSpinner;

    @BindView(R.id.et_birth_date)
    RSBTNMedium birthDateET;

    @BindString(R.string.hint_man)
    String male;
    @BindString(R.string.hint_woman)
    String female;

    @BindString(R.string.hint_username)
    String usernameText;
    @BindString(R.string.public_name_value)
    String publicNameText;

    // save changes
    @OnClick(R.id.btn_save_changes)
    public void saveChanges() {
    }

    // show date picker
    @OnClick(R.id.et_birth_date)
    public void showDatePicker(){
        DialogFragment newFragment = DatePickerFragment.newInstance(currentDate);
        newFragment.show(getFragmentManager(), "datePicker");
    }

    // current location
    @OnClick(R.id.btn_use_c_location)
    public void cLocation() {
        getCurrentLocation();
    }

    private void getCurrentLocation() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        } else {
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
        presenterUpdateProfile.loadCountriesList();
        bindData(currentUser);
    }

    private void bindData(User user) {
        inputFirstNameET.setText(user.getFirstName());
        inputLastNameET.setText(user.getLastName());
        inputUserNameET.setText(user.getUsername());
        inputPhoneET.setText(user.getPhone());
        birthDateET.setText(user.getBirthDate());
        currentDate = user.getBirthDate();
        initPublicNameView();
        initGenderView();
        setUserPic(user.getPictureProfile());
    }

    private void initGenderView() {
        genderArray = new String[]{male,female};
        SpinnerGenderAdapter spinnerGenderAdapter = new SpinnerGenderAdapter(getContext(), genderArray);
        genderSpinner.setAdapter(spinnerGenderAdapter);
        setGender(currentUser.getGender());
    }

    private void initPublicNameView() {
        publicNameArray = new String[]{usernameText,publicNameText};
        SpinnerPublicNameAdapter spinnerPublicNameAdapter = new SpinnerPublicNameAdapter(getContext(), publicNameArray);
        publicNameSpinner.setAdapter(spinnerPublicNameAdapter);
        setPublicName(currentUser.getNameToUse());
    }

    private void setPublicName(RSPublicUserName publicName) {
        if (publicName.getType().toLowerCase().trim().equals(usernameText)){
            publicNameSpinner.setSelection(0);
        }else if (publicName.getType().toLowerCase().trim().equals(publicNameText)){
            publicNameSpinner.setSelection(0);
        }

        selectedPublicName = publicName.getType();
    }

    private void setGender(String gender) {
        if (gender != null) {
            if (gender.toLowerCase().trim().equals(male)) {
                genderSpinner.setSelection(0);
            } else if (gender.toLowerCase().trim().equals(female)) {
                genderSpinner.setSelection(1);
            }
        }
        selectedGender = gender;
    }

    private void setUserAddress(int index, String city) {
        if (index != -1) {
            countriesSpinner.setSelection(index);
            selectedCountry = countries.get(index);
        }
        inputCityET.setText(city);
    }

    private void bindViews() {
        toolbar.setTitle("Modifier profil");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setFragmentActionListener((ContainerActivity) getActivity());

        countriesSpinner.setOnItemSelectedListener(this);
        genderSpinner.setOnItemSelectedListener(this);
        publicNameSpinner.setOnItemSelectedListener(this);

        context = getContext();
    }

    private void setUserPic(String picture) {
        Uri imageUri = Uri.parse(picture);
        avatar.setImageURI(imageUri);
    }

    @Override
    public void onDestroyView() {
        rootView = null;
        unbinder.unbind();
        instance = null;
        presenterUpdateProfile.onDestroy();
        super.onDestroyView();
    }

    @Override
    public void onSuccess(String target, Object data) {
        switch (target) {
            case RSConstants.COUNTRIES_LIST:
                Country[] array = new Gson().fromJson(new Gson().toJson(data), Country[].class);
                countries = Arrays.asList(array);
                initCountriesList();
                break;
        }
    }

    @Override
    public void onFailure(String target) {
        switch (target) {
            case "":
                break;
        }
    }

    @Override
    public void onError(String target) {

    }

    @Override
    public void showProgressBar(String target) {
        switch (target) {
            case "":
                break;
        }
    }

    @Override
    public void hideProgressBar(String target) {
        switch (target) {
            case "":
                break;
        }
    }

    @Override
    public void showMessage(String target, String message) {
        switch (target) {
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

                setUserAddress(findCountry(getCountryCode(currentUserLatLang)), getCity(currentUserLatLang));

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

    private String getCountryCode(LatLng latLng) {
        geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            String country = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0).getCountryCode();
            return country != null ? country : "";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private int findCountry(String countryCode) {
        if (countryCode != null)
            for (int i = 0; i < countries.size(); i++) {
                if (countries.get(i).getCountryCode().toLowerCase().trim().equals(countryCode.toLowerCase().trim()))
                    return i;
            }
        return -1;
    }

    private void initCountriesList() {
        SpinnerCountryAdapter spinnerCountryAdapter = new SpinnerCountryAdapter(getContext(), countries);
        countriesSpinner.setAdapter(spinnerCountryAdapter);
        setUserAddress(findCountry(currentUser.getLocation().getCountry().getCountryCode()), currentUser.getLocation().getCity());
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (view.getId()){
            case R.id.countries_spinner:
                selectedCountry = (Country) parent.getItemAtPosition(position);
                break;
            case R.id.gender_spinner:
                selectedGender = (String) parent.getItemAtPosition(position);
                break;
            case R.id.public_name_spinner:
                selectedPublicName = (String) parent.getItemAtPosition(position);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    // This method will be called when a MessageEvent is posted (in the UI thread for Toast)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DateListener event) {
        String date = RSDateParser.convertToDateFormat(event.date, "dd/MM/yyyy", "dd/MM/yyyy");
        currentDate = date;
        birthDateET.setText(date);
    }

    /*// This method will be called when a SomeOtherEvent is posted
    @Subscribe
    public void handleSomethingElse(SomeOtherEvent event) {
        doSomethingWith(event);
    }*/

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        DatePickerDialog dialog;
        public static DatePickerFragment fragment;

        private String currentDate;

        public static DatePickerFragment newInstance(String currentDate) {

            Bundle args = new Bundle();

            args.putSerializable(RSConstants.CURRENT_DATE, currentDate);
            if (fragment == null) {
                fragment = new DatePickerFragment();
            }
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker

            currentDate = getArguments().getString(RSConstants.CURRENT_DATE);
            Calendar calendar;
            if (currentDate != null){
                calendar = RSDateParser.convertToDate(currentDate, "dd/MM/yyyy");
            }else {
                calendar = Calendar.getInstance();
            }
            final Calendar curCalendar = Calendar.getInstance();

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            int curYear = curCalendar.get(Calendar.YEAR);
            int curMonth = curCalendar.get(Calendar.MONTH);
            int curDay = curCalendar.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            dialog = new DatePickerDialog(getActivity(), this, year, month, day);
            curCalendar.set(curYear-15, curMonth, curDay);
            dialog.getDatePicker().setMaxDate(curCalendar.getTimeInMillis());
            return dialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            EventBus.getDefault().post(new DateListener(""+day+"/"+(month+1)+"/"+year));
        }
    }
}
