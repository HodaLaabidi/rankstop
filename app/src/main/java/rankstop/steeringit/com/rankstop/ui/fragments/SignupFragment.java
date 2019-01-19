package rankstop.steeringit.com.rankstop.ui.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Base64;
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
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rankstop.steeringit.com.rankstop.MVP.model.PresenterAuthImpl;
import rankstop.steeringit.com.rankstop.data.model.db.Country;
import rankstop.steeringit.com.rankstop.data.model.db.RSAddress;
import rankstop.steeringit.com.rankstop.data.model.db.User;
import rankstop.steeringit.com.rankstop.data.model.network.GeoPluginResponse;
import rankstop.steeringit.com.rankstop.data.model.network.RSDeviceIP;
import rankstop.steeringit.com.rankstop.data.model.network.RSFollow;
import rankstop.steeringit.com.rankstop.data.model.network.RSNavigationData;
import rankstop.steeringit.com.rankstop.data.model.network.RSRequestSocialLogin;
import rankstop.steeringit.com.rankstop.data.model.network.RSResponseFindEmail;
import rankstop.steeringit.com.rankstop.data.model.network.RSResponseLogin;
import rankstop.steeringit.com.rankstop.session.RSSession;
import rankstop.steeringit.com.rankstop.ui.activities.ContainerActivity;
import rankstop.steeringit.com.rankstop.ui.dialogFragment.LoginDialog;
import rankstop.steeringit.com.rankstop.ui.dialogFragment.RegisterDialog;
import rankstop.steeringit.com.rankstop.MVP.presenter.RSPresenter;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.MVP.view.RSView;
import rankstop.steeringit.com.rankstop.utils.RSConstants;

import org.json.JSONException;
import org.json.JSONObject;

public class SignupFragment extends Fragment implements RSView.SignupView {

    private final String TAG = "SIGNUP FRAGMENT";
    private final String LOGIN_DIALOG_TAG = "game_dialog_tag";
    private final String REGISTER_DIALOG_TAG = "game_dialog_tag";

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
    private GoogleSignInAccount account;

    private RSRequestSocialLogin user;

    private ProgressDialog mDialog;

    @BindView(R.id.input_email)
    TextInputEditText inputEmail;

    @BindView(R.id.input_layout_email)
    TextInputLayout inputLayoutEmail;

    @OnClick(R.id.rs_login_btn)
    void rsLogin(){
        inputLayoutEmail.setErrorEnabled(false);
        inputLayoutEmail.setError("");
        signupPresenter.performFindEmail(inputEmail.getText().toString().trim());
    }

    @OnClick(R.id.fb_login_btn)
    void fbLogin(){
        LoginManager.getInstance().logOut();
        LoginManager.getInstance().logInWithReadPermissions(fragmentContext.get(), Arrays.asList("public_profile","email","user_birthday","user_gender","user_location"));
    }

    @OnClick(R.id.google_login_btn)
    void googleLogin(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 1);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentContext = new WeakReference<SignupFragment>(this);
        rootView = inflater.inflate(R.layout.fragment_signup, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        rsNavigationData = (RSNavigationData) getArguments().getSerializable(RSConstants.NAVIGATION_DATA);
        signupPresenter = new PresenterAuthImpl(fragmentContext.get());
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        mDialog = new ProgressDialog(fragmentContext.get().getContext());
                        mDialog.setMessage("Retrieving data ...");
                        mDialog.show();

                        AccessToken accessToken = AccessToken.getCurrentAccessToken();
                        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

                        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {

                                RSRequestSocialLogin user = getData(object);
                                if (user != null){
                                    performSocialLogin(user);
                                }else {
                                    Log.i("TAG_REGISTER","user null");
                                }
                            }
                        });

                        Bundle parametrs = new Bundle();
                        parametrs.putString("fields","id,email,birthday,first_name,last_name,gender,location");
                        request.setParameters(parametrs);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        mDialog.dismiss();
                        Toast.makeText(getContext(), "cancel", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Log.i("TAG_REGISTER_ERROR",exception.getMessage());
                        mDialog.dismiss();
                        Toast.makeText(getContext(), "error = "+exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.PLUS_ME))
                .requestEmail()
                .requestId()
                .requestProfile()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);
    }

    @Override
    public void onResume() {
        super.onResume();

        account = GoogleSignIn.getLastSignedInAccount(fragmentContext.get().getContext());
        //updateUI(account);
    }

    public void dialogLogin(String email) {
        LoginDialog dialog = LoginDialog.newInstance(inputEmail.getText().toString().trim(), rsNavigationData);
        dialog.setCancelable(false);
        dialog.show(getFragmentManager(), LOGIN_DIALOG_TAG);
    }

    public void dialogRegister(String email) {
        RegisterDialog dialog = RegisterDialog.newInstance(inputEmail.getText().toString().trim(), rsNavigationData);
        dialog.setCancelable(false);
        dialog.show(getFragmentManager(), REGISTER_DIALOG_TAG);
    }


    private static SignupFragment instance;

    public static SignupFragment getInstance(RSNavigationData data) {
        Bundle args = new Bundle();
        args.putSerializable(RSConstants.NAVIGATION_DATA, data);
        if (instance == null)
            instance = new SignupFragment();
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onDestroyView() {
        instance = null;
        rootView=null;
        fragmentContext.clear();
        unbinder.unbind();
        signupPresenter.onDestroyFindEmail();
        super.onDestroyView();
    }

    @Override
    public void findEmailValidations() {
        inputLayoutEmail.setErrorEnabled(true);
        inputLayoutEmail.setError(getString(R.string.signup_email_format_invalid));
    }

    @Override
    public void findEmailSuccess(boolean isEmailExist, Object data) {
        if (isEmailExist) {

            RSResponseFindEmail findEmailResponse = new Gson().fromJson(new Gson().toJson(data), RSResponseFindEmail.class);
            if (findEmailResponse.isConnectSocialMedia()) {
                Toast.makeText(getContext(), "Please use your facebook account to login", Toast.LENGTH_LONG).show();
            } else {
                dialogLogin(inputEmail.getText().toString().trim());
            }
        } else {
            dialogRegister(inputEmail.getText().toString().trim());
        }
    }

    @Override
    public void findEmailError() {

    }

    @Override
    public void socialLoginSuccess(Object data) {

        mDialog.dismiss();
        RSResponseLogin loginResponse = new Gson().fromJson(new Gson().toJson(data), RSResponseLogin.class);
        String token = loginResponse.getToken();
        //Log.i("TAG_REGISTER",""+token);
        RSSession.startSession(getContext(), token);

        if (rsNavigationData.getAction().equals(RSConstants.ACTION_FOLLOW)) {
            signupPresenter.followItem(new RSFollow(RSSession.getCurrentUser(getContext()).get_id(), rsNavigationData.getItemId()), RSConstants.SOCIAL_LOGIN);
        }else {
            ((ContainerActivity)getActivity()).manageSession(true, rsNavigationData);
        }
    }

    @Override
    public void socialLoginError(String message) {
        Toast.makeText(getContext(), ""+message, Toast.LENGTH_LONG).show();
        Log.i("TAG_REGISTER",""+message);
        mDialog.dismiss();
    }

    @Override
    public void onFollowSuccess(String target, Object data) {
        if (data.equals("1")) {
            Toast.makeText(getContext(), getResources().getString(R.string.follow), Toast.LENGTH_SHORT).show();
        } else if (data.equals("0")) {
            Toast.makeText(getContext(), getResources().getString(R.string.already_followed), Toast.LENGTH_SHORT).show();
        }
        ((ContainerActivity)getActivity()).manageSession(true, rsNavigationData);
    }

    @Override
    public void onFollowFailure(String target) {
        ((ContainerActivity)getActivity()).manageSession(true, rsNavigationData);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        //Toast.makeText(getContext(), "code  = "+requestCode, Toast.LENGTH_LONG).show();
        if (requestCode == 1){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }else {
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
            user.setEmail(account.getEmail());
            user.setFirstName(account.getGivenName());
            user.setLastName(account.getFamilyName());
            user.setId(account.getId());
            user.setPhotoUrl(account.getPhotoUrl().toString());

            performSocialLogin(user);

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            //updateUI(null);
        }
    }

    private void performSocialLogin(RSRequestSocialLogin user){
        this.user = user;
        signupPresenter.getPublicIP("json", RSConstants.SOCIAL_LOGIN);
    }

    @Override
    public void showProgressBar() {
        /*dialog = new ProgressDialog(getContext());
        dialog.setMessage("loading ...");
        dialog.show();*/
    }

    @Override
    public void hideProgressBar() {
        //dialog.dismiss();
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(getContext(), "" + message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAddressFetched(GeoPluginResponse response) {
        RSAddress address = new RSAddress();
        address.setCountry(new Country(response.getGeoplugin_countryCode(), response.getGeoplugin_countryName()));
        user.setLocation(address);
        signupPresenter.performSocialLogin(user);
    }

    @Override
    public void onAddressFailed() {
        signupPresenter.performSocialLogin(user);
    }

    @Override
    public void onPublicIPFetched(RSDeviceIP response) {
        RSDeviceIP rsDeviceIP = new Gson().fromJson(new Gson().toJson(response), RSDeviceIP.class);
        signupPresenter.getAddress(rsDeviceIP.getIp(), RSConstants.SOCIAL_LOGIN);
    }

    @Override
    public void onPublicIPFailed() {
        signupPresenter.performSocialLogin(user);
    }

    private RSRequestSocialLogin getData(JSONObject object) {
        try{
            URL profile_picture = new URL("https://graph.facebook.com/"+object.getString("id")+"/picture?width=250&height=250");

            RSAddress rsAddress = new RSAddress();
            rsAddress.setCity(object.getJSONObject("location").getString("name"));

            RSRequestSocialLogin user = new RSRequestSocialLogin();
            user.setEmail(object.getString("email"));
            user.setBirthday(object.getString("birthday"));
            user.setGender(object.getString("gender"));
            user.setFirstName(object.getString("first_name"));
            user.setLastName(object.getString("last_name"));
            user.setPhotoUrl(profile_picture.toString());
            user.setLocation(rsAddress);
            user.setProvider(RSConstants.PROVIDER_FB);

            Log.i("LOGIN_OBJECT",object.toString());
            Log.i("LOGIN_EMAIL",object.getString("email"));
            Log.i("LOGIN_BIRTHDAY",object.getString("birthday"));
            Log.i("LOGIN_BIRTHDAY",object.getString("gender"));
            Log.i("LOGIN_BIRTHDAY",object.getString("first_name"));
            Log.i("LOGIN_BIRTHDAY",object.getString("last_name"));
            Log.i("URL_PIX",""+profile_picture);
            //Log.i("LOGIN_FRIENDS", object.getJSONObject("friends").getJSONObject("summary").getString("total_count"));
            Log.i("LOGIN_FRIENDS", object.getJSONObject("location").getString("name"));

            return user;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void printKeyHash() {
        try{
            PackageInfo info = getContext().getPackageManager().getPackageInfo("com.steeringit.rankstop", PackageManager.GET_SIGNATURES);
            for (Signature signature:info.signatures){
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.i("KEY_HASH", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        }catch(PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }catch(NoSuchAlgorithmException e){
            e.printStackTrace();
        }
    }
}
