package rankstop.steeringit.com.rankstop.MVP.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import rankstop.steeringit.com.rankstop.MVP.presenter.RSPresenter;
import rankstop.steeringit.com.rankstop.MVP.view.RSView;
import rankstop.steeringit.com.rankstop.data.model.network.RSResponse;
import rankstop.steeringit.com.rankstop.data.model.network.RSUpdateItem;
import rankstop.steeringit.com.rankstop.data.webservices.WebService;
import rankstop.steeringit.com.rankstop.utils.Helpers;
import rankstop.steeringit.com.rankstop.utils.RSConstants;
import rankstop.steeringit.com.rankstop.utils.RSNetwork;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PresenterUpdateItemImpl implements RSPresenter.UpdateItemPresenter {

    private RSView.UpdateItemView updateItemView;

    private Call<RSResponse> callUpdateItem;
    private Context context;

    public PresenterUpdateItemImpl(RSView.UpdateItemView updateItemView, Context context) {
        this.updateItemView = updateItemView;
        this.context = context;
    }

    @Override
    public void updateItem(RSUpdateItem rsUpdateItem) {
        if (RSNetwork.isConnected()) {
            if (updateItemView != null) {
                updateItemView.showProgressBar();
                List<MultipartBody.Part> parts = new ArrayList<>();
                for (int i = 0; i < rsUpdateItem.getGallery().size(); i++) {
                    parts.add(Helpers.prepareFilePart("gallery", rsUpdateItem.getGallery().get(i), context));
                }
                callUpdateItem = WebService.getInstance().getApi().updateItem(
                        parts,
                        Helpers.createPartFormString(rsUpdateItem.getItemId()),
                        Helpers.createPartFormString(rsUpdateItem.getUrlFacebook()),
                        Helpers.createPartFormString(rsUpdateItem.getUrlInstagram()),
                        Helpers.createPartFormString(rsUpdateItem.getUrlTwitter()),
                        Helpers.createPartFormString(rsUpdateItem.getUrlLinkedIn()),
                        Helpers.createPartFormString(rsUpdateItem.getUrlGooglePlus()),
                        rsUpdateItem.getPicDelete()
                );
                callUpdateItem.enqueue(new Callback<RSResponse>() {
                    @Override
                    public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                        if (response.body().getStatus() == 1) {
                            updateItemView.onSuccess(RSConstants.UPDATE_ITEM, response.body().getData());
                            //updateItemView.showMessage(RSConstants.UPDATE_ITEM, response.body().getMessage());
                        } else if (response.body().getStatus() == 0) {
                            updateItemView.onError(RSConstants.UPDATE_ITEM);
                            //updateItemView.showMessage(RSConstants.UPDATE_ITEM, response.body().getMessage());
                        }
                        updateItemView.hideProgressBar();
                    }

                    @Override
                    public void onFailure(Call<RSResponse> call, Throwable t) {
                        if (!call.isCanceled()) {
                            updateItemView.onFailure(RSConstants.UPDATE_ITEM);
                            updateItemView.hideProgressBar();
                        }
                    }
                });
            }
        }else {
            updateItemView.onOffLine();
        }
    }

    @Override
    public void onDestroy() {
        if (callUpdateItem != null)
            if (callUpdateItem.isExecuted())
                callUpdateItem.cancel();

        updateItemView = null;
    }


}
