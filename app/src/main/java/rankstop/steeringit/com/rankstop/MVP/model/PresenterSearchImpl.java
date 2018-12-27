package rankstop.steeringit.com.rankstop.MVP.model;

import rankstop.steeringit.com.rankstop.MVP.presenter.RSPresenter;
import rankstop.steeringit.com.rankstop.MVP.view.RSView;
import rankstop.steeringit.com.rankstop.data.model.custom.RSRequestItemByCategory;
import rankstop.steeringit.com.rankstop.data.model.custom.RSResponse;
import rankstop.steeringit.com.rankstop.data.webservices.WebService;
import rankstop.steeringit.com.rankstop.utils.RSConstants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PresenterSearchImpl implements RSPresenter.SearchPresenter {

    private RSView.SearchView searchView;
    private Call<RSResponse> callSearch, callSearchItems;

    public PresenterSearchImpl(RSView.SearchView searchView) {
        this.searchView = searchView;
    }

    @Override
    public void search(String query) {
        if (searchView != null) {
            if (callSearch != null)
                if (callSearch.isExecuted())
                    callSearch.cancel();
            searchView.showProgressBar(RSConstants.SEARCH);
            callSearch = WebService.getInstance().getApi().search(query);
            callSearch.enqueue(new Callback<RSResponse>() {
                @Override
                public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                    if (response.body().getStatus() == 1) {
                        searchView.onSuccess(RSConstants.SEARCH, response.body().getData());
                    } else if (response.body().getStatus() == 0) {
                        searchView.onError(RSConstants.SEARCH);
                    }
                    searchView.hideProgressBar(RSConstants.SEARCH);
                }

                @Override
                public void onFailure(Call<RSResponse> call, Throwable t) {
                    if (!callSearch.isCanceled()) {
                        searchView.hideProgressBar(RSConstants.SEARCH);
                        searchView.onFailure(RSConstants.SEARCH);
                    }
                }
            });
        }
    }

    @Override
    public void searchItems(RSRequestItemByCategory rsRequestSearch) {
        if (searchView != null) {
            searchView.showProgressBar(RSConstants.SEARCH_ITEMS);
            callSearchItems = WebService.getInstance().getApi().searchItems(rsRequestSearch);
            callSearchItems.enqueue(new Callback<RSResponse>() {
                @Override
                public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                    if (response.body().getStatus() == 1) {
                        searchView.onSuccess(RSConstants.SEARCH_ITEMS, response.body().getData());
                    } else if (response.body().getStatus() == 0) {
                        searchView.onError(RSConstants.SEARCH_ITEMS);
                    }
                    searchView.hideProgressBar(RSConstants.SEARCH_ITEMS);
                }

                @Override
                public void onFailure(Call<RSResponse> call, Throwable t) {
                    if (!callSearchItems.isCanceled()) {
                        searchView.hideProgressBar(RSConstants.SEARCH_ITEMS);
                        searchView.onFailure(RSConstants.SEARCH_ITEMS);
                    }
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        if (callSearchItems != null)
            if (callSearchItems.isExecuted())
                callSearchItems.cancel();

        if (callSearch != null)
            if (callSearch.isExecuted())
                callSearch.cancel();

        searchView = null;
    }
}
