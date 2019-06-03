package rankstop.steeringit.com.rankstop.ui.dialogFragment;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.customviews.RSTVBold;
import rankstop.steeringit.com.rankstop.customviews.RSTVMedium;
import rankstop.steeringit.com.rankstop.data.model.db.ItemDetails;
import rankstop.steeringit.com.rankstop.ui.activities.ContainerActivity;
import rankstop.steeringit.com.rankstop.ui.callbacks.FragmentActionListener;
import rankstop.steeringit.com.rankstop.utils.RSConstants;

public class ItemInfoDialog extends DialogFragment {

    private View rootView;
    private FragmentActionListener fragmentActionListener;
    private ItemDetails itemDetails;

    @BindView(R.id.tv_description)
    RSTVMedium descriptionTV;

    @BindView(R.id.tv_address)
    RSTVMedium addressTV;

    @BindView(R.id.itinerary_link)
    LinearLayout itineraryLink ;

    @BindView(R.id.tv_phone)
    RSTVMedium phoneTV;

    @BindView(R.id.tv_title)
    RSTVBold titleTV;
    @BindView(R.id.tv_goode)
    RSTVMedium goodeTV;
    @BindView(R.id.tv_bade)
    RSTVMedium badeTV;
    @BindView(R.id.tv_nutre)
    RSTVMedium nutreTV;

    @BindView(R.id.ic_facebook)
    ImageButton icFacebookBTN;

    @BindView(R.id.ic_instagram)
    ImageButton icInstagramBTN;

    @BindView(R.id.ic_linkedin)
    ImageButton icLinkedInBTN;

    @BindView(R.id.ic_twitter)
    ImageButton icTwitterBTN;

    @BindView(R.id.ic_google_plus)
    ImageButton icGooglePlusBTN;

    @OnClick(R.id.negative_btn)
    void closeDialog() {
        dismiss();
    }

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

    public static ItemInfoDialog newInstance(ItemDetails itemDetails) {
        if (instance == null) {
            instance = new ItemInfoDialog();
        }
        Bundle args = new Bundle();
        args.putSerializable(RSConstants.RS_ITEM_DETAILS, itemDetails);
        instance.setArguments(args);
        return instance;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        rootView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_item_info, null, false);
        unbinder = ButterKnife.bind(this, rootView);
        setFragmentActionListener((ContainerActivity) getActivity());

        final AlertDialog alertDialog = new AlertDialog.Builder(getContext()).setView(rootView).setCancelable(false).create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setOnShowListener(dialog -> onDialogShow(alertDialog));
        return alertDialog;
    }

    private void onDialogShow(AlertDialog dialog) {
        dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dialog_ask_login));

        itemDetails = (ItemDetails) getArguments().getSerializable(RSConstants.RS_ITEM_DETAILS);
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
        }


        descriptionTV.setText(itemDetails.getDescription());
        titleTV.setText(itemDetails.getTitle());

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
                itineraryLink.setVisibility(View.VISIBLE);
                itineraryLink.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("geo:"+itemDetails.getLocation().getLatitude()+","+itemDetails.getLocation().getLongitude()));
                        intent.setPackage("com.google.android.apps.maps");
                        startActivity(intent);

                    }
                });
            }
        } catch (Exception e) {
        }
    }

    public void setFragmentActionListener(FragmentActionListener fragmentActionListener) {
        this.fragmentActionListener = fragmentActionListener;
    }

    @Override
    public void onDestroyView() {
        rootView = null;
        instance = null;
        unbinder.unbind();
        fragmentActionListener = null;

        super.onDestroyView();
    }
}
