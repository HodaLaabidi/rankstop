package rankstop.steeringit.com.rankstop.ui.dialogFragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.ui.fragments.HomeFragment;
import rankstop.steeringit.com.rankstop.ui.fragments.ItemDetailsFragment;
import rankstop.steeringit.com.rankstop.ui.fragments.MyEvaluationsFragment;
import rankstop.steeringit.com.rankstop.ui.fragments.ProfileFragment;
import rankstop.steeringit.com.rankstop.utils.RSConstants;

public class AskToLoginDialog extends DialogFragment{

    private View rootView;
    private MaterialButton cancelBtn, loginBtn;
    private TextView messageTV;
    private ColorStateList colorStateList;
    private LinearLayout.LayoutParams layoutParams;
    private ItemDetailsFragment itemDetailsFragment;
    private ProfileFragment profileFragment;
    private HomeFragment homeFragment;
    private MyEvaluationsFragment myEvaluationsFragment;

    public static AskToLoginDialog newInstance(ItemDetailsFragment fragment, String message) {
        AskToLoginDialog dialog = new AskToLoginDialog();
        dialog.itemDetailsFragment = fragment;
        Bundle args = new Bundle();
        args.putString(RSConstants.MESSAGE_TO_LOGIN, message);
        dialog.setArguments(args);
        return dialog;
    }
    public static AskToLoginDialog newInstance(ProfileFragment fragment, String message) {
        AskToLoginDialog dialog = new AskToLoginDialog();
        dialog.profileFragment = fragment;
        Bundle args = new Bundle();
        args.putString(RSConstants.MESSAGE_TO_LOGIN, message);
        dialog.setArguments(args);
        return dialog;
    }
    public static AskToLoginDialog newInstance(HomeFragment fragment, String message) {
        AskToLoginDialog dialog = new AskToLoginDialog();
        dialog.homeFragment = fragment;
        Bundle args = new Bundle();
        args.putString(RSConstants.MESSAGE_TO_LOGIN, message);
        dialog.setArguments(args);
        return dialog;
    }
    public static AskToLoginDialog newInstance(MyEvaluationsFragment fragment, String message) {
        AskToLoginDialog dialog = new AskToLoginDialog();
        dialog.myEvaluationsFragment = fragment;
        Bundle args = new Bundle();
        args.putString(RSConstants.MESSAGE_TO_LOGIN, message);
        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        initViews();

        final AlertDialog alertDialog = new AlertDialog.Builder(getContext()).setView(rootView).setCancelable(false).create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                onDialogShow(alertDialog);
            }
        });
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                rootView = null;
                colorStateList = null;
                layoutParams = null;
            }
        });
        return alertDialog;
    }

    private void initViews() {
        rootView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_ask_for_login, null, false);

        cancelBtn = rootView.findViewById(R.id.negative_btn);
        loginBtn = rootView.findViewById(R.id.positive_btn);
        messageTV = rootView.findViewById(R.id.tv_message);
    }

    private void onDialogShow(AlertDialog dialog) {

        dialog.getWindow().setLayout((int)getContext().getResources().getDimension(R.dimen.w_dialog_ask_login), LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dialog_ask_login));
        colorStateList = new ColorStateList(new int[][] {{0}}, new int[] {getResources().getColor(R.color.colorGray)}); // 0xAARRGGBB
        cancelBtn.setBackgroundTintList(colorStateList);

        if (loginBtn.getWidth() > cancelBtn.getWidth()){
            layoutParams = new LinearLayout.LayoutParams(loginBtn.getWidth(),ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMarginEnd(10);
            cancelBtn.setLayoutParams(layoutParams);
        }else if (loginBtn.getWidth() < cancelBtn.getWidth()) {
            layoutParams = new LinearLayout.LayoutParams(cancelBtn.getWidth(),ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMarginStart(10);
            loginBtn.setLayoutParams(layoutParams);
        }

        messageTV.setText(getArguments().getString(RSConstants.MESSAGE_TO_LOGIN));

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
