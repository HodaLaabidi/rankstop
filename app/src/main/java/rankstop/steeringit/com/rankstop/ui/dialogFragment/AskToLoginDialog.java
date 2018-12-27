package rankstop.steeringit.com.rankstop.ui.dialogFragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.customviews.RSMBMontserratBold;
import rankstop.steeringit.com.rankstop.customviews.RSTVMontserratMedium;
import rankstop.steeringit.com.rankstop.data.model.custom.RSNavigationData;
import rankstop.steeringit.com.rankstop.ui.activities.ContainerActivity;
import rankstop.steeringit.com.rankstop.ui.callbacks.FragmentActionListener;
import rankstop.steeringit.com.rankstop.ui.fragments.HomeFragment;
import rankstop.steeringit.com.rankstop.ui.fragments.ItemDetailsFragment;
import rankstop.steeringit.com.rankstop.ui.fragments.MyEvaluationsFragment;
import rankstop.steeringit.com.rankstop.ui.fragments.SignupFragment;
import rankstop.steeringit.com.rankstop.utils.RSConstants;

public class AskToLoginDialog extends DialogFragment {

    private View rootView;
    private RSMBMontserratBold cancelBtn, loginBtn;
    private RSTVMontserratMedium messageTV;
    private ColorStateList colorStateList;
    private LinearLayout.LayoutParams layoutParams;
    private FragmentActionListener fragmentActionListener;

    private static AskToLoginDialog instance;

    public static AskToLoginDialog newInstance(RSNavigationData rsNavigationData) {
        if (instance == null) {
            instance = new AskToLoginDialog();
        }
        Bundle args = new Bundle();
        args.putSerializable(RSConstants.NAVIGATION_DATA, rsNavigationData);
        instance.setArguments(args);
        return instance;
    }

    /*public static AskToLoginDialog newInstance(RSNavigationData rsNavigationData) {
        if (instance == null) {
            instance = new AskToLoginDialog();
        }
        Bundle args = new Bundle();
        args.putSerializable(RSConstants.NAVIGATION_DATA, rsNavigationData);
        instance.setArguments(args);
        return instance;
    }

    public static AskToLoginDialog newInstance(RSNavigationData rsNavigationData) {
        if (instance == null) {
            instance = new AskToLoginDialog();
        }
        Bundle args = new Bundle();
        args.putSerializable(RSConstants.NAVIGATION_DATA, rsNavigationData);
        instance.setArguments(args);
        return instance;
    }*/

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        rootView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_ask_for_login, null, false);
        initViews();
        setFragmentActionListener((ContainerActivity)getActivity());

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
                instance = null;
            }
        });
        return alertDialog;
    }

    private void initViews() {
        cancelBtn = rootView.findViewById(R.id.negative_btn);
        loginBtn = rootView.findViewById(R.id.positive_btn);
        messageTV = rootView.findViewById(R.id.tv_message);
    }

    private void onDialogShow(AlertDialog dialog) {

        dialog.getWindow().setLayout((int) getContext().getResources().getDimension(R.dimen.w_dialog_ask_login), LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dialog_ask_login));
        colorStateList = new ColorStateList(new int[][]{{0}}, new int[]{getResources().getColor(R.color.colorGray)}); // 0xAARRGGBB
        cancelBtn.setBackgroundTintList(colorStateList);

        if (loginBtn.getWidth() > cancelBtn.getWidth()) {
            layoutParams = new LinearLayout.LayoutParams(loginBtn.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMarginEnd((int) getResources().getDimension(R.dimen.margin_end_btn_dialog));
            cancelBtn.setLayoutParams(layoutParams);
        } else if (loginBtn.getWidth() < cancelBtn.getWidth()) {
            layoutParams = new LinearLayout.LayoutParams(cancelBtn.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMarginStart((int) getResources().getDimension(R.dimen.margin_end_btn_dialog));
            loginBtn.setLayoutParams(layoutParams);
        }

        RSNavigationData rsNavigationData  = (RSNavigationData) getArguments().getSerializable(RSConstants.NAVIGATION_DATA);

        messageTV.setText(rsNavigationData.getMessage());

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToSignUp((RSNavigationData) getArguments().getSerializable(RSConstants.NAVIGATION_DATA));
                dismiss();
            }
        });
    }

    public void setFragmentActionListener(FragmentActionListener fragmentActionListener) {
        this.fragmentActionListener = fragmentActionListener;
    }

    private void navigateToSignUp(RSNavigationData rsNavigationData) {
        fragmentActionListener.startFragment(SignupFragment.getInstance(rsNavigationData), RSConstants.FRAGMENT_SIGN_UP);
    }
}
