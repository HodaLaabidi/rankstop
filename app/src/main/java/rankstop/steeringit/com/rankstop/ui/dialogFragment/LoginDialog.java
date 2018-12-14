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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;

import rankstop.steeringit.com.rankstop.MVP.model.PresenterAuthImpl;
import rankstop.steeringit.com.rankstop.data.model.custom.RSFollow;
import rankstop.steeringit.com.rankstop.data.model.custom.RSNavigationData;
import rankstop.steeringit.com.rankstop.data.model.custom.RSResponseLogin;
import rankstop.steeringit.com.rankstop.ui.activities.ContainerActivity;
import rankstop.steeringit.com.rankstop.ui.fragments.SignupFragment;
import rankstop.steeringit.com.rankstop.data.model.User;
import rankstop.steeringit.com.rankstop.MVP.presenter.RSPresenter;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.session.RSSession;
import rankstop.steeringit.com.rankstop.MVP.view.RSView;
import rankstop.steeringit.com.rankstop.utils.RSConstants;

public class LoginDialog extends DialogFragment implements RSView.LoginView{

    private TextInputLayout emailLayout;
    private TextInputLayout passwordLayout;

    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;

    private MaterialButton cancelBtn, loginBtn, resetPassword;

    private String password;
    private RSNavigationData rsNavigationData;

    private View rootView;
    //private SignupFragment fragment;

    private RSPresenter.LoginPresenter loginPresenter;

    private static LoginDialog instance;

    public static LoginDialog newInstance(String email, RSNavigationData rsNavigationData) {
        if (instance == null) {
            instance = new LoginDialog();
        }
        //instance.fragment = fragment;
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
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                instance = null;
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

        emailEditText.setText(getArguments().getString(RSConstants.EMAIL));

        addTextWatchers();
    }

    private void onDialogShow(AlertDialog dialog) {

        dialog.getWindow().setLayout((int) getContext().getResources().getDimension(R.dimen.w_dialog_login), LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dialog_ask_login));

        loginPresenter = new PresenterAuthImpl( LoginDialog.this);

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
                user.setEmail(getArguments().getString(RSConstants.EMAIL));
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
        if (rsNavigationData.getAction().equals(RSConstants.ACTION_FOLLOW)) {
            loginPresenter.followItem(new RSFollow(RSSession.getCurrentUser(getContext()).get_id(), rsNavigationData.getItemId()), RSConstants.LOGIN);
        }else {
            dismiss();
            ((ContainerActivity)getActivity()).manageSession(true, rsNavigationData);
        }
    }

    @Override
    public void loginError() {
        passwordLayout.setErrorEnabled(true);
        passwordLayout.setError(getString(R.string.login_dialog_invalid_password));
    }

    @Override
    public void onFollowSuccess(String target, Object data) {
        if (data.equals("1")) {
            Toast.makeText(getContext(), getResources().getString(R.string.follow), Toast.LENGTH_SHORT).show();
        } else if (data.equals("0")) {
            Toast.makeText(getContext(), getResources().getString(R.string.already_followed), Toast.LENGTH_SHORT).show();
        }
        dismiss();
        ((ContainerActivity)getActivity()).manageSession(true, rsNavigationData);
    }

    @Override
    public void onFollowFailure(String target) {
        dismiss();
        ((ContainerActivity)getActivity()).manageSession(true, rsNavigationData);
    }

    @Override
    public void showProgressBar() {

    }

    @Override
    public void hideProgressBar() {

    }
}
