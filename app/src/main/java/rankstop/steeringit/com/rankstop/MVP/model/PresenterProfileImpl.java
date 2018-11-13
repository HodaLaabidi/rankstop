package rankstop.steeringit.com.rankstop.MVP.model;

import rankstop.steeringit.com.rankstop.MVP.presenter.RSPresenter;
import rankstop.steeringit.com.rankstop.MVP.view.RSView;
import rankstop.steeringit.com.rankstop.data.model.custom.RSRequestListItem;
import rankstop.steeringit.com.rankstop.data.model.custom.RSResponse;
import rankstop.steeringit.com.rankstop.data.webservices.WebService;
import rankstop.steeringit.com.rankstop.utils.RSConstants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PresenterProfileImpl implements RSPresenter.ProfilePresenter {

    private RSView.StandardView standardView;

    public PresenterProfileImpl(RSView.StandardView standardView) {
        this.standardView = standardView;
    }

    @Override
    public void loadItemCreated(RSRequestListItem rsRequestListItem) {
        standardView.showProgressBar(RSConstants.ITEM_CREATED);

        WebService.getInstance().getApi().loadItemCreated(rsRequestListItem).enqueue(new Callback<RSResponse>() {
            @Override
            public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                if (response.body().getStatus() == 1) {
                    standardView.onSuccess(RSConstants.ITEM_CREATED, response.body().getData());
                } else if (response.body().getStatus() == 0) {
                    standardView.onFailure(RSConstants.ITEM_CREATED);
                }
                standardView.hideProgressBar(RSConstants.ITEM_CREATED);
            }

            @Override
            public void onFailure(Call<RSResponse> call, Throwable t) {
                standardView.hideProgressBar(RSConstants.ITEM_CREATED);
            }
        });
    }

    @Override
    public void loadItemOwned(RSRequestListItem rsRequestListItem) {
        standardView.showProgressBar(RSConstants.ITEM_OWNED);

        WebService.getInstance().getApi().loadItemOwned(rsRequestListItem).enqueue(new Callback<RSResponse>() {
            @Override
            public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                if (response.body().getStatus() == 1) {
                    standardView.onSuccess(RSConstants.ITEM_OWNED, response.body().getData());
                } else if (response.body().getStatus() == 0) {
                    standardView.onFailure(RSConstants.ITEM_OWNED);
                }
                standardView.hideProgressBar(RSConstants.ITEM_OWNED);
            }

            @Override
            public void onFailure(Call<RSResponse> call, Throwable t) {
                standardView.hideProgressBar(RSConstants.ITEM_OWNED);
            }
        });
    }

    @Override
    public void loadItemFollowed(RSRequestListItem rsRequestListItem) {
        standardView.showProgressBar(RSConstants.ITEM_FOLLOWED);

        WebService.getInstance().getApi().loadItemFollowed(rsRequestListItem).enqueue(new Callback<RSResponse>() {
            @Override
            public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                if (response.body().getStatus() == 1) {
                    standardView.onSuccess(RSConstants.ITEM_FOLLOWED, response.body().getData());
                } else if (response.body().getStatus() == 0) {
                    standardView.onFailure(RSConstants.ITEM_FOLLOWED);
                }
                standardView.hideProgressBar(RSConstants.ITEM_FOLLOWED);
            }

            @Override
            public void onFailure(Call<RSResponse> call, Throwable t) {
                standardView.hideProgressBar(RSConstants.ITEM_FOLLOWED);
            }
        });
    }

    @Override
    public void loadUserInfo(String id) {
        standardView.showProgressBar(RSConstants.USER_INFO);

        WebService.getInstance().getApi().loadUserInfo(id).enqueue(new Callback<RSResponse>() {
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
                standardView.hideProgressBar(RSConstants.USER_INFO);
            }
        });
    }

    @Override
    public void onDestroy() {
        standardView = null;
    }
}
