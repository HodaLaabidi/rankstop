package com.steeringit.rankstop.MVP.model;

import android.content.Context;

import com.steeringit.rankstop.MVP.presenter.RSPresenter;
import com.steeringit.rankstop.MVP.view.RSView;
import com.steeringit.rankstop.data.model.network.RSRequestReportAbuse;
import com.steeringit.rankstop.data.model.network.RSResponse;
import com.steeringit.rankstop.data.webservices.WebService;
import com.steeringit.rankstop.session.RSSession;
import com.steeringit.rankstop.session.RSSessionToken;
import com.steeringit.rankstop.utils.RSConstants;
import com.steeringit.rankstop.utils.RSNetwork;
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
    public void loadAbusesList(String langue, Context context) {
        if (RSNetwork.isConnected(context)) {
            if (abuseView != null) {
                abuseView.showProgressBar(RSConstants.LOAD_ABUSES_LIST);
                callAbusesList = WebService.getInstance().getApi().loadAbusesList(RSSessionToken.getUsergestToken(), langue);
                callAbusesList.enqueue(new Callback<RSResponse>() {
                    @Override
                    public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                        if (response.code() == RSConstants.CODE_TOKEN_EXPIRED) {
                            RSSession.Reconnecter();
                            abuseView.hideProgressBar(RSConstants.LOAD_ABUSES_LIST);
                            loadAbusesList(langue, context);
                        } else {
                            if (response.body() != null) {
                                if (response.body().getStatus() == 1) {
                                    abuseView.onSuccess(RSConstants.LOAD_ABUSES_LIST, response.body().getData());
                                } else if (response.body().getStatus() == 0) {
                                    abuseView.onFailure(RSConstants.LOAD_ABUSES_LIST);
                                }
                            }
                            abuseView.hideProgressBar(RSConstants.LOAD_ABUSES_LIST);
                        }
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
        } else {
            abuseView.onOffLine();
        }
    }

    @Override
    public void reportAbuse(RSRequestReportAbuse rsRequestReportAbuse, Context context) {
        if (RSNetwork.isConnected(context)) {
            if (abuseView != null) {
                abuseView.showProgressBar(RSConstants.REPORT_ABUSES);
                callReportAbuse = WebService.getInstance().getApi().reportAbuse(RSSessionToken.getUsergestToken(), rsRequestReportAbuse);
                callReportAbuse.enqueue(new Callback<RSResponse>() {
                    @Override
                    public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                        if (response.code() == RSConstants.CODE_TOKEN_EXPIRED) {
                            RSSession.Reconnecter();
                            abuseView.hideProgressBar(RSConstants.REPORT_ABUSES);
                            reportAbuse(rsRequestReportAbuse, context);
                        } else {
                            if( response.body() != null) {
                                if (response.body().getStatus() == 1) {
                                    abuseView.onSuccess(RSConstants.REPORT_ABUSES, response.body().getData());
                                } else if (response.body().getStatus() == 0) {
                                    abuseView.onFailure(RSConstants.REPORT_ABUSES);
                                }
                            }
                            abuseView.hideProgressBar(RSConstants.REPORT_ABUSES);
                        }
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
        } else {
            abuseView.onOffLine();
        }
    }

    @Override
    public void onOkClick(Context context) {
        if (abuseView != null) {
            abuseView.onReportClicked();
        }
    }

    @Override
    public void onDestroy(Context context) {
        if (callAbusesList != null)
            if (callAbusesList.isExecuted())
                callAbusesList.cancel();

        if (callReportAbuse != null)
            if (callReportAbuse.isExecuted())
                callReportAbuse.cancel();

        abuseView = null;
    }
}
