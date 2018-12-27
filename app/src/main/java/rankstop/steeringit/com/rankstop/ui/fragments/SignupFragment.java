package rankstop.steeringit.com.rankstop.ui.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;

import java.lang.ref.WeakReference;

import rankstop.steeringit.com.rankstop.MVP.model.PresenterAuthImpl;
import rankstop.steeringit.com.rankstop.customviews.RSMBMontserratBold;
import rankstop.steeringit.com.rankstop.data.model.custom.RSNavigationData;
import rankstop.steeringit.com.rankstop.data.model.custom.RSResponseFindEmail;
import rankstop.steeringit.com.rankstop.ui.dialogFragment.LoginDialog;
import rankstop.steeringit.com.rankstop.ui.dialogFragment.RegisterDialog;
import rankstop.steeringit.com.rankstop.MVP.presenter.RSPresenter;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.MVP.view.RSView;
import rankstop.steeringit.com.rankstop.utils.RSConstants;

public class SignupFragment extends Fragment implements RSView.SignupView {

    private final String TAG = "SIGNUP FRAGMENT";
    private final String LOGIN_DIALOG_TAG = "game_dialog_tag";
    private final String REGISTER_DIALOG_TAG = "game_dialog_tag";

    private RSMBMontserratBold loginBtn;
    private View rootView;

    private RSPresenter.SignupPresenter signupPresenter;

    private TextInputLayout inputLayoutEmail;

    private WeakReference<SignupFragment> fragmentContext;
    private RSNavigationData rsNavigationData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentContext = new WeakReference<SignupFragment>(this);
        rootView = inflater.inflate(R.layout.fragment_signup, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        rsNavigationData = (RSNavigationData) getArguments().getSerializable(RSConstants.NAVIGATION_DATA);

        loginBtn = rootView.findViewById(R.id.rs_login_btn);

        inputLayoutEmail = rootView.findViewById(R.id.input_layout_email);

        signupPresenter = new PresenterAuthImpl(fragmentContext.get());

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputLayoutEmail.setErrorEnabled(false);
                inputLayoutEmail.setError("");
                signupPresenter.performFindEmail(((TextInputEditText) rootView.findViewById(R.id.input_email)).getText().toString().trim());
            }
        });
    }

    public void dialogLogin(String email) {
        LoginDialog dialog = LoginDialog.newInstance(((TextInputEditText) rootView.findViewById(R.id.input_email)).getText().toString().trim(), rsNavigationData);
        dialog.setCancelable(false);
        dialog.show(getFragmentManager(), LOGIN_DIALOG_TAG);
    }

    public void dialogRegister(String email) {
        RegisterDialog dialog = RegisterDialog.newInstance(((TextInputEditText) rootView.findViewById(R.id.input_email)).getText().toString().trim(), rsNavigationData);
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
                dialogLogin(((TextInputEditText) rootView.findViewById(R.id.input_email)).getText().toString().trim());
            }
        } else {
            dialogRegister(((TextInputEditText) rootView.findViewById(R.id.input_email)).getText().toString().trim());
        }
    }

    @Override
    public void findEmailError() {

    }

    @Override
    public void showProgressBar() {
        dialog = new ProgressDialog(getContext());
        dialog.setMessage("loading ...");
        dialog.show();
    }

    @Override
    public void hideProgressBar() {
        dialog.dismiss();
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(getContext(), "" + message, Toast.LENGTH_LONG).show();
    }

    ProgressDialog dialog;
}
