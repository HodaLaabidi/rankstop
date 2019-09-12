package com.steeringit.rankstop.MVP.model;

import android.content.Context;
import android.util.Log;

import com.steeringit.rankstop.MVP.presenter.RSPresenter;
import com.steeringit.rankstop.MVP.view.RSView;
import com.steeringit.rankstop.data.model.network.RSResponse;
import com.steeringit.rankstop.data.webservices.WebService;
import com.steeringit.rankstop.session.RSSession;
import com.steeringit.rankstop.session.RSSessionToken;
import com.steeringit.rankstop.ui.dialogFragment.RSLoader;
import com.steeringit.rankstop.utils.RSConstants;
import com.steeringit.rankstop.utils.RSNetwork;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PresenterDeviceLangImpl implements RSPresenter.EditDeviceLangPresenter {

    private RSView.EditLangView standardView;
    private Call<RSResponse> callEditLang;

    public PresenterDeviceLangImpl(RSView.EditLangView standardView) {
        this.standardView = standardView;
    }

    @Override
    public void editLang(String userId, String lang, Context context) {
        if (RSNetwork.isConnected(context)) {
            if (standardView != null) {
                standardView.showProgressBar();
                callEditLang = WebService.getInstance().getApi().editDeviceLanguage(RSSessionToken.getUsergestToken(), userId, lang);
                callEditLang.enqueue(new Callback<RSResponse>() {
                    @Override
                    public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                        if (response.code() == RSConstants.CODE_TOKEN_EXPIRED) {
                            RSSession.Reconnecter();
                            standardView.hideProgressBar();
                            editLang(userId, lang, context);

                        } else {
                            if(response.body() != null) {
                                if (response.body().getStatus() == 1) {
                                    standardView.onSuccess(lang, response.body().getData());
                                } else if (response.body().getStatus() == 0) {
                                    standardView.onError();
                                }
                            }
                            standardView.hideProgressBar();
                        }
                    }

                    @Override
                    public void onFailure(Call<RSResponse> call, Throwable t) {
                        if (!callEditLang.isCanceled()) {
                            standardView.hideProgressBar();
                            standardView.onFailure();
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
        if (callEditLang != null)
            if (callEditLang.isExecuted())
                callEditLang.cancel();

        standardView = null;
    }
}
