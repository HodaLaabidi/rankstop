package rankstop.steeringit.com.rankstop.ui.dialogFragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.customviews.RSTVMedium;
import rankstop.steeringit.com.rankstop.utils.RSConstants;

public class RSLoader extends DialogFragment {

    public static final String TAG = "RS_LOADER";
    private View rootView;
    private Unbinder unbinder;
    private static RSLoader instance;
    private String message;

    @BindView(R.id.et_message)
    RSTVMedium messageET;

    public static RSLoader newInstance(String message) {
        if (instance == null) {
            instance = new RSLoader();
        }
        //instance.fragment = fragment;
        Bundle args = new Bundle();
        args.putString(RSConstants.MESSAGE, message);
        instance.setArguments(args);
        return instance;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        message = getArguments().getString(RSConstants.MESSAGE);
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
        rootView = LayoutInflater.from(getContext()).inflate(R.layout.alert_dialog_rs_loader, null, false);
        unbinder = ButterKnife.bind(this, rootView);
        messageET.setText(message);
    }

    private void onDialogShow(AlertDialog dialog) {

        dialog.getWindow().setLayout((int) getContext().getResources().getDimension(R.dimen.w_dialog_login), LinearLayout.LayoutParams.WRAP_CONTENT);
        //dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dialog_ask_login));
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        instance = null;
        super.onDestroyView();
    }

}
