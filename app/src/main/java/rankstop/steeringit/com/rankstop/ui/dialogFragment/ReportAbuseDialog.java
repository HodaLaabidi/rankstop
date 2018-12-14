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
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

import rankstop.steeringit.com.rankstop.MVP.model.PresenterAbuseImpl;
import rankstop.steeringit.com.rankstop.MVP.presenter.RSPresenter;
import rankstop.steeringit.com.rankstop.MVP.view.RSView;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.data.model.Abuse;
import rankstop.steeringit.com.rankstop.data.model.custom.RSNavigationData;
import rankstop.steeringit.com.rankstop.data.model.custom.RSRequestReportAbuse;
import rankstop.steeringit.com.rankstop.session.RSSession;
import rankstop.steeringit.com.rankstop.ui.fragments.ItemDetailsFragment;
import rankstop.steeringit.com.rankstop.utils.RSConstants;

public class ReportAbuseDialog extends DialogFragment implements RSView.AbuseView {

    private View rootView;
    private MaterialButton cancelBtn, reportAbuseBtn;
    private TextView serverFeedbackTV;
    private NestedScrollView scrollView;
    private ProgressBar progressBar;
    private RadioGroup abusesRG;
    private ColorStateList colorStateList;
    private LinearLayout.LayoutParams layoutParams;
    private RSPresenter.abusePresenter abusePresenter;
    private List<Abuse> abuseList;
    private String itemId, abuseId;
    private static ReportAbuseDialog instance;

    public static ReportAbuseDialog newInstance(RSNavigationData rsNavigationData) {
        if (instance == null) {
            instance = new ReportAbuseDialog();
        }
        Bundle args = new Bundle();
        args.putSerializable(RSConstants.NAVIGATION_DATA, rsNavigationData);
        instance.setArguments(args);
        return instance;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        rootView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_report_abuse, null, false);
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
                abusesRG=null;
                instance = null;
                abusePresenter.onDestroy();
            }
        });
        return alertDialog;
    }

    private void initViews() {
        cancelBtn = rootView.findViewById(R.id.negative_btn);
        reportAbuseBtn = rootView.findViewById(R.id.positive_btn);
        progressBar = rootView.findViewById(R.id.progress_bar);
        abusesRG = rootView.findViewById(R.id.rg_abuses);
        serverFeedbackTV = rootView.findViewById(R.id.tv_server_feedback);
        scrollView = rootView.findViewById(R.id.scroll_view);
        scrollView.setSmoothScrollingEnabled(true);
        abusePresenter= new PresenterAbuseImpl(ReportAbuseDialog.this);
    }

    private void onDialogShow(AlertDialog dialog) {

        dialog.getWindow().setLayout((int) getContext().getResources().getDimension(R.dimen.w_dialog_ask_login), LinearLayout.LayoutParams.WRAP_CONTENT);

        dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dialog_ask_login));


        colorStateList = new ColorStateList(new int[][]{{0}}, new int[]{getResources().getColor(R.color.colorGray)}); // 0xAARRGGBB
        cancelBtn.setBackgroundTintList(colorStateList);

        if (reportAbuseBtn.getWidth() > cancelBtn.getWidth()) {
            layoutParams = new LinearLayout.LayoutParams(reportAbuseBtn.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMarginEnd((int) getResources().getDimension(R.dimen.margin_end_btn_dialog));
            cancelBtn.setLayoutParams(layoutParams);
        } else if (reportAbuseBtn.getWidth() < cancelBtn.getWidth()) {
            layoutParams = new LinearLayout.LayoutParams(cancelBtn.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMarginStart((int) getResources().getDimension(R.dimen.margin_end_btn_dialog));
            reportAbuseBtn.setLayoutParams(layoutParams);
        }

        RSNavigationData rsNavigationData  = (RSNavigationData) getArguments().getSerializable(RSConstants.NAVIGATION_DATA);
        itemId = rsNavigationData.getItemId();

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abusePresenter.onCancelClick();
            }
        });
        reportAbuseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abusePresenter.onOkClick();
            }
        });

        abusesRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                reportAbuseBtn.setEnabled(true);
                abuseId = abuseList.get(abusesRG.indexOfChild(group.findViewById(checkedId))).get_id();
            }
        });
        loadAbuseList();
    }

    private void loadAbuseList() {
        abusePresenter.loadAbusesList("EN");
    }

    private void initAbusesList(List<Abuse> abuseList) {
        try {
            for (Abuse abuse: abuseList) {
                RadioButton rb = new RadioButton(getContext());
                rb.setText(abuse.getName());
                abusesRG.addView(rb);
            }
        }catch (Exception e){
            displayServerFeedBack("No data");
        }
    }

    private void displayServerFeedBack(String message) {
        serverFeedbackTV.setText(message);
        serverFeedbackTV.setVisibility(View.VISIBLE);
    }

    @Override
    public void onReportClicked() {
        abusePresenter.reportAbuse(new RSRequestReportAbuse(RSSession.getCurrentUser(getContext()).get_id(), itemId, abuseId));
    }

    @Override
    public void onDialogCanceled() {
        dismiss();
    }

    @Override
    public void onSuccess(String target, Object data) {
        switch (target){
            case RSConstants.LOAD_ABUSES_LIST:
                abuseList = Arrays.asList(new Gson().fromJson(new Gson().toJson(data), Abuse[].class));
                initAbusesList(abuseList);
                break;
            case RSConstants.REPORT_ABUSES:
                Toast.makeText(getContext(), "done", Toast.LENGTH_SHORT).show();
                dismiss();
                break;
        }
    }

    @Override
    public void onFailure(String target) {
        switch (target){
            case RSConstants.LOAD_ABUSES_LIST:
                displayServerFeedBack("No data");
                break;
            case RSConstants.REPORT_ABUSES:
                break;
        }
    }

    @Override
    public void showProgressBar(String target) {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar(String target) {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showMessage(String target, String message) {

    }
}
