package rankstop.steeringit.com.rankstop.MVP.model;

import rankstop.steeringit.com.rankstop.MVP.presenter.RSPresenter;
import rankstop.steeringit.com.rankstop.MVP.view.RSView;
import rankstop.steeringit.com.rankstop.data.model.db.RSContact;
import rankstop.steeringit.com.rankstop.data.model.db.RequestOwnership;
import rankstop.steeringit.com.rankstop.data.model.network.RSResponse;
import rankstop.steeringit.com.rankstop.data.webservices.WebService;
import rankstop.steeringit.com.rankstop.utils.RSConstants;
import rankstop.steeringit.com.rankstop.utils.RSNetwork;
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
    public void requestOwnership(RequestOwnership requestOwnership) {
        if (RSNetwork.isConnected()) {
            if (standardView != null) {
                standardView.showProgressBar(RSConstants.SEND_REQ_OWNER_SHIP);
                callReqOwnership = WebService.getInstance().getApi().requestOwnership(requestOwnership);
                callReqOwnership.enqueue(new Callback<RSResponse>() {
                    @Override
                    public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                        if (response.body().getStatus() == 1) {
                            standardView.onSuccess(RSConstants.SEND_REQ_OWNER_SHIP, response.body().getData());
                        } else if (response.body().getStatus() == 0) {
                            standardView.onError(RSConstants.SEND_REQ_OWNER_SHIP);
                        }
                        standardView.hideProgressBar(RSConstants.SEND_REQ_OWNER_SHIP);
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
        }else {
            standardView.onOffLine();
        }
    }

    @Override
    public void contact(RSContact rsContact) {
        if (RSNetwork.isConnected()) {
            if (standardView != null) {
                standardView.showProgressBar(RSConstants.RS_CONTACT);
                callContact = WebService.getInstance().getApi().contact(rsContact);
                callContact.enqueue(new Callback<RSResponse>() {
                    @Override
                    public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                        if (response.body().getStatus() == 1) {
                            standardView.onSuccess(RSConstants.RS_CONTACT, response.body().getData());
                        } else if (response.body().getStatus() == 0) {
                            standardView.onError(RSConstants.RS_CONTACT);
                        }
                        standardView.hideProgressBar(RSConstants.RS_CONTACT);
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
        }else {
            standardView.onOffLine();
        }
    }

    @Override
    public void onDestroy() {
        if (callReqOwnership != null)
            if (callReqOwnership.isExecuted())
                callReqOwnership.cancel();

        if (callContact != null)
            if (callContact.isExecuted())
                callContact.cancel();

        standardView = null;
    }
}
