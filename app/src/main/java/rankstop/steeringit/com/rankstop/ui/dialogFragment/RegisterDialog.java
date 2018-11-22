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
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import rankstop.steeringit.com.rankstop.MVP.model.PresenterAuthImpl;
import rankstop.steeringit.com.rankstop.data.model.custom.RSResponseLogin;
import rankstop.steeringit.com.rankstop.ui.activities.ContainerActivity;
import rankstop.steeringit.com.rankstop.ui.fragments.SignupFragment;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.data.model.User;
import rankstop.steeringit.com.rankstop.MVP.presenter.RSPresenter;
import rankstop.steeringit.com.rankstop.session.RSSession;
import rankstop.steeringit.com.rankstop.MVP.view.RSView;

public class RegisterDialog extends DialogFragment  implements RSView.RegisterView{

    private TextInputLayout emailLayout;
    private TextInputLayout passwordLayout;
    private TextInputLayout confirmPasswordLayout;

    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;
    private TextInputEditText confirmPasswordEditText;

    private MaterialButton cancelBtn, registerBtn;

    private String password, confirmPassword;

    private View rootView;
    private SignupFragment fragment;

    private RSPresenter.RegisterPresenter registerPresenter;

    public static RegisterDialog newInstance(SignupFragment fragment, String email) {

        RegisterDialog dialog = new RegisterDialog();
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
                registerPresenter.onDestroyRegister();
            }
        });
        return alertDialog;
    }

    private void initViews() {
        rootView = LayoutInflater.from(getContext())
                .inflate(R.layout.alert_dialog_register, null, false);

        emailLayout = rootView.findViewById(R.id.input_layout_email);
        passwordLayout = rootView.findViewById(R.id.input_layout_password);
        confirmPasswordLayout = rootView.findViewById(R.id.input_layout_confirm_password);

        emailEditText = rootView.findViewById(R.id.input_email);
        passwordEditText = rootView.findViewById(R.id.input_password);
        confirmPasswordEditText = rootView.findViewById(R.id.input_confirm_password);

        cancelBtn = rootView.findViewById(R.id.negative_btn);
        registerBtn = rootView.findViewById(R.id.positive_btn);

        emailEditText.setText(getArguments().getString("email"));

        addTextWatchers();
    }

    private void onDialogShow(AlertDialog dialog) {

        registerPresenter = new PresenterAuthImpl( RegisterDialog.this);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidPassword(passwordLayout, password) && isValidPassword(confirmPasswordLayout, confirmPassword)) {
                    User user = new User();
                    user.setPassword(password);
                    user.setEmail(getArguments().getString("email"));
                    registerPresenter.performRegister(user);
                }
            }
        });
    }

    private boolean isValidPassword(TextInputLayout layout, String value) {
        layout.setErrorEnabled(false);
        layout.setError("");
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            passwordLayout.setErrorEnabled(true);
            passwordLayout.setError(getString(R.string.login_dialog_empty_password));
            return false;
        }

        if (password.length() >= 6 && !password.equalsIgnoreCase(confirmPassword)) {
            confirmPasswordLayout.setErrorEnabled(true);
            confirmPasswordLayout.setError(getString(R.string.register_dialog_matching_password));
            return false;
        }

        layout.setErrorEnabled(false);
        layout.setError("");
        return true;
    }

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
        confirmPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                confirmPassword = s.toString();
            }
        });
    }

    @Override
    public void registerValidations() {
        Toast.makeText(getContext(), "password invalid", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void registerSuccess(Object data) {
        passwordLayout.setErrorEnabled(false);
        passwordLayout.setError("");
        confirmPasswordLayout.setErrorEnabled(false);
        confirmPasswordLayout.setError("");

        RSResponseLogin loginResponse = new Gson().fromJson(new Gson().toJson(data), RSResponseLogin.class);
        String token = loginResponse.getToken();
        RSSession.startSession(getContext(), token);
        Toast.makeText(getContext(), "register success", Toast.LENGTH_SHORT).show();
        dismiss();
        ((ContainerActivity)getActivity()).manageSession(true);
    }

    @Override
    public void registerError() {
        Toast.makeText(getContext(), "register failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgressBar() {

    }

    @Override
    public void hideProgressBar() {

    }
}
