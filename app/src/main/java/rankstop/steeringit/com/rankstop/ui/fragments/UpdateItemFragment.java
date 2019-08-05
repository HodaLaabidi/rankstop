package rankstop.steeringit.com.rankstop.ui.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindInt;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rankstop.steeringit.com.rankstop.MVP.model.PresenterUpdateItemImpl;
import rankstop.steeringit.com.rankstop.MVP.view.RSView;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.RankStop;
import rankstop.steeringit.com.rankstop.customviews.RSCustomToast;
import rankstop.steeringit.com.rankstop.customviews.RSTVMedium;
import rankstop.steeringit.com.rankstop.customviews.RSTVRegular;
import rankstop.steeringit.com.rankstop.data.model.db.ItemDetails;
import rankstop.steeringit.com.rankstop.data.model.network.RSNavigationData;
import rankstop.steeringit.com.rankstop.data.model.network.RSUpdateItem;
import rankstop.steeringit.com.rankstop.ui.activities.ContainerActivity;
import rankstop.steeringit.com.rankstop.ui.adapter.ReviewPixAdapter;
import rankstop.steeringit.com.rankstop.ui.callbacks.BottomSheetDialogListener;
import rankstop.steeringit.com.rankstop.ui.callbacks.FragmentActionListener;
import rankstop.steeringit.com.rankstop.ui.callbacks.RecyclerViewClickListener;
import rankstop.steeringit.com.rankstop.ui.dialogFragment.RSBottomSheetDialog;
import rankstop.steeringit.com.rankstop.ui.dialogFragment.RSLoader;
import rankstop.steeringit.com.rankstop.utils.FileCompressor;
import rankstop.steeringit.com.rankstop.utils.HorizontalSpace;
import rankstop.steeringit.com.rankstop.utils.RSConstants;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class UpdateItemFragment extends Fragment implements RSView.UpdateItemView, BottomSheetDialogListener {

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
    @BindView(R.id.input_layout_facebook)
    TextInputLayout inputLayoutFacebook ;
    @BindView(R.id.input_layout_instagram)
    TextInputLayout inputLayoutInstagram ;
    @BindView(R.id.input_layout_twitter)
    TextInputLayout inputLayoutTwitter ;
    @BindView(R.id.input_layout_google)
    TextInputLayout inputLayoutGoogle ;
    @BindView(R.id.input_layout_linkedin)
    TextInputLayout inputLayoutLinkedin ;

    @BindView(R.id.action_delete_barcode)
    RSTVMedium actionDeleteBarcode;
    @BindView(R.id.action_scanner)
    RSTVMedium actionScanner;

    @BindView(R.id.input_barcode_scanner)
    TextInputEditText inputBarcodeScanner;

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

    private void createLoader() {
        rsLoader = RSLoader.newInstance(loadingMsg);
        rsLoader.setCancelable(false);
    }

    @OnClick(R.id.btn_save_changes)
    public void saveChanges() {
        RSUpdateItem rsUpdateItem = new RSUpdateItem();
        rsUpdateItem.setItemId(itemDetails.get_id());
        rsUpdateItem.setUrlFacebook(inputFacebook.getText().toString());
        rsUpdateItem.setUrlInstagram(inputInstagram.getText().toString());
        rsUpdateItem.setUrlTwitter(inputTwitter.getText().toString());
        rsUpdateItem.setUrlLinkedIn(inputLinkedIn.getText().toString());
        rsUpdateItem.setUrlGooglePlus(inputGoogle.getText().toString());
        rsUpdateItem.setGallery(listNewPics);
        rsUpdateItem.setPicDelete(listDeletedPics);
        if( inputBarcodeScanner.getText().toString().equalsIgnoreCase("") || TextUtils.isEmpty(inputBarcodeScanner.getText())){
            rsUpdateItem.setBarcode("");
        } else {
            rsUpdateItem.setBarcode(inputBarcodeScanner.getText().toString() + "");
        }
            if( isValidSocialNetworks()){

                presenterUpdateItem.updateItem(rsUpdateItem, getContext());
            }

        }





    private boolean isValidSocialNetworks() {

        int x = 0 ;
        if (!inputFacebook.getText().toString().equalsIgnoreCase("")){
            if (!inputFacebook.getText().toString().matches(RSConstants.FACEBOOK_URL_REGEX )){
               // Toast.makeText(getContext(), R.string.invalid_facebook_url, Toast.LENGTH_LONG).show();
                inputLayoutFacebook.setErrorEnabled(true);
                inputLayoutFacebook.setError(getContext().getString(R.string.invalid_facebook_url));
                x++ ;
            }
        }
        if (!inputInstagram.getText().toString().equalsIgnoreCase("")){
            if (!inputInstagram.getText().toString().matches(RSConstants.INSTAGRAM_URL_REGEX )){
                //Toast.makeText(getContext(), R.string.invalid_instagram_url, Toast.LENGTH_LONG).show();
                inputLayoutInstagram.setErrorEnabled(true);
                inputLayoutInstagram.setError(getContext().getString(R.string.invalid_instagram_url));
                x++ ;
            }
        }
        if (!inputTwitter.getText().toString().equalsIgnoreCase("")){
            if (!inputTwitter.getText().toString().matches(RSConstants.TWITTER_URL_REGEX )){
                //Toast.makeText(getContext(), R.string.invalid_twitter_url, Toast.LENGTH_LONG).show();
                inputLayoutTwitter.setErrorEnabled(true);
                inputLayoutTwitter.setError(getContext().getString(R.string.invalid_twitter_url));
                x++ ;
            }
        }
        if (!inputLinkedIn.getText().toString().equalsIgnoreCase("")){
            if (!inputLinkedIn.getText().toString().matches(RSConstants.LINKEDIN_URL_REGEX )){
                //Toast.makeText(getContext(), R.string.invalid_linkedin_url, Toast.LENGTH_LONG).show();
                inputLayoutLinkedin.setErrorEnabled(true);
                inputLayoutLinkedin.setError(getContext().getString(R.string.invalid_linkedin_url));
                x++ ;
            }
        }

        if (!inputGoogle.getText().toString().equalsIgnoreCase("")){
            if (!inputGoogle.getText().toString().matches(RSConstants.GOOGLE_URL_REGEX )){
                //Toast.makeText(getContext(), R.string.invalid_linkedin_url, Toast.LENGTH_LONG).show();
                inputLayoutGoogle.setErrorEnabled(true);
                inputLayoutGoogle.setError(getContext().getString(R.string.invalid_google_url));
                x++ ;
            }
        }
       return x == 0 ;
    }

    @OnClick(R.id.btn_take_pic)
    public void addPicture() {

        if (listPics.size() < RSConstants.MAX_GALLERY_PIX) {
            RSBottomSheetDialog dialog = new RSBottomSheetDialog();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            dialog.setTargetFragment(this, 0);
            dialog.show(ft, RSBottomSheetDialog.TAG);
        } else {
            Toast.makeText(getContext(), "" + RSConstants.MAX_GALLERY_PIX + " " + maxPhotoMsg, Toast.LENGTH_LONG).show();
        }
    }

    private View rootView;
    private Unbinder unbinder;


    private static ItemDetails itemDetails;

    private static UpdateItemFragment instance;
    private ReviewPixAdapter reviewPixAdapter;

    private PresenterUpdateItemImpl presenterUpdateItem;

    private List<Uri> listPics = new ArrayList<>(), listNewPics = new ArrayList<>();
    private ArrayList<String> listDeletedPics = new ArrayList();

    private FileCompressor mCompressor;
    private File mPhotoFile;

    public static UpdateItemFragment getInstance(ItemDetails itemD) {
        Bundle args = new Bundle();
        args.putSerializable(RSConstants.RS_ITEM_DETAILS, itemD);
        if (instance == null) {
            instance = new UpdateItemFragment();
        }
        instance.setArguments(args);
        if (instance.getArguments() != null) {
            itemDetails = (ItemDetails) instance.getArguments().getSerializable(RSConstants.RS_ITEM_DETAILS);

        }
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
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
        mCompressor = new FileCompressor(getContext());
        presenterUpdateItem = new PresenterUpdateItemImpl(UpdateItemFragment.this, getContext());
        toolbar.setTitle(updateItemTitle);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //itemDetails = (ItemDetails) getArguments().getSerializable(RSConstants.RS_ITEM_DETAILS);

        inputFacebook.setText(itemDetails.getUrlFacebook());
        inputInstagram.setText(itemDetails.getUrlInstagram());
        inputBarcodeScanner.setText(itemDetails.getBarcode());
        inputTwitter.setText(itemDetails.getUrlTwitter());
        inputGoogle.setText(itemDetails.getUrlGooglePlus());
        inputLinkedIn.setText(itemDetails.getUrlLinkedIn());
        inputFacebook.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().matches(RSConstants.FACEBOOK_URL_REGEX ) || s.toString().equalsIgnoreCase("")){
                    inputLayoutFacebook.setErrorEnabled(false);
                    inputLayoutFacebook.setError("");
                } else {
                    inputLayoutFacebook.setErrorEnabled(true);
                    inputLayoutFacebook.setError(getContext().getString(R.string.invalid_facebook_url));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        inputInstagram.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().matches(RSConstants.INSTAGRAM_URL_REGEX  )|| s.toString().equalsIgnoreCase("")){
                    inputLayoutInstagram.setErrorEnabled(false);
                    inputLayoutInstagram.setError("");
                } else {
                    inputLayoutInstagram.setErrorEnabled(true);
                    inputLayoutInstagram.setError(getContext().getString(R.string.invalid_instagram_url));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        inputTwitter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().matches(RSConstants.TWITTER_URL_REGEX ) || s.toString().equalsIgnoreCase("")){
                    inputLayoutTwitter.setErrorEnabled(false);
                    inputLayoutTwitter.setError("");
                } else {
                    inputLayoutTwitter.setErrorEnabled(true);
                    inputLayoutTwitter.setError(getContext().getString(R.string.invalid_twitter_url));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        inputGoogle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().matches(RSConstants.GOOGLE_URL_REGEX ) || s.toString().equalsIgnoreCase("")){
                    inputLayoutGoogle.setErrorEnabled(false);
                    inputLayoutGoogle.setError("");
                } else {
                    inputLayoutGoogle.setErrorEnabled(true);
                    inputLayoutGoogle.setError(getContext().getString(R.string.invalid_google_url));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        inputLinkedIn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().matches(RSConstants.LINKEDIN_URL_REGEX )|| s.toString().equalsIgnoreCase("")){
                    inputLayoutLinkedin.setErrorEnabled(false);
                    inputLayoutLinkedin.setError("");
                } else {
                    inputLayoutLinkedin.setErrorEnabled(true);
                    inputLayoutLinkedin.setError(getContext().getString(R.string.invalid_linkedin_url));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (itemDetails.getBarcode() != null) {
            if (!itemDetails.getBarcode().equalsIgnoreCase("")) {
                actionDeleteBarcode.setVisibility(View.VISIBLE);
                actionDeleteBarcode.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        inputBarcodeScanner.setText("");
                        //rsAddItem.setBarcode(barcode);
                        actionDeleteBarcode.setVisibility(View.GONE);
                    }
                });
            } else {
                actionDeleteBarcode.setVisibility(View.GONE);
            }
        } else {
            actionDeleteBarcode.setVisibility(View.GONE);
        }

        setFragmentActionListener((ContainerActivity) getActivity());
        actionScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemDetails.setBarcode(inputBarcodeScanner.getText() + "");
                itemDetails.setUrlFacebook(inputFacebook.getText() + "");
                itemDetails.setUrlInstagram(inputInstagram.getText() + "");
                itemDetails.setUrlTwitter(inputTwitter.getText() + "");
                itemDetails.setUrlGooglePlus(inputGoogle.getText() + "");
                itemDetails.setUrlLinkedIn(inputLinkedIn.getText() + "");

                ((ContainerActivity) getActivity()).manageSession(true, new RSNavigationData(RSConstants.FRAGMENT_SCANNER, RSConstants.ACTION_UPDATE));
                fragmentActionListener.startFragment(ScannerFragment.getInstance(itemDetails), RSConstants.FRAGMENT_SCANNER);


            }
        });
    }


    private void initPixList() {
        recyclerViewPix.setVisibility(View.VISIBLE);
        for (int cpt = 0; cpt < itemDetails.getGallery().size(); cpt++) {
            if (listPics != null)
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
        for (int i = 0; i < itemDetails.getGallery().size(); i++) {
            if (Uri.parse(itemDetails.getGallery().get(i).getUrlPicture()).equals(uri))
                return i;
        }
        return -1;
    }

    private int findUriPosition(Uri uri) {
        for (int i = 0; i < listNewPics.size(); i++) {
            if (uri.equals(listNewPics.get(i)))
                return i;
        }
        return -1;
    }

    private FragmentActionListener fragmentActionListener;

    public void setFragmentActionListener(FragmentActionListener fragmentActionListener) {
        this.fragmentActionListener = fragmentActionListener;
    }

    @Override
    public void onDestroyView() {

        rootView = null;
        if (unbinder != null)
            unbinder.unbind();
        if (presenterUpdateItem != null)
            presenterUpdateItem.onDestroy(getContext());
        if (listPics != null)
            listPics.clear();
        listPics = null;
        instance = null;
        super.onDestroyView();
    }

    @Override
    public void onSuccess(String target, Object data) {
        switch (target) {
            case RSConstants.UPDATE_ITEM:
                Toast.makeText(getContext(), itemUpdatedSuccessfullyMsg, Toast.LENGTH_SHORT).show();
                fragmentActionListener.pop();
                break;
        }
    }

    @Override
    public void onExistItem(String message , Object data) {
        Toast.makeText(getContext() , message , Toast.LENGTH_LONG).show();

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
        //Toast.makeText(getContext(), offlineMsg, Toast.LENGTH_LONG).show();
        new RSCustomToast(getActivity(), getResources().getString(R.string.error), offlineMsg, R.drawable.ic_error, RSCustomToast.ERROR).show();

    }

    @Override
    public void onTakePictureClicked() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CAMERA);
        } else {
            openCamera();
        }
    }

    @Override
    public void onChoosePictureClicked() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_STORAGE);
        } else {
            openGallery();
        }
    }

    public static final int REQUEST_TAKE_PHOTO = 100;
    public static final int REQUEST_GALLERY_PHOTO = 200;
    public static final int REQUEST_PERMISSION_STORAGE = 300;
    public static final int REQUEST_PERMISSION_CAMERA = 400;

    private void openCamera() {
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (pictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            mPhotoFile = photoFile;
            Uri photoUri = FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".provider", photoFile);
            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(pictureIntent, REQUEST_TAKE_PHOTO);
        }
    }

    private void openGallery() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(pickPhoto, REQUEST_GALLERY_PHOTO);
    }

    private File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                // No Permitions Granted
            }
        } else if (requestCode == REQUEST_PERMISSION_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                // No Permitions Granted
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            Uri imageUri;
            if (requestCode == REQUEST_TAKE_PHOTO) {
                try {
                    mPhotoFile = mCompressor.compressToFile(mPhotoFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String path = MediaStore.Images.Media.insertImage(RankStop.getInstance().getContentResolver(), BitmapFactory.decodeFile(mPhotoFile.getAbsolutePath()), "Image Description", null);
                imageUri = Uri.parse(path);

                addPicToList(imageUri);


            } else if (requestCode == REQUEST_GALLERY_PHOTO) {
                Uri selectedImage = data.getData();
                try {
                    mPhotoFile = mCompressor.compressToFile(new File(getRealPathFromUri(selectedImage)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String path = MediaStore.Images.Media.insertImage(RankStop.getInstance().getContentResolver(), BitmapFactory.decodeFile(mPhotoFile.getAbsolutePath()), "Image Description", null);
                imageUri = Uri.parse(path);
                addPicToList(imageUri);
            }
        } else if (resultCode == RESULT_CANCELED) {
            //Toast.makeText(getContext(), "You cancelled the operation", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case android.R.id.home:
                getActivity().onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addPicToList(Uri imageUri) {
        if (listPics.size() == 0)
            addPixTV.setVisibility(View.GONE);
        listPics.add(imageUri);
        listNewPics.add(imageUri);
        reviewPixAdapter.notifyDataSetChanged();
    }

    public String getRealPathFromUri(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = getContext().getContentResolver().query(contentUri, proj, null, null, null);
            assert cursor != null;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
