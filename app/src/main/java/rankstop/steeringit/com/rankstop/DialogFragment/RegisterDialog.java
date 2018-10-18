package rankstop.steeringit.com.rankstop.DialogFragment;

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

import rankstop.steeringit.com.rankstop.Fragments.SignupFragment;
import rankstop.steeringit.com.rankstop.R;

public class RegisterDialog extends DialogFragment {

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
                    if (fragment.isRegisteredSuccess(password)) {
                        passwordLayout.setErrorEnabled(false);
                        passwordLayout.setError("");
                        confirmPasswordLayout.setErrorEnabled(false);
                        confirmPasswordLayout.setError("");
                        Toast.makeText(getContext(), "register success", Toast.LENGTH_SHORT).show();
                        dismiss();
                    } else {
                        Toast.makeText(getContext(), "register failed", Toast.LENGTH_SHORT).show();
                    }
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

}
