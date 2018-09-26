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

import rankstop.steeringit.com.rankstop.R;

public class SignupFragment extends Fragment {

    final String TAG = "SIGNUP FRAGMENT";

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
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.alert_dialog_login, null);
        ((TextInputEditText) dialogLayout.findViewById(R.id.input_email)).setText(email);
        //Toast.makeText(getContext(), ""+((TextInputEditText)dialogLayout.findViewById(R.id.input_password)).getText().toString(), Toast.LENGTH_SHORT).show();
        builder.setView(dialogLayout);
        builder.setCancelable(false);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        ((MaterialButton)dialogLayout.findViewById(R.id.negative_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "hide dialog from login", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        });
        ((MaterialButton)dialogLayout.findViewById(R.id.positive_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void dialogRegister(String email) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.alert_dialog_register, null);
        ((TextInputEditText) dialogLayout.findViewById(R.id.input_email)).setText(email);
        builder.setView(dialogLayout);
        builder.setCancelable(false);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        ((MaterialButton)dialogLayout.findViewById(R.id.negative_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "hide dialog from register", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        });
        ((MaterialButton)dialogLayout.findViewById(R.id.positive_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
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
