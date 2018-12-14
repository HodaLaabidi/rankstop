package rankstop.steeringit.com.rankstop.MVP.model;

import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rankstop.steeringit.com.rankstop.MVP.presenter.RSPresenter;
import rankstop.steeringit.com.rankstop.MVP.view.RSView;
import rankstop.steeringit.com.rankstop.data.model.custom.RSAddReview;
import rankstop.steeringit.com.rankstop.data.model.custom.RSResponse;
import rankstop.steeringit.com.rankstop.data.webservices.WebService;
import rankstop.steeringit.com.rankstop.utils.FileUtils;
import rankstop.steeringit.com.rankstop.utils.RSConstants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PresenterAddReviewImpl implements RSPresenter.AddReviewPresenter {

    private RSView.StandardView standardView;
    private Context context;

    private Call<RSResponse> callLoadMyEval, callLoadCategory, calladdReview, calladdItem, callUpdateReview;

    public PresenterAddReviewImpl(RSView.StandardView standardView, Context context) {
        this.standardView = standardView;
        this.context = context;
    }

    @Override
    public void loadCategory(String id) {
        //standardView.showProgressBar(RSConstants.LOAD_CATEGORY);
        callLoadCategory = WebService.getInstance().getApi().loadCategory(id);
        callLoadCategory.enqueue(new Callback<RSResponse>() {
            @Override
            public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                if (response.body().getStatus() == 1) {
                    standardView.onSuccess(RSConstants.LOAD_CATEGORY, response.body().getData());
                    //standardView.showMessage(RSConstants.LOAD_CATEGORY, response.body().getMessage());
                } else if (response.body().getStatus() == 0) {
                    standardView.onFailure(RSConstants.LOAD_CATEGORY);
                }
                //standardView.hideProgressBar(RSConstants.LOAD_CATEGORY);
            }

            @Override
            public void onFailure(Call<RSResponse> call, Throwable t) {
                if (!call.isCanceled()) {
                    //standardView.hideProgressBar(RSConstants.LOAD_CATEGORY);
                }
            }
        });
    }

    @Override
    public void addReview(RSAddReview rsAddReview) {
        //standardView.showProgressBar(RSConstants.ADD_REVIEW);

        List<MultipartBody.Part> parts = new ArrayList<>();

        for (int i = 0; i < rsAddReview.getFiles().size(); i++) {
            parts.add(prepareFilePart("files", rsAddReview.getFiles().get(i)));
        }

        calladdReview = WebService.getInstance().getApi().addReview(
                parts,
                createPartFormString(rsAddReview.getComment()),
                createPartFormString(rsAddReview.getUserId()),
                createPartFormString(rsAddReview.getItemId()),
                rsAddReview.getEvalCri()
        );

        calladdReview.enqueue(new Callback<RSResponse>() {
            @Override
            public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                if (response.body().getStatus() == 1) {
                    standardView.onSuccess(RSConstants.ADD_REVIEW, response.body().getData());
                    standardView.showMessage(RSConstants.ADD_REVIEW, response.body().getMessage());
                } else if (response.body().getStatus() == 0) {
                    standardView.onFailure(RSConstants.ADD_REVIEW);
                    standardView.showMessage(RSConstants.ADD_REVIEW, response.body().getMessage());
                }
                //standardView.hideProgressBar(RSConstants.ADD_REVIEW);
            }

            @Override
            public void onFailure(Call<RSResponse> call, Throwable t) {
                if (!call.isCanceled()) {
                    standardView.showMessage(RSConstants.ADD_REVIEW, "erreur");
                }
            }
        });
    }

    @Override
    public void updateReview(RSAddReview rsAddReview) {
        List<MultipartBody.Part> parts = new ArrayList<>();

        for (int i = 0; i < rsAddReview.getFiles().size(); i++) {
            parts.add(prepareFilePart("files", rsAddReview.getFiles().get(i)));
        }

        callUpdateReview = WebService.getInstance().getApi().updateReview(
                parts,
                createPartFormString(rsAddReview.getComment()),
                createPartFormString(rsAddReview.getUserId()),
                createPartFormString(rsAddReview.getItemId()),
                createPartFormString(rsAddReview.getEvalId())
        );

        callUpdateReview.enqueue(new Callback<RSResponse>() {
            @Override
            public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                if (response.body().getStatus() == 1) {
                    standardView.onSuccess(RSConstants.UPDATE_REVIEW, response.body().getData());
                    standardView.showMessage(RSConstants.UPDATE_REVIEW, response.body().getMessage());
                } else if (response.body().getStatus() == 0) {
                    standardView.onFailure(RSConstants.UPDATE_REVIEW);
                    standardView.showMessage(RSConstants.UPDATE_REVIEW, response.body().getMessage());
                }
                //standardView.hideProgressBar(RSConstants.UPDATE_REVIEW);
            }

            @Override
            public void onFailure(Call<RSResponse> call, Throwable t) {
                if (!call.isCanceled()) {
                    standardView.showMessage(RSConstants.UPDATE_REVIEW, "erreur");
                }
            }
        });
    }

    @Override
    public void addItem(RSAddReview rsAddItem) {
        List<MultipartBody.Part> parts = new ArrayList<>();

        for (int i = 0; i < rsAddItem.getFiles().size(); i++) {
            parts.add(prepareFilePart("files", rsAddItem.getFiles().get(i)));
        }

        calladdItem = WebService.getInstance().getApi().addItem(
                parts,
                createPartFormString(rsAddItem.getUserId()),
                rsAddItem.getEvalCri(),
                createPartFormString(rsAddItem.getCategoryId()),
                createPartFormString(rsAddItem.getDescription()),
                createPartFormString(rsAddItem.getTitle()),
                createPartFormString(rsAddItem.getAddress()),
                createPartFormString(rsAddItem.getLatitude()),
                createPartFormString(rsAddItem.getLongitude()),
                createPartFormString(rsAddItem.getComment())
        );
        calladdItem.enqueue(new Callback<RSResponse>() {
            @Override
            public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                if (response.body().getStatus() == 1) {
                    standardView.onSuccess(RSConstants.ADD_ITEM, response.body().getData());
                    standardView.showMessage(RSConstants.ADD_ITEM, response.body().getMessage());
                } else if (response.body().getStatus() == 0) {
                    standardView.onFailure(RSConstants.ADD_ITEM);
                    standardView.showMessage(RSConstants.ADD_ITEM, response.body().getMessage());
                }
                //standardView.hideProgressBar(RSConstants.ADD_ITEM);
            }

            @Override
            public void onFailure(Call<RSResponse> call, Throwable t) {
                if (!call.isCanceled()) {
                    standardView.showMessage(RSConstants.ADD_ITEM, "erreur");
                }
            }
        });
    }

    @Override
    public void loadMyEval(String userId, String itemId) {
        //standardView.showProgressBar(RSConstants.LOAD_MY_EVAL);

        callLoadMyEval = WebService.getInstance().getApi().loadMyEval(userId, itemId);
        callLoadMyEval.enqueue(new Callback<RSResponse>() {
            @Override
            public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                if (response.body().getStatus() == 1 || response.body().getStatus() == 2) {
                    standardView.onSuccess(RSConstants.LOAD_MY_EVAL, response.body().getData());
                    //standardView.showMessage(RSConstants.LOAD_MY_EVAL, response.body().getMessage());
                } else if (response.body().getStatus() == 0) {
                    standardView.onFailure(RSConstants.LOAD_MY_EVAL);
                }
                //standardView.hideProgressBar(RSConstants.LOAD_MY_EVAL);
            }

            @Override
            public void onFailure(Call<RSResponse> call, Throwable t) {
                if (!call.isCanceled()) {
                    //standardView.hideProgressBar(RSConstants.LOAD_MY_EVAL);
                }
            }
        });
    }

    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        File file = FileUtils.getFile(context, fileUri);

        RequestBody requestFile = RequestBody.create(MediaType.parse(context.getContentResolver().getType(fileUri)), file);

        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    private RequestBody createPartFormString(String value) {
        return RequestBody.create(MultipartBody.FORM, value);
    }

    @Override
    public void onDestroy() {
        if (callLoadMyEval != null)
            if (callLoadMyEval.isExecuted())
                callLoadMyEval.cancel();

        if (callLoadCategory != null)
            if (callLoadCategory.isExecuted())
                callLoadCategory.cancel();

        if (calladdReview != null)
            if (calladdReview.isExecuted())
                calladdReview.cancel();

        if (calladdItem != null)
            if (calladdItem.isExecuted())
                calladdItem.cancel();

        if (callUpdateReview != null)
            if (callUpdateReview.isExecuted())
                callUpdateReview.cancel();

        standardView = null;
    }

}
