package rankstop.steeringit.com.rankstop.MVP.model;

import rankstop.steeringit.com.rankstop.MVP.presenter.RSPresenter;
import rankstop.steeringit.com.rankstop.MVP.view.RSView;
import rankstop.steeringit.com.rankstop.data.model.custom.RSResponse;
import rankstop.steeringit.com.rankstop.data.webservices.WebService;
import rankstop.steeringit.com.rankstop.utils.RSConstants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PresenterUserImpl implements RSPresenter.UserPresenter {

    private RSView.StandardView standardView;

    private Call<RSResponse> callUserInfo;

    public PresenterUserImpl(RSView.StandardView standardView) {
        this.standardView = standardView;
    }

    @Override
    public void loadUserInfo(String id) {
        if (standardView != null) {
            standardView.showProgressBar(RSConstants.USER_INFO);
            callUserInfo = WebService.getInstance().getApi().loadUserInfo(id);
            callUserInfo.enqueue(new Callback<RSResponse>() {
                @Override
                public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                    if (response.body().getStatus() == 1) {
                        standardView.onSuccess(RSConstants.USER_INFO, response.body().getData());
                    } else if (response.body().getStatus() == 0) {
                        standardView.onFailure(RSConstants.USER_INFO);
                    }
                    standardView.hideProgressBar(RSConstants.USER_INFO);
                }

                @Override
                public void onFailure(Call<RSResponse> call, Throwable t) {
                    if (!call.isCanceled())
                        standardView.hideProgressBar(RSConstants.USER_INFO);
                }
            });
        }
    }

    @Override
    public void onDestroyUser() {
        if (callUserInfo != null)
            if (callUserInfo.isExecuted())
                callUserInfo.cancel();
        standardView = null;
    }
}
