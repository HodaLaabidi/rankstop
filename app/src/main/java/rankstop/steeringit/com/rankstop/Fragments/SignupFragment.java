package rankstop.steeringit.com.rankstop.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import rankstop.steeringit.com.rankstop.DialogFragment.LoginDialog;
import rankstop.steeringit.com.rankstop.DialogFragment.RegisterDialog;
import rankstop.steeringit.com.rankstop.R;

public class SignupFragment extends Fragment {

    private static final String TAG = "SIGNUP FRAGMENT";
    private static final String LOGIN_DIALOG_TAG = "game_dialog_tag";
    private static final String REGISTER_DIALOG_TAG = "game_dialog_tag";

    private MaterialButton loginBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("LIFE_CYCLE", "" + TAG + " onCreateView");
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i("LIFE_CYCLE", "" + TAG + " onAttach");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("LIFE_CYCLE", "" + TAG + " onCreate");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i("LIFE_CYCLE", "" + TAG + " onActivityCreated");

        loginBtn = getActivity().findViewById(R.id.rs_login_btn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((TextInputEditText)getActivity().findViewById(R.id.input_email)).getText().toString().trim().equals("login"))
                    dialogLogin(((TextInputEditText)getActivity().findViewById(R.id.input_email)).getText().toString().trim());
                else
                    dialogRegister(((TextInputEditText) getActivity().findViewById(R.id.input_email)).getText().toString().trim());
            }
        });
    }

    public void dialogLogin(String email) {
        LoginDialog dialog = LoginDialog.newInstance(this, ((TextInputEditText)getActivity().findViewById(R.id.input_email)).getText().toString().trim());
        dialog.setCancelable(false);
        dialog.show(getFragmentManager(), LOGIN_DIALOG_TAG);
    }

    public void dialogRegister(String email) {
        RegisterDialog dialog = RegisterDialog.newInstance(this, ((TextInputEditText)getActivity().findViewById(R.id.input_email)).getText().toString().trim());
        dialog.setCancelable(false);
        dialog.show(getFragmentManager(), REGISTER_DIALOG_TAG);
    }

    public boolean isLoginSuccess(String password){
        return password.equals("azerty");
    }
    public boolean isRegisteredSuccess(String password) {
        return password.equals("azerty");
    }





    @Override
    public void onStart() {
        super.onStart();
        Log.i("LIFE_CYCLE", "" + TAG + " onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("LIFE_CYCLE", "" + TAG + " onResume");
    }

    @Override
    public void onPause() {
        Log.i("LIFE_CYCLE", "" + TAG + " onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.i("LIFE_CYCLE", "" + TAG + " onStop");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Log.i("LIFE_CYCLE", "" + TAG + " onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.i("LIFE_CYCLE", "" + TAG + " onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.i("LIFE_CYCLE", "" + TAG + " onDetach");
        super.onDetach();
    }
}
