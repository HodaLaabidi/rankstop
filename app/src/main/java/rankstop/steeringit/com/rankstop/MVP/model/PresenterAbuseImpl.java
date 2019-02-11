package rankstop.steeringit.com.rankstop.MVP.model;

import rankstop.steeringit.com.rankstop.MVP.presenter.RSPresenter;
import rankstop.steeringit.com.rankstop.MVP.view.RSView;
import rankstop.steeringit.com.rankstop.data.model.network.RSRequestReportAbuse;
import rankstop.steeringit.com.rankstop.data.model.network.RSResponse;
import rankstop.steeringit.com.rankstop.data.webservices.Urls;
import rankstop.steeringit.com.rankstop.data.webservices.WebService;
import rankstop.steeringit.com.rankstop.utils.RSConstants;
import rankstop.steeringit.com.rankstop.utils.RSNetwork;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PresenterAbuseImpl implements RSPresenter.abusePresenter {

    private Call<RSResponse> callAbusesList, callReportAbuse;

    private RSView.AbuseView abuseView;

    public PresenterAbuseImpl(RSView.AbuseView abuseView) {
        this.abuseView = abuseView;
    }

    @Override
    public void loadAbusesList(String langue) {
        if (RSNetwork.isConnected()){
            if (abuseView != null) {
                abuseView.showProgressBar(RSConstants.LOAD_ABUSES_LIST);
                callAbusesList = WebService.getInstance().getApi().loadAbusesList(langue);
                callAbusesList.enqueue(new Callback<RSResponse>() {
                    @Override
                    public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                        if (response.body().getStatus() == 1) {
                            abuseView.onSuccess(RSConstants.LOAD_ABUSES_LIST, response.body().getData());
                        } else if (response.body().getStatus() == 0) {
                            abuseView.onFailure(RSConstants.LOAD_ABUSES_LIST);
                        }
                        abuseView.hideProgressBar(RSConstants.LOAD_ABUSES_LIST);
                    }

                    @Override
                    public void onFailure(Call<RSResponse> call, Throwable t) {
                        if (!callAbusesList.isCanceled()) {
                            abuseView.hideProgressBar(RSConstants.LOAD_ABUSES_LIST);
                            abuseView.showMessage(RSConstants.LOAD_ABUSES_LIST, "failure");
                        }
                    }
                });
            }
        }else {
            abuseView.onOffLine();
        }
    }

    @Override
    public void reportAbuse(RSRequestReportAbuse rsRequestReportAbuse) {
        if (RSNetwork.isConnected()) {
            if (abuseView != null) {
                abuseView.showProgressBar(RSConstants.REPORT_ABUSES);
                callReportAbuse = WebService.getInstance().getApi().reportAbuse(rsRequestReportAbuse);
                callReportAbuse.enqueue(new Callback<RSResponse>() {
                    @Override
                    public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                        if (response.body().getStatus() == 1) {
                            abuseView.onSuccess(RSConstants.REPORT_ABUSES, response.body().getData());
                        } else if (response.body().getStatus() == 0) {
                            abuseView.onFailure(RSConstants.REPORT_ABUSES);
                        }
                        abuseView.hideProgressBar(RSConstants.REPORT_ABUSES);
                    }

                    @Override
                    public void onFailure(Call<RSResponse> call, Throwable t) {
                        if (!callReportAbuse.isCanceled()) {
                            abuseView.hideProgressBar(RSConstants.REPORT_ABUSES);
                            abuseView.showMessage(RSConstants.REPORT_ABUSES, "failure");
                        }
                    }
                });
            }
        }else {
            abuseView.onOffLine();
        }
    }

    @Override
    public void onOkClick() {
        if (abuseView != null) {
            abuseView.onReportClicked();
        }
    }

    @Override
    public void onCancelClick() {
        if (abuseView != null) {
            abuseView.onDialogCanceled();
        }
    }

    @Override
    public void onDestroy() {
        if (callAbusesList != null)
            if (callAbusesList.isExecuted())
                callAbusesList.cancel();

        if (callReportAbuse != null)
            if (callReportAbuse.isExecuted())
                callReportAbuse.cancel();

        abuseView = null;
    }
}
