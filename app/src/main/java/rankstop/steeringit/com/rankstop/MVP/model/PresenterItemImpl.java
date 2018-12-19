package rankstop.steeringit.com.rankstop.MVP.model;

import rankstop.steeringit.com.rankstop.MVP.presenter.RSPresenter;
import rankstop.steeringit.com.rankstop.MVP.view.RSView;
import rankstop.steeringit.com.rankstop.data.model.custom.RSFollow;
import rankstop.steeringit.com.rankstop.data.model.custom.RSRequestItemData;
import rankstop.steeringit.com.rankstop.data.model.custom.RSRequestListItem;
import rankstop.steeringit.com.rankstop.data.model.custom.RSRequestReportAbuse;
import rankstop.steeringit.com.rankstop.data.model.custom.RSResponse;
import rankstop.steeringit.com.rankstop.data.webservices.WebService;
import rankstop.steeringit.com.rankstop.utils.RSConstants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PresenterItemImpl implements RSPresenter.ItemPresenter {

    private RSView.StandardView standardView;

    private Call<RSResponse> callLoadItem, callTopRankedItems, callTopViewedItems, callTopCommentedItems, callTopFollowedItems,
            callItemCreated, callItemOwned, callItemFollowed, callMyEvals, callCategoriesList, callFollowItem, callUnfollowItem,
            callItemComments, callItemPix, callItemPixByUser, callItemCommentsByUser;

    public PresenterItemImpl(RSView.StandardView standardView) {
        this.standardView = standardView;
    }

    @Override
    public void loadItem(String itemId, String userId) {
        standardView.showProgressBar(RSConstants.ONE_ITEM);

        callLoadItem = WebService.getInstance().getApi().loadItem(itemId, userId);
        callLoadItem.enqueue(new Callback<RSResponse>() {
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
                if (!call.isCanceled())
                    standardView.hideProgressBar(RSConstants.ONE_ITEM);
            }
        });
    }

    @Override
    public void loadTopRankedItems(RSRequestListItem rsRequestListItem) {
        standardView.showProgressBar(RSConstants.TOP_RANKED_ITEMS);

        callTopRankedItems = WebService.getInstance().getApi().loadTopRankedItems(rsRequestListItem);
        callTopRankedItems.enqueue(new Callback<RSResponse>() {
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
                if (!callTopRankedItems.isCanceled())
                    standardView.hideProgressBar(RSConstants.TOP_RANKED_ITEMS);
            }
        });
    }

    @Override
    public void loadTopViewedItems(RSRequestListItem rsRequestListItem) {
        standardView.showProgressBar(RSConstants.TOP_VIEWED_ITEMS);

        callTopViewedItems = WebService.getInstance().getApi().loadTopViewedItems(rsRequestListItem);
        callTopViewedItems.enqueue(new Callback<RSResponse>() {
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
                if (!callTopViewedItems.isCanceled())
                    standardView.hideProgressBar(RSConstants.TOP_VIEWED_ITEMS);
            }
        });
    }

    @Override
    public void loadTopCommentedItems(RSRequestListItem rsRequestListItem) {
        standardView.showProgressBar(RSConstants.TOP_COMMENTED_ITEMS);

        callTopCommentedItems = WebService.getInstance().getApi().loadTopCommentedItems(rsRequestListItem);
        callTopCommentedItems.enqueue(new Callback<RSResponse>() {
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
                if (!callTopCommentedItems.isCanceled())
                    standardView.hideProgressBar(RSConstants.TOP_COMMENTED_ITEMS);
            }
        });
    }

    @Override
    public void loadTopFollowedItems(RSRequestListItem rsRequestListItem) {
        standardView.showProgressBar(RSConstants.TOP_FOLLOWED_ITEMS);

        callTopFollowedItems = WebService.getInstance().getApi().loadTopFollowedItems(rsRequestListItem);
        callTopFollowedItems.enqueue(new Callback<RSResponse>() {
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
                if (!callTopFollowedItems.isCanceled())
                    standardView.hideProgressBar(RSConstants.TOP_FOLLOWED_ITEMS);
            }
        });
    }

    @Override
    public void loadItemCreated(RSRequestListItem rsRequestListItem) {
        standardView.showProgressBar(RSConstants.ITEM_CREATED);

        callItemCreated = WebService.getInstance().getApi().loadItemCreated(rsRequestListItem);
        callItemCreated.enqueue(new Callback<RSResponse>() {
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
                if (!callItemCreated.isCanceled())
                    standardView.hideProgressBar(RSConstants.ITEM_CREATED);
            }
        });
    }

    @Override
    public void loadItemOwned(RSRequestListItem rsRequestListItem) {
        standardView.showProgressBar(RSConstants.ITEM_OWNED);

        callItemOwned = WebService.getInstance().getApi().loadItemOwned(rsRequestListItem);
        callItemOwned.enqueue(new Callback<RSResponse>() {
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
                if (!callItemOwned.isCanceled())
                    standardView.hideProgressBar(RSConstants.ITEM_OWNED);
            }
        });
    }

    @Override
    public void loadItemFollowed(RSRequestListItem rsRequestListItem) {
        standardView.showProgressBar(RSConstants.ITEM_FOLLOWED);

        callItemFollowed = WebService.getInstance().getApi().loadItemFollowed(rsRequestListItem);
        callItemFollowed.enqueue(new Callback<RSResponse>() {
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
                if (!callItemFollowed.isCanceled())
                    standardView.hideProgressBar(RSConstants.ITEM_FOLLOWED);
            }
        });
    }

    @Override
    public void loadMyEvals(RSRequestListItem rsRequestListItem) {
        standardView.showProgressBar(RSConstants.MY_EVALS);

        callMyEvals = WebService.getInstance().getApi().loadMyEvals(rsRequestListItem);
        callMyEvals.enqueue(new Callback<RSResponse>() {
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
                if (!callMyEvals.isCanceled())
                    standardView.hideProgressBar(RSConstants.MY_EVALS);
            }
        });
    }

    @Override
    public void loadCategoriesList() {
        //standardView.showProgressBar(RSConstants.LOAD_CATEGORIES);

        callCategoriesList = WebService.getInstance().getApi().loadCategoriesList();
        callCategoriesList.enqueue(new Callback<RSResponse>() {
            @Override
            public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                if (response.body().getStatus() == 1) {
                    standardView.onSuccess(RSConstants.LOAD_CATEGORIES, response.body().getData());
                } else if (response.body().getStatus() == 0) {
                    standardView.onFailure(RSConstants.LOAD_CATEGORIES);
                }
                //standardView.hideProgressBar(RSConstants.LOAD_CATEGORIES);
            }

            @Override
            public void onFailure(Call<RSResponse> call, Throwable t) {
                if (!callCategoriesList.isCanceled()) {
                    //standardView.hideProgressBar(RSConstants.LOAD_CATEGORIES);
                }
            }
        });
    }

    @Override
    public void followItem(RSFollow rsFollow) {
        callFollowItem = WebService.getInstance().getApi().followItem(rsFollow);
        callFollowItem.enqueue(new Callback<RSResponse>() {
            @Override
            public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                if (response.body().getStatus() == 1) {
                    standardView.onSuccess(RSConstants.FOLLOW_ITEM, "1");
                } else if (response.body().getStatus() == 0) {
                    standardView.onSuccess(RSConstants.FOLLOW_ITEM, "0");
                }
            }

            @Override
            public void onFailure(Call<RSResponse> call, Throwable t) {
                if (!callFollowItem.isCanceled()) {

                }
            }
        });
    }

    @Override
    public void unfollowItem(RSFollow rsFollow) {
        callUnfollowItem = WebService.getInstance().getApi().unfollowItem(rsFollow);
        callUnfollowItem.enqueue(new Callback<RSResponse>() {
            @Override
            public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                if (response.body().getStatus() == 1) {
                    standardView.onSuccess(RSConstants.UNFOLLOW_ITEM, null);
                } else if (response.body().getStatus() == 0) {
                    standardView.onFailure(RSConstants.UNFOLLOW_ITEM);
                }
            }

            @Override
            public void onFailure(Call<RSResponse> call, Throwable t) {
                if (!callUnfollowItem.isCanceled()) {
                }
            }
        });
    }

    @Override
    public void loadItemComments(RSRequestItemData rsRequestItemData) {
        standardView.showProgressBar(RSConstants.ITEM_COMMENTS);

        callItemComments = WebService.getInstance().getApi().loadItemComments(rsRequestItemData);
        callItemComments.enqueue(new Callback<RSResponse>() {
            @Override
            public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                if (response.body().getStatus() == 1) {
                    standardView.onSuccess(RSConstants.ITEM_COMMENTS, response.body().getData());
                } else if (response.body().getStatus() == 0) {
                    standardView.onFailure(RSConstants.ITEM_COMMENTS);
                }
                standardView.hideProgressBar(RSConstants.ITEM_COMMENTS);
            }

            @Override
            public void onFailure(Call<RSResponse> call, Throwable t) {
                if (!callItemComments.isCanceled()) {
                    standardView.hideProgressBar(RSConstants.ITEM_COMMENTS);
                    standardView.showMessage(RSConstants.ITEM_COMMENTS, "failure com");
                }
            }
        });
    }

    @Override
    public void loadItemCommentsByUser(RSRequestItemData rsRequestItemData) {
        standardView.showProgressBar(RSConstants.ITEM_COMMENTS_BY_USER);

        callItemCommentsByUser = WebService.getInstance().getApi().loadItemCommentsByUser(rsRequestItemData);
        callItemCommentsByUser.enqueue(new Callback<RSResponse>() {
            @Override
            public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                if (response.body().getStatus() == 1) {
                    standardView.onSuccess(RSConstants.ITEM_COMMENTS_BY_USER, response.body().getData());
                } else if (response.body().getStatus() == 0) {
                    standardView.onFailure(RSConstants.ITEM_COMMENTS_BY_USER);
                }
                standardView.hideProgressBar(RSConstants.ITEM_COMMENTS_BY_USER);
            }

            @Override
            public void onFailure(Call<RSResponse> call, Throwable t) {
                if (!callItemCommentsByUser.isCanceled()) {
                    standardView.hideProgressBar(RSConstants.ITEM_COMMENTS_BY_USER);
                    standardView.showMessage(RSConstants.ITEM_COMMENTS_BY_USER, "failure com");
                }
            }
        });
    }

    @Override
    public void loadItemPix(RSRequestItemData rsRequestItemData) {
        standardView.showProgressBar(RSConstants.ITEM_PIX);

        callItemPix = WebService.getInstance().getApi().loadItemPix(rsRequestItemData);
        callItemPix.enqueue(new Callback<RSResponse>() {
            @Override
            public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                if (response.body().getStatus() == 1) {
                    standardView.onSuccess(RSConstants.ITEM_PIX, response.body().getData());
                } else if (response.body().getStatus() == 0) {
                    standardView.onFailure(RSConstants.ITEM_PIX);
                }
                standardView.hideProgressBar(RSConstants.ITEM_PIX);
            }

            @Override
            public void onFailure(Call<RSResponse> call, Throwable t) {
                if (!callItemPix.isCanceled()) {
                    standardView.hideProgressBar(RSConstants.ITEM_PIX);
                    standardView.showMessage(RSConstants.ITEM_PIX, "failure");
                }
            }
        });
    }

    @Override
    public void loadItemPixByUser(RSRequestItemData rsRequestItemData) {
        standardView.showProgressBar(RSConstants.ITEM_PIX_BY_USER);

        callItemPixByUser = WebService.getInstance().getApi().loadItemPixByUser(rsRequestItemData);
        callItemPixByUser.enqueue(new Callback<RSResponse>() {
            @Override
            public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                if (response.body().getStatus() == 1) {
                    standardView.onSuccess(RSConstants.ITEM_PIX_BY_USER, response.body().getData());
                } else if (response.body().getStatus() == 0) {
                    standardView.onFailure(RSConstants.ITEM_PIX_BY_USER);
                }
                standardView.hideProgressBar(RSConstants.ITEM_PIX_BY_USER);
            }

            @Override
            public void onFailure(Call<RSResponse> call, Throwable t) {
                if (!callItemPixByUser.isCanceled()) {
                    standardView.hideProgressBar(RSConstants.ITEM_PIX_BY_USER);
                    standardView.showMessage(RSConstants.ITEM_PIX_BY_USER, "failure");
                }
            }
        });
    }

    @Override
    public void onDestroyItem() {
        if (callLoadItem != null)
            if (callLoadItem.isExecuted())
                callLoadItem.cancel();

        if (callTopRankedItems != null)
            if (callTopRankedItems.isExecuted())
                callTopRankedItems.cancel();

        if (callTopViewedItems != null)
            if (callTopViewedItems.isExecuted())
                callTopViewedItems.cancel();

        if (callTopCommentedItems != null)
            if (callTopCommentedItems.isExecuted())
                callTopCommentedItems.cancel();

        if (callTopFollowedItems != null)
            if (callTopFollowedItems.isExecuted())
                callTopFollowedItems.cancel();

        if (callItemCreated != null)
            if (callItemCreated.isExecuted())
                callItemCreated.cancel();

        if (callItemOwned != null)
            if (callItemOwned.isExecuted())
                callItemOwned.cancel();

        if (callItemFollowed != null)
            if (callItemFollowed.isExecuted())
                callItemFollowed.cancel();

        if (callMyEvals != null)
            if (callMyEvals.isExecuted())
                callMyEvals.cancel();

        if (callCategoriesList != null)
            if (callCategoriesList.isExecuted())
                callCategoriesList.cancel();

        if (callFollowItem != null)
            if (callFollowItem.isExecuted())
                callFollowItem.cancel();

        if (callUnfollowItem != null)
            if (callUnfollowItem.isExecuted())
                callUnfollowItem.cancel();

        if (callItemComments != null)
            if (callItemComments.isExecuted())
                callItemComments.cancel();

        if (callItemCommentsByUser != null)
            if (callItemCommentsByUser.isExecuted())
                callItemCommentsByUser.cancel();

        if (callItemPix != null)
            if (callItemPix.isExecuted())
                callItemPix.cancel();

        if (callItemPixByUser != null)
            if (callItemPixByUser.isExecuted())
                callItemPixByUser.cancel();

        standardView = null;
    }
}
