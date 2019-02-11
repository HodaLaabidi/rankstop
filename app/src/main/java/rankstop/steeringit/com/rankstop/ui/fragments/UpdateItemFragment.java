package rankstop.steeringit.com.rankstop.ui.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.florent37.expansionpanel.ExpansionLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindInt;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rankstop.steeringit.com.rankstop.MVP.model.PresenterUpdateItemImpl;
import rankstop.steeringit.com.rankstop.MVP.view.RSView;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.customviews.RSTVRegular;
import rankstop.steeringit.com.rankstop.data.model.db.ItemDetails;
import rankstop.steeringit.com.rankstop.data.model.network.RSUpdateItem;
import rankstop.steeringit.com.rankstop.ui.activities.ContainerActivity;
import rankstop.steeringit.com.rankstop.ui.activities.TakePictureActivity;
import rankstop.steeringit.com.rankstop.ui.adapter.ReviewPixAdapter;
import rankstop.steeringit.com.rankstop.ui.callbacks.FragmentActionListener;
import rankstop.steeringit.com.rankstop.ui.callbacks.RecyclerViewClickListener;
import rankstop.steeringit.com.rankstop.ui.dialogFragment.RSLoader;
import rankstop.steeringit.com.rankstop.utils.Helpers;
import rankstop.steeringit.com.rankstop.utils.HorizontalSpace;
import rankstop.steeringit.com.rankstop.utils.RSConstants;

public class UpdateItemFragment extends Fragment implements RSView.UpdateItemView {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.input_facebook)
    TextInputEditText inputFacebook;
    @BindView(R.id.input_instagram)
    TextInputEditText inputInstagram;
    @BindView(R.id.input_twitter)
    TextInputEditText inputTwitter;
    @BindView(R.id.input_google)
    TextInputEditText inputGoogle;
    @BindView(R.id.input_linkedin)
    TextInputEditText inputLinkedIn;
    @BindView(R.id.recycler_view_pix)
    RecyclerView recyclerViewPix;
    @BindView(R.id.tv_add_pix)
    RSTVRegular addPixTV;

    @BindString(R.string.update_item_title)
    String updateItemTitle;
    @BindString(R.string.item_updated_successfully_msg)
    String itemUpdatedSuccessfullyMsg;
    @BindString(R.string.max_photo_msg)
    String maxPhotoMsg;
    @BindString(R.string.off_line)
    String offlineMsg;

    @BindInt(R.integer.m_card_view)
    int marginCardView;

    @BindString(R.string.loading_msg)
    String loadingMsg;
    private RSLoader rsLoader;
    private void createLoader(){
        rsLoader = RSLoader.newInstance(loadingMsg);
        rsLoader.setCancelable(false);
    }

    @OnClick(R.id.btn_save_changes)
    public void saveChanges(){
        RSUpdateItem rsUpdateItem = new RSUpdateItem();
        rsUpdateItem.setItemId(itemDetails.get_id());
        rsUpdateItem.setUrlFacebook(inputFacebook.getText().toString());
        rsUpdateItem.setUrlInstagram(inputInstagram.getText().toString());
        rsUpdateItem.setUrlTwitter(inputTwitter.getText().toString());
        rsUpdateItem.setUrlLinkedIn(inputLinkedIn.getText().toString());
        rsUpdateItem.setUrlGooglePlus(inputGoogle.getText().toString());
        rsUpdateItem.setGallery(listNewPics);
        rsUpdateItem.setPicDelete(listDeletedPics);
        presenterUpdateItem.updateItem(rsUpdateItem);
    }

    @OnClick(R.id.btn_take_pic)
    public void addPicture(){
        if (listPics.size() < RSConstants.MAX_GALLERY_PIX)
            startActivityForResult(new Intent(getContext(), TakePictureActivity.class), RSConstants.REQUEST_CODE);
        else
            Toast.makeText(getContext(), ""+RSConstants.MAX_GALLERY_PIX+" "+maxPhotoMsg, Toast.LENGTH_SHORT).show();
    }

    private View rootView;
    private Unbinder unbinder;

    private ItemDetails itemDetails;

    private static UpdateItemFragment instance;
    private ReviewPixAdapter reviewPixAdapter;

    private PresenterUpdateItemImpl presenterUpdateItem;

    private List<Uri> listPics = new ArrayList<>(), listNewPics = new ArrayList<>();
    private ArrayList<String> listDeletedPics = new ArrayList();

    public static UpdateItemFragment getInstance(ItemDetails itemDetails) {
        Bundle args = new Bundle();
        args.putSerializable(RSConstants.RS_ITEM_DETAILS, itemDetails);
        if (instance == null) {
            instance = new UpdateItemFragment();
        }
        instance.setArguments(args);
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_update_item, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bindViews();
        initPixList();
    }

    private void bindViews() {
        createLoader();
        presenterUpdateItem = new PresenterUpdateItemImpl(UpdateItemFragment.this, getContext());
        toolbar.setTitle(updateItemTitle);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        itemDetails = (ItemDetails) getArguments().getSerializable(RSConstants.RS_ITEM_DETAILS);
        inputFacebook.setText(itemDetails.getUrlFacebook());
        inputInstagram.setText(itemDetails.getUrlInstagram());
        inputTwitter.setText(itemDetails.getUrlTwitter());
        inputGoogle.setText(itemDetails.getUrlGooglePlus());
        inputLinkedIn.setText(itemDetails.getUrlLinkedIn());
        setFragmentActionListener((ContainerActivity) getActivity());

    }

    private void initPixList() {
        recyclerViewPix.setVisibility(View.VISIBLE);
        for (int cpt=0; cpt < itemDetails.getGallery().size(); cpt++){
            listPics.add(Uri.parse(itemDetails.getGallery().get(cpt).getUrlPicture()));
        }
        RecyclerViewClickListener listener = (view, position) -> {

            int indexUri = findUriPosition(listPics.get(position));
            int indexId = findPicIdByUri(listPics.get(position));
            if (indexId != -1)
                listDeletedPics.add(itemDetails.getGallery().get(indexId).get_id());
            listPics.remove(position);

            if (indexUri != -1)
                listNewPics.remove(indexUri);

            reviewPixAdapter.notifyDataSetChanged();

            if (listPics.size() == 0)
                addPixTV.setVisibility(View.VISIBLE);
        };
        reviewPixAdapter = new ReviewPixAdapter(listPics, listener);
        recyclerViewPix.setLayoutManager(new LinearLayoutManager(recyclerViewPix.getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewPix.setAdapter(reviewPixAdapter);
        recyclerViewPix.addItemDecoration(new HorizontalSpace(marginCardView));
        recyclerViewPix.setNestedScrollingEnabled(false);
    }

    private int findPicIdByUri(Uri uri) {
        for (int i =0; i < itemDetails.getGallery().size(); i++){
            if (Uri.parse(itemDetails.getGallery().get(i).getUrlPicture()).equals(uri))
                return i;
        }
        return -1;
    }

    private int findUriPosition(Uri uri) {
        for (int i=0; i<listNewPics.size(); i++){
            if (uri.equals(listNewPics.get(i)))
                return i;
        }
        return -1;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == RSConstants.REQUEST_CODE) {
            if (data != null) {
                byte[] chartData = data.getByteArrayExtra("byte_array");
                if (listPics.size() == 0)
                    addPixTV.setVisibility(View.GONE);
                listPics.add(Helpers.getImageUri(chartData));
                listNewPics.add(Helpers.getImageUri(chartData));
                reviewPixAdapter.notifyDataSetChanged();
            }
        }
    }

    private FragmentActionListener fragmentActionListener;

    public void setFragmentActionListener(FragmentActionListener fragmentActionListener) {
        this.fragmentActionListener = fragmentActionListener;
    }

    @Override
    public void onDestroyView() {

        rootView = null;
        unbinder.unbind();
        presenterUpdateItem.onDestroy();
        listPics.clear();
        listPics = null;
        instance= null;
        super.onDestroyView();
    }

    @Override
    public void onSuccess(String target, Object data) {
        switch (target){
            case RSConstants.UPDATE_ITEM:
                Toast.makeText(getContext(), itemUpdatedSuccessfullyMsg, Toast.LENGTH_SHORT).show();
                fragmentActionListener.pop();
                break;
        }
    }

    @Override
    public void onFailure(String target) {

    }

    @Override
    public void onError(String target) {

    }

    @Override
    public void showProgressBar() {
        rsLoader.show(getFragmentManager(), RSLoader.TAG);
    }

    @Override
    public void hideProgressBar() {
        rsLoader.dismiss();
    }

    @Override
    public void onOffLine() {
        Toast.makeText(getContext(), offlineMsg, Toast.LENGTH_LONG).show();
    }
}
