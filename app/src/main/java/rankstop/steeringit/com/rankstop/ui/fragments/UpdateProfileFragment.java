package rankstop.steeringit.com.rankstop.ui.fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindDrawable;
import butterknife.BindInt;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rankstop.steeringit.com.rankstop.MVP.model.PresenterUpdateProfileImpl;
import rankstop.steeringit.com.rankstop.MVP.presenter.RSPresenter;
import rankstop.steeringit.com.rankstop.MVP.view.RSView;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.RankStop;
import rankstop.steeringit.com.rankstop.customviews.RSBTNBold;
import rankstop.steeringit.com.rankstop.customviews.RSBTNMedium;
import rankstop.steeringit.com.rankstop.customviews.RSCustomToast;
import rankstop.steeringit.com.rankstop.customviews.RSETMedium;
import rankstop.steeringit.com.rankstop.customviews.RSTVMedium;
import rankstop.steeringit.com.rankstop.data.model.db.Country;
import rankstop.steeringit.com.rankstop.data.model.db.RSPublicUserName;
import rankstop.steeringit.com.rankstop.data.model.db.RSRequestEditProfile;
import rankstop.steeringit.com.rankstop.data.model.db.User;
import rankstop.steeringit.com.rankstop.data.model.network.RSNavigationData;
import rankstop.steeringit.com.rankstop.ui.activities.ContainerActivity;
import rankstop.steeringit.com.rankstop.ui.adapter.SpinnerCountryAdapter;
import rankstop.steeringit.com.rankstop.ui.adapter.SpinnerGenderAdapter;
import rankstop.steeringit.com.rankstop.ui.adapter.SpinnerPublicNameAdapter;
import rankstop.steeringit.com.rankstop.ui.callbacks.BottomSheetDialogListener;
import rankstop.steeringit.com.rankstop.ui.callbacks.DateListener;
import rankstop.steeringit.com.rankstop.ui.callbacks.FragmentActionListener;
import rankstop.steeringit.com.rankstop.ui.dialogFragment.RSBottomSheetDialog;
import rankstop.steeringit.com.rankstop.ui.dialogFragment.RSLoader;
import rankstop.steeringit.com.rankstop.utils.FileCompressor;
import rankstop.steeringit.com.rankstop.utils.RSConstants;
import rankstop.steeringit.com.rankstop.utils.RSDateParser;
import rankstop.steeringit.com.rankstop.utils.RSNetwork;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.LOCATION_SERVICE;

public class UpdateProfileFragment extends Fragment implements RSView.UpdateProfileView, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, BottomSheetDialogListener, DateListener{

    private View rootView;
    private Unbinder unbinder;
    private static UpdateProfileFragment instance;
    private RSPresenter.UpdateProfilePresenter presenterUpdateProfile;
    private User currentUser;
    private Country selectedCountry;
    private String selectedGender, selectedPublicName;
    private List<Country> countries;
    public static Context context;
    private String currentDate;

    private boolean isPwdHidden = true;

    private FileCompressor mCompressor;
    private File mPhotoFile;
    private Uri imageUri;

    private Geocoder geocoder;
    private GoogleApiClient googleApiClient;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.avatar)
    SimpleDraweeView avatar;

    @BindView(R.id.input_layout_username)
    TextInputLayout userNameInputLayout;
    @BindView(R.id.input_username)
    RSETMedium inputUserNameET;

    @BindView(R.id.input_layout_first_name)
    TextInputLayout firstNameInputLayout;

    @BindView(R.id.input_layout_phone)
    TextInputLayout inputLayoutPhone;
    @BindView(R.id.input_first_name)
    RSETMedium inputFirstNameET;

    @BindView(R.id.input_layout_last_name)
    TextInputLayout lastNameInputLayout;
    @BindView(R.id.input_last_name)
    RSETMedium inputLastNameET;

    @BindView(R.id.input_layout_city)
    TextInputLayout cityInputLayout;
    @BindView(R.id.input_city)
    RSETMedium inputCityET;

    @BindView(R.id.input_phone)
    RSETMedium inputPhoneET;

    @BindView(R.id.tv_error_birthday)
    RSTVMedium birthDateLayout;
    @BindView(R.id.et_birth_date)
    RSBTNMedium birthDateET;

    @BindView(R.id.countries_spinner)
    AppCompatSpinner countriesSpinner;

    @BindView(R.id.gender_spinner)
    AppCompatSpinner genderSpinner;

    @BindView(R.id.public_name_spinner)
    AppCompatSpinner publicNameSpinner;

    @BindView(R.id.input_layout_old_pwd)
    TextInputLayout inputLayoutOldPwd;
    @BindView(R.id.input_old_pwd)
    RSETMedium inputOldPasswordET;

    @BindView(R.id.input_layout_new_pwd)
    TextInputLayout inputLayoutNewPwd;
    @BindView(R.id.input_new_pwd)
    RSETMedium inputNewPasswordET;

    @BindView(R.id.input_layout_confirm_pwd)
    TextInputLayout inputLayoutConfirmPwd;
    @BindView(R.id.input_confirm_pwd)
    RSETMedium inputConfirmPasswordET;

    @BindString(R.string.hint_man)
    String male;
    @BindString(R.string.hint_woman)
    String female;
    @BindString(R.string.date_format)
    String dateFormat;
    @BindString(R.string.server_date_format)
    String serverDateFormat;
    @BindString(R.string.old_pwd_error)
    String oldPwdErrorMsg;
    @BindString(R.string.title_edit_profile)
    String editProfileTitle;

    @BindString(R.string.username_text)
    String usernameText;
    @BindString(R.string.fullname_text)
    String fullNameText;
    @BindString(R.string.field_required)
    String requiredField;
    @BindString(R.string.login_dialog_empty_password)
    String minLength6Msg;
    @BindInt(R.integer.min_length_pwd)
    int minLength6;
    @BindString(R.string.register_dialog_matching_password)
    String pwdMatching;

    @BindString(R.string.edit_pwd)
    String editPwdText;
    @BindString(R.string.cancel_edit_pwd)
    String cancelEditPwdText;

    @BindView(R.id.btn_change_pwd)
    RSBTNBold changePwdBTN;

    @BindString(R.string.loading_msg)
    String loadingMsg;
    private RSLoader rsLoader;

    @BindView(R.id.btn_save_changes)
    RSBTNBold btnSaveChanges ;

    private void createLoader() {
        rsLoader = RSLoader.newInstance(loadingMsg);
        rsLoader.setCancelable(false);
    }

    private TextWatcher userNameTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().trim().length() > 0) {
                userNameInputLayout.setErrorEnabled(false);
            } else {
                userNameInputLayout.setError(requiredField);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    },
            firstNameTextWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.toString().trim().length() > 0) {
                        firstNameInputLayout.setErrorEnabled(false);
                    } else {
                        firstNameInputLayout.setError(requiredField);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            },
            lastNameTextWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.toString().trim().length() > 0) {
                        lastNameInputLayout.setErrorEnabled(false);
                    } else {
                        lastNameInputLayout.setError(requiredField);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            },
            cityTextWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.toString().trim().length() > 0) {
                        cityInputLayout.setErrorEnabled(false);
                    } else {
                        cityInputLayout.setError(requiredField);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            },
            oldPwdTextWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.toString().trim().length() > 0) {
                        if (s.toString().trim().length() < minLength6) {
                            inputLayoutOldPwd.setError(minLength6Msg);
                        } else {
                            inputLayoutOldPwd.setErrorEnabled(false);
                        }
                    } else {
                        inputLayoutOldPwd.setError(requiredField);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            },
            newPwdTextWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.toString().trim().length() > 0) {
                        if (s.toString().trim().length() < minLength6) {
                            inputLayoutNewPwd.setError(minLength6Msg);
                        } else {
                            inputLayoutNewPwd.setErrorEnabled(false);
                        }
                    } else {
                        inputLayoutNewPwd.setError(requiredField);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            },
            confPwdTextWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.toString().trim().length() > 0) {
                        if (!s.toString().trim().equals(inputNewPasswordET.getText().toString().trim())) {
                            inputLayoutConfirmPwd.setError(pwdMatching);
                        } else {
                            inputLayoutConfirmPwd.setErrorEnabled(false);
                        }
                    } else {
                        inputLayoutConfirmPwd.setError(requiredField);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            },
            inputPhoneTextWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.toString().trim().length() > 0) {
                        inputLayoutPhone.setErrorEnabled(false);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            }
             ;

    // save changes
    @OnClick(R.id.btn_save_changes)
    public void saveChanges() {
        if (validForm()) {
            if (RSNetwork.isConnected(getContext())) {
                RSRequestEditProfile rsRequestEditProfile = new RSRequestEditProfile();
                rsRequestEditProfile.setUsername(inputUserNameET.getText().toString().trim());
                rsRequestEditProfile.setLastName(inputLastNameET.getText().toString().trim());
                rsRequestEditProfile.setFirstName(inputFirstNameET.getText().toString().trim());
                rsRequestEditProfile.setBirthDate(RSDateParser.convertToDateFormat(birthDateET.getText().toString().trim(), dateFormat, serverDateFormat));
                rsRequestEditProfile.setCountryName(selectedCountry.getCountryName());
                rsRequestEditProfile.setCity(inputCityET.getText().toString().trim());
                rsRequestEditProfile.setCountryCode(selectedCountry.getCountryCode());
                rsRequestEditProfile.setGender(selectedGender);
                rsRequestEditProfile.setPhone(inputPhoneET.getText().toString().trim());
                rsRequestEditProfile.setNameToUse(selectedPublicName);
                rsRequestEditProfile.setUserId(currentUser.get_id());
                if (!isPwdHidden) {
                    rsRequestEditProfile.setNewPassword(inputNewPasswordET.getText().toString().trim());
                    rsRequestEditProfile.setOldPassword(inputOldPasswordET.getText().toString().trim());
                }
                inputLayoutOldPwd.setError("");

                //if (rsRequestEditProfile.getFile() != null) {
                    if ( !rsRequestEditProfile.getBirthDate().trim().equalsIgnoreCase("") &&
                            !rsRequestEditProfile.getCountryName().trim().equalsIgnoreCase("") && !rsRequestEditProfile.getCity().trim().equalsIgnoreCase("") &&
                            !rsRequestEditProfile.getCountryCode().trim().equalsIgnoreCase("")) {
                        presenterUpdateProfile.editProfile(rsRequestEditProfile, getContext(), isPwdHidden);
                    } else {
                        Toast.makeText(getContext(), R.string.edit_profile, Toast.LENGTH_LONG).show();
                    }
               /* } else {
                    if (currentUser.getPictureProfile() == null) {
                        Toast.makeText(getContext(), R.string.edit_profile, Toast.LENGTH_LONG).show();
                    } else if (currentUser.getPictureProfile() == "") {
                        Toast.makeText(getContext(), R.string.edit_profile, Toast.LENGTH_LONG).show();
                    } else {
                        if ( !rsRequestEditProfile.getBirthDate().trim().equalsIgnoreCase("") &&
                                !rsRequestEditProfile.getCountryName().trim().equalsIgnoreCase("") && !rsRequestEditProfile.getCity().trim().equalsIgnoreCase("") &&
                                !rsRequestEditProfile.getCountryCode().trim().equalsIgnoreCase("")
                        ) {
                            presenterUpdateProfile.editProfile(rsRequestEditProfile, getContext(), isPwdHidden);
                        } else {
                            Toast.makeText(getContext(), R.string.edit_profile, Toast.LENGTH_LONG).show();
                        }
                    }
                }*/


            } else {
                btnSaveChanges.setEnabled(true);
                onOffLine();
            }
        }
    }

    private boolean validForm() {
        int x = 0;
        userNameInputLayout.setErrorEnabled(false);
        firstNameInputLayout.setErrorEnabled(false);
        lastNameInputLayout.setErrorEnabled(false);
        cityInputLayout.setErrorEnabled(false);
        birthDateLayout.setVisibility(View.GONE);

        if (TextUtils.isEmpty(inputUserNameET.getText().toString().trim())) {
            userNameInputLayout.setError(requiredField);
            x++;
        }
        if (TextUtils.isEmpty(inputFirstNameET.getText().toString().trim())) {
            firstNameInputLayout.setError(requiredField);
            x++;
        }
        if (TextUtils.isEmpty(inputLastNameET.getText().toString().trim())) {
            lastNameInputLayout.setError(requiredField);
            x++;
        }
        if (TextUtils.isEmpty(inputCityET.getText().toString().trim())) {
            cityInputLayout.setError(requiredField);
            x++;
        }
        if (TextUtils.isEmpty(birthDateET.getText().toString().trim())) {
            birthDateLayout.setVisibility(View.VISIBLE);
            x++;
        }

        if (!isPwdHidden) {
            if (TextUtils.isEmpty(inputOldPasswordET.getText().toString().trim())) {
                inputLayoutOldPwd.setError(requiredField);
                x++;
            } else if (inputOldPasswordET.getText().toString().trim().length() < minLength6) {
                inputLayoutOldPwd.setError(minLength6Msg);
            }
            if (TextUtils.isEmpty(inputNewPasswordET.getText().toString().trim())) {
                inputLayoutNewPwd.setError(requiredField);
                x++;
            } else if (inputNewPasswordET.getText().toString().trim().length() < minLength6) {
                inputLayoutNewPwd.setError(minLength6Msg);
            }
            if (TextUtils.isEmpty(inputConfirmPasswordET.getText().toString().trim())) {
                inputLayoutConfirmPwd.setError(requiredField);
                x++;
            } else if (!inputConfirmPasswordET.getText().toString().trim().equals(inputNewPasswordET.getText().toString().trim())) {
                inputLayoutConfirmPwd.setError(pwdMatching);
            }

        }
        return x == 0;
    }

    private void addTextWatcher() {
        inputUserNameET.addTextChangedListener(userNameTextWatcher);
        inputFirstNameET.addTextChangedListener(firstNameTextWatcher);
        inputLastNameET.addTextChangedListener(lastNameTextWatcher);
        inputCityET.addTextChangedListener(cityTextWatcher);
        inputPhoneET.addTextChangedListener(inputPhoneTextWatcher);
        inputOldPasswordET.addTextChangedListener(oldPwdTextWatcher);
        inputNewPasswordET.addTextChangedListener(newPwdTextWatcher);
        inputConfirmPasswordET.addTextChangedListener(confPwdTextWatcher);
    }

    // show date picker
    @OnClick(R.id.et_birth_date)
    public void showDatePicker() {
        DialogFragment newFragment = DatePickerFragment.newInstance(currentDate, dateFormat);
        newFragment.setTargetFragment(this, 0);
        newFragment.show(getFragmentManager(), "datePicker");


       /*SpinnerDatePickerDialogBuilder datePicker =   new SpinnerDatePickerDialogBuilder()
                .context(getContext()).callback(UpdateProfileFragment.this)
                .spinnerTheme(R.style.NumberPickerStyle)
                .showTitle(true)
                .showDaySpinner(true)
                .defaultDate(2019, 0, 1)
                .maxDate(2020, 0, 1)
                .minDate(1960, 0, 1);
        datePicker.build().show();*/

    }

    // current location
    @OnClick(R.id.btn_use_c_location)
    public void cLocation() {
        getCurrentLocation();
    }

    private Resources res;
    @BindDrawable(R.drawable.ic_expand_more)
    Drawable expandMore;
    @BindDrawable(R.drawable.ic_expand_less)
    Drawable expandLess;
    @BindString(R.string.off_line)
    String offlineMsg;
    @BindString(R.string.gps_disabled_msg)
    String gpsDisabledMsg;
    @BindString(R.string.gps_positive_btn_msg)
    String positiveBtnText;
    @BindString(R.string.cancel)
    String negativeBtnText;

    // current location
    @OnClick(R.id.btn_change_pwd)
    public void changePWD() {
        if (isPwdHidden) {
            changePwdBTN.setIcon(expandLess);
            changePwdBTN.setText(cancelEditPwdText);
            inputLayoutOldPwd.setVisibility(View.VISIBLE);
            inputLayoutNewPwd.setVisibility(View.VISIBLE);
            inputLayoutConfirmPwd.setVisibility(View.VISIBLE);
        } else {
            changePwdBTN.setIcon(expandMore);
            changePwdBTN.setText(editPwdText);
            inputLayoutOldPwd.setVisibility(View.GONE);
            inputLayoutNewPwd.setVisibility(View.GONE);
            inputLayoutConfirmPwd.setVisibility(View.GONE);
        }
        isPwdHidden = !isPwdHidden;
    }

    @OnClick(R.id.fab)
    void takepic() {
        RSBottomSheetDialog dialog = new RSBottomSheetDialog();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        dialog.setTargetFragment(this, 0);
        dialog.show(ft, RSBottomSheetDialog.TAG);
    }

    private void getCurrentLocation() {
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
            googleApiClient = new GoogleApiClient.Builder(RankStop.getInstance())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        } else {
            onConnected(null);
        }
        googleApiClient.connect();
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
                (dialog, id) -> dialog.cancel());
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
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

    private LocationManager locationManager;

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
        mCompressor = new FileCompressor(getContext());
        presenterUpdateProfile = new PresenterUpdateProfileImpl(UpdateProfileFragment.this, getContext());
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        currentUser = (User) getArguments().getSerializable(RSConstants.CURRENT_USER);
        presenterUpdateProfile.loadCountriesList(RankStop.getDeviceLanguage(), getContext());
        bindData(currentUser);
    }

    private void bindData(User user) {
        inputFirstNameET.setText(user.getFirstName());
        inputLastNameET.setText(user.getLastName());
        inputUserNameET.setText(user.getUsername());
        if (user.getPhone() != null && !user.getPhone().equalsIgnoreCase("")){
            if (!user.getPhone().toLowerCase().equalsIgnoreCase("null"))
            inputPhoneET.setText(user.getPhone());
            else
                inputPhoneET.setText("");
        } else {
            inputPhoneET.setText("");
        }

        if (user.getBirthDate() != null) {
            birthDateET.setText(RSDateParser.convertToDateFormat(user.getBirthDate(), dateFormat));
            currentDate = RSDateParser.convertToDateFormat(user.getBirthDate(), dateFormat);
        }
        initPublicNameView();
        initGenderView();
        setUserPic(user.getPictureProfile());
        if (user.isPasswordExist())
            changePwdBTN.setVisibility(View.VISIBLE);
    }

    private void initGenderView() {
        String[] genderArray = new String[]{male, female};
        SpinnerGenderAdapter spinnerGenderAdapter = new SpinnerGenderAdapter(getContext(), genderArray);
        genderSpinner.setAdapter(spinnerGenderAdapter);
        setGender(currentUser.getGender());
    }

    private void initPublicNameView() {
        String[] publicNameArray = new String[]{usernameText, fullNameText};
        SpinnerPublicNameAdapter spinnerPublicNameAdapter = new SpinnerPublicNameAdapter(getContext(), publicNameArray);
        publicNameSpinner.setAdapter(spinnerPublicNameAdapter);
        if (currentUser.getNameToUse() != null)
            setPublicName(currentUser.getNameToUse());
    }

    private void setPublicName(RSPublicUserName publicName) {
        if (publicName.getType() != null) {
            if (publicName.getType().toLowerCase().trim().equals("username")) {
                publicNameSpinner.setSelection(0);
            } else if (publicName.getType().toLowerCase().trim().equals("fullname")) {
                publicNameSpinner.setSelection(1);
            }

            selectedPublicName = publicName.getType();
        }
    }

    private void setGender(String gender) {
        if (gender != null) {
            if (gender.toLowerCase().trim().equals("male")) {
                genderSpinner.setSelection(0);
            } else if (gender.toLowerCase().trim().equals("female")) {
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
        createLoader();
        toolbar.setTitle(editProfileTitle);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setFragmentActionListener((ContainerActivity) getActivity());

        countriesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCountry = (Country) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).toString().toLowerCase().trim().equals(male.toLowerCase().trim()))
                    selectedGender = "male";
                else if (parent.getItemAtPosition(position).toString().toLowerCase().trim().equals(female.toLowerCase().trim()))
                    selectedGender = "female";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        publicNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).toString().toLowerCase().trim().equals(fullNameText.toLowerCase().trim()))
                    selectedPublicName = "fullName";
                else if (parent.getItemAtPosition(position).toString().toLowerCase().trim().equals(usernameText.toLowerCase().trim()))
                    selectedPublicName = "username";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        addTextWatcher();

        context = getContext();

    }

    private void setUserPic(String picture) {
        if (picture != null){
            if (picture != ""){
                avatar.setImageURI(Uri.parse(picture));
            } else {
                ImageRequest request =
                        ImageRequestBuilder.newBuilderWithResourceId(R.drawable.ava_256)
                                .build();
                DraweeController controller = Fresco.newDraweeControllerBuilder()
                        .setImageRequest(request)
                        .setOldController(avatar.getController())
                        .build();
                avatar.setController(controller);
            }

        }else {
            ImageRequest request =
                    ImageRequestBuilder.newBuilderWithResourceId(R.drawable.ava_256)
                            .build();
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setOldController(avatar.getController())
                    .build();
            avatar.setController(controller);
        }

    }

    @Override
    public void onDestroyView() {
        inputUserNameET.removeTextChangedListener(userNameTextWatcher);
        inputFirstNameET.removeTextChangedListener(firstNameTextWatcher);
        inputLastNameET.removeTextChangedListener(lastNameTextWatcher);
        inputPhoneET.removeTextChangedListener(inputPhoneTextWatcher);
        inputCityET.removeTextChangedListener(cityTextWatcher);
        inputOldPasswordET.removeTextChangedListener(oldPwdTextWatcher);
        inputNewPasswordET.removeTextChangedListener(newPwdTextWatcher);
        inputConfirmPasswordET.removeTextChangedListener(confPwdTextWatcher);
        rootView = null;
        if (unbinder != null)
            unbinder.unbind();
        instance = null;
        if (presenterUpdateProfile != null)
            presenterUpdateProfile.onDestroy(getContext());
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
            case RSConstants.UPDATE_PROFILE:
                fragmentActionListener.pop();

                //RSNavigationData rsNavigationData= new RSNavigationData();
                //rsNavigationData.setFrom(RSConstants.UPDATE_PROFILE);
                //fragmentActionListener.startFragment(ProfileFragment.getInstance(rsNavigationData) , RSConstants.UPDATE_PROFILE);

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
    public void onOldPwdIncorrect(String message) {
        inputLayoutOldPwd.setError(oldPwdErrorMsg);
    }

    @Override
    public void showProgressBar(String target) {
        switch (target) {
            case RSConstants.COUNTRIES_LIST:
                rsLoader.show(getFragmentManager(), RSLoader.TAG);
                break;
            case RSConstants.UPDATE_PROFILE:
                rsLoader.show(getFragmentManager(), RSLoader.TAG);
                btnSaveChanges.setEnabled(false);
                break;
        }
    }

    @Override
    public void hideProgressBar(String target) {
        switch (target) {
            case RSConstants.COUNTRIES_LIST:
                rsLoader.dismiss();
                break;
            case RSConstants.UPDATE_PROFILE:
                rsLoader.dismiss();
                btnSaveChanges.setEnabled(true);
                break;
        }
    }

    @Override
    public void showMessage(String target, String message) {
        switch (target) {
            case RSConstants.UPDATE_PROFILE:
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onOffLine() {
        //Toast.makeText(context, offlineMsg, Toast.LENGTH_LONG).show();
        new RSCustomToast(getActivity(), getResources().getString(R.string.error), offlineMsg, R.drawable.ic_error, RSCustomToast.ERROR).show();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case android.R.id.home:
                getActivity().onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(RankStop.getInstance(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(RankStop.getInstance(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION_LOCATION);
        } else {
            FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(RankStop.getInstance());
            mFusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), location -> {
                // Got last known location. In some rare situations, this can be null.
                if (location != null) {
                    LatLng currentUserLatLang = new LatLng(location.getLatitude(), location.getLongitude());
                    setUserAddress(findCountry(getCountryCode(currentUserLatLang)), getCity(currentUserLatLang));
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
    public void onTakePictureClicked() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CAMERA);
        } else {
            openCamera();
        }
    }

    @Override
    public void onChoosePictureClicked() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_STORAGE);
        } else {
            openGallery();
        }
    }

    @Override
    public void onDateChanged(String date) {
        currentDate = RSDateParser.convertToDateFormat(date, "dd/MM/yyyy", dateFormat);
        birthDateET.setText(currentDate);
    }



    /*public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        DatePickerDialog dialog;
        public static DatePickerFragment fragment;
        private DateListener callback;

        private String currentDate, dateFormat;

        public static DatePickerFragment newInstance(String currentDate, String format) {

            Bundle args = new Bundle();

            args.putString(RSConstants.CURRENT_DATE, currentDate);
            args.putString(RSConstants.FORMAT_DATE, format);
            if (fragment == null) {
                fragment = new DatePickerFragment();
            }
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker

            try {
                callback = (DateListener) getTargetFragment();
            } catch (ClassCastException e) {
                throw new ClassCastException("Calling Fragment must implement OnAddFriendListener");
            }

            currentDate = getArguments().getString(RSConstants.CURRENT_DATE);
            dateFormat = getArguments().getString(RSConstants.FORMAT_DATE);
            Calendar calendar;
            if (currentDate != null) {
                calendar = RSDateParser.convertToDate(currentDate, dateFormat);
            } else {
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
            curCalendar.set(curYear - RSConstants.MIN_AGE_USER, curMonth, curDay);
            dialog.getDatePicker().setMaxDate(curCalendar.getTimeInMillis());
            return dialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            //EventBus.getDefault().post(new DateListener("" + day + "/" + (month + 1) + "/" + year));
            callback.onDateChanged("" + day + "/" + (month + 1) + "/" + year);
        }
    }*/
    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{

      /*  SpinnerDatePickerDialogBuilder datePicker =   new SpinnerDatePickerDialogBuilder()
                .context(getContext()).callback(UpdateProfileFragment.this)
                .spinnerTheme(R.style.NumberPickerStyle)
                .showTitle(true)
                .showDaySpinner(true)
                .defaultDate(2019, 0, 1)
                .maxDate(2020, 0, 1)
                .minDate(1960, 0, 1);
        datePicker.build().show();*/

        SpinnerDatePickerDialogBuilder dialog;
        public static DatePickerFragment fragment;
        private DateListener callback;

        private String currentDate, dateFormat;

        public static DatePickerFragment newInstance(String currentDate, String format) {

            Bundle args = new Bundle();

            args.putString(RSConstants.CURRENT_DATE, currentDate);
            args.putString(RSConstants.FORMAT_DATE, format);
            if (fragment == null) {
                fragment = new DatePickerFragment();
            }
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker

            try {
                callback = (DateListener) getTargetFragment();
            } catch (ClassCastException e) {
                throw new ClassCastException("Calling Fragment must implement OnAddFriendListener");
            }

            currentDate = getArguments().getString(RSConstants.CURRENT_DATE);
            dateFormat = getArguments().getString(RSConstants.FORMAT_DATE);
            Calendar calendar;
            if (currentDate != null) {
                calendar = RSDateParser.convertToDate(currentDate, dateFormat);
            } else {
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
            dialog = new SpinnerDatePickerDialogBuilder();
            curCalendar.set(curYear - RSConstants.MIN_AGE_USER, curMonth, curDay);
            dialog.context(getContext())
                    .spinnerTheme(R.style.NumberPickerStyle)
                    .defaultDate(curYear,curMonth,curDay)
            .callback( this);
            return dialog.build();
        }



        @Override
        public void onDateSet(com.tsongkha.spinnerdatepicker.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            callback.onDateChanged("" + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
        }
    }


    public static final int REQUEST_TAKE_PHOTO = 100;
    public static final int REQUEST_GALLERY_PHOTO = 200;
    public static final int REQUEST_PERMISSION_STORAGE = 300;
    public static final int REQUEST_PERMISSION_LOCATION = 400;
    public static final int REQUEST_PERMISSION_CAMERA = 500;

    private void openCamera() {

        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (pictureIntent.resolveActivity(getContext().getPackageManager()) != null) {

            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            mPhotoFile = photoFile;
            Uri photoUri = FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".provider", photoFile);
            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(pictureIntent, REQUEST_TAKE_PHOTO);
        }

        //startActivity(new Intent(getContext(), TakePicture2Activity.class));
    }

    private void openGallery() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(pickPhoto, REQUEST_GALLERY_PHOTO);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onConnected(null);
            } else {
                // No Permitions Granted
            }
        } else if (requestCode == REQUEST_PERMISSION_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                // No Permitions Granted
            }
        } else if (requestCode == REQUEST_PERMISSION_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                // No Permitions Granted
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_TAKE_PHOTO) {
                try {
                    mPhotoFile = mCompressor.compressToFile(mPhotoFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String path = MediaStore.Images.Media.insertImage(RankStop.getInstance().getContentResolver(), BitmapFactory.decodeFile(mPhotoFile.getAbsolutePath()), "Image Description", null);
                imageUri = Uri.parse(path);
                avatar.setImageURI(imageUri);

            } else if (requestCode == REQUEST_GALLERY_PHOTO) {
                Uri selectedImage = data.getData();
                try {
                    mPhotoFile = mCompressor.compressToFile(new File(getRealPathFromUri(selectedImage)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String path = MediaStore.Images.Media.insertImage(RankStop.getInstance().getContentResolver(), BitmapFactory.decodeFile(mPhotoFile.getAbsolutePath()), "Image Description", null);
                imageUri = Uri.parse(path);
                avatar.setImageURI(imageUri);
            }
        } else if (resultCode == RESULT_CANCELED) {
        }
    }

    public String getRealPathFromUri(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = getContext().getContentResolver().query(contentUri, proj, null, null, null);
            assert cursor != null;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
