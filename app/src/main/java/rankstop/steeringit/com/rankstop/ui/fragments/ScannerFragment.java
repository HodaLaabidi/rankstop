package com.steeringit.rankstop.ui.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.gson.Gson;
import com.google.zxing.Result;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.steeringit.rankstop.MVP.model.PresenterBarcodeImpl;
import com.steeringit.rankstop.MVP.presenter.RSPresenter;
import com.steeringit.rankstop.MVP.view.RSView;
import com.steeringit.rankstop.R;
import com.steeringit.rankstop.customviews.RSCustomToast;
import com.steeringit.rankstop.data.model.db.ItemDetails;
import com.steeringit.rankstop.data.model.db.ItemSearchedByBarcode;
import com.steeringit.rankstop.data.model.network.RSAddReview;
import com.steeringit.rankstop.data.model.network.RSNavigationData;
import com.steeringit.rankstop.session.RSSession;
import com.steeringit.rankstop.ui.activities.ContainerActivity;
import com.steeringit.rankstop.ui.callbacks.FragmentActionListener;
import com.steeringit.rankstop.ui.dialogFragment.ContactDialog;
import com.steeringit.rankstop.utils.RSConstants;
import com.steeringit.rankstop.utils.RSNetwork;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 */
public class ScannerFragment extends Fragment implements  RSView.SearchByBarcodeView{


    private FragmentActionListener fragmentActionListener;
    private RSPresenter.BarcodePresenter barcodePresenter;
    private Unbinder unbinder;
    private View rootView;
    RSNavigationData rsNavigationData ;
    Activity activity ;

    private CodeScanner mCodeScanner;
    int currentVersion = Build.VERSION.SDK_INT;
    private static final int REQUEST_CAMERA = 1;
    CodeScannerView scannerView;
    private static boolean isOpen = false ;
    @Nullable
    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @BindString(R.string.off_line)
    String offlineMsg;
    RSAddReview rsAddItem ;
    ItemDetails itemDetails ;

    public void setFragmentActionListener(FragmentActionListener fragmentActionListener) {
        this.fragmentActionListener = fragmentActionListener;
    }


    private static ScannerFragment instance;

    public static ScannerFragment getInstance() {
        if (instance == null) {
            instance = new ScannerFragment();
        }
        return instance;
    }

    public static ScannerFragment getInstance(RSAddReview rsAddItem) {


        Bundle args = new Bundle();
        args.putSerializable(RSConstants.RS_ADD_REVIEW, rsAddItem);
        if (instance == null)
            instance = new ScannerFragment();
        instance.setArguments(args);
        return instance;
    }

    public static ScannerFragment getInstance(ItemDetails itemDetails) {

        Bundle args = new Bundle();
        args.putSerializable(RSConstants.RS_UPDATE_ITEM, itemDetails);
        if (instance == null)
            instance = new ScannerFragment();
        instance.setArguments(args);
        return instance;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (this.getArguments() != null) {

            if (this.getArguments().get(RSConstants.RS_ADD_REVIEW) != null) {
                rsAddItem = (RSAddReview) this.getArguments().get(RSConstants.RS_ADD_REVIEW);
            }

            if (this.getArguments().get(RSConstants.RS_UPDATE_ITEM) != null) {
                itemDetails = (ItemDetails) this.getArguments().get(RSConstants.RS_UPDATE_ITEM);
            }
        }


        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        activity = getActivity();
        rootView = inflater.inflate(R.layout.fragment_scanner, container, false);
        unbinder = ButterKnife.bind(this, rootView);


        if (currentVersion >= Build.VERSION_CODES.M) {
            if (!checkPermission()) {
                requestPermission();
            } else {
                openScanCamera();
            }
        } else {
            openScanCamera();
        }


        return rootView;

    }

    private void openScanCamera() {

        if (scannerView == null) {
            scannerView = rootView.findViewById(R.id.scanner_view);
        }


        if (mCodeScanner == null) {
            mCodeScanner = new CodeScanner(getContext(), scannerView);
        }
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!result.getText().matches("^[0-9]+$")){
                            Toast.makeText(getContext() , R.string.invalid_barcode, Toast.LENGTH_LONG).show();
                        } else {
                            barcodePresenter.getItemByBarcode(result.getText().trim(), getContext());
                        }

                        //Toast.makeText(activity, result.getText(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
    }
    private void requestPermission() {
        ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.CAMERA}, REQUEST_CAMERA);
    }

    private boolean checkPermission(){
        return (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        bindViews();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openScanCamera();
                    isOpen = true;
                    this.onResume();

                } else {
                    scannerView.setVisibility(View.GONE);
                }

            }

        }
    }


    private void bindViews() {

        toolbar.setTitle("Scanner");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        setFragmentActionListener((ContainerActivity) getActivity());
        rsNavigationData = new RSNavigationData();
        barcodePresenter = new PresenterBarcodeImpl(ScannerFragment.this);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.rs_menu, menu);

            MenuItem item = menu.findItem(R.id.logout);
            item.setVisible(false);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.setting:
                fragmentActionListener.startFragment(SettingsFragment.getInstance(), RSConstants.FRAGMENT_SETTINGS);
                break;
            case R.id.logout:
                RSSession.cancelSession();
                ((ContainerActivity) getActivity()).manageSession(false, new RSNavigationData(RSConstants.FRAGMENT_SIGN_UP, ""));
                break;
            case R.id.history:
                fragmentActionListener.startFragment(HistoryFragment.getInstance(), RSConstants.FRAGMENT_HISTORY);
                break;
            case R.id.contact:
                openContactDialog();
                break;
            case R.id.notifications:
                fragmentActionListener.startFragment(ListNotifFragment.getInstance(), RSConstants.FRAGMENT_NOTIF);
                break;
            case R.id.profil:
                if(RSSession.isLoggedIn())
                    fragmentActionListener.startFragment(ProfileFragment.getInstance(), RSConstants.FRAGMENT_PROFILE);
                else
                    fragmentActionListener.startFragment(SignupFragment.getInstance(rsNavigationData), RSConstants.FRAGMENT_SIGN_UP);
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    private void openContactDialog() {
        ContactDialog dialog = new ContactDialog();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        dialog.show(ft, ContactDialog.TAG);
    }


    @Override
    public void onDestroyView() {
        instance = null;
        fragmentActionListener = null;
        scannerView = null;
        mCodeScanner = null;
        if (unbinder != null)
            unbinder.unbind();
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermission()) {
            openScanCamera();
            mCodeScanner.startPreview();

        }


    }

    @Override
    public void onStop(){
        super.onStop();
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
    }


    @Override
    public void onPause() {
        super.onPause();
        if (checkPermission()) {
            openScanCamera();
            mCodeScanner.releaseResources();
        }

    }


    @Override
    public void onSuccess(String target, Object data) {


        switch (target) {
            case RSConstants.SEARCH_BARCODE:
                if (RSNetwork.isConnected(getContext())) {
                    if (rsAddItem != null) {
                        Toast.makeText(getContext(), R.string.existing_barcode, Toast.LENGTH_LONG).show();
                        if (rsAddItem.getTitle() == null && rsAddItem.getPhone() == null && rsAddItem.getDescription() == null && rsAddItem.getCategoryId() == null) {
                            fragmentActionListener.startFragment(AddItemFragment.getInstance(), RSConstants.FRAGMENT_SCANNER);
                        } else {

                            fragmentActionListener.startFragment(AddItemFragment.getInstance(rsAddItem), RSConstants.FRAGMENT_SCANNER);
                        }
                        ((ContainerActivity) getActivity()).manageSession(false, new RSNavigationData(RSConstants.SEARCH_BARCODE, "", "", ""));


                    } else if (itemDetails != null) {
                        Toast.makeText(getContext(), R.string.existing_barcode, Toast.LENGTH_LONG).show();
                        ((ContainerActivity) getActivity()).manageSession(false, new RSNavigationData(RSConstants.UPDATE_BARCODE, "", "", ""));

                        fragmentActionListener.startFragment(UpdateItemFragment.getInstance(itemDetails), RSConstants.FRAGMENT_SCANNER);
                    } else {
                        ItemSearchedByBarcode itemSearchedByBarcode = new Gson().fromJson(new Gson().toJson(data), ItemSearchedByBarcode.class);
                        fragmentActionListener.startFragment(ItemDetailsFragment.getInstance(new RSNavigationData(RSConstants.EXISTING_BARCODE, "", itemSearchedByBarcode.get_id())), RSConstants.FRAGMENT_SCANNER);
                        //((ContainerActivity) getActivity()).manageSession(true, new RSNavigationData(RSConstants.EXISTING_BARCODE, ""));
                    }


                } else {
                    onOffLine();

                }

                break;
        }


    }

    @Override
    public void onFailure(String target, String barcode) {


        switch (target) {
            case RSConstants.SEARCH_BARCODE:
                if (RSNetwork.isConnected(getContext())) {
                    if (rsAddItem != null) {
                        if (rsAddItem.getTitle() == null && rsAddItem.getPhone() == null && rsAddItem.getDescription() == null && rsAddItem.getCategoryId() == null) {
                            fragmentActionListener.startFragment(AddItemFragment.getInstance(), RSConstants.FRAGMENT_SCANNER);
                        } else {

                            fragmentActionListener.startFragment(AddItemFragment.getInstance(rsAddItem), RSConstants.FRAGMENT_SCANNER);
                        }
                        ((ContainerActivity) getActivity()).manageSession(false, new RSNavigationData(RSConstants.SEARCH_BARCODE, "", "", barcode));

                    } else if (itemDetails != null) {
                        ((ContainerActivity) getActivity()).manageSession(false, new RSNavigationData(RSConstants.UPDATE_BARCODE, "", "", barcode));
                        if (barcode != null) {
                            itemDetails.setBarcode(barcode);
                        }
                        fragmentActionListener.startFragment(UpdateItemFragment.getInstance(itemDetails), RSConstants.FRAGMENT_SCANNER);
                    } else {
                        ((ContainerActivity) getActivity()).manageSession(false, new RSNavigationData(RSConstants.SEARCH_BARCODE, "", "", barcode));
                    }
                } else {
                    onOffLine();

                }


                break;
        }
    }


    @Override
    public void onError(String target) {

    }

    @Override
    public void showProgressBar(String target) {

    }

    @Override
    public void hideProgressBar(String target) {

    }

    @Override
    public void showMessage(String target, String message) {

    }

    @Override
    public void onOffLine() {
        new RSCustomToast(getActivity(), getResources().getString(R.string.error), offlineMsg, R.drawable.ic_error, RSCustomToast.ERROR).show();

    }




}
