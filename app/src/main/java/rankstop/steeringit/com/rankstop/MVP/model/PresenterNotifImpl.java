package rankstop.steeringit.com.rankstop.MVP.model;

import android.content.Context;
import android.util.Log;

import rankstop.steeringit.com.rankstop.MVP.presenter.RSPresenter;
import rankstop.steeringit.com.rankstop.MVP.view.RSView;
import rankstop.steeringit.com.rankstop.data.model.network.RSRequestListItem;
import rankstop.steeringit.com.rankstop.data.model.network.RSResponse;
import rankstop.steeringit.com.rankstop.data.webservices.WebService;
import rankstop.steeringit.com.rankstop.session.RSSession;
import rankstop.steeringit.com.rankstop.session.RSSessionToken;
import rankstop.steeringit.com.rankstop.utils.RSConstants;
import rankstop.steeringit.com.rankstop.utils.RSNetwork;
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
                Log.e("list notif" , "lang = " + rsRequestListItem.getLang() +  "getUserId  = " + rsRequestListItem.getUserId() +"getPage  = " + rsRequestListItem.getPage() +"getPerPage  = " + rsRequestListItem.getPerPage() + " " );
                callLoadListNotif = WebService.getInstance().getApi().loadListNotif(RSSessionToken.getUsergestToken(), rsRequestListItem);
                Log.e("list notif RSSessionToken.getUsergestToken" , RSSessionToken.getUsergestToken()+"      !");
                callLoadListNotif.enqueue(new Callback<RSResponse>() {
                    @Override
                    public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                        Log.e("list notif response code" , response.code() + " !");
                        if (response.code() == RSConstants.CODE_TOKEN_EXPIRED) {
                            RSSession.Reconnecter();
                            Log.e("list notif" , "  not login");
                            loadListNotif(rsRequestListItem, context);
                        } else {
                            if(response.body() != null) {
                                Log.e("list notif response code if response.body() != null" , response.body().getStatus()+ " " +  response.body().getData() + " !");
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
                        Log.e("list notif onFailure" , "  "+ t.getMessage());
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
                            Log.e("list notif editNotifVisibility" , " not login"+"!");
                            RSSession.Reconnecter();
                            editNotifVisibility(notifId, itemId, context);
                        } else {
                            Log.e("list notif editNotifVisibility" , response.code() + " "+ response.body().getData()+"!");
                            if(response.body() != null) {
                                if (response.body().getStatus() == 1) {
                                    notifView.onSuccess(RSConstants.EDIT_NOTIF_VISIBILITY, response.body().getData(), itemId);
                                } else if (response.body().getStatus() == 0) {
                                    notifView.onError(RSConstants.EDIT_NOTIF_VISIBILITY);
                                }
                            } else {
                                Log.e("list notif " , " response.body == null");
                            }
                            notifView.hideProgressBar(RSConstants.EDIT_NOTIF_VISIBILITY);
                        }
                    }

                    @Override
                    public void onFailure(Call<RSResponse> call, Throwable t) {
                        Log.e("list notif  editNotifVisibility onFailure" , t.getMessage()+"!");
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
