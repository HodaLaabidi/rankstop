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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import rankstop.steeringit.com.rankstop.MVP.model.PresenterAuthImpl;
import rankstop.steeringit.com.rankstop.data.model.network.GeoPluginResponse;
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

public class RegisterDialog extends DialogFragment  implements RSView.RegisterView{

    private TextInputLayout emailLayout;
    private TextInputLayout passwordLayout;
    private TextInputLayout confirmPasswordLayout;

    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;
    private TextInputEditText confirmPasswordEditText;

    private MaterialButton cancelBtn, registerBtn;

    private String password, confirmPassword;
    private RSNavigationData rsNavigationData = new RSNavigationData();

    private View rootView;

    private User user;

    private RSPresenter.RegisterPresenter registerPresenter;
    private static RegisterDialog instance;

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
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                instance = null;
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

        emailEditText.setText(getArguments().getString(RSConstants.EMAIL));

        addTextWatchers();
    }

    private void onDialogShow(AlertDialog dialog) {

        dialog.getWindow().setLayout((int) getContext().getResources().getDimension(R.dimen.w_dialog_login), LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dialog_ask_login));

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
                    user = new User();
                    user.setPassword(password);
                    user.setEmail(getArguments().getString(RSConstants.EMAIL));


                    Log.i("TAG_RESPONSE_IP",""+getLocalIpAddress());
                    //registerPresenter.getAddress(ip);
                }
            }
        });
    }

    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
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
        Log.i("TAG_REGISTER",""+token);
        RSSession.startSession(getContext(), token);
        //Toast.makeText(getContext(), "register success", Toast.LENGTH_SHORT).show();

        if (rsNavigationData.getAction().equals(RSConstants.ACTION_FOLLOW)) {
            registerPresenter.followItem(new RSFollow(RSSession.getCurrentUser(getContext()).get_id(), rsNavigationData.getItemId()), RSConstants.REGISTER);
        }else {
            dismiss();
            ((ContainerActivity)getActivity()).manageSession(true, rsNavigationData);
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

    @Override
    public void onAddressFetched(GeoPluginResponse response) {
        //RSAddress address = new RSAddress();
        //address.setCountry(new Country(response.getGeoplugin_countryCode(), response.getGeoplugin_countryName()));
        //user.setLocation(address);
        //registerPresenter.performRegister(user);
    }
}
