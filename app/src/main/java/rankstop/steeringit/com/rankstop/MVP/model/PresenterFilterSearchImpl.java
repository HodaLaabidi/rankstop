package rankstop.steeringit.com.rankstop.MVP.model;

import rankstop.steeringit.com.rankstop.MVP.presenter.RSPresenter;
import rankstop.steeringit.com.rankstop.MVP.view.RSView;
import rankstop.steeringit.com.rankstop.data.model.network.RSResponse;
import rankstop.steeringit.com.rankstop.data.webservices.WebService;
import rankstop.steeringit.com.rankstop.session.RSSession;
import rankstop.steeringit.com.rankstop.session.RSSessionToken;
import rankstop.steeringit.com.rankstop.utils.RSConstants;
import rankstop.steeringit.com.rankstop.utils.RSNetwork;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PresenterFilterSearchImpl implements RSPresenter.SearchFilterPresenter {

    private RSView.SearchView searchView;

    private Call<RSResponse> callSearch;

    public PresenterFilterSearchImpl(RSView.SearchView searchView) {
        this.searchView = searchView;
    }

    @Override
    public void loadCategories(String lang) {
        if (RSNetwork.isConnected()) {
            if (searchView != null) {
                searchView.showProgressBar(RSConstants.LOAD_CATEGORIES_USED_BY_LOCATION);
                callSearch = WebService.getInstance().getApi().loadCategoriesUsedByLocations(RSSessionToken.getUsergestToken(), lang);
                callSearch.enqueue(new Callback<RSResponse>() {
                    @Override
                    public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                        if (response.code() == RSConstants.CODE_TOKEN_EXPIRED) {
                            RSSession.Reconnecter();
                            loadCategories(lang);
                        } else {
                            if (response.body().getStatus() == 1) {
                                searchView.onSuccess(RSConstants.LOAD_CATEGORIES_USED_BY_LOCATION, response.body().getData());
                            } else if (response.body().getStatus() == 0) {
                                searchView.onError(RSConstants.LOAD_CATEGORIES_USED_BY_LOCATION);
                            }
                            searchView.hideProgressBar(RSConstants.LOAD_CATEGORIES_USED_BY_LOCATION);
                        }
                    }

                    @Override
                    public void onFailure(Call<RSResponse> call, Throwable t) {
                        if (!callSearch.isCanceled()) {
                            searchView.hideProgressBar(RSConstants.LOAD_CATEGORIES_USED_BY_LOCATION);
                            searchView.onFailure(RSConstants.LOAD_CATEGORIES_USED_BY_LOCATION);
                        }
                    }
                });
            }
        } else {
            searchView.onOffLine();
        }
    }

    @Override
    public void onDestroy() {
        if (callSearch != null)
            if (callSearch.isExecuted())
                callSearch.cancel();

        searchView = null;
    }
}
