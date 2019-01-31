package rankstop.steeringit.com.rankstop.ui.dialogFragment;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
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

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rankstop.steeringit.com.rankstop.MVP.model.PresenterAbuseImpl;
import rankstop.steeringit.com.rankstop.MVP.presenter.RSPresenter;
import rankstop.steeringit.com.rankstop.MVP.view.RSView;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.data.model.db.Abuse;
import rankstop.steeringit.com.rankstop.data.model.network.RSNavigationData;
import rankstop.steeringit.com.rankstop.data.model.network.RSRequestReportAbuse;
import rankstop.steeringit.com.rankstop.session.RSSession;
import rankstop.steeringit.com.rankstop.utils.RSConstants;

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
    TextView serverFeedbackTV;

    @BindString(R.string.report_abuse)
    String reportAbuse;

    @BindString(R.string.choose_reason)
    String chooseReason;

    @OnClick(R.id.positive_btn)
    void reportAbuse() {
        abusePresenter.onOkClick();
    }

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

        RSNavigationData rsNavigationData = (RSNavigationData) getArguments().getSerializable(RSConstants.NAVIGATION_DATA);
        itemId = rsNavigationData.getItemId();

        abusePresenter = new PresenterAbuseImpl(ReportAbuseDialog.this);

        abusesRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                abuseId = abuseList.get(abusesRG.indexOfChild(group.findViewById(checkedId))).get_id();
            }
        });
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

        unbinder.unbind();
        rootView = null;
        colorStateList = null;
        layoutParams = null;
        abusesRG = null;
        instance = null;
        abusePresenter.onDestroy();

        super.onDestroyView();
    }

    private void loadAbuseList() {
        abusePresenter.loadAbusesList("EN");
    }

    private void initAbusesList(List<Abuse> abuseList) {
        try {
            for (Abuse abuse : abuseList) {
                RadioButton rb = new RadioButton(getContext());
                rb.setText(abuse.getName());
                abusesRG.addView(rb);
            }
        } catch (Exception e) {
            displayServerFeedBack("No data");
        }
    }

    private void displayServerFeedBack(String message) {
        serverFeedbackTV.setText(message);
        serverFeedbackTV.setVisibility(View.VISIBLE);
    }

    @Override
    public void onReportClicked() {
        if (abuseId != null){
            abusePresenter.reportAbuse(new RSRequestReportAbuse(RSSession.getCurrentUser().get_id(), itemId, abuseId));
        }else{
            Toast.makeText(getContext(), chooseReason, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDialogCanceled() {
        dismiss();
    }

    @Override
    public void onSuccess(String target, Object data) {
        switch (target) {
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
        switch (target) {
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
