package com.steeringit.rankstop.MVP.model;

import android.content.Context;

import com.steeringit.rankstop.MVP.presenter.RSPresenter;
import com.steeringit.rankstop.MVP.view.RSView;
import com.steeringit.rankstop.data.model.db.RSContact;
import com.steeringit.rankstop.data.model.db.RequestOwnership;
import com.steeringit.rankstop.data.model.network.RSResponse;
import com.steeringit.rankstop.data.webservices.WebService;
import com.steeringit.rankstop.session.RSSession;
import com.steeringit.rankstop.session.RSSessionToken;
import com.steeringit.rankstop.utils.RSConstants;
import com.steeringit.rankstop.utils.RSNetwork;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PresenterContact implements RSPresenter.ContactPresenter {

    private RSView.StandardView standardView;
    private Call<RSResponse> callReqOwnership, callContact;

    public PresenterContact(RSView.StandardView standardView) {
        this.standardView = standardView;
    }

    @Override
    public void requestOwnership(RequestOwnership requestOwnership, Context context) {
        if (RSNetwork.isConnected(context)) {
            if (standardView != null) {
                standardView.showProgressBar(RSConstants.SEND_REQ_OWNER_SHIP);
                callReqOwnership = WebService.getInstance().getApi().requestOwnership(RSSessionToken.getUsergestToken(), requestOwnership);
                callReqOwnership.enqueue(new Callback<RSResponse>() {
                    @Override
                    public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                        if (response.code() == RSConstants.CODE_TOKEN_EXPIRED) {
                            RSSession.Reconnecter();
                            standardView.hideProgressBar(RSConstants.SEND_REQ_OWNER_SHIP);
                            requestOwnership(requestOwnership, context);
                        } else {
                            if (response.body() != null) {
                                if (response.body() != null) {
                                    if (response.body().getStatus() == 1) {
                                        standardView.onSuccess(RSConstants.SEND_REQ_OWNER_SHIP, response.body().getData());
                                    } else if (response.body().getStatus() == 0) {
                                        standardView.onError(RSConstants.SEND_REQ_OWNER_SHIP);
                                    }
                                }
                            }
                            standardView.hideProgressBar(RSConstants.SEND_REQ_OWNER_SHIP);
                        }
                    }

                    @Override
                    public void onFailure(Call<RSResponse> call, Throwable t) {
                        if (!callReqOwnership.isCanceled()) {
                            standardView.hideProgressBar(RSConstants.SEND_REQ_OWNER_SHIP);
                            standardView.onFailure(RSConstants.SEND_REQ_OWNER_SHIP);
                        }
                    }
                });
            }
        } else {
            standardView.onOffLine();
        }
    }

    @Override
    public void contact(RSContact rsContact, Context context) {
        if (RSNetwork.isConnected(context)) {
            if (standardView != null) {
                standardView.showProgressBar(RSConstants.RS_CONTACT);
                callContact = WebService.getInstance().getApi().contact(RSSessionToken.getUsergestToken(), rsContact);
                callContact.enqueue(new Callback<RSResponse>() {
                    @Override
                    public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                        if (response.code() == RSConstants.CODE_TOKEN_EXPIRED) {
                            RSSession.Reconnecter();
                            standardView.hideProgressBar(RSConstants.RS_CONTACT);
                            contact(rsContact, context);
                        } else {
                            if (response.body() != null) {
                                if (response.body().getStatus() == 1) {
                                    standardView.onSuccess(RSConstants.RS_CONTACT, response.body().getData());
                                } else if (response.body().getStatus() == 0) {
                                    standardView.onError(RSConstants.RS_CONTACT);
                                }
                            }
                            standardView.hideProgressBar(RSConstants.RS_CONTACT);
                        }
                    }

                    @Override
                    public void onFailure(Call<RSResponse> call, Throwable t) {
                        if (!callContact.isCanceled()) {
                            standardView.hideProgressBar(RSConstants.RS_CONTACT);
                            standardView.onFailure(RSConstants.RS_CONTACT);
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
        if (callReqOwnership != null)
            if (callReqOwnership.isExecuted())
                callReqOwnership.cancel();

        if (callContact != null)
            if (callContact.isExecuted())
                callContact.cancel();

        standardView = null;
    }
}
