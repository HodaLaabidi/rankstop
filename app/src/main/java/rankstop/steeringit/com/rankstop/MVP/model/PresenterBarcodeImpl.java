package rankstop.steeringit.com.rankstop.MVP.model;

import android.content.Context;
import android.util.Log;

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
                            Log.e("code " , response.code() +"!");
                            if (response.code() == RSConstants.CODE_TOKEN_EXPIRED) {
                                Log.e("reconnect" , "ok");
                                RSSession.Reconnecter();
                                searchByBarcodeView.hideProgressBar(RSConstants.SEARCH_BARCODE);
                                getItemByBarcode(barcode, context);
                            } else {
                                if (response.body() != null) {
                                    Log.e("search barcode" , "ok");
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
                            Log.e("onFailure" , "ok");

                        }
                    });
                }
            }


        } else {
            searchByBarcodeView.onOffLine();
        }
    }
}
