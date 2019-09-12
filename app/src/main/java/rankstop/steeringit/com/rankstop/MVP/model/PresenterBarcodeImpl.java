package com.steeringit.rankstop.MVP.model;

import android.content.Context;
import android.util.Log;

import com.steeringit.rankstop.MVP.presenter.RSPresenter;
import com.steeringit.rankstop.MVP.view.RSView;
import com.steeringit.rankstop.data.model.network.RSResponse;
import com.steeringit.rankstop.data.webservices.WebService;
import com.steeringit.rankstop.session.RSSession;
import com.steeringit.rankstop.session.RSSessionToken;
import com.steeringit.rankstop.utils.RSConstants;
import com.steeringit.rankstop.utils.RSNetwork;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PresenterBarcodeImpl implements RSPresenter.BarcodePresenter {

    private RSView.SearchByBarcodeView searchByBarcodeView;
    Call<RSResponse> callSearchBarcode ;

    public PresenterBarcodeImpl(RSView.SearchByBarcodeView searchByBarcodeView) {
        this.searchByBarcodeView = searchByBarcodeView;
    }


    @Override
    public void getItemByBarcode(String barcode , Context context) {

        if (RSNetwork.isConnected(context)) {

            if (searchByBarcodeView != null) {
                searchByBarcodeView.showProgressBar(RSConstants.SEARCH_BARCODE);
                if (RSNetwork.isConnected(context)) {
                    callSearchBarcode = WebService.getInstance().getApi().searchBarcode(RSSessionToken.getUsergestToken(), barcode);
                    callSearchBarcode.enqueue(new Callback<RSResponse>() {
                        @Override
                        public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                            if (response.code() == RSConstants.CODE_TOKEN_EXPIRED) {
                                RSSession.Reconnecter();
                                searchByBarcodeView.hideProgressBar(RSConstants.SEARCH_BARCODE);
                                getItemByBarcode(barcode, context);
                            } else {
                                if (response.body() != null) {
                                    if (response.body().getStatus() == 1) {
                                        searchByBarcodeView.onSuccess(RSConstants.SEARCH_BARCODE, response.body().getData());
                                    } else if (response.body().getStatus() == 2) {
                                        searchByBarcodeView.onFailure(RSConstants.SEARCH_BARCODE , barcode);
                                    }

                                }
                            }


                        }

                        @Override
                        public void onFailure(Call<RSResponse> call, Throwable t) {


                        }
                    });
                }
            }


        } else {
            searchByBarcodeView.onOffLine();
        }
    }
}
