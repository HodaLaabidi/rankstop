package com.steeringit.rankstop.ui.dialogFragment;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.steeringit.rankstop.MVP.model.PresenterAbuseImpl;
import com.steeringit.rankstop.MVP.presenter.RSPresenter;
import com.steeringit.rankstop.MVP.view.RSView;
import com.steeringit.rankstop.R;
import com.steeringit.rankstop.RankStop;
import com.steeringit.rankstop.customviews.RSCustomToast;
import com.steeringit.rankstop.customviews.RSRBMedium;
import com.steeringit.rankstop.customviews.RSTVRegular;
import com.steeringit.rankstop.data.model.db.Abuse;
import com.steeringit.rankstop.data.model.network.RSNavigationData;
import com.steeringit.rankstop.data.model.network.RSRequestReportAbuse;
import com.steeringit.rankstop.session.RSSession;
import com.steeringit.rankstop.utils.RSConstants;

public class ReportAbuseDialog extends DialogFragment implements RSView.AbuseView {

    private View rootView;
    private Unbinder unbinder;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.rg_abuses)
    RadioGroup abusesRG;

    @BindView(R.id.tv_server_feedback)
    RSTVRegular serverFeedbackTV;

    @BindString(R.string.report_abuse)
    String reportAbuse;

    @BindString(R.string.choose_reason)
    String chooseReason;

    @BindString(R.string.off_line)
    String offLineMsg;

    @OnClick(R.id.positive_btn)
    void reportAbuse() {
        abusePresenter.onOkClick(getContext());
    }

    @BindString(R.string.loading_msg)
    String loadingMsg;

    @BindString(R.string.no_data_msg)
    String noDataMsg;

    @BindString(R.string.done_msg)
    String doneMsg;

    private RSPresenter.abusePresenter abusePresenter;
    private List<Abuse> abuseList;
    private String itemId, abuseId;
    private static ReportAbuseDialog instance;
    private RSLoader rsLoader;

    private void createLoader() {
        rsLoader = RSLoader.newInstance(loadingMsg);
        rsLoader.setCancelable(false);
    }

    public static ReportAbuseDialog newInstance(RSNavigationData rsNavigationData) {
        if (instance == null) {
            instance = new ReportAbuseDialog();
        }
        Bundle args = new Bundle();
        args.putSerializable(RSConstants.NAVIGATION_DATA, rsNavigationData);
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = inflater.inflate(R.layout.dialog_report_abuse, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        toolbar.setNavigationIcon(R.drawable.ic_close);
        toolbar.setNavigationOnClickListener(view1 -> dismiss());
        toolbar.setTitle(reportAbuse);

        return rootView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        createLoader();
        RSNavigationData rsNavigationData = (RSNavigationData) getArguments().getSerializable(RSConstants.NAVIGATION_DATA);
        itemId = rsNavigationData.getItemId();
        abusePresenter = new PresenterAbuseImpl(ReportAbuseDialog.this);
        abusesRG.setOnCheckedChangeListener((group, checkedId) -> abuseId = abuseList.get(abusesRG.indexOfChild(group.findViewById(checkedId))).get_id());
        loadAbuseList();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null)
            unbinder.unbind();
        rootView = null;
        abusesRG = null;
        instance = null;
        if (abusePresenter != null)
            abusePresenter.onDestroy(getContext());
        super.onDestroyView();
    }

    private void loadAbuseList() {
        abusePresenter.loadAbusesList(RankStop.getDeviceLanguage(), getContext());
    }

    private void initAbusesList(List<Abuse> abuseList) {
        try {
            for (Abuse abuse : abuseList) {
                RSRBMedium rb = new RSRBMedium(getContext());
                rb.setText(abuse.getName());
                abusesRG.addView(rb);
            }
        } catch (Exception e) {
            displayServerFeedBack(noDataMsg);
        }
    }

    private void displayServerFeedBack(String message) {
        serverFeedbackTV.setText(message);
        serverFeedbackTV.setVisibility(View.VISIBLE);
    }

    @Override
    public void onReportClicked() {
        if (abuseId != null) {
            abusePresenter.reportAbuse(new RSRequestReportAbuse(RSSession.getCurrentUser().get_id(), itemId, abuseId), getContext());
        } else {
            Toast.makeText(getContext(), chooseReason, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSuccess(String target, Object data) {
        switch (target) {
            case RSConstants.LOAD_ABUSES_LIST:
                abuseList = Arrays.asList(new Gson().fromJson(new Gson().toJson(data), Abuse[].class));
                initAbusesList(abuseList);
                break;
            case RSConstants.REPORT_ABUSES:
                Toast.makeText(getContext(), doneMsg, Toast.LENGTH_SHORT).show();
                dismiss();
                break;
        }
    }

    @Override
    public void onFailure(String target) {
        switch (target) {
            case RSConstants.LOAD_ABUSES_LIST:
                displayServerFeedBack(noDataMsg);
                break;
            case RSConstants.REPORT_ABUSES:
                break;
        }
    }

    @Override
    public void showProgressBar(String target) {
        switch (target) {
            case RSConstants.LOAD_ABUSES_LIST:
                progressBar.setVisibility(View.VISIBLE);
                break;
            case RSConstants.REPORT_ABUSES:
                rsLoader.show(getFragmentManager(), RSLoader.TAG);
                break;
        }
    }

    @Override
    public void hideProgressBar(String target) {
        switch (target) {
            case RSConstants.LOAD_ABUSES_LIST:
                progressBar.setVisibility(View.GONE);
                break;
            case RSConstants.REPORT_ABUSES:
                rsLoader.dismiss();
                break;
        }
    }

    @Override
    public void showMessage(String target, String message) {

    }

    @Override
    public void onOffLine() {
        //Toast.makeText(getContext(), offLineMsg, Toast.LENGTH_LONG).show();
        new RSCustomToast(getActivity(), getResources().getString(R.string.error), offLineMsg, R.drawable.ic_error, RSCustomToast.ERROR).show();

    }
}
