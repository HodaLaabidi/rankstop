package rankstop.steeringit.com.rankstop.MVP.model;

import android.content.Context;

import rankstop.steeringit.com.rankstop.MVP.presenter.RSPresenter;
import rankstop.steeringit.com.rankstop.MVP.view.RSView;
import rankstop.steeringit.com.rankstop.data.model.network.RSRequestFilter;
import rankstop.steeringit.com.rankstop.data.model.network.RSRequestItemByCategory;
import rankstop.steeringit.com.rankstop.data.model.network.RSResponse;
import rankstop.steeringit.com.rankstop.data.webservices.WebService;
import rankstop.steeringit.com.rankstop.session.RSSession;
import rankstop.steeringit.com.rankstop.session.RSSessionToken;
import rankstop.steeringit.com.rankstop.utils.RSConstants;
import rankstop.steeringit.com.rankstop.utils.RSNetwork;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PresenterSearchImpl implements RSPresenter.SearchPresenter {

    private RSView.SearchView searchView;
    private Call<RSResponse> callSearch, callSearchItems, callSearchItemsFiltered;

    public PresenterSearchImpl(RSView.SearchView searchView) {
        this.searchView = searchView;
    }

    @Override
    public void search(String query, String lang, Context context) {
        if (RSNetwork.isConnected(context)) {
            if (searchView != null) {
                if (callSearch != null)
                    if (callSearch.isExecuted())
                        callSearch.cancel();
                searchView.showProgressBar(RSConstants.SEARCH);
                callSearch = WebService.getInstance().getApi().search(RSSessionToken.getUsergestToken(), query, lang);
                callSearch.enqueue(new Callback<RSResponse>() {
                    @Override
                    public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                        if (response.code() == RSConstants.CODE_TOKEN_EXPIRED) {
                            RSSession.Reconnecter();
                            search(query, lang, context);
                        } else {
                            if (response.body().getStatus() == 1) {
                                searchView.onSuccess(RSConstants.SEARCH, response.body().getData());
                            } else if (response.body().getStatus() == 0) {
                                searchView.onError(RSConstants.SEARCH);
                            }
                            searchView.hideProgressBar(RSConstants.SEARCH);
                        }
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
        } else {
            searchView.onOffLine();
        }
    }

    @Override
    public void searchItems(RSRequestItemByCategory rsRequestSearch, Context context) {
        if (RSNetwork.isConnected(context)) {
            if (searchView != null) {
                searchView.showProgressBar(RSConstants.SEARCH_ITEMS);
                callSearchItems = WebService.getInstance().getApi().searchItems(RSSessionToken.getUsergestToken(), rsRequestSearch);
                callSearchItems.enqueue(new Callback<RSResponse>() {
                    @Override
                    public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                        if (response.code() == RSConstants.CODE_TOKEN_EXPIRED) {
                            RSSession.Reconnecter();
                            searchView.hideProgressBar(RSConstants.SEARCH_ITEMS);
                            searchItems(rsRequestSearch, context);
                        } else {
                            if (response.body().getStatus() == 1) {
                                searchView.onSuccess(RSConstants.SEARCH_ITEMS, response.body().getData());
                            } else if (response.body().getStatus() == 0) {
                                searchView.onError(RSConstants.SEARCH_ITEMS);
                            }
                            searchView.hideProgressBar(RSConstants.SEARCH_ITEMS);
                        }
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
        } else {
            searchView.onOffLine();
        }
    }

    @Override
    public void searchItemsFiltered(RSRequestFilter data, Context context) {
        if (RSNetwork.isConnected(context)) {
            if (searchView != null) {
                searchView.showProgressBar(RSConstants.SEARCH_ITEMS_FILTERED);
                callSearchItemsFiltered = WebService.getInstance().getApi().searchItemsFiltered(RSSessionToken.getUsergestToken(), data);
                callSearchItemsFiltered.enqueue(new Callback<RSResponse>() {
                    @Override
                    public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                        if (response.code() == RSConstants.CODE_TOKEN_EXPIRED) {
                            RSSession.Reconnecter();
                            searchItemsFiltered(data, context);
                        } else {
                            if (response.body().getStatus() == 1) {
                                searchView.onSuccess(RSConstants.SEARCH_ITEMS_FILTERED, response.body().getData());
                            } else if (response.body().getStatus() == 0) {
                                searchView.onError(RSConstants.SEARCH_ITEMS_FILTERED);
                            }
                            searchView.hideProgressBar(RSConstants.SEARCH_ITEMS_FILTERED);
                        }
                    }

                    @Override
                    public void onFailure(Call<RSResponse> call, Throwable t) {
                        if (!callSearchItemsFiltered.isCanceled()) {
                            searchView.hideProgressBar(RSConstants.SEARCH_ITEMS_FILTERED);
                            searchView.onFailure(RSConstants.SEARCH_ITEMS_FILTERED);
                        }
                    }
                });
            }
        } else {
            searchView.onOffLine();
        }
    }

    @Override
    public void onDestroy(Context context) {
        if (callSearchItems != null)
            if (callSearchItems.isExecuted())
                callSearchItems.cancel();

        if (callSearch != null)
            if (callSearch.isExecuted())
                callSearch.cancel();

        searchView = null;
    }
}
