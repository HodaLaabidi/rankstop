package com.steeringit.rankstop.MVP.model;

import android.content.Context;
import android.util.Log;

import com.steeringit.rankstop.MVP.presenter.RSPresenter;
import com.steeringit.rankstop.MVP.view.RSView;
import com.steeringit.rankstop.data.model.network.RSRequestFilter;
import com.steeringit.rankstop.data.model.network.RSRequestItemByCategory;
import com.steeringit.rankstop.data.model.network.RSResponse;
import com.steeringit.rankstop.data.webservices.WebService;
import com.steeringit.rankstop.session.RSSession;
import com.steeringit.rankstop.session.RSSessionToken;
import com.steeringit.rankstop.utils.RSConstants;
import com.steeringit.rankstop.utils.RSNetwork;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PresenterSearchImpl implements RSPresenter.SearchPresenter {

    private RSView.SearchView searchView;
    private Call<RSResponse> callSearch, callSearchItems, callSearchItemsFiltered, callCategoriesList;
    private static final String TAG = "PresenterSearchImpl";

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
                            if (response.body() != null) {
                                if (response.body().getStatus() == 1) {
                                    searchView.onSuccess(RSConstants.SEARCH, response.body().getData());
                                } else if (response.body().getStatus() == 0) {
                                    searchView.onError(RSConstants.SEARCH);
                                }
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
    public void loadCategoriesList(String lang, Context context) {
        if (RSNetwork.isConnected(context)) {
            if (searchView != null) {
                searchView.showProgressBar(RSConstants.LOAD_CATEGORIES);
                callCategoriesList = WebService.getInstance().getApi().loadCategoriesList(RSSessionToken.getUsergestToken(), lang);
                callCategoriesList.enqueue(new Callback<RSResponse>() {
                    @Override
                    public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                        if (response.body() != null) {
                            if (response.body().getStatus() == RSConstants.CODE_TOKEN_EXPIRED) {
                                RSSession.Reconnecter();
                                searchView.hideProgressBar(RSConstants.LOAD_CATEGORIES);
                                loadCategoriesList(lang, context);
                            } else {
                                if (response.body().getStatus() == 1) {
                                    searchView.onSuccess(RSConstants.LOAD_CATEGORIES, response.body().getData());
                                } else if (response.body().getStatus() == 0) {
                                    searchView.onFailure(RSConstants.LOAD_CATEGORIES);
                                }
                                searchView.hideProgressBar(RSConstants.LOAD_CATEGORIES);
                            }
                        } else {
                            searchView.hideProgressBar(RSConstants.LOAD_CATEGORIES);
                        }
                    }

                    @Override
                    public void onFailure(Call<RSResponse> call, Throwable t) {
                        if (!callCategoriesList.isCanceled()) {
                            searchView.hideProgressBar(RSConstants.LOAD_CATEGORIES);
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
                            if (response.body() != null) {
                                if (response.body().getStatus() == 1) {
                                    searchView.onSuccess(RSConstants.SEARCH_ITEMS, response.body().getData());
                                } else if (response.body().getStatus() == 0) {
                                    searchView.onError(RSConstants.SEARCH_ITEMS);
                                }
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
                            if (response.body() != null) {
                                if (response.body().getStatus() == 1) {
                                    searchView.onSuccess(RSConstants.SEARCH_ITEMS_FILTERED, response.body().getData());
                                } else if (response.body().getStatus() == 0) {
                                    searchView.onError(RSConstants.SEARCH_ITEMS_FILTERED);
                                }
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
