package rankstop.steeringit.com.rankstop.MVP.model;

import rankstop.steeringit.com.rankstop.MVP.presenter.RSPresenter;
import rankstop.steeringit.com.rankstop.MVP.view.RSView;
import rankstop.steeringit.com.rankstop.data.model.network.RSRequestListItem;
import rankstop.steeringit.com.rankstop.data.model.network.RSResponse;
import rankstop.steeringit.com.rankstop.data.webservices.WebService;
import rankstop.steeringit.com.rankstop.utils.RSConstants;
import rankstop.steeringit.com.rankstop.utils.RSNetwork;
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
    public void loadHistory(RSRequestListItem rsRequestListItem) {
        if (RSNetwork.isConnected()) {
            if (standardView != null) {
                standardView.showProgressBar(RSConstants.USER_HISTORY);
                callUserHistory = WebService.getInstance().getApi().loadUserHistory(rsRequestListItem);
                callUserHistory.enqueue(new Callback<RSResponse>() {
                    @Override
                    public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                        if (response.body().getStatus() == 1) {
                            standardView.onSuccess(RSConstants.USER_HISTORY, response.body().getData());
                        } else if (response.body().getStatus() == 0) {
                            standardView.onError(RSConstants.USER_HISTORY);
                        }
                        standardView.hideProgressBar(RSConstants.USER_HISTORY);
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
        }else {
            standardView.onOffLine();
        }
    }

    @Override
    public void onDestroy() {
        if (callUserHistory != null)
            if (callUserHistory.isExecuted())
                callUserHistory.cancel();
        standardView = null;
    }
}
