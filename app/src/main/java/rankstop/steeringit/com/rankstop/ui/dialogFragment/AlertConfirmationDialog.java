package rankstop.steeringit.com.rankstop.ui.dialogFragment;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.customviews.RSBTNBold;
import rankstop.steeringit.com.rankstop.customviews.RSTVMedium;
import rankstop.steeringit.com.rankstop.ui.callbacks.DialogConfirmationListener;
import rankstop.steeringit.com.rankstop.utils.RSConstants;

public class AlertConfirmationDialog extends DialogFragment {

    public static String TAG = "ALERT_CONFIRMATION_DIALOG";

    private DialogConfirmationListener callback;

    private View rootView;
    private ColorStateList colorStateList;
    private LinearLayout.LayoutParams layoutParams;
    private String elementId;

    private Unbinder unbinder;

    @BindView(R.id.positive_btn)
    RSBTNBold loginBtn;

    @BindView(R.id.negative_btn)
    RSBTNBold cancelBtn;

    @BindView(R.id.tv_message)
    RSTVMedium messageTV;

    @OnClick(R.id.negative_btn)
    void cancel() {
        callback.onCancelClicked();
        dismiss();
    }

    @OnClick(R.id.positive_btn)
    void confirm() {
        callback.onConfirmClicked(elementId);
        dismiss();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        rootView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_alert_confirmation, null, false);
        unbinder = ButterKnife.bind(this, rootView);

        try {
            callback = (DialogConfirmationListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling Fragment must implement OnAddFriendListener");
        }

        final AlertDialog alertDialog = new AlertDialog.Builder(getContext()).setView(rootView).setCancelable(false).create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setOnShowListener(dialog -> onDialogShow(alertDialog));
        alertDialog.setOnDismissListener(dialog -> {
            rootView = null;
            colorStateList = null;
            layoutParams = null;
            callback = null;
            unbinder.unbind();
        });
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

        Bundle b = getArguments();
        elementId = b.getString(RSConstants._ID);
        messageTV.setText(b.getString(RSConstants.MESSAGE));
    }
}
