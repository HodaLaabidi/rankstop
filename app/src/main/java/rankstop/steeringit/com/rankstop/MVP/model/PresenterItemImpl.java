package rankstop.steeringit.com.rankstop.MVP.model;

import android.util.Log;

import rankstop.steeringit.com.rankstop.MVP.presenter.RSPresenter;
import rankstop.steeringit.com.rankstop.MVP.view.RSView;
import rankstop.steeringit.com.rankstop.data.model.custom.RSFollow;
import rankstop.steeringit.com.rankstop.data.model.custom.RSRequestListItem;
import rankstop.steeringit.com.rankstop.data.model.custom.RSResponse;
import rankstop.steeringit.com.rankstop.data.webservices.WebService;
import rankstop.steeringit.com.rankstop.utils.RSConstants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PresenterItemImpl implements RSPresenter.ItemPresenter {

    private RSView.StandardView standardView;

    public PresenterItemImpl(RSView.StandardView standardView) {
        Log.i("TAG_HOME","home created");
        this.standardView = standardView;
    }

    @Override
    public void loadItem(String itemId, String userId) {
        standardView.showProgressBar(RSConstants.ONE_ITEM);

        WebService.getInstance().getApi().loadItem(itemId, userId).enqueue(new Callback<RSResponse>() {
            @Override
            public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                if (response.body().getStatus() == 1) {
                    standardView.onSuccess(RSConstants.ONE_ITEM, response.body().getData());
                } else if (response.body().getStatus() == 0) {
                    standardView.onFailure(RSConstants.ONE_ITEM);
                }
                standardView.hideProgressBar(RSConstants.ONE_ITEM);
            }

            @Override
            public void onFailure(Call<RSResponse> call, Throwable t) {
                standardView.hideProgressBar(RSConstants.ONE_ITEM);
            }
        });
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
    public void loadMyEvals(RSRequestListItem rsRequestListItem) {
        standardView.showProgressBar(RSConstants.MY_EVALS);

        WebService.getInstance().getApi().loadMyEvals(rsRequestListItem).enqueue(new Callback<RSResponse>() {
            @Override
            public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                if (response.body().getStatus() == 1) {
                    standardView.onSuccess(RSConstants.MY_EVALS, response.body().getData());
                } else if (response.body().getStatus() == 0) {
                    standardView.onFailure(RSConstants.MY_EVALS);
                }
                standardView.hideProgressBar(RSConstants.MY_EVALS);
            }

            @Override
            public void onFailure(Call<RSResponse> call, Throwable t) {
                standardView.hideProgressBar(RSConstants.MY_EVALS);
            }
        });
    }

    @Override
    public void loadCategoriesList() {
        //standardView.showProgressBar(RSConstants.MY_EVALS);

        WebService.getInstance().getApi().loadCategoriesList().enqueue(new Callback<RSResponse>() {
            @Override
            public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                if (response.body().getStatus() == 1) {
                    standardView.onSuccess(RSConstants.MY_EVALS, response.body().getData());
                } else if (response.body().getStatus() == 0) {
                    standardView.onFailure(RSConstants.MY_EVALS);
                }
                //standardView.hideProgressBar(RSConstants.MY_EVALS);
            }

            @Override
            public void onFailure(Call<RSResponse> call, Throwable t) {
                //standardView.hideProgressBar(RSConstants.MY_EVALS);
            }
        });
    }

    @Override
    public void followItem(RSFollow rsFollow) {
        WebService.getInstance().getApi().followItem(rsFollow).enqueue(new Callback<RSResponse>() {
            @Override
            public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                if (response.body().getStatus() == 1) {
                    standardView.onSuccess(RSConstants.FOLLOW_ITEM, null);
                } else if (response.body().getStatus() == 0) {
                    standardView.onFailure(RSConstants.FOLLOW_ITEM);
                }
            }

            @Override
            public void onFailure(Call<RSResponse> call, Throwable t) {}
        });
    }

    @Override
    public void unfollowItem(RSFollow rsFollow) {
        WebService.getInstance().getApi().unfollowItem(rsFollow).enqueue(new Callback<RSResponse>() {
            @Override
            public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                if (response.body().getStatus() == 1) {
                    standardView.onSuccess(RSConstants.UNFOLLOW_ITEM, null);
                } else if (response.body().getStatus() == 0) {
                    standardView.onFailure(RSConstants.UNFOLLOW_ITEM);
                }
            }

            @Override
            public void onFailure(Call<RSResponse> call, Throwable t) {}
        });
    }

    @Override
    public void onDestroyItem() {
        standardView = null;
    }
}
