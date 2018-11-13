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

public class PresenterHomeImpl implements RSPresenter.HomePresenter {

    private RSView.StandardView standardView;

    public PresenterHomeImpl(RSView.StandardView standardView) {
        this.standardView = standardView;
    }

    @Override
    public void loadTopRankedItems(RSRequestListItem rsRequestListItem) {
        standardView.showProgressBar(RSConstants.TOP_RANKED_ITEMS);

        WebService.getInstance().getApi().loadTopRankedItems(rsRequestListItem).enqueue(new Callback<RSResponse>() {
            @Override
            public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                if (response.body().getStatus() == 1) {
                    standardView.onSuccess(RSConstants.TOP_RANKED_ITEMS, response.body().getData());
                } else if (response.body().getStatus() == 0) {
                    standardView.onFailure(RSConstants.TOP_RANKED_ITEMS);
                }
                standardView.hideProgressBar(RSConstants.TOP_RANKED_ITEMS);
            }

            @Override
            public void onFailure(Call<RSResponse> call, Throwable t) {
                standardView.hideProgressBar(RSConstants.TOP_RANKED_ITEMS);
            }
        });
    }

    @Override
    public void loadTopViewedItems(RSRequestListItem rsRequestListItem) {
        standardView.showProgressBar(RSConstants.TOP_VIEWED_ITEMS);

        WebService.getInstance().getApi().loadTopViewedItems(rsRequestListItem).enqueue(new Callback<RSResponse>() {
            @Override
            public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                if (response.body().getStatus() == 1) {
                    standardView.onSuccess(RSConstants.TOP_VIEWED_ITEMS, response.body().getData());
                } else if (response.body().getStatus() == 0) {
                    standardView.onFailure(RSConstants.TOP_VIEWED_ITEMS);
                }
                standardView.hideProgressBar(RSConstants.TOP_VIEWED_ITEMS);
            }

            @Override
            public void onFailure(Call<RSResponse> call, Throwable t) {
                standardView.hideProgressBar(RSConstants.TOP_VIEWED_ITEMS);
            }
        });
    }

    @Override
    public void loadTopCommentedItems(RSRequestListItem rsRequestListItem) {
        standardView.showProgressBar(RSConstants.TOP_COMMENTED_ITEMS);

        WebService.getInstance().getApi().loadTopCommentedItems(rsRequestListItem).enqueue(new Callback<RSResponse>() {
            @Override
            public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                if (response.body().getStatus() == 1) {
                    standardView.onSuccess(RSConstants.TOP_COMMENTED_ITEMS, response.body().getData());
                } else if (response.body().getStatus() == 0) {
                    standardView.onFailure(RSConstants.TOP_COMMENTED_ITEMS);
                }
                standardView.hideProgressBar(RSConstants.TOP_COMMENTED_ITEMS);
            }

            @Override
            public void onFailure(Call<RSResponse> call, Throwable t) {
                standardView.hideProgressBar(RSConstants.TOP_COMMENTED_ITEMS);
            }
        });
    }

    @Override
    public void loadTopFollowedItems(RSRequestListItem rsRequestListItem) {
        standardView.showProgressBar(RSConstants.TOP_FOLLOWED_ITEMS);

        WebService.getInstance().getApi().loadTopFollowedItems(rsRequestListItem).enqueue(new Callback<RSResponse>() {
            @Override
            public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                if (response.body().getStatus() == 1) {
                    standardView.onSuccess(RSConstants.TOP_FOLLOWED_ITEMS, response.body().getData());
                } else if (response.body().getStatus() == 0) {
                    standardView.onFailure(RSConstants.TOP_FOLLOWED_ITEMS);
                }
                standardView.hideProgressBar(RSConstants.TOP_FOLLOWED_ITEMS);
            }

            @Override
            public void onFailure(Call<RSResponse> call, Throwable t) {
                standardView.hideProgressBar(RSConstants.TOP_FOLLOWED_ITEMS);
            }
        });
    }

    @Override
    public void onDestroy() {
        standardView = null;
    }
}
