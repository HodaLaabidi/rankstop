package rankstop.steeringit.com.rankstop.ui.dialogFragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;

import com.google.gson.Gson;

import rankstop.steeringit.com.rankstop.MVP.model.PresenterUserImpl;
import rankstop.steeringit.com.rankstop.data.model.custom.RSResponseLogin;
import rankstop.steeringit.com.rankstop.ui.activities.ContainerActivity;
import rankstop.steeringit.com.rankstop.ui.fragments.SignupFragment;
import rankstop.steeringit.com.rankstop.data.model.User;
import rankstop.steeringit.com.rankstop.MVP.presenter.RSPresenter;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.session.RSSession;
import rankstop.steeringit.com.rankstop.MVP.view.RSView;
import rankstop.steeringit.com.rankstop.utils.RSJWTParser;

public class LoginDialog extends DialogFragment implements RSView.LoginView{

    private TextInputLayout emailLayout;
    private TextInputLayout passwordLayout;

    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;

    private MaterialButton cancelBtn, loginBtn, resetPassword;

    private String password;

    private View rootView;
    private SignupFragment fragment;

    private RSPresenter.LoginPresenter loginPresenter;

    public static LoginDialog newInstance(SignupFragment fragment, String email) {
        LoginDialog dialog = new LoginDialog();
        dialog.fragment = fragment;
        Bundle args = new Bundle();
        args.putString("email", email);
        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        initViews();

        final AlertDialog alertDialog = new AlertDialog.Builder(getContext()).setView(rootView).setCancelable(false).create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                onDialogShow(alertDialog);
            }
        });
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                loginPresenter.onDestroyLogin();
            }
        });
        return alertDialog;
    }

    private void initViews() {
        rootView = LayoutInflater.from(getContext())
                .inflate(R.layout.alert_dialog_login, null, false);

        emailLayout = rootView.findViewById(R.id.input_layout_email);
        passwordLayout = rootView.findViewById(R.id.input_layout_password);

        emailEditText = rootView.findViewById(R.id.input_email);
        passwordEditText = rootView.findViewById(R.id.input_password);

        cancelBtn = rootView.findViewById(R.id.negative_btn);
        loginBtn = rootView.findViewById(R.id.positive_btn);
        resetPassword = rootView.findViewById(R.id.forget_password_btn);

        emailEditText.setText(getArguments().getString("email"));

        addTextWatchers();
    }

    private void onDialogShow(AlertDialog dialog) {

        loginPresenter = new PresenterUserImpl( LoginDialog.this);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                passwordLayout.setErrorEnabled(false);
                passwordLayout.setError("");

                User user = new User();
                user.setPassword(password);
                user.setEmail(getArguments().getString("email"));
                loginPresenter.performLogin(user);
            }
        });
        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    /*private boolean isValidPassword(TextInputLayout layout, String value) {
        if (TextUtils.isEmpty(value) || value.length() < 6) {
            layout.setErrorEnabled(true);
            layout.setError(getString(R.string.login_dialog_empty_password));
            return false;
        }

        layout.setErrorEnabled(false);
        layout.setError("");
        return true;
    }*/

    private void addTextWatchers() {
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                password = s.toString();
            }
        });
    }

    @Override
    public void loginValidations() {
        passwordLayout.setErrorEnabled(true);
        passwordLayout.setError(getString(R.string.login_dialog_empty_password));
    }

    @Override
    public void loginSuccess(Object data) {
        RSResponseLogin loginResponse = new Gson().fromJson(new Gson().toJson(data), RSResponseLogin.class);
        String token = loginResponse.getToken();
        RSSession.startSession(getContext(), token);
        dismiss();
        ((ContainerActivity)getActivity()).manageSession(true);
    }

    @Override
    public void loginError() {
        passwordLayout.setErrorEnabled(true);
        passwordLayout.setError(getString(R.string.login_dialog_invalid_password));
    }

    @Override
    public void showProgressBar() {

    }

    @Override
    public void hideProgressBar() {

    }
}
