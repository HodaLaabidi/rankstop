package com.steeringit.rankstop.ui.dialogFragment;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;

import butterknife.BindInt;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.steeringit.rankstop.MVP.model.PresenterAuthImpl;
import com.steeringit.rankstop.customviews.RSCustomToast;
import com.steeringit.rankstop.customviews.RSETRegular;
import com.steeringit.rankstop.data.model.db.Country;
import com.steeringit.rankstop.data.model.db.RSAddress;
import com.steeringit.rankstop.data.model.network.GeoPluginResponse;
import com.steeringit.rankstop.data.model.network.RSDeviceIP;
import com.steeringit.rankstop.data.model.network.RSFollow;
import com.steeringit.rankstop.data.model.network.RSNavigationData;
import com.steeringit.rankstop.data.model.network.RSResponseLogin;
import com.steeringit.rankstop.ui.activities.ContainerActivity;
import com.steeringit.rankstop.R;
import com.steeringit.rankstop.data.model.db.User;
import com.steeringit.rankstop.MVP.presenter.RSPresenter;
import com.steeringit.rankstop.session.RSSession;
import com.steeringit.rankstop.MVP.view.RSView;
import com.steeringit.rankstop.utils.RSConstants;
import com.steeringit.rankstop.utils.RSNetwork;

public class RegisterDialog extends DialogFragment implements RSView.RegisterView {

    @BindView(R.id.input_layout_password)
    TextInputLayout passwordLayout;
    @BindView(R.id.input_password)
    RSETRegular passwordEditText;

    @BindView(R.id.input_layout_confirm_password)
    TextInputLayout confirmPasswordLayout;
    @BindView(R.id.input_confirm_password)
    RSETRegular confirmPasswordEditText;

    @BindView(R.id.input_email)
    RSETRegular emailEditText;
    @BindString(R.string.off_line)
    String offLineMsg;

    @OnClick(R.id.negative_btn)
    void cancelDialog() {
        dismiss();
    }

    @OnClick(R.id.positive_btn)
    void onClick(View v) {
        if (validForm(passwordEditText.getText().toString().trim(), confirmPasswordEditText.getText().toString().trim())) {
            if (RSNetwork.isConnected(getContext())) {
                user = new User();
                user.setPassword(passwordEditText.getText().toString().trim());
                user.setEmail(getArguments().getString(RSConstants.EMAIL));
                registerPresenter.getPublicIP("json", RSConstants.REGISTER, getContext());
            } else {
                onOffLine();
            }
        }
    }

    private boolean validForm(String password, String confirmPassword) {
        int x = 0;
        passwordLayout.setErrorEnabled(false);

        if (TextUtils.isEmpty(password)) {
            passwordLayout.setError(requiredField);
            x++;
        } else if (password.length() < minLength6) {
            passwordLayout.setError(minLength6Msg);
            x++;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordLayout.setError(requiredField);
            x++;
        } else if (!confirmPassword.equals(password)) {
            x++;
        }
        return x == 0;
    }

    @BindString(R.string.field_required)
    String requiredField;
    @BindString(R.string.register_failed_msg)
    String registerFailedMsg;
    @BindString(R.string.login_dialog_empty_password)
    String minLength6Msg;
    @BindInt(R.integer.min_length_pwd)
    int minLength6;
    @BindString(R.string.register_dialog_matching_password)
    String pwdMatching;

    @BindString(R.string.loading_msg)
    String loadingMsg;
    private RSLoader rsLoader;

    private void createLoader() {
        rsLoader = RSLoader.newInstance(loadingMsg);
        rsLoader.setCancelable(false);
    }

    private RSNavigationData rsNavigationData = new RSNavigationData();
    private View rootView;
    private Unbinder unbinder;
    private User user;
    private RSPresenter.RegisterPresenter registerPresenter;
    private static RegisterDialog instance;

    private TextWatcher pwdTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().trim().length() > 0) {
                if (s.toString().trim().length() < minLength6) {
                    passwordLayout.setError(minLength6Msg);
                } else {
                    passwordLayout.setErrorEnabled(false);
                }
            } else {
                passwordLayout.setError(requiredField);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    },
            confirmPwdTextWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.toString().trim().length() > 0) {
                        if (!s.toString().trim().equals(passwordEditText.getText().toString().trim())) {
                            confirmPasswordLayout.setError(pwdMatching);
                        } else {
                            confirmPasswordLayout.setErrorEnabled(false);
                        }
                    } else {
                        confirmPasswordLayout.setError(requiredField);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            };

    public static RegisterDialog newInstance(String email, RSNavigationData rsNavigationData) {

        if (instance == null) {
            instance = new RegisterDialog();
        }
        Bundle args = new Bundle();
        args.putString(RSConstants.EMAIL, email);
        args.putSerializable(RSConstants.NAVIGATION_DATA, rsNavigationData);
        instance.setArguments(args);
        return instance;
    }
    public static RegisterDialog getInstance(){
        return instance;
    }

    @Override
    public void onDestroy() { super.onDestroy(); instance = null;}

    public RegisterDialog getRegisterDialogInstance(){
        return instance ;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        initViews();
        rsNavigationData = (RSNavigationData) getArguments().getSerializable(RSConstants.NAVIGATION_DATA);
        final AlertDialog alertDialog = new AlertDialog.Builder(getContext()).setView(rootView).setCancelable(false).create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        alertDialog.setOnShowListener(dialog -> onDialogShow(alertDialog));
        return alertDialog;
    }

    private void initViews() {
        rootView = LayoutInflater.from(getContext()).inflate(R.layout.alert_dialog_register, null, false);
        unbinder = ButterKnife.bind(this, rootView);

        emailEditText.setText(getArguments().getString(RSConstants.EMAIL));

        addTextWatchers();
        createLoader();
    }

    private void onDialogShow(AlertDialog dialog) {

        dialog.getWindow().setLayout((int) getContext().getResources().getDimension(R.dimen.w_dialog_login), LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dialog_ask_login));

        registerPresenter = new PresenterAuthImpl(RegisterDialog.this);
    }

    private void addTextWatchers() {
        passwordEditText.addTextChangedListener(pwdTextWatcher);
        confirmPasswordEditText.addTextChangedListener(confirmPwdTextWatcher);
    }

    @Override
    public void registerSuccess(Object data) {
        passwordLayout.setErrorEnabled(false);
        passwordLayout.setError("");
        confirmPasswordLayout.setErrorEnabled(false);
        confirmPasswordLayout.setError("");

        RSResponseLogin loginResponse = new Gson().fromJson(new Gson().toJson(data), RSResponseLogin.class);
        String token = loginResponse.getToken();
        RSSession.startSession(token);

        if (rsNavigationData == null){

            rsNavigationData = new RSNavigationData();
            rsNavigationData.setFrom(RSConstants.FRAGMENT_HOME);
            rsLoader.dismiss();
            dismiss();
            ((ContainerActivity) getActivity()).manageSession(true, rsNavigationData);
        } else {


            if (rsNavigationData.getAction() != null) {
                if (rsNavigationData.getAction().equals(RSConstants.ACTION_FOLLOW)) {
                    registerPresenter.followItem(new RSFollow(RSSession.getCurrentUser().get_id(), rsNavigationData.getItemId()), RSConstants.REGISTER, getContext());
                } else {
                    if (rsNavigationData.getFrom() == null || rsNavigationData.getFrom() == "") {
                        rsNavigationData.setFrom(RSConstants.FRAGMENT_HOME);
                        rsLoader.dismiss();
                        dismiss();
                        ((ContainerActivity) getActivity()).manageSession(true, rsNavigationData);
                    } else
                    if (rsNavigationData.getFrom().equalsIgnoreCase(RSConstants.FRAGMENT_PROFILE)) {
                        rsNavigationData.setFrom(RSConstants.FRAGMENT_HOME);
                        dismiss();
                        rsLoader.dismiss();
                        ((ContainerActivity) getActivity()).manageSession(true, rsNavigationData);
                    } else {
                        dismiss();
                        rsLoader.dismiss();
                        ((ContainerActivity) getActivity()).manageSession(true, rsNavigationData);
                    }


                }
            } else {
                if (rsNavigationData.getFrom() == null || rsNavigationData.getFrom() == "") {
                    rsNavigationData.setFrom(RSConstants.FRAGMENT_HOME);
                }
                dismiss();
                rsLoader.dismiss();
                ((ContainerActivity) getActivity()).manageSession(true, rsNavigationData);
            }
        }


    }

    @Override
    public void registerError() {
        Toast.makeText(getContext(), registerFailedMsg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFollowSuccess(String target, Object data) {
        if (data.equals("1")) {
            Toast.makeText(getContext(), getResources().getString(R.string.follow), Toast.LENGTH_SHORT).show();
        } else if (data.equals("0")) {
            Toast.makeText(getContext(), getResources().getString(R.string.already_followed), Toast.LENGTH_SHORT).show();
        }
        dismiss();
        ((ContainerActivity) getActivity()).manageSession(true, rsNavigationData);
    }

    @Override
    public void onFollowFailure(String target) {
        dismiss();
        ((ContainerActivity) getActivity()).manageSession(true, rsNavigationData);
    }

    @Override
    public void showProgressBar(String target) {
        switch (target) {
            case RSConstants.PUBLIC_IP:
                rsLoader.show(getFragmentManager(), RSLoader.TAG);
                break;
        }
    }

    @Override
    public void hideProgressBar(String target) {
        switch (target) {
            case RSConstants.REGISTER:
                rsLoader.dismiss();
                break;
            case RSConstants.FOLLOW_ITEM:
                rsLoader.dismiss();
                break;
            case "":
                rsLoader.dismiss();
                break;
        }
    }

    @Override
    public void onAddressFetched(GeoPluginResponse response) {
        RSAddress address = new RSAddress();
        address.setCountry(new Country(response.getGeoplugin_countryCode(), response.getGeoplugin_countryName()));
        user.setLocation(address);
        registerPresenter.performRegister(user, getContext());
    }

    @Override
    public void onAddressFailed() {
        registerPresenter.performRegister(user, getContext());
    }

    @Override
    public void onPublicIPFetched(RSDeviceIP response) {
        RSDeviceIP rsDeviceIP = new Gson().fromJson(new Gson().toJson(response), RSDeviceIP.class);
        registerPresenter.getAddress(rsDeviceIP.getIp(), RSConstants.REGISTER, getContext());
    }

    @Override
    public void onPublicIPFailed() {
        registerPresenter.performRegister(user, getContext());
    }

    @Override
    public void onOffLine() {
        //Toast.makeText(getContext(), offLineMsg, Toast.LENGTH_LONG).show();
        new RSCustomToast(getActivity(), getResources().getString(R.string.error), offLineMsg, R.drawable.ic_error, RSCustomToast.ERROR).show();

    }

    @Override
    public void onDestroyView() {
        passwordEditText.removeTextChangedListener(pwdTextWatcher);
        confirmPasswordEditText.removeTextChangedListener(confirmPwdTextWatcher);
        if (unbinder != null)
            unbinder.unbind();
        instance = null;
        if (registerPresenter != null)
            registerPresenter.onDestroyRegister(getContext());
        super.onDestroyView();
    }
}
