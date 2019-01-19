package rankstop.steeringit.com.rankstop.MVP.model;

import rankstop.steeringit.com.rankstop.MVP.presenter.RSPresenter;
import rankstop.steeringit.com.rankstop.MVP.view.RSView;
import rankstop.steeringit.com.rankstop.data.model.db.RequestOwnership;
import rankstop.steeringit.com.rankstop.data.model.network.RSResponse;
import rankstop.steeringit.com.rankstop.data.webservices.Urls;
import rankstop.steeringit.com.rankstop.data.webservices.WebService;
import rankstop.steeringit.com.rankstop.utils.RSConstants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PresenterRequestOwnerShip implements RSPresenter.RequestOwnerShipPresenter {

    private RSView.StandardView standardView;
    private Call<RSResponse> callReqOwnership;

    public PresenterRequestOwnerShip(RSView.StandardView standardView) {
        this.standardView = standardView;
    }

    @Override
    public void requestOwnership(RequestOwnership requestOwnership) {
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
    }

    @Override
    public void onDestroy() {
        if (callReqOwnership != null)
            if (callReqOwnership.isExecuted())
                callReqOwnership.cancel();

        standardView = null;
    }
}
