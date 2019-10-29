package rankstop.steeringit.com.rankstop.ui.dialogFragment;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.customviews.RSBTNBold;
import rankstop.steeringit.com.rankstop.customviews.RSTVMedium;
import rankstop.steeringit.com.rankstop.data.model.network.RSNavigationData;
import rankstop.steeringit.com.rankstop.ui.activities.ContainerActivity;
import rankstop.steeringit.com.rankstop.ui.callbacks.FragmentActionListener;
import rankstop.steeringit.com.rankstop.ui.fragments.SignupFragment;
import rankstop.steeringit.com.rankstop.utils.RSConstants;

public class AskToLoginDialog extends DialogFragment {


    @BindView(R.id.negative_btn)
    RSBTNBold cancelBtn;
    @BindView(R.id.positive_btn)
    RSBTNBold loginBtn;
    @BindView(R.id.tv_message)
    RSTVMedium messageTV;
    private ColorStateList colorStateList;
    private LinearLayout.LayoutParams layoutParams;
    private FragmentActionListener fragmentActionListener;
    private View rootView;
    private Unbinder unbinder;
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

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        rootView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_ask_for_login, null, false);
        unbinder = ButterKnife.bind(this, rootView);
        setFragmentActionListener((ContainerActivity) getActivity());
        final AlertDialog alertDialog = new AlertDialog.Builder(getContext()).setView(rootView).setCancelable(false).create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setOnShowListener(dialog -> onDialogShow(alertDialog));
        return alertDialog;
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
        RSNavigationData rsNavigationData = (RSNavigationData) getArguments().getSerializable(RSConstants.NAVIGATION_DATA);
        messageTV.setText(rsNavigationData.getMessage());
        cancelBtn.setOnClickListener(v -> dismiss());
        loginBtn.setOnClickListener(v -> {
            navigateToSignUp((RSNavigationData) getArguments().getSerializable(RSConstants.NAVIGATION_DATA));
            dismiss();
        });
    }

    @Override
    public void onDestroyView() {
        rootView = null;
        if (unbinder != null)
            unbinder.unbind();
        colorStateList = null;
        layoutParams = null;
        instance = null;
        super.onDestroyView();
    }

    private void setFragmentActionListener(FragmentActionListener fragmentActionListener) {
        this.fragmentActionListener = fragmentActionListener;
    }

    private void navigateToSignUp(RSNavigationData rsNavigationData) {
        fragmentActionListener.startFragment(SignupFragment.getInstance(rsNavigationData), RSConstants.FRAGMENT_SIGN_UP);
    }
}
