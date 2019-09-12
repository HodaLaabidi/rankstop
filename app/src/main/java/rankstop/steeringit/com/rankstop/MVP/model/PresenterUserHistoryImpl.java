package com.steeringit.rankstop.MVP.model;

import android.content.Context;
import android.util.Log;

import com.steeringit.rankstop.MVP.presenter.RSPresenter;
import com.steeringit.rankstop.MVP.view.RSView;
import com.steeringit.rankstop.data.model.network.RSRequestListItem;
import com.steeringit.rankstop.data.model.network.RSResponse;
import com.steeringit.rankstop.data.webservices.WebService;
import com.steeringit.rankstop.session.RSSession;
import com.steeringit.rankstop.session.RSSessionToken;
import com.steeringit.rankstop.utils.RSConstants;
import com.steeringit.rankstop.utils.RSNetwork;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PresenterUserHistoryImpl implements RSPresenter.UserHistoryPresenter {

    private RSView.StandardView standardView;
    private Call<RSResponse> callUserHistory;

    public PresenterUserHistoryImpl(RSView.StandardView standardView) {
        this.standardView = standardView;
    }

    @Override
    public void loadHistory(RSRequestListItem rsRequestListItem, Context context) {
        if (RSNetwork.isConnected(context)) {
            if (standardView != null) {
                standardView.showProgressBar(RSConstants.USER_HISTORY);
                callUserHistory = WebService.getInstance().getApi().loadUserHistory(RSSessionToken.getUsergestToken(), rsRequestListItem);
                callUserHistory.enqueue(new Callback<RSResponse>() {
                    @Override
                    public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                        if (response.code() == RSConstants.CODE_TOKEN_EXPIRED) {
                            RSSession.Reconnecter();
                           loadHistory(rsRequestListItem , context);
                        } else {
                            if (response.body() != null) {
                                if (response.body().getStatus() == 1) {
                                    standardView.onSuccess(RSConstants.USER_HISTORY, response.body().getData());
                                } else if (response.body().getStatus() == 0) {
                                    standardView.onError(RSConstants.USER_HISTORY);
                                }
                            }
                            standardView.hideProgressBar(RSConstants.USER_HISTORY);
                        }
                    }

                    @Override
                    public void onFailure(Call<RSResponse> call, Throwable t) {
                        if (!call.isCanceled()) {
                            standardView.hideProgressBar(RSConstants.USER_HISTORY);
                            standardView.onFailure(RSConstants.USER_HISTORY);
                        }
                    }
                });
            }
        } else {
            standardView.onOffLine();
        }
    }

    @Override
    public void onDestroy(Context context) {
        if (callUserHistory != null)
            if (callUserHistory.isExecuted())
                callUserHistory.cancel();
        standardView = null;
    }
}
