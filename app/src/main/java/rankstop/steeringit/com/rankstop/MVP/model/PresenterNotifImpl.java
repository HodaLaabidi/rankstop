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

public class PresenterNotifImpl implements RSPresenter.ListNotifPresenter {

    private RSView.ListNotifView notifView;
    private Call<RSResponse> callLoadListNotif, callEditNotifVisibility;

    public PresenterNotifImpl(RSView.ListNotifView notifView) {
        this.notifView = notifView;
    }


    @Override
    public void loadListNotif(RSRequestListItem rsRequestListItem, Context context) {
        if (RSNetwork.isConnected(context)) {
            if (notifView != null) {
                callLoadListNotif = WebService.getInstance().getApi().loadListNotif(RSSessionToken.getUsergestToken(), rsRequestListItem);
                callLoadListNotif.enqueue(new Callback<RSResponse>() {
                    @Override
                    public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                        if (response.code() == RSConstants.CODE_TOKEN_EXPIRED) {
                            RSSession.Reconnecter();
                            loadListNotif(rsRequestListItem, context);
                        } else {
                            if(response.body() != null) {
                                if (response.body().getStatus() == 1) {
                                    notifView.onSuccess(RSConstants.LIST_NOTIFS, response.body().getData(), null);
                                } else if (response.body().getStatus() == 0) {
                                    notifView.onError(RSConstants.LIST_NOTIFS);
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<RSResponse> call, Throwable t) {
                        if (!call.isCanceled()) {
                            notifView.onFailure(RSConstants.LIST_NOTIFS);
                        }
                    }
                });
            }
        } else {
            notifView.onOffLine();
        }
    }

    @Override
    public void editNotifVisibility(String notifId, String itemId, Context context) {
        if (RSNetwork.isConnected(context)) {
            if (notifView != null) {
                notifView.showProgressBar(RSConstants.EDIT_NOTIF_VISIBILITY);


                callEditNotifVisibility = null;
                callEditNotifVisibility = WebService.getInstance().getApi().editNotifVisibility(RSSessionToken.getUsergestToken(), notifId);



                callEditNotifVisibility.enqueue(new Callback<RSResponse>() {
                    @Override
                    public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                        if (response.code() == RSConstants.CODE_TOKEN_EXPIRED) {
                            RSSession.Reconnecter();
                            editNotifVisibility(notifId, itemId, context);
                        } else {
                            if(response.body() != null) {
                                if (response.body().getStatus() == 1) {
                                    notifView.onSuccess(RSConstants.EDIT_NOTIF_VISIBILITY, response.body().getData(), itemId);
                                } else if (response.body().getStatus() == 0) {
                                    notifView.onError(RSConstants.EDIT_NOTIF_VISIBILITY);
                                }
                            }
                            notifView.hideProgressBar(RSConstants.EDIT_NOTIF_VISIBILITY);
                        }
                    }

                    @Override
                    public void onFailure(Call<RSResponse> call, Throwable t) {
                        if (!call.isCanceled()) {
                            notifView.hideProgressBar(RSConstants.EDIT_NOTIF_VISIBILITY);
                            notifView.onFailure(RSConstants.EDIT_NOTIF_VISIBILITY);
                        }
                    }
                });
            }
        } else {
            notifView.onOffLine();
        }
    }

    @Override
    public void onDestroy(Context context) {
        if (callLoadListNotif != null)
            if (callLoadListNotif.isExecuted())
                callLoadListNotif.cancel();

        if (callEditNotifVisibility != null)
            if (callEditNotifVisibility.isExecuted())
                callEditNotifVisibility.cancel();

        notifView = null;
    }
}
