package rankstop.steeringit.com.rankstop.ui.dialogFragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AlertDialog;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.customviews.RSTVBold;
import rankstop.steeringit.com.rankstop.customviews.RSTVMedium;
import rankstop.steeringit.com.rankstop.data.model.db.Item;
import rankstop.steeringit.com.rankstop.data.model.db.ItemDetails;
import rankstop.steeringit.com.rankstop.ui.activities.ContainerActivity;
import rankstop.steeringit.com.rankstop.ui.callbacks.FragmentActionListener;
import rankstop.steeringit.com.rankstop.utils.RSConstants;
import rankstop.steeringit.com.rankstop.utils.WorkaroundMapFragment;

public class ItemInfoDialog extends DialogFragment implements OnMapReadyCallback {


    public static float latitude = 36.81897f;
    public static float longitude = 10.16579f;

    private View rootView;
    Marker marker;
    private FragmentActionListener fragmentActionListener;
    private ItemDetails itemDetails;

    @BindView(R.id.tv_description)
    RSTVMedium descriptionTV;

    @BindView(R.id.tv_address)
    RSTVMedium addressTV;

    GoogleMap map;


    @BindView(R.id.tv_phone)
    RSTVMedium phoneTV;

    @BindView(R.id.tv_title)
    RSTVBold titleTV;

    @BindView(R.id.tv_barcode)
    RSTVMedium barcodeTV;
    @BindView(R.id.tv_goode)
    RSTVMedium goodeTV;
    @BindView(R.id.tv_bade)
    RSTVMedium badeTV;
    @BindView(R.id.tv_nutre)
    RSTVMedium nutreTV;

    @BindView(R.id.nbr_eval)
    RSTVMedium nbrEval ;
    @BindView(R.id.nbr_followers)
    RSTVMedium nbrFollowers ;

    private static final float DEFAULT_ZOOM = 15f;
    Item item ;

    @BindView(R.id.ic_facebook)
    ImageButton icFacebookBTN;

    @BindView(R.id.ll_map_fragment)
    LinearLayout llMapFragment;

    AlertDialog alertDialog;

    @BindView(R.id.ll_barcode_scanner)
    LinearLayout llBarocdeScanner;
    @BindView(R.id.tv_label_barcode)
    RSTVMedium labelBarcode;

    @BindView(R.id.ic_instagram)
    ImageButton icInstagramBTN;

    @BindView(R.id.ic_linkedin)
    ImageButton icLinkedInBTN;

    @BindView(R.id.ic_twitter)
    ImageButton icTwitterBTN;

    @BindView(R.id.scroll_view)
    NestedScrollView scrollView;

    @BindView(R.id.ic_google_plus)
    ImageButton icGooglePlusBTN;

    @OnClick(R.id.negative_btn)
    void closeDialog() {
        dismiss();
    }

    WorkaroundMapFragment mapFragment;

    @OnClick(R.id.ic_facebook)
    void navigateToFB() {
        Intent i = new Intent(Intent.ACTION_VIEW);
        try {
            i.setData(Uri.parse(itemDetails.getUrlFacebook()));
            startActivity(i);
        } catch (Exception e) {
            i.setData(Uri.parse("https://www.facebook.com/" + itemDetails.getTitle()));
            startActivity(i);
        }
    }

    @OnClick(R.id.ic_instagram)
    void navigateToInsta() {
        Intent i = new Intent(Intent.ACTION_VIEW);
        try {
            i.setData(Uri.parse(itemDetails.getUrlInstagram()));
            startActivity(i);
        } catch (Exception e) {
            i.setData(Uri.parse("https://www.instagram.com/" + itemDetails.getTitle()));
            startActivity(i);
        }
    }

    @OnClick(R.id.ic_twitter)
    void navigateToTweet() {
        Intent i = new Intent(Intent.ACTION_VIEW);
        try {
            i.setData(Uri.parse(itemDetails.getUrlTwitter()));
            startActivity(i);
        } catch (Exception e) {
            i.setData(Uri.parse("https://twitter.com/" + itemDetails.getTitle()));
            startActivity(i);
        }
    }

    @OnClick(R.id.ic_linkedin)
    void navigateToLinkedIn() {
        Intent i = new Intent(Intent.ACTION_VIEW);
        try {
            i.setData(Uri.parse(itemDetails.getUrlLinkedIn()));
            startActivity(i);
        } catch (Exception e) {
            i.setData(Uri.parse("https://www.linkedin.com/in/" + itemDetails.getTitle()));
            startActivity(i);
        }
    }

    @OnClick(R.id.ic_google_plus)
    void navigateToGL() {
        Intent i = new Intent(Intent.ACTION_VIEW);
        try {
            i.setData(Uri.parse(itemDetails.getUrlGooglePlus()));
            startActivity(i);
        } catch (Exception e) {
            i.setData(Uri.parse("https://plus.google.com/u/0/" + itemDetails.getTitle()));
            startActivity(i);
        }
    }

    private Unbinder unbinder;

    private static ItemInfoDialog instance;

    public static ItemInfoDialog newInstance(Item item) {
        if (instance == null) {
            instance = new ItemInfoDialog();
        }
        Bundle args = new Bundle();
        args.putSerializable(RSConstants.RS_ITEM, item);
        instance.setArguments(args);
        return instance;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (rootView == null)
            rootView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_item_info, null, false);
        unbinder = ButterKnife.bind(this, rootView);
        setFragmentActionListener((ContainerActivity) getActivity());
        mapFragment = (WorkaroundMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map_item_info);
        mapFragment.getMapAsync(this);
        mapFragment.setListener(() -> scrollView.requestDisallowInterceptTouchEvent(true));
        if (alertDialog == null)
            alertDialog = new AlertDialog.Builder(getContext()).setView(rootView).setCancelable(false).create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.setOnShowListener(dialog -> onDialogShow(alertDialog));
        alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    alertDialog.cancel();
                }
                return false;
            }
        });
        return alertDialog;
    }

    private void onDialogShow(AlertDialog dialog) {
        dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dialog_ask_login));
        item = (Item) getArguments().getSerializable(RSConstants.ITEM);
        if ( item != null){
            itemDetails = item.getItemDetails();
            //Add item
            if (itemDetails != null) {
                DecimalFormat df = new DecimalFormat();
                df.setMaximumFractionDigits(2); //arrondi à 2 chiffres apres la virgules
                df.setMinimumFractionDigits(0);

                int Totale = itemDetails.getGood() + itemDetails.getBad() + itemDetails.getNeutral();

                double pourcentageGoode = ((double) itemDetails.getGood() / Totale) * 100;
                double pourcentageBade = ((double) itemDetails.getBad() / Totale) * 100;
                double pourcentageNutre = ((double) itemDetails.getNeutral() / Totale) * 100;

                //  Log.i("totale",  Double.parseDouble(df.format(pourcentageGoode))+ "//" + Double.parseDouble(df.format(pourcentageBade)) + "//" + Double.parseDouble(df.format(pourcentageNutre)));
                goodeTV.setText(String.format("%s %%", String.valueOf(df.format(pourcentageGoode))));
                badeTV.setText(String.format("%s %%", String.valueOf(df.format(pourcentageBade))));
                nutreTV.setText(String.format("%s %%", String.valueOf(df.format(pourcentageNutre))));

                descriptionTV.setText(itemDetails.getDescription());
                titleTV.setText(itemDetails.getTitle());

                if (itemDetails.getBarcode() != null) {
                    if (!itemDetails.getBarcode().trim().equalsIgnoreCase("")) {
                        barcodeTV.setVisibility(View.VISIBLE);
                        labelBarcode.setVisibility(View.VISIBLE);
                        llBarocdeScanner.setVisibility(View.VISIBLE);
                        barcodeTV.setText(itemDetails.getBarcode());
                    } else {
                        barcodeTV.setVisibility(View.GONE);
                        labelBarcode.setVisibility(View.GONE);
                        llBarocdeScanner.setVisibility(View.GONE);
                    }

                } else {
                    barcodeTV.setVisibility(View.GONE);
                    labelBarcode.setVisibility(View.GONE);
                    llBarocdeScanner.setVisibility(View.GONE);
                }

                nbrEval.setText(item.getNumberEval()+"");
                nbrFollowers.setText(item.getNumberFollows()+"");



                if (itemDetails.getUrlFacebook() != null)
                    if (!itemDetails.getUrlFacebook().equals(""))
                        icFacebookBTN.setVisibility(View.VISIBLE);

                if (itemDetails.getUrlInstagram() != null)
                    if (!itemDetails.getUrlInstagram().equals(""))
                        icInstagramBTN.setVisibility(View.VISIBLE);

                if (itemDetails.getUrlTwitter() != null)
                    if (!itemDetails.getUrlTwitter().equals(""))
                        icTwitterBTN.setVisibility(View.VISIBLE);

                if (itemDetails.getUrlGooglePlus() != null)
                    if (!itemDetails.getUrlGooglePlus().equals(""))
                        icGooglePlusBTN.setVisibility(View.VISIBLE);

                if (itemDetails.getUrlLinkedIn() != null)
                    if (!itemDetails.getUrlLinkedIn().equals(""))
                        icLinkedInBTN.setVisibility(View.VISIBLE);

                try {
                    if (!itemDetails.getPhone().equals("")) {
                        phoneTV.setVisibility(View.VISIBLE);
                        phoneTV.setText(itemDetails.getPhone());
                    }
                } catch (Exception e) {
                }

                try {
                    if (!itemDetails.getLocation().getAddress().equals("")) {
                        addressTV.setVisibility(View.VISIBLE);
                        addressTV.setText(itemDetails.getLocation().getAddress());
                        addressTV.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                latitude = itemDetails.getLocation().getLatitude();
                                longitude = itemDetails.getLocation().getLongitude();
                                LatLng latlong = new LatLng(latitude, longitude);
                                map.addMarker(new MarkerOptions().position(latlong));
                                map.moveCamera(CameraUpdateFactory.newLatLng(latlong));
                                onMapReady(map);
                                if (llMapFragment.getVisibility() == View.VISIBLE) {
                                    llMapFragment.setVisibility(View.GONE);
                                } else {
                                    llMapFragment.setVisibility(View.VISIBLE);
                                }

                            }
                        });

                    }
                } catch (Exception e) {
                }
            }
        }






    }

    public void setFragmentActionListener(FragmentActionListener fragmentActionListener) {
        this.fragmentActionListener = fragmentActionListener;
    }

    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        LatLng pp = new LatLng(latitude, longitude);


        try {
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(pp);
            if (marker != null)
                marker.remove();
            marker = map.addMarker(markerOptions);
            moveCamera(pp, DEFAULT_ZOOM, "");

        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
        }

    }


    private void moveCamera(LatLng latLng, float zoom, String title) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }


    @Override
    public void onDestroyView() {
        map.clear();
        marker = null;
        rootView = null;
        instance = null;
        unbinder.unbind();
        fragmentActionListener = null;
        if (mapFragment != null)
            getActivity().getSupportFragmentManager().beginTransaction().remove(mapFragment).commit();

        super.onDestroyView();
    }


}
