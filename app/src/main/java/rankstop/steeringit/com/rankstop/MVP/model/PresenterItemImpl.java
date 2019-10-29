package rankstop.steeringit.com.rankstop.MVP.model;


import android.content.Context;
import android.util.Log;

import java.util.List;

import rankstop.steeringit.com.rankstop.MVP.presenter.RSPresenter;
import rankstop.steeringit.com.rankstop.MVP.view.RSView;
import rankstop.steeringit.com.rankstop.data.model.db.Item;
import rankstop.steeringit.com.rankstop.data.model.network.RSFollow;
import rankstop.steeringit.com.rankstop.data.model.network.RSRequestItemData;
import rankstop.steeringit.com.rankstop.data.model.network.RSRequestListItem;
import rankstop.steeringit.com.rankstop.data.model.network.RSResponse;
import rankstop.steeringit.com.rankstop.data.webservices.WebService;
import rankstop.steeringit.com.rankstop.session.RSSession;
import rankstop.steeringit.com.rankstop.session.RSSessionToken;
import rankstop.steeringit.com.rankstop.utils.RSConstants;
import rankstop.steeringit.com.rankstop.utils.RSNetwork;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PresenterItemImpl implements RSPresenter.ItemPresenter {

    private RSView.StandardView standardView;
    private RSView.StandardView2 standardView2;
    private Call<RSResponse> callLoadItem, callTopRankedItems, callTopCommentedItems, callTopViewedItems, callTopFollowedItems,
            callItemCreated, callItemOwned, callItemFollowed, callMyEvals, callCategoriesList, callFollowItem, callUnfollowItem,
            callItemComments, callItemPix, callItemPixByUser, callItemCommentsByUser, callDeletePic, callDeleteComment;

    public PresenterItemImpl(RSView.StandardView standardView, RSView.StandardView2 standardView2){
        this.standardView = standardView;
        this.standardView2 = standardView2;
    }

    @Override
    public void loadItem(String itemId, String userId, String lang, Context context) {
        if (RSNetwork.isConnected(context)) {
            if (standardView != null) {
                standardView.showProgressBar(RSConstants.ONE_ITEM);
                callLoadItem = WebService.getInstance().getApi().loadItem(RSSessionToken.getUsergestToken(), itemId, userId, lang);
                callLoadItem.enqueue(new Callback<RSResponse>() {
                    @Override
                    public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                        if (response.body() != null) {
                            if (response.body().getStatus() == RSConstants.CODE_TOKEN_EXPIRED) {
                                RSSession.Reconnecter();
                                loadItem(itemId, userId, lang, context);
                            } else {
                                if (response.body().getStatus() == 1) {
                                    standardView.onSuccess(RSConstants.ONE_ITEM, response.body().getData());
                                } else if (response.body().getStatus() == 0) {
                                    standardView.onFailure(RSConstants.ONE_ITEM);
                                }
                            }
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
        } else {
            standardView.onOffLine();
        }
    }

    @Override
    public void refreshItems( Context context, String userId, String itemId, String message,  String lang) {

        if (RSNetwork.isConnected(context)) {
            if (standardView2 != null) {
                //standardView.showProgressBar(RSConstants.ONE_ITEM);
                callLoadItem = WebService.getInstance().getApi().loadItem(RSSessionToken.getUsergestToken(), itemId, userId, lang);
                callLoadItem.enqueue(new Callback<RSResponse>() {
                    @Override
                    public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                        if (response.body() != null) {

                            if (response.body().getStatus() == RSConstants.CODE_TOKEN_EXPIRED) {
                                RSSession.Reconnecter();
                                refreshItems(context ,userId, itemId,message, lang);
                            } else {
                                if (response.body().getStatus() == 1) {

                                    standardView2.onSuccessRefreshItem(RSConstants.REFRESH_ITEM, itemId, message,response.body().getData());
                                } else if (response.body().getStatus() == 0) {
                                    //standardView2.onFailure(RSConstants.REFRESH_ITEM);
                                }
                            }
                        }

                    }

                    @Override
                    public void onFailure(Call<RSResponse> call, Throwable t) {
                        if (!call.isCanceled()){

                        }

                    }
                });
            }
        } else {
            //standardView2.onOffLine();
        }

    }

    @Override
    public void loadTopRankedItems(RSRequestListItem rsRequestListItem, Context context) {

        if (standardView != null) {
            callTopRankedItems = WebService.getInstance().getApi().loadTopRankedItems(RSSessionToken.getUsergestToken(), rsRequestListItem);
            standardView.showProgressBar(RSConstants.TOP_RANKED_ITEMS);
            callTopRankedItems.enqueue(new Callback<RSResponse>() {
                @Override
                public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                    if (response.body() != null) {
                        if (response.body().getStatus() == RSConstants.CODE_TOKEN_EXPIRED) {
                            RSSession.Reconnecter();
                            loadTopRankedItems(rsRequestListItem, context);
                        } else {
                            if (response.body().getStatus() == 1) {
                                standardView.onSuccess(RSConstants.TOP_RANKED_ITEMS, response.body().getData());
                            } else if (response.body().getStatus() == 0) {
                                standardView.onFailure(RSConstants.TOP_RANKED_ITEMS);
                            }
                            standardView.hideProgressBar(RSConstants.TOP_RANKED_ITEMS);
                        }
                    } else  {
                             standardView.hideProgressBar(RSConstants.TOP_RANKED_ITEMS);
                    }
                }



                @Override
                public void onFailure(Call<RSResponse> call, Throwable t) {

                    if (!callTopRankedItems.isCanceled())
                        standardView.hideProgressBar(RSConstants.TOP_RANKED_ITEMS);
                }
            });
        }
    }

    @Override
    public void loadTopViewedItems(RSRequestListItem rsRequestListItem, Context context) {
        if (standardView != null) {
            standardView.showProgressBar(RSConstants.TOP_VIEWED_ITEMS);
            callTopViewedItems = WebService.getInstance().getApi().loadTopViewedItems(RSSessionToken.getUsergestToken(), rsRequestListItem);
            if ( RSSessionToken.getUsergestToken() != null){
                callTopViewedItems.enqueue(new Callback<RSResponse>() {
                    @Override
                    public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                        if (response.body() != null) {
                            if (response.body().getStatus() == RSConstants.CODE_TOKEN_EXPIRED) {
                                RSSession.Reconnecter();
                                loadTopViewedItems(rsRequestListItem, context);
                            } else {
                                if (response.body().getStatus() == 1) {
                                    standardView.onSuccess(RSConstants.TOP_VIEWED_ITEMS, response.body().getData());
                                } else if (response.body().getStatus() == 0) {
                                    standardView.onFailure(RSConstants.TOP_VIEWED_ITEMS);
                                }
                                standardView.hideProgressBar(RSConstants.TOP_VIEWED_ITEMS);
                            }
                        } else {
                            standardView.hideProgressBar(RSConstants.TOP_VIEWED_ITEMS);
                        }
                    }

                    @Override
                    public void onFailure(Call<RSResponse> call, Throwable t) {
                        if (!callTopViewedItems.isCanceled())
                            standardView.hideProgressBar(RSConstants.TOP_VIEWED_ITEMS);
                    }
                });
            } else {

            }

        }
    }

    @Override
    public void loadTopCommentedItems(RSRequestListItem rsRequestListItem, Context context) {

        if (standardView != null) {
            standardView.showProgressBar(RSConstants.TOP_COMMENTED_ITEMS);
            callTopCommentedItems = WebService.getInstance().getApi().loadTopCommentedItems(RSSessionToken.getUsergestToken(), rsRequestListItem);
            callTopCommentedItems.enqueue(new Callback<RSResponse>() {
                @Override
                public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                    if (response.body() != null) {
                        if (response.body().getStatus() == RSConstants.CODE_TOKEN_EXPIRED) {
                            RSSession.Reconnecter();
                            loadTopCommentedItems(rsRequestListItem, context);
                        } else {
                            if (response.body().getStatus() == 1) {
                                standardView.onSuccess(RSConstants.TOP_COMMENTED_ITEMS, response.body().getData());
                            } else if (response.body().getStatus() == 0) {
                                standardView.onFailure(RSConstants.TOP_COMMENTED_ITEMS);
                            }
                            standardView.hideProgressBar(RSConstants.TOP_COMMENTED_ITEMS);
                        }
                    } else {
                        standardView.hideProgressBar(RSConstants.TOP_COMMENTED_ITEMS);
                    }
                }

                @Override
                public void onFailure(Call<RSResponse> call, Throwable t) {
                    if (!callTopCommentedItems.isCanceled())
                        standardView.hideProgressBar(RSConstants.TOP_COMMENTED_ITEMS);
                }
            });
            // test
        }
    }

    @Override
    public void loadTopFollowedItems(RSRequestListItem rsRequestListItem, Context context) {
        if (standardView != null) {
            standardView.showProgressBar(RSConstants.TOP_FOLLOWED_ITEMS);
            callTopFollowedItems = WebService.getInstance().getApi().loadTopFollowedItems(RSSessionToken.getUsergestToken(), rsRequestListItem);
            callTopFollowedItems.enqueue(new Callback<RSResponse>() {
                @Override
                public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                    if (response.body() != null) {
                        if (response.body().getStatus() == RSConstants.CODE_TOKEN_EXPIRED) {
                            RSSession.Reconnecter();
                            loadTopFollowedItems(rsRequestListItem, context);
                        } else {
                            if (response.body().getStatus() == 1) {
                                standardView.onSuccess(RSConstants.TOP_FOLLOWED_ITEMS, response.body().getData());
                            } else if (response.body().getStatus() == 0) {
                                standardView.onFailure(RSConstants.TOP_FOLLOWED_ITEMS);
                            }
                            standardView.hideProgressBar(RSConstants.TOP_FOLLOWED_ITEMS);
                        }
                    } else {
                            standardView.hideProgressBar(RSConstants.TOP_FOLLOWED_ITEMS);
                    }
                }

                @Override
                public void onFailure(Call<RSResponse> call, Throwable t) {
                    if (!callTopFollowedItems.isCanceled())
                        standardView.hideProgressBar(RSConstants.TOP_FOLLOWED_ITEMS);
                }
            });
        }
    }

    @Override
    public void loadItemCreated(RSRequestListItem rsRequestListItem, Context context) {
        if (standardView != null) {
            standardView.showProgressBar(RSConstants.ITEM_CREATED);
            callItemCreated = WebService.getInstance().getApi().loadItemCreated(RSSessionToken.getUsergestToken(), rsRequestListItem);
            callItemCreated.enqueue(new Callback<RSResponse>() {
                @Override
                public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                    if (response.body() != null) {
                        if (response.body().getStatus() == RSConstants.CODE_TOKEN_EXPIRED) {
                            RSSession.Reconnecter();
                            loadItemCreated(rsRequestListItem, context);
                        } else {
                            if (response.body().getStatus() == 1) {
                                standardView.onSuccess(RSConstants.ITEM_CREATED, response.body().getData());
                            } else if (response.body().getStatus() == 0) {
                                standardView.onFailure(RSConstants.ITEM_CREATED);
                            }
                            standardView.hideProgressBar(RSConstants.ITEM_CREATED);
                        }
                    } else {
                        standardView.hideProgressBar(RSConstants.ITEM_CREATED);
                    }
                }

                @Override
                public void onFailure(Call<RSResponse> call, Throwable t) {
                    if (!callItemCreated.isCanceled())
                        standardView.hideProgressBar(RSConstants.ITEM_CREATED);
                }
            });
        }
    }

    @Override
    public void loadItemOwned(RSRequestListItem rsRequestListItem, Context context) {
        if (standardView != null) {
            standardView.showProgressBar(RSConstants.ITEM_OWNED);
            callItemOwned = WebService.getInstance().getApi().loadItemOwned(RSSessionToken.getUsergestToken(), rsRequestListItem);
            callItemOwned.enqueue(new Callback<RSResponse>() {
                @Override
                public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                    if (response.body() != null) {
                        if (response.body().getStatus() == RSConstants.CODE_TOKEN_EXPIRED) {
                            RSSession.Reconnecter();
                            loadItemOwned(rsRequestListItem, context);
                        } else {
                            if (response.body().getStatus() == 1) {
                                standardView.onSuccess(RSConstants.ITEM_OWNED, response.body().getData());
                            } else if (response.body().getStatus() == 0) {
                                standardView.onFailure(RSConstants.ITEM_OWNED);
                            }
                            standardView.hideProgressBar(RSConstants.ITEM_OWNED);
                        }
                    } else {
                            standardView.hideProgressBar(RSConstants.ITEM_OWNED);
                    }
                }

                @Override
                public void onFailure(Call<RSResponse> call, Throwable t) {
                    if (!callItemOwned.isCanceled())
                        standardView.hideProgressBar(RSConstants.ITEM_OWNED);
                }
            });
        }
    }

    @Override
    public void loadItemFollowed(RSRequestListItem rsRequestListItem , Context context) {
        if (standardView != null) {
            standardView.showProgressBar(RSConstants.ITEM_FOLLOWED);
            callItemFollowed = WebService.getInstance().getApi().loadItemFollowed(RSSessionToken.getUsergestToken(), rsRequestListItem);
            callItemFollowed.enqueue(new Callback<RSResponse>() {
                @Override
                public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                    if (response.body() != null) {
                        if (response.body().getStatus() == RSConstants.CODE_TOKEN_EXPIRED) {
                            RSSession.Reconnecter();
                            loadItemFollowed(rsRequestListItem, context);
                        } else {
                            if (response.body().getStatus() == 1) {
                                standardView.onSuccess(RSConstants.ITEM_FOLLOWED, response.body().getData());
                            } else if (response.body().getStatus() == 0) {
                                standardView.onFailure(RSConstants.ITEM_FOLLOWED);
                            }
                            standardView.hideProgressBar(RSConstants.ITEM_FOLLOWED);
                        }
                    } else {
                           standardView.hideProgressBar(RSConstants.ITEM_FOLLOWED);
                    }
                }

                @Override
                public void onFailure(Call<RSResponse> call, Throwable t) {
                    if (!callItemFollowed.isCanceled())
                        standardView.hideProgressBar(RSConstants.ITEM_FOLLOWED);
                }
            });
        }
    }

    @Override
    public void loadMyEvals(RSRequestListItem rsRequestListItem, Context context) {
        if (RSNetwork.isConnected(context)) {
            if (standardView != null) {
                standardView.showProgressBar(RSConstants.MY_EVALS);
                callMyEvals = WebService.getInstance().getApi().loadMyEvals(RSSessionToken.getUsergestToken(), rsRequestListItem);
                callMyEvals.enqueue(new Callback<RSResponse>() {
                    @Override
                    public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                        if (response.body() != null) {
                            if (response.body().getStatus() == RSConstants.CODE_TOKEN_EXPIRED) {
                                RSSession.Reconnecter();
                                standardView.hideProgressBar(RSConstants.MY_EVALS);
                                loadMyEvals(rsRequestListItem, context);
                            } else {
                                if (response.body().getStatus() == 1) {
                                    standardView.onSuccess(RSConstants.MY_EVALS, response.body().getData());
                                } else if (response.body().getStatus() == 0) {
                                    standardView.onError(RSConstants.MY_EVALS);
                                }
                                standardView.hideProgressBar(RSConstants.MY_EVALS);
                            }
                        } else {
                            standardView.hideProgressBar(RSConstants.MY_EVALS);
                        }
                    }

                    @Override
                    public void onFailure(Call<RSResponse> call, Throwable t) {
                        if (!callMyEvals.isCanceled()) {
                            standardView.onFailure(RSConstants.MY_EVALS);
                            standardView.hideProgressBar(RSConstants.MY_EVALS);
                        }
                    }
                });
            }
        } else {
            standardView.onOffLine();
        }
    }

    @Override
    public void loadCategoriesList(String lang, Context context) {
        if (RSNetwork.isConnected(context)) {
            if (standardView != null) {
                standardView.showProgressBar(RSConstants.LOAD_CATEGORIES);
                callCategoriesList = WebService.getInstance().getApi().loadCategoriesList(RSSessionToken.getUsergestToken(), lang);
                callCategoriesList.enqueue(new Callback<RSResponse>() {
                    @Override
                    public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                        if (response.body() != null) {
                            if (response.body().getStatus() == RSConstants.CODE_TOKEN_EXPIRED) {
                                RSSession.Reconnecter();
                                standardView.hideProgressBar(RSConstants.LOAD_CATEGORIES);
                                loadCategoriesList(lang, context);
                            } else {
                                if (response.body().getStatus() == 1) {
                                    standardView.onSuccess(RSConstants.LOAD_CATEGORIES, response.body().getData());
                                } else if (response.body().getStatus() == 0) {
                                    standardView.onFailure(RSConstants.LOAD_CATEGORIES);
                                }
                                standardView.hideProgressBar(RSConstants.LOAD_CATEGORIES);
                            }
                        } else {
                            standardView.hideProgressBar(RSConstants.LOAD_CATEGORIES);
                        }
                    }

                    @Override
                    public void onFailure(Call<RSResponse> call, Throwable t) {
                        if (!callCategoriesList.isCanceled()) {
                            standardView.hideProgressBar(RSConstants.LOAD_CATEGORIES);
                        }
                    }
                });
            }
        } else {
            standardView.onOffLine();
        }
    }

    @Override
    public void followItem(RSFollow rsFollow, Context context) {
        if (RSNetwork.isConnected(context)) {
            if (standardView != null) {
                callFollowItem = WebService.getInstance().getApi().followItem(RSSessionToken.getUsergestToken(), rsFollow);
                callFollowItem.enqueue(new Callback<RSResponse>() {
                    @Override
                    public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                        if (response.body() != null) {
                            if (response.body().getStatus() == RSConstants.CODE_TOKEN_EXPIRED) {
                                RSSession.Reconnecter();
                                followItem(rsFollow, context);
                            } else {
                                if (response.body().getStatus() == 1) {
                                    standardView.onSuccess(RSConstants.FOLLOW_ITEM, "1");
                                } else if (response.body().getStatus() == 0) {
                                    standardView.onSuccess(RSConstants.FOLLOW_ITEM, "0");
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<RSResponse> call, Throwable t) {
                        if (!callFollowItem.isCanceled()) {

                        }
                    }
                });
            }
        } else {
            standardView.onOffLine();
        }
    }

    @Override
    public void unfollowItem(RSFollow rsFollow, Context context) {
        if (RSNetwork.isConnected(context)) {
            if (standardView != null) {
                callUnfollowItem = WebService.getInstance().getApi().unfollowItem(RSSessionToken.getUsergestToken(), rsFollow);
                callUnfollowItem.enqueue(new Callback<RSResponse>() {
                    @Override
                    public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                        if (response.body() != null) {
                            if (response.body().getStatus() == RSConstants.CODE_TOKEN_EXPIRED) {
                                RSSession.Reconnecter();
                                unfollowItem(rsFollow, context);
                            } else {
                                if (response.body().getStatus() == 1) {
                                    standardView.onSuccess(RSConstants.UNFOLLOW_ITEM, null);
                                } else if (response.body().getStatus() == 0) {
                                    standardView.onFailure(RSConstants.UNFOLLOW_ITEM);
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<RSResponse> call, Throwable t) {
                        if (!callUnfollowItem.isCanceled()) {
                        }
                    }
                });
            }
        } else {
            standardView.onOffLine();
        }
    }

    @Override
    public void loadItemComments(RSRequestItemData rsRequestItemData, Context context) {
        if (RSNetwork.isConnected(context)) {
            if (standardView != null) {
                standardView.showProgressBar(RSConstants.ITEM_COMMENTS);
                callItemComments = WebService.getInstance().getApi().loadItemComments(RSSessionToken.getUsergestToken(), rsRequestItemData);
                callItemComments.enqueue(new Callback<RSResponse>() {
                    @Override
                    public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {

                            if (response.body() != null) {
                            if (response.body().getStatus() == RSConstants.CODE_TOKEN_EXPIRED) {
                                RSSession.Reconnecter();
                                standardView.hideProgressBar(RSConstants.ITEM_COMMENTS);
                                loadItemComments(rsRequestItemData, context);
                            } else {

                                if (response.body().getStatus() == 1) {
                                    standardView.onSuccess(RSConstants.ITEM_COMMENTS, response.body().getData());

                                } else if (response.body().getStatus() == 0) {
                                    standardView.onFailure(RSConstants.ITEM_COMMENTS);
                                }
                                standardView.hideProgressBar(RSConstants.ITEM_COMMENTS);
                            }
                        } else {
                            standardView.hideProgressBar(RSConstants.ITEM_COMMENTS);
                        }
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
        } else {
            standardView.onOffLine();
        }
    }

    @Override
    public void loadItemCommentsByUser(RSRequestItemData rsRequestItemData, Context context) {
        if (RSNetwork.isConnected(context)) {
            if (standardView != null) {
                standardView.showProgressBar(RSConstants.ITEM_COMMENTS_BY_USER);
                callItemCommentsByUser = WebService.getInstance().getApi().loadItemCommentsByUser(RSSessionToken.getUsergestToken(), rsRequestItemData);
                callItemCommentsByUser.enqueue(new Callback<RSResponse>() {
                    @Override
                    public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                        if (response.body() != null) {
                            if (response.body().getStatus() == RSConstants.CODE_TOKEN_EXPIRED) {
                                RSSession.Reconnecter();
                                standardView.hideProgressBar(RSConstants.ITEM_COMMENTS_BY_USER);
                                loadItemCommentsByUser(rsRequestItemData, context);
                            } else {
                                if (response.body().getStatus() == 1) {
                                    standardView.onSuccess(RSConstants.ITEM_COMMENTS_BY_USER, response.body().getData());
                                } else if (response.body().getStatus() == 0) {
                                    standardView.onFailure(RSConstants.ITEM_COMMENTS_BY_USER);
                                }
                                standardView.hideProgressBar(RSConstants.ITEM_COMMENTS_BY_USER);
                            }
                        } else {
                            standardView.hideProgressBar(RSConstants.ITEM_COMMENTS_BY_USER);
                        }
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
        } else {
            standardView.onOffLine();
        }
    }

    @Override
    public void loadItemPix(RSRequestItemData rsRequestItemData, Context context) {
        if (RSNetwork.isConnected(context)) {
            if (standardView != null) {
                standardView.showProgressBar(RSConstants.ITEM_PIX);
                callItemPix = WebService.getInstance().getApi().loadItemPix(RSSessionToken.getUsergestToken(), rsRequestItemData);
                callItemPix.enqueue(new Callback<RSResponse>() {
                    @Override
                    public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                        if (response.body() != null) {
                            if (response.body().getStatus() == RSConstants.CODE_TOKEN_EXPIRED) {
                                RSSession.Reconnecter();
                                standardView.hideProgressBar(RSConstants.ITEM_PIX);
                                loadItemPix(rsRequestItemData, context);
                            } else {

                                if (response.body().getStatus() == 1) {
                                    standardView.onSuccess(RSConstants.ITEM_PIX, response.body().getData());
                                } else if (response.body().getStatus() == 0) {
                                    standardView.onFailure(RSConstants.ITEM_PIX);
                                }
                                standardView.hideProgressBar(RSConstants.ITEM_PIX);
                            }
                        } else {
                            standardView.hideProgressBar(RSConstants.ITEM_PIX);
                        }
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
        } else {
            standardView.onOffLine();
        }
    }

    @Override
    public void loadItemPixByUser(RSRequestItemData rsRequestItemData, Context context) {
        if (RSNetwork.isConnected(context)) {
            if (standardView != null) {
                standardView.showProgressBar(RSConstants.ITEM_PIX_BY_USER);
                callItemPixByUser = WebService.getInstance().getApi().loadItemPixByUser(RSSessionToken.getUsergestToken(), rsRequestItemData);
                callItemPixByUser.enqueue(new Callback<RSResponse>() {
                    @Override
                    public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                        if (response.body() != null) {
                            if (response.body().getStatus() == RSConstants.CODE_TOKEN_EXPIRED) {
                                RSSession.Reconnecter();
                                standardView.hideProgressBar(RSConstants.ITEM_PIX_BY_USER);
                                loadItemPixByUser(rsRequestItemData, context);
                            } else {
                                if (response.body().getStatus() == 1) {
                                    standardView.onSuccess(RSConstants.ITEM_PIX_BY_USER, response.body().getData());
                                } else if (response.body().getStatus() == 0) {
                                    standardView.onFailure(RSConstants.ITEM_PIX_BY_USER);
                                }
                                standardView.hideProgressBar(RSConstants.ITEM_PIX_BY_USER);
                            }
                        } else {
                            standardView.hideProgressBar(RSConstants.ITEM_PIX_BY_USER);
                        }
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
        } else {
            standardView.onOffLine();
        }
    }

    @Override
    public void deleteComment(String commentId, String itemId, Context context) {
        if (RSNetwork.isConnected(context)) {
            if (standardView != null) {
                standardView.showProgressBar(RSConstants.DELETE_COMMENT);
                callDeleteComment = WebService.getInstance().getApi().deleteComment(RSSessionToken.getUsergestToken(), commentId, itemId);
                callDeleteComment.enqueue(new Callback<RSResponse>() {
                    @Override
                    public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                        if (response.body() != null) {
                            if (response.body().getStatus() == RSConstants.CODE_TOKEN_EXPIRED) {
                                RSSession.Reconnecter();
                                standardView.hideProgressBar(RSConstants.DELETE_COMMENT);
                                deleteComment(commentId, itemId, context);
                            } else {
                                if (response.body().getStatus() == 1) {
                                    standardView.onSuccess(RSConstants.DELETE_COMMENT, commentId);
                                } else if (response.body().getStatus() == 0) {
                                    standardView.onFailure(RSConstants.DELETE_COMMENT);
                                }
                                standardView.hideProgressBar(RSConstants.DELETE_COMMENT);
                            }
                        } else {
                            standardView.hideProgressBar(RSConstants.DELETE_COMMENT);
                        }
                    }

                    @Override
                    public void onFailure(Call<RSResponse> call, Throwable t) {
                        if (!callDeleteComment.isCanceled()) {
                            standardView.hideProgressBar(RSConstants.DELETE_COMMENT);
                            standardView.showMessage(RSConstants.DELETE_COMMENT, "failure");
                        }
                    }
                });
            }
        } else {
            standardView.onOffLine();
        }
    }

    @Override
    public void deletePicture(String pictureId, String itemId, Context context) {
        if (RSNetwork.isConnected(context)) {
            if (standardView != null) {
                standardView.showProgressBar(RSConstants.DELETE_PICTURE);
                callDeletePic = WebService.getInstance().getApi().deletePicture(RSSessionToken.getUsergestToken(), pictureId, itemId);
                callDeletePic.enqueue(new Callback<RSResponse>() {
                    @Override
                    public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                        if (response.body() != null) {
                            if (response.body().getStatus() == RSConstants.CODE_TOKEN_EXPIRED) {
                                RSSession.Reconnecter();
                                standardView.hideProgressBar(RSConstants.DELETE_PICTURE);
                                deletePicture(pictureId, itemId, context);
                            } else {
                                if (response.body().getStatus() == 1) {
                                    standardView.onSuccess(RSConstants.DELETE_PICTURE, pictureId);
                                } else if (response.body().getStatus() == 0) {
                                    standardView.onFailure(RSConstants.DELETE_PICTURE);
                                }
                                standardView.hideProgressBar(RSConstants.DELETE_PICTURE);
                            }
                        } else {
                            standardView.hideProgressBar(RSConstants.DELETE_PICTURE);
                        }
                    }

                    @Override
                    public void onFailure(Call<RSResponse> call, Throwable t) {
                        if (!callDeletePic.isCanceled()) {
                            standardView.hideProgressBar(RSConstants.DELETE_PICTURE);
                            standardView.showMessage(RSConstants.DELETE_PICTURE, "failure");
                        }
                    }
                });
            }
        } else {
            standardView.onOffLine();
        }
    }

    @Override
    public void onDestroyItem(Context context) {

        if (callLoadItem != null)
            if (callLoadItem.isExecuted())
                callLoadItem.cancel();

        if (callTopRankedItems != null) {
            if (callTopRankedItems.isExecuted()) {
                callTopRankedItems.cancel();
            }
        }

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

        if (callDeleteComment != null)
            if (callDeleteComment.isExecuted())
                callDeleteComment.cancel();

        if (callDeletePic != null)
            if (callDeletePic.isExecuted())
                callDeletePic.cancel();

        standardView = null;
    }
}
