package rankstop.steeringit.com.rankstop.MVP.model;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import rankstop.steeringit.com.rankstop.MVP.presenter.RSPresenter;
import rankstop.steeringit.com.rankstop.MVP.view.RSView;
import rankstop.steeringit.com.rankstop.data.model.network.RSAddReview;
import rankstop.steeringit.com.rankstop.data.model.network.RSResponse;
import rankstop.steeringit.com.rankstop.data.webservices.WebService;
import rankstop.steeringit.com.rankstop.session.RSSession;
import rankstop.steeringit.com.rankstop.session.RSSessionToken;
import rankstop.steeringit.com.rankstop.utils.FileUtils;
import rankstop.steeringit.com.rankstop.utils.RSConstants;
import rankstop.steeringit.com.rankstop.utils.RSNetwork;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;

public class PresenterAddReviewImpl implements RSPresenter.AddReviewPresenter {

    private RSView.StandardView standardView;
    private Context context;

    private Call<RSResponse> callLoadMyEval, callLoadCategory, callAddReview, callAddItem, callUpdateReview;

    public PresenterAddReviewImpl(RSView.StandardView standardView, Context context) {
        this.standardView = standardView;
        this.context = context;
    }

    @Override
    public void loadCategory(String id, String lang, Context context) {
        if (RSNetwork.isConnected(context)) {
            if (standardView != null) {
                standardView.showProgressBar(RSConstants.LOAD_CATEGORY);
                callLoadCategory = WebService.getInstance().getApi().loadCategory(RSSessionToken.getUsergestToken(), id, lang);
                callLoadCategory.enqueue(new Callback<RSResponse>() {
                    @Override
                    public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                        if (response.code() == RSConstants.CODE_TOKEN_EXPIRED) {
                            RSSession.Reconnecter();
                            standardView.hideProgressBar(RSConstants.LOAD_CATEGORY);
                            loadCategory(id, lang, context);
                        } else {
                            if (response.body() != null) {
                                if (response.body().getStatus() == 1) {
                                    standardView.onSuccess(RSConstants.LOAD_CATEGORY, response.body().getData());
                                    standardView.showMessage(RSConstants.LOAD_CATEGORY, response.body().getMessage());
                                } else if (response.body().getStatus() == 0) {
                                    standardView.hideProgressBar(RSConstants.LOAD_CATEGORY);
                                    standardView.onError(RSConstants.LOAD_CATEGORY);
                                }
                            } else {
                                standardView.hideProgressBar(RSConstants.LOAD_CATEGORY);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<RSResponse> call, Throwable t) {
                        if (!call.isCanceled()) {
                            standardView.hideProgressBar(RSConstants.LOAD_CATEGORY);
                            standardView.onFailure(RSConstants.LOAD_CATEGORY);
                        }
                    }
                });
            }
        } else {
            standardView.onOffLine();
        }
    }

    @Override
    public void addReview(RSAddReview rsAddReview, Context context) {
        if (RSNetwork.isConnected(context)) {
            if (standardView != null) {
                standardView.showProgressBar(RSConstants.ADD_REVIEW);
                List<MultipartBody.Part> parts = new ArrayList<>();
                for (int i = 0; i < rsAddReview.getFiles().size(); i++) {
                    parts.add(prepareFilePart("files", rsAddReview.getFiles().get(i)));
                }


                callAddReview = WebService.getInstance().getApi().addReview(
                        RSSessionToken.getUsergestToken(),
                        parts,
                        createPartFormString(rsAddReview.getComment()),
                        createPartFormString(rsAddReview.getUserId()),
                        createPartFormString(rsAddReview.getItemId()),
                        rsAddReview.getEvalCri()
                );

                callAddReview.enqueue(new Callback<RSResponse>() {
                    @Override
                    public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                        if (response.code() == RSConstants.CODE_TOKEN_EXPIRED) {
                            RSSession.Reconnecter();
                            standardView.hideProgressBar(RSConstants.ADD_REVIEW);
                            addReview(rsAddReview, context);
                        } else {
                            if (response.body() != null) {
                                if (response.body().getStatus() == 1) {
                                    standardView.onSuccess(RSConstants.ADD_REVIEW, response.body().getData());
                                    standardView.showMessage(RSConstants.ADD_REVIEW, response.body().getMessage());
                                } else if (response.body().getStatus() == 0) {
                                    standardView.onError(RSConstants.ADD_REVIEW);
                                    standardView.showMessage(RSConstants.ADD_REVIEW, response.body().getMessage());
                                }
                            }
                            standardView.hideProgressBar(RSConstants.ADD_REVIEW);
                        }
                    }

                    @Override
                    public void onFailure(Call<RSResponse> call, Throwable t) {
                        if (!call.isCanceled()) {
                            standardView.showMessage(RSConstants.ADD_REVIEW, "erreur");
                            standardView.hideProgressBar(RSConstants.ADD_REVIEW);
                            standardView.onFailure(RSConstants.ADD_REVIEW);
                        }
                    }
                });
            }
        } else {
            standardView.onOffLine();
        }
    }

    @Override
    public void updateReview(RSAddReview rsAddReview, Context context) {
        if (RSNetwork.isConnected(context)) {
            if (standardView != null) {
                //standardView.showProgressBar(RSConstants.UPDATE_REVIEW);
                List<MultipartBody.Part> parts = new ArrayList<>();
                for (int i = 0; i < rsAddReview.getFiles().size(); i++) {
                    parts.add(prepareFilePart("files", rsAddReview.getFiles().get(i)));
                }

                callUpdateReview = WebService.getInstance().getApi().updateReview(
                        RSSessionToken.getUsergestToken(),
                        parts,
                        createPartFormString(rsAddReview.getComment()),
                        createPartFormString(rsAddReview.getUserId()),
                        createPartFormString(rsAddReview.getItemId()),
                        createPartFormString(rsAddReview.getEvalId())
                );

                callUpdateReview.enqueue(new Callback<RSResponse>() {
                    @Override
                    public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                        if (response.code() == RSConstants.CODE_TOKEN_EXPIRED) {
                            RSSession.Reconnecter();
                            //standardView.showProgressBar(RSConstants.UPDATE_REVIEW);
                            updateReview(rsAddReview, context);
                        } else {
                            if (response.body() != null) {
                                if (response.body().getStatus() == 1) {

                                    standardView.onSuccess(RSConstants.UPDATE_REVIEW, response.body().getData());
                                    standardView.showMessage(RSConstants.UPDATE_REVIEW, response.body().getMessage());
                                } else if (response.body().getStatus() == 0) {

                                    standardView.onError(RSConstants.UPDATE_REVIEW);
                                    standardView.showMessage(RSConstants.UPDATE_REVIEW, response.body().getMessage());
                                }
                            }
                            //standardView.hideProgressBar(RSConstants.UPDATE_REVIEW);
                        }
                    }

                    @Override
                    public void onFailure(Call<RSResponse> call, Throwable t) {

                        if (!call.isCanceled()) {
                            standardView.onFailure(RSConstants.UPDATE_REVIEW);
                            //standardView.showMessage(RSConstants.UPDATE_REVIEW, "erreur");
                            standardView.hideProgressBar(RSConstants.UPDATE_REVIEW);
                        }
                    }
                });
            }
        } else {
            standardView.onOffLine();
        }
    }

    @Override
    public void addItem(RSAddReview rsAddItem, Context context) {
        if (RSNetwork.isConnected(context)) {
            if (standardView != null) {
                standardView.showProgressBar(RSConstants.ADD_ITEM);
                List<MultipartBody.Part> parts = new ArrayList<>();
                for (int i = 0; i < rsAddItem.getFiles().size(); i++) {
                    parts.add(prepareFilePart("files", rsAddItem.getFiles().get(i)));
                }

                callAddItem = WebService.getInstance().getApi().addItem(
                        RSSessionToken.getUsergestToken(),
                        parts,
                        createPartFormString(rsAddItem.getUserId()),
                        rsAddItem.getEvalCri(),
                        createPartFormString(rsAddItem.getCategoryId()),
                        createPartFormString(rsAddItem.getDescription()),
                        createPartFormString(rsAddItem.getTitle()),
                        createPartFormString(rsAddItem.getBarcode()),
                        createPartFormString(rsAddItem.getAddress()),
                        createPartFormString(rsAddItem.getPhone()),
                        createPartFormString(rsAddItem.getLatitude()),
                        createPartFormString(rsAddItem.getLongitude()),
                        createPartFormString(rsAddItem.getCity()),
                        createPartFormString(rsAddItem.getGovernorate()),
                        createPartFormString(rsAddItem.getCountry()),
                        createPartFormString(rsAddItem.getComment())

                );
                callAddItem.enqueue(new Callback<RSResponse>() {
                    @Override
                    public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                        if (response.code() == RSConstants.CODE_TOKEN_EXPIRED) {
                            RSSession.Reconnecter();
                            standardView.hideProgressBar(RSConstants.ADD_ITEM);
                            addItem(rsAddItem, context);
                        } else {

                            if (response.body() != null) {
                                if (response.body().getStatus() == 1) {

                                    standardView.onSuccess(RSConstants.ADD_ITEM, response.body().getData());
                                    standardView.showMessage(RSConstants.ADD_ITEM, response.body().getMessage());
                                } else if (response.body().getStatus() == 0) {

                                    standardView.onError(RSConstants.ADD_ITEM);
                                    standardView.showMessage(RSConstants.ADD_ITEM, response.body().getMessage());
                                }
                            }
                            standardView.hideProgressBar(RSConstants.ADD_ITEM);
                        }
                    }

                    @Override
                    public void onFailure(Call<RSResponse> call, Throwable t) {

                        if (!call.isCanceled()) {
                            standardView.onFailure(RSConstants.ADD_ITEM);
                            standardView.showMessage(RSConstants.ADD_ITEM, "erreur");
                            standardView.hideProgressBar(RSConstants.ADD_ITEM);
                        }
                    }
                });
            }
        } else {
            standardView.onOffLine();
        }
    }

    @Override
    public void loadMyEval(String userId, String itemId, Context context) {
        if (RSNetwork.isConnected(context)) {
            if (standardView != null) {
                callLoadMyEval = WebService.getInstance().getApi().loadMyEval(RSSessionToken.getUsergestToken(), userId, itemId);
                callLoadMyEval.enqueue(new Callback<RSResponse>() {
                    @Override
                    public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                        if (response.code() == RSConstants.CODE_TOKEN_EXPIRED) {
                            RSSession.Reconnecter();
                            standardView.hideProgressBar(RSConstants.LOAD_MY_EVAL);
                            loadMyEval(userId,itemId, context);
                        } else {
                            if (response.body() != null) {
                                if (response.body().getStatus() == 1 || response.body().getStatus() == 2) {
                                    standardView.onSuccess(RSConstants.LOAD_MY_EVAL, response.body().getData());
                                    //standardView.showMessage(RSConstants.LOAD_MY_EVAL, response.body().getMessage());
                                } else if (response.body().getStatus() == 0) {
                                    standardView.onError(RSConstants.LOAD_MY_EVAL);
                                }
                            }
                            standardView.hideProgressBar(RSConstants.LOAD_MY_EVAL);
                        }
                    }

                    @Override
                    public void onFailure(Call<RSResponse> call, Throwable t) {
                        if (!call.isCanceled()) {
                            standardView.hideProgressBar(RSConstants.LOAD_MY_EVAL);
                            standardView.onFailure(RSConstants.LOAD_MY_EVAL);
                        }
                    }
                });
            }
        } else {
            standardView.onOffLine();
        }
    }

    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        File file = FileUtils.getFile(context, fileUri);
        RequestBody requestFile = RequestBody.create(MediaType.parse(context.getContentResolver().getType(fileUri)), file);
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);

        /*/storage/emulated/0/Android/data/rankstop.steeringit.com.rankstop/files/Pictures/Screenshot_20190206-104436.png
            file:///storage/emulated/0/Android/data/rankstop.steeringit.com.rankstop/files/Pictures/Screenshot_20190206-104436.png*/
    }

    private RequestBody createPartFormString(String value) {
        RequestBody requestBody = RequestBody.create(MultipartBody.FORM, value);
        return requestBody ;

    }

    @Override
    public void onDestroy(Context context) {
        if (callLoadMyEval != null)
            if (callLoadMyEval.isExecuted())
                callLoadMyEval.cancel();

        if (callLoadCategory != null)
            if (callLoadCategory.isExecuted())
                callLoadCategory.cancel();

        if (callAddReview != null)
            if (callAddReview.isExecuted())
                callAddReview.cancel();

        if (callAddItem != null)
            if (callAddItem.isExecuted())
                callAddItem.cancel();

        if (callUpdateReview != null)
            if (callUpdateReview.isExecuted())
                callUpdateReview.cancel();

        standardView = null;
        context = null;
    }

}
