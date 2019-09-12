package com.steeringit.rankstop.ui.fragments;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Set;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.steeringit.rankstop.MVP.model.PresenterAuthImpl;
import com.steeringit.rankstop.customviews.RSCustomToast;
import com.steeringit.rankstop.customviews.RSETRegular;
import com.steeringit.rankstop.customviews.RSTVRegular;
import com.steeringit.rankstop.data.model.db.Country;
import com.steeringit.rankstop.data.model.db.RSAddress;
import com.steeringit.rankstop.data.model.network.GeoPluginResponse;
import com.steeringit.rankstop.data.model.network.RSDeviceIP;
import com.steeringit.rankstop.data.model.network.RSFollow;
import com.steeringit.rankstop.data.model.network.RSNavigationData;
import com.steeringit.rankstop.data.model.network.RSRequestSocialLogin;
import com.steeringit.rankstop.data.model.network.RSResponseFindEmail;
import com.steeringit.rankstop.data.model.network.RSResponseLogin;
import com.steeringit.rankstop.session.RSSession;
import com.steeringit.rankstop.ui.activities.ContainerActivity;
import com.steeringit.rankstop.ui.dialogFragment.LoginDialog;
import com.steeringit.rankstop.ui.dialogFragment.RSLoader;
import com.steeringit.rankstop.ui.dialogFragment.RegisterDialog;
import com.steeringit.rankstop.MVP.presenter.RSPresenter;
import com.steeringit.rankstop.R;
import com.steeringit.rankstop.MVP.view.RSView;
import com.steeringit.rankstop.utils.RSConstants;
import com.steeringit.rankstop.utils.RSNetwork;

import org.json.JSONException;
import org.json.JSONObject;

public class SignupFragment extends Fragment implements RSView.SignupView {

    private final String TAG = "SIGNUP FRAGMENT";
    private View rootView;
    private Unbinder unbinder;
    private RSPresenter.SignupPresenter signupPresenter;
    private WeakReference<SignupFragment> fragmentContext;
    private RSNavigationData rsNavigationData;
    // facebook
    private CallbackManager callbackManager;
    // google
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;
    private RSRequestSocialLogin user;
    @BindString(R.string.use_fb_login_msg)
    String useFBLoginMsg;

    @BindView(R.id.input_email)
    RSETRegular inputEmail;

    @BindView(R.id.input_layout_email)
    TextInputLayout inputLayoutEmail;

    @BindView(R.id.tv_rs_privacy_policy)
    RSTVRegular privacyPolicyTV;

    @BindString(R.string.email_format_incorrect)
    String emailFormatIncorrect;
    @BindString(R.string.field_required)
    String requiredField;
    @BindString(R.string.off_line)
    String offlineMsg;

    @BindString(R.string.privacy_policy_text)
    String privacyPolicyText;

    @BindString(R.string.privacy_policy_msg)
    String privacyPolicyMsg;


    @OnClick(R.id.rs_login_btn)
    void rsLogin() {
        if (validForm(inputEmail.getText().toString().trim())) {
            if (RSNetwork.isConnected(getContext())) {
                signupPresenter.performFindEmail(inputEmail.getText().toString().trim().toLowerCase(), getContext());
            }else
                onOffLine();
        }
    }

    @OnClick(R.id.fb_login_btn)
    void fbLogin() {
        if (RSNetwork.isConnected(getContext())) {
            LoginManager.getInstance().logOut();
            LoginManager.getInstance().logInWithReadPermissions(fragmentContext.get(), Arrays.asList("public_profile", "email"));
        } else {
            onOffLine();
        }

    }

    @OnClick(R.id.google_login_btn)
    void googleLogin() {
        if (RSNetwork.isConnected(getContext())) {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, 1);
        } else {
            onOffLine();
        }
    }


    private TextWatcher emailTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().trim().length() > 0) {
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(s.toString()).matches()) {
                    inputLayoutEmail.setError(emailFormatIncorrect);
                } else {
                    inputLayoutEmail.setErrorEnabled(false);
                }
            } else {
                inputLayoutEmail.setError(requiredField);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private boolean validForm(String email) {
        int x = 0;
        inputLayoutEmail.setErrorEnabled(false);

        if (TextUtils.isEmpty(email)) {
            inputLayoutEmail.setError(requiredField);
            x++;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputLayoutEmail.setError(emailFormatIncorrect);
            x++;
        }
        return x == 0;
    }

    @BindString(R.string.loading_msg)
    String loadingMsg;
    private RSLoader rsLoader;

    private void createLoader() {
        rsLoader = RSLoader.newInstance(loadingMsg);
        rsLoader.setCancelable(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentContext = new WeakReference<>(this);
        rootView = inflater.inflate(R.layout.fragment_signup, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        createLoader();
        inputEmail.addTextChangedListener(emailTextWatcher);
        rsNavigationData = (RSNavigationData) getArguments().getSerializable(RSConstants.NAVIGATION_DATA);
        signupPresenter = new PresenterAuthImpl(fragmentContext.get());
        callbackManager = CallbackManager.Factory.create();
        String value = privacyPolicyMsg + " " + privacyPolicyText;
        Spannable wordtoSpan = new SpannableString(value);
        wordtoSpan.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                try {
                    i.setData(Uri.parse(RSConstants.PRIVACY_POLICY_LINK));
                    startActivity(i);
                } catch (Exception e) {
                }
            }
        }, privacyPolicyMsg.length() + 1, value.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        privacyPolicyTV.setText(wordtoSpan);
        privacyPolicyTV.setMovementMethod(LinkMovementMethod.getInstance());

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        AccessToken accessToken = AccessToken.getCurrentAccessToken();
                        Set<String> listPermissions = accessToken.getPermissions();

                        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
                        GraphRequest request = GraphRequest.newMeRequest(accessToken, (object, response) -> {
                            RSRequestSocialLogin user = getData(object);
                            if (user != null) {
                                performSocialLogin(user);
                            }
                        });

                        Bundle parametrs = new Bundle();
                        parametrs.putString("fields", "id,email,first_name,last_name");
                        request.setParameters(parametrs);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {

                        rsLoader.onDestroy();


                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        rsLoader.dismissDialog();
                    }
                });

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.PLUS_ME))
                .requestEmail()
                .requestId()
                .requestProfile()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);

        // show register popup
        if (rsNavigationData != null){
            if (rsNavigationData.getFrom() != null){
            if (rsNavigationData.getFrom().equalsIgnoreCase(RSConstants.ACTIVITY_CONTAINER)) {
                // get the mail before showing
                Log.e("dialogRegister" , rsNavigationData.getAction());
                if (RegisterDialog.getInstance() != null){
                    getFragmentManager().popBackStack();
                    RegisterDialog.getInstance().onDestroyView();

                }
                dialogRegister(rsNavigationData.getAction());
            }
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        //GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(fragmentContext.get().getContext());
        //updateUI(account);
    }

    public void dialogLogin(String email) {
        LoginDialog dialog = LoginDialog.newInstance(inputEmail.getText().toString().trim().toLowerCase(), rsNavigationData);
        dialog.setCancelable(false);
        String LOGIN_DIALOG_TAG = "LOGIN_DIALOG";
        dialog.show(getFragmentManager(), LOGIN_DIALOG_TAG);
    }

    public void dialogRegister(String email) {
        Log.e("email" , email+"!");
        rsNavigationData.setFrom(RSConstants.FRAGMENT_HOME);
        rsNavigationData.setAction("");
        RegisterDialog dialog = RegisterDialog.newInstance(email, rsNavigationData);
        inputEmail.setText(email);
        dialog.setCancelable(false);
        String REGISTER_DIALOG_TAG = "REGISTER_DIALOG";
        dialog.show(getFragmentManager(), REGISTER_DIALOG_TAG);
    }


    private static SignupFragment instance;

    public static SignupFragment getInstance(RSNavigationData data) {
         Bundle args = new Bundle();
        args.putSerializable(RSConstants.NAVIGATION_DATA, data);
        if (instance == null  ) {
            instance = new SignupFragment();
        } else
            if (data.getFrom().equalsIgnoreCase(RSConstants.ACTIVITY_CONTAINER)){
                instance = new SignupFragment();
        }

                instance.setArguments(args);



        return instance;
    }

    @Override
    public void onDestroyView() {
        inputEmail.removeTextChangedListener(emailTextWatcher);
        instance = null;
        rootView = null;
        if (fragmentContext != null)
            fragmentContext.clear();
        if (unbinder != null)
            unbinder.unbind();
        if (signupPresenter != null)
            signupPresenter.onDestroyFindEmail(getContext());
        super.onDestroyView();
    }

    @Override
    public void findEmailValidations() {
        inputLayoutEmail.setErrorEnabled(true);
        inputLayoutEmail.setError(getString(R.string.email_format_incorrect));
    }

    @Override
    public void findEmailSuccess(boolean isEmailExist, Object data) {
        if (isEmailExist) {
            RSResponseFindEmail findEmailResponse = new Gson().fromJson(new Gson().toJson(data), RSResponseFindEmail.class);
            if (findEmailResponse.isConnectSocialMedia()) {
                Toast.makeText(getContext(), useFBLoginMsg, Toast.LENGTH_LONG).show();
            } else {
                dialogLogin(inputEmail.getText().toString().trim().toLowerCase());
            }
        } else {
            //dialogRegister(inputEmail.getText().toString().trim().toLowerCase());
            Toast.makeText(getContext() , R.string.email_confirmation_sent,Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void socialLoginSuccess(Object data) {

        RSResponseLogin loginResponse = new Gson().fromJson(new Gson().toJson(data), RSResponseLogin.class);
        String token = loginResponse.getToken();
        RSSession.startSession(token);
        if (rsNavigationData == null) {
            rsNavigationData = new RSNavigationData();
            rsNavigationData.setFrom(RSConstants.FRAGMENT_HOME);
            rsLoader.dismiss();
            ((ContainerActivity) getActivity()).manageSession(true, rsNavigationData);
        } else
            if (rsNavigationData.getAction()== RSConstants.ACTION_FOLLOW) {
                signupPresenter.followItem(new RSFollow(RSSession.getCurrentUser().get_id(), rsNavigationData.getItemId()), RSConstants.SOCIAL_LOGIN, getContext());
            } else {
                rsLoader.dismiss();

                        if ( rsNavigationData.getFrom() == RSConstants.FRAGMENT_PROFILE ||rsNavigationData.getFrom() == RSConstants.FRAGMENT_SIGN_UP ){

                            rsNavigationData.setFrom(RSConstants.FRAGMENT_SIGN_UP);

                        } else  if (rsNavigationData.getFrom() == null || rsNavigationData.getFrom() == ""){
                            rsNavigationData.setFrom(RSConstants.FRAGMENT_HOME);
                        }
                ((ContainerActivity) getActivity()).manageSession(true, rsNavigationData);
            }
    }

    @Override
    public void socialLoginError(String message) {
        Toast.makeText(getContext(), R.string.technical_error + message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFollowSuccess(String target, Object data) {
        if (data.equals("1")) {
            Toast.makeText(getContext(), getResources().getString(R.string.follow), Toast.LENGTH_SHORT).show();
        } else if (data.equals("0")) {
            Toast.makeText(getContext(), getResources().getString(R.string.already_followed), Toast.LENGTH_SHORT).show();
        }
        ((ContainerActivity) getActivity()).manageSession(true, rsNavigationData);
    }

    @Override
    public void onFollowFailure(String target) {
        ((ContainerActivity) getActivity()).manageSession(true, rsNavigationData);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == 1) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            //updateUI(account);

            RSRequestSocialLogin user = new RSRequestSocialLogin();
            user.setProvider(RSConstants.PROVIDER_GOOGLE);
            user.setEmail(account.getEmail().toLowerCase());
            user.setFirstName(account.getGivenName());
            user.setLastName(account.getFamilyName());
            user.setId(account.getId());
            if (account.getPhotoUrl() != null) {
                user.setPhotoUrl(account.getPhotoUrl().toString());
            }
            performSocialLogin(user);

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            //updateUI(null);
        }
    }

    private void performSocialLogin(RSRequestSocialLogin user) {
        this.user = user;
        signupPresenter.getPublicIP("json", RSConstants.SOCIAL_LOGIN, getContext());
    }

    @Override
    public void showProgressBar(String target) {
        switch (target) {
            case RSConstants.LOGIN:
                rsLoader.show(getFragmentManager(), RSLoader.TAG);
                break;
            case RSConstants.PUBLIC_IP:
                rsLoader.show(getFragmentManager(), RSLoader.TAG);
                break;
        }
    }

    @Override
    public void hideProgressBar(String target) {
        switch (target) {
            case RSConstants.LOGIN:
                rsLoader.dismiss();
                break;
            case RSConstants.FOLLOW_ITEM:
                rsLoader.dismiss();
                break;
            case RSConstants.SOCIAL_LOGIN:
                rsLoader.dismiss();
                break;
        }
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(getContext(), "" + message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAddressFetched(GeoPluginResponse response) {
        RSAddress address = new RSAddress();
        address.setCountry(new Country(response.getGeoplugin_countryCode().toLowerCase(), response.getGeoplugin_countryName()));
        user.setLocation(address);
        signupPresenter.performSocialLogin(user, getContext());
    }

    @Override
    public void onAddressFailed() {
        signupPresenter.performSocialLogin(user, getContext());
    }

    @Override
    public void onPublicIPFetched(RSDeviceIP response) {
        RSDeviceIP rsDeviceIP = new Gson().fromJson(new Gson().toJson(response), RSDeviceIP.class);
        signupPresenter.getAddress(rsDeviceIP.getIp(), RSConstants.SOCIAL_LOGIN, getContext());
    }

    @Override
    public void onPublicIPFailed() {
        signupPresenter.performSocialLogin(user, getContext());
    }

    @Override
    public void onOffLine() {
        // Toast.makeText(getContext(), offlineMsg, Toast.LENGTH_LONG).show();
        new RSCustomToast(getActivity(), getResources().getString(R.string.error), offlineMsg, R.drawable.ic_error, RSCustomToast.ERROR).show();
    }

    private RSRequestSocialLogin getData(JSONObject object) {
        if (object != null) {
            try {
                URL profile_picture = new URL("https://graph.facebook.com/" + object.getString("id") + "/picture?width=250&height=250");
                RSAddress rsAddress = new RSAddress();
                /*if (object.getJSONObject("location") != null) {
                    if (!object.getJSONObject("location").getString("name").equalsIgnoreCase("") && object.getJSONObject("location").getString("name") != null)
                        rsAddress.setCity(object.getJSONObject("location").getString("name"));
                }
*/
                RSRequestSocialLogin user = new RSRequestSocialLogin();
                if (!object.getString("email").equalsIgnoreCase("") && object.getString("email") != null)
                user.setEmail(object.getString("email").toLowerCase());
                /*if (!object.getString("birthday").equalsIgnoreCase("") && object.getString("birthday") != null)
                user.setBirthday(object.getString("birthday"));
                if (!object.getString("gender").equalsIgnoreCase("") && object.getString("gender") != null)
                user.setGender(object.getString("gender"));*/
                if (!object.getString("first_name").equalsIgnoreCase("") && object.getString("first_name") != null)
                user.setFirstName(object.getString("first_name"));
                if (!object.getString("last_name").equalsIgnoreCase("") && object.getString("last_name") != null)
                user.setLastName(object.getString("last_name"));
                if (profile_picture != null)
                user.setPhotoUrl(profile_picture.toString());
                user.setLocation(rsAddress);
                user.setProvider(RSConstants.PROVIDER_FB);
                return user;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    private void printKeyHash() {
        try {
            PackageInfo info = getContext().getPackageManager().getPackageInfo("com.steeringit.rankstop", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
