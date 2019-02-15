package rankstop.steeringit.com.rankstop.ui.dialogFragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
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
import rankstop.steeringit.com.rankstop.MVP.model.PresenterAuthImpl;
import rankstop.steeringit.com.rankstop.data.model.db.Country;
import rankstop.steeringit.com.rankstop.data.model.db.RSAddress;
import rankstop.steeringit.com.rankstop.data.model.network.GeoPluginResponse;
import rankstop.steeringit.com.rankstop.data.model.network.RSDeviceIP;
import rankstop.steeringit.com.rankstop.data.model.network.RSFollow;
import rankstop.steeringit.com.rankstop.data.model.network.RSNavigationData;
import rankstop.steeringit.com.rankstop.data.model.network.RSResponseLogin;
import rankstop.steeringit.com.rankstop.ui.activities.ContainerActivity;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.data.model.db.User;
import rankstop.steeringit.com.rankstop.MVP.presenter.RSPresenter;
import rankstop.steeringit.com.rankstop.session.RSSession;
import rankstop.steeringit.com.rankstop.MVP.view.RSView;
import rankstop.steeringit.com.rankstop.utils.RSConstants;
import rankstop.steeringit.com.rankstop.utils.RSNetwork;

public class RegisterDialog extends DialogFragment implements RSView.RegisterView {

    @BindView(R.id.input_layout_password)
    TextInputLayout passwordLayout;
    @BindView(R.id.input_password)
    TextInputEditText passwordEditText;

    @BindView(R.id.input_layout_confirm_password)
    TextInputLayout confirmPasswordLayout;
    @BindView(R.id.input_confirm_password)
    TextInputEditText confirmPasswordEditText;

    @BindView(R.id.input_email)
    TextInputEditText emailEditText;
    @BindString(R.string.off_line)
    String offLineMsg;

    @OnClick(R.id.negative_btn)
    void cancelDialog() {
        dismiss();
    }

    @OnClick(R.id.positive_btn)
    void onClick(View v) {
        if (validForm(passwordEditText.getText().toString().trim(), confirmPasswordEditText.getText().toString().trim())) {
            if (RSNetwork.isConnected()) {
                user = new User();
                user.setPassword(passwordEditText.getText().toString().trim());
                user.setEmail(getArguments().getString(RSConstants.EMAIL));
                registerPresenter.getPublicIP("json", RSConstants.REGISTER);
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

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        initViews();
        rsNavigationData = (RSNavigationData) getArguments().getSerializable(RSConstants.NAVIGATION_DATA);
        final AlertDialog alertDialog = new AlertDialog.Builder(getContext()).setView(rootView).setCancelable(false).create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                onDialogShow(alertDialog);
            }
        });
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
        Log.i("TAG_REGISTER", "" + token);
        RSSession.startSession(token);
        //Toast.makeText(getContext(), "register success", Toast.LENGTH_SHORT).show();

        if (rsNavigationData.getAction().equals(RSConstants.ACTION_FOLLOW)) {
            registerPresenter.followItem(new RSFollow(RSSession.getCurrentUser().get_id(), rsNavigationData.getItemId()), RSConstants.REGISTER);
        } else {
            dismiss();
            rsLoader.dismiss();
            ((ContainerActivity) getActivity()).manageSession(true, rsNavigationData);
        }
    }

    @Override
    public void registerError() {
        Toast.makeText(getContext(), "register failed", Toast.LENGTH_SHORT).show();
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
        }
    }

    @Override
    public void onAddressFetched(GeoPluginResponse response) {
        RSAddress address = new RSAddress();
        address.setCountry(new Country(response.getGeoplugin_countryCode(), response.getGeoplugin_countryName()));
        user.setLocation(address);
        registerPresenter.performRegister(user);
    }

    @Override
    public void onAddressFailed() {
        registerPresenter.performRegister(user);
    }

    @Override
    public void onPublicIPFetched(RSDeviceIP response) {
        RSDeviceIP rsDeviceIP = new Gson().fromJson(new Gson().toJson(response), RSDeviceIP.class);
        registerPresenter.getAddress(rsDeviceIP.getIp(), RSConstants.REGISTER);
    }

    @Override
    public void onPublicIPFailed() {
        registerPresenter.performRegister(user);
    }

    @Override
    public void onOffLine() {
        Toast.makeText(getContext(), offLineMsg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroyView() {
        passwordEditText.removeTextChangedListener(pwdTextWatcher);
        confirmPasswordEditText.removeTextChangedListener(confirmPwdTextWatcher);
        if (unbinder != null)
            unbinder.unbind();
        instance = null;
        if (registerPresenter != null)
            registerPresenter.onDestroyRegister();
        super.onDestroyView();
    }
}
