package rankstop.steeringit.com.rankstop.MVP.model;

import rankstop.steeringit.com.rankstop.MVP.presenter.RSPresenter;
import rankstop.steeringit.com.rankstop.MVP.view.RSView;
import rankstop.steeringit.com.rankstop.data.model.network.RSResponse;
import rankstop.steeringit.com.rankstop.data.webservices.WebService;
import rankstop.steeringit.com.rankstop.utils.RSNetwork;
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
    public void editLang(String userId, String lang) {
        if (RSNetwork.isConnected()) {
            if (standardView != null) {
                standardView.showProgressBar();
                callEditLang = WebService.getInstance().getApi().editDeviceLanguage(userId, lang);
                callEditLang.enqueue(new Callback<RSResponse>() {
                    @Override
                    public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                        if (response.body().getStatus() == 1) {
                            standardView.onSuccess(lang, response.body().getData());
                        } else if (response.body().getStatus() == 0) {
                            standardView.onError();
                        }
                        standardView.hideProgressBar();
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
        }else {
            standardView.onOffLine();
        }
    }

    @Override
    public void onDestroy() {
        if (callEditLang != null)
            if (callEditLang.isExecuted())
                callEditLang.cancel();

        standardView = null;
    }
}
