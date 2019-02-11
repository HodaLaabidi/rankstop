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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;

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
import rankstop.steeringit.com.rankstop.MVP.model.PresenterAddReviewImpl;
import rankstop.steeringit.com.rankstop.MVP.presenter.RSPresenter;
import rankstop.steeringit.com.rankstop.MVP.view.RSView;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.RankStop;
import rankstop.steeringit.com.rankstop.customviews.RSETMedium;
import rankstop.steeringit.com.rankstop.customviews.RSTVMedium;
import rankstop.steeringit.com.rankstop.data.model.db.Category;
import rankstop.steeringit.com.rankstop.data.model.db.CriteriaEval;
import rankstop.steeringit.com.rankstop.data.model.db.Evaluation;
import rankstop.steeringit.com.rankstop.data.model.network.RSAddReview;
import rankstop.steeringit.com.rankstop.data.model.network.ResponseAddItem;
import rankstop.steeringit.com.rankstop.session.RSSession;
import rankstop.steeringit.com.rankstop.ui.activities.ContainerActivity;
import rankstop.steeringit.com.rankstop.ui.adapter.EvalCriteriasAdapter;
import rankstop.steeringit.com.rankstop.ui.adapter.ReviewPixAdapter;
import rankstop.steeringit.com.rankstop.ui.callbacks.BottomSheetDialogListener;
import rankstop.steeringit.com.rankstop.ui.callbacks.CriteriaEvalListener;
import rankstop.steeringit.com.rankstop.ui.callbacks.FragmentActionListener;
import rankstop.steeringit.com.rankstop.ui.callbacks.RecyclerViewClickListener;
import rankstop.steeringit.com.rankstop.data.model.db.Criteria;
import rankstop.steeringit.com.rankstop.ui.dialogFragment.RSBottomSheetDialog;
import rankstop.steeringit.com.rankstop.ui.dialogFragment.RSLoader;
import rankstop.steeringit.com.rankstop.utils.FileCompressor;
import rankstop.steeringit.com.rankstop.utils.HorizontalSpace;
import rankstop.steeringit.com.rankstop.utils.RSConstants;
import rankstop.steeringit.com.rankstop.utils.RSNetwork;
import rankstop.steeringit.com.rankstop.utils.VerticalSpace;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class AddReviewFragment extends Fragment implements RSView.StandardView, BottomSheetDialogListener {

    @BindView(R.id.recycler_view_eval_criteria)
    RecyclerView recyclerViewEvalCriteria;

    @BindView(R.id.recycler_view_pix)
    RecyclerView recyclerViewPix;

    @BindView(R.id.input_comment)
    RSETMedium commentInput;

    @BindView(R.id.tv_add_pix)
    RSTVMedium addPixTV;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindString(R.string.no_value_changed)
    String noValueChanged;

    @BindString(R.string.no_eval_message)
    String noEvalMessage;
    @BindString(R.string.title_add_review)
    String addReviewTitle;
    @BindString(R.string.off_line)
    String offlineMsg;
    @BindString(R.string.max_photo_msg)
    String maxPhotoMsg;

    @BindInt(R.integer.m_card_view)
    int marginCardView;
    @BindInt(R.integer.max_length_500)
    int maxLength500;

    @OnClick(R.id.btn_take_pic)
    void takePic() {
        if (listPics.size() < RSConstants.MAX_GALLERY_PIX) {
            RSBottomSheetDialog dialog = new RSBottomSheetDialog();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            dialog.setTargetFragment(this, 0);
            dialog.show(ft, RSBottomSheetDialog.TAG);
        } else {
            Toast.makeText(getContext(), "" + RSConstants.MAX_GALLERY_PIX + " " + maxPhotoMsg, Toast.LENGTH_LONG).show();
        }
    }

    @OnClick(R.id.btn_add_review)
    void addReview() {
        if (RSNetwork.isConnected()) {
            if (validForm()) {
                rsAddReview.setComment(commentInput.getText().toString().trim());
                rsAddReview.setUserId(userId);
                rsAddReview.setEvalCri(criteriaEvalList);
                rsAddReview.setFiles(listPics);

                if (rsAddReview.getItemId() == null) {
                    // add item with review
                    addReviewPresenter.addItem(rsAddReview);
                } else {
                    if (isEvalChanged(myCriteriaEvalList, criteriaEvalList)) {
                        // Modification de l'item lors ajout d'un commentaire ou photos sans changer l'évaluation des critères
                        addReviewPresenter.addReview(rsAddReview);
                    } else {
                        //update review
                        if (rsAddReview.getComment().equals("") && listPics.size() == 0) {
                            Toast.makeText(getContext(), noValueChanged, Toast.LENGTH_LONG).show();
                        } else {
                            // Modification de l'item lors de l'ajout d'un commentaire ou de photos sans modifier l'évaluation des critères
                            rsAddReview.setEvalId(myEval.get_id());
                            addReviewPresenter.updateReview(rsAddReview);
                        }
                    }
                }
            }
        }else {
            onOffLine();
        }
    }

    private boolean validForm() {
        int x = 0;

        if (commentInput.getText().toString().trim().length() > maxLength500) {
            x++;
        }
        if (!isItemEvaluated(criteriaEvalList)) {
            Toast.makeText(getContext(), noEvalMessage, Toast.LENGTH_LONG).show();
        }
        return x == 0;
    }

    @BindString(R.string.loading_msg)
    String loadingMsg;
    private RSLoader rsLoader;

    private void createLoader() {
        rsLoader = RSLoader.newInstance(loadingMsg);
        rsLoader.setCancelable(false);
    }

    private Unbinder unbinder;
    private View rootView;

    private List<Uri> listPics = new ArrayList<>();
    private List<CriteriaEval> criteriaEvalList, myCriteriaEvalList;
    private String userId;
    private ReviewPixAdapter reviewPixAdapter;

    private RSPresenter.AddReviewPresenter addReviewPresenter;
    private RSAddReview rsAddReview;
    private Evaluation myEval;
    private String from;
    private Category currentCategory;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_add_review, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        bindViews();
        userId = RSSession.getCurrentUser().get_id();
        rsAddReview = (RSAddReview) getArguments().getSerializable(RSConstants.RS_ADD_REVIEW);
        myEval = (Evaluation) getArguments().getSerializable(RSConstants.MY_EVAL);
        from = getArguments().getString(RSConstants.FROM);

        loadCategoriesList(rsAddReview.getCategoryId());

        toolbar.setTitle(addReviewTitle);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initPixList();

    }

    @Override
    public void onResume() {
        super.onResume();
        mCompressor = new FileCompressor(getContext());
    }

    private boolean isEvalChanged(List<CriteriaEval> myCriteriaEvalList, List<CriteriaEval> criteriaEvalList) {
        for (int i = 0; i < myCriteriaEvalList.size(); i++) {
            if (myCriteriaEvalList.get(i).getNote() != criteriaEvalList.get(i).getNote() || myCriteriaEvalList.get(i).getCoefficient() != criteriaEvalList.get(i).getCoefficient())
                return true;
        }
        return false;
    }

    private boolean isItemEvaluated(List<CriteriaEval> criteriaEvalList) {
        int somme = 0;
        for (int i = 0; i < myCriteriaEvalList.size(); i++) {
            if (criteriaEvalList.get(i).getNote() != -1)
                somme++;
        }
        return somme >= 3;
    }

    private void loadCategoriesList(String id) {
        if (RSNetwork.isConnected())
            addReviewPresenter.loadCategory(id, RankStop.getDeviceLanguage());
        else
            onOffLine();
    }

    private void bindViews() {
        addReviewPresenter = new PresenterAddReviewImpl(AddReviewFragment.this, getContext());
        setFragmentActionListener((ContainerActivity) getActivity());
        createLoader();
    }

    private void initCriteriasList(Category category) {

        List<Criteria> listCriterias = (List<Criteria>) category.getCriterias();

        criteriaEvalList = new ArrayList<>();
        myCriteriaEvalList = new ArrayList<>();

        for (Criteria criteria : listCriterias) {
            //Toast.makeText(getContext(), "my eval = "+myEval.get_id(), Toast.LENGTH_LONG).show();
            if (myEval != null) {
                if (myEval.get_id() != null) {
                    CriteriaEval criteriaEval = findCriteriaNote(myEval, criteria.get_id());
                    if (criteriaEval == null) {
                        myCriteriaEvalList.add(new CriteriaEval(-1, 1, criteria.get_id(), criteria.getName().toString()));
                        criteriaEvalList.add(new CriteriaEval(-1, 1, criteria.get_id()));
                    } else {
                        myCriteriaEvalList.add(
                                new CriteriaEval(
                                        criteriaEval.getNote(),
                                        criteriaEval.getCoefficient(),
                                        criteria.get_id(),
                                        criteria.getName().toString()));
                        criteriaEvalList.add(
                                new CriteriaEval(
                                        criteriaEval.getNote(),
                                        criteriaEval.getCoefficient(),
                                        criteria.get_id()));
                    }
                } else {
                    myCriteriaEvalList.add(new CriteriaEval(-1, 1, criteria.get_id(), criteria.getName().toString()));
                    criteriaEvalList.add(new CriteriaEval(-1, 1, criteria.get_id()));
                }
            } else {
                myCriteriaEvalList.add(new CriteriaEval(-1, 1, criteria.get_id(), criteria.getName().toString()));
                criteriaEvalList.add(new CriteriaEval(-1, 1, criteria.get_id()));
            }
        }

        CriteriaEvalListener listener = new CriteriaEvalListener() {
            @Override
            public void onNoteChanged(int note, int position) {
                criteriaEvalList.get(position).setNote(note);
                //Toast.makeText(getContext(), "" + note + " at position " + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onImportanceChanged(int importance, int position) {
                criteriaEvalList.get(position).setCoefficient(importance);
                //Toast.makeText(getContext(), "importance = " + importance + " at position " + position, Toast.LENGTH_SHORT).show();
            }
        };
        recyclerViewEvalCriteria.setLayoutManager(new LinearLayoutManager(recyclerViewEvalCriteria.getContext()));
        recyclerViewEvalCriteria.setAdapter(new EvalCriteriasAdapter(myCriteriaEvalList, listener));
        recyclerViewEvalCriteria.addItemDecoration(new VerticalSpace(10, 1));

    }

    private CriteriaEval findCriteriaNote(Evaluation myEval, String id) {
        for (int i = 0; i < myEval.getEvalCriterias().size(); i++) {
            if (((Criteria) myEval.getEvalCriterias().get(i).getCriteria()).get_id().equals(id)) {
                return myEval.getEvalCriterias().get(i);
            }
        }
        return null;
    }

    private void initPixList() {
        recyclerViewPix.setVisibility(View.VISIBLE);
        RecyclerViewClickListener listener = (view, position) -> {
            listPics.remove(position);
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

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.profile_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case android.R.id.home:
                getActivity().onBackPressed();
                break;
            case R.id.setting:
                fragmentActionListener.startFragment(SettingsFragment.getInstance(), RSConstants.FRAGMENT_SETTINGS);
                break;
            case R.id.logout:
                /*RSSession.removeToken(getContext());
                ((ContainerActivity)getActivity()).manageSession(false);*/
                break;
            case R.id.history:
                fragmentActionListener.startFragment(HistoryFragment.getInstance(""), RSConstants.FRAGMENT_HISTORY);
                break;
            case R.id.contact:
                fragmentActionListener.startFragment(ContactFragment.getInstance(), RSConstants.FRAGMENT_CONTACT);
                break;
            case R.id.notifications:
                fragmentActionListener.startFragment(ListNotifFragment.getInstance(), RSConstants.FRAGMENT_NOTIF);
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    private FragmentActionListener fragmentActionListener;

    public void setFragmentActionListener(FragmentActionListener fragmentActionListener) {
        this.fragmentActionListener = fragmentActionListener;
    }

    private static AddReviewFragment instance;

    public static AddReviewFragment getInstance(RSAddReview rsAddReview, Evaluation myEval, String from) {

        Bundle args = new Bundle();
        args.putSerializable(RSConstants.RS_ADD_REVIEW, rsAddReview);
        args.putSerializable(RSConstants.MY_EVAL, myEval);
        args.putString(RSConstants.FROM, from);

        if (instance == null) {
            instance = new AddReviewFragment();
        }
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onDestroyView() {
        instance = null;
        fragmentActionListener = null;
        rootView = null;
        listPics.clear();
        addReviewPresenter.onDestroy();
        unbinder.unbind();
        super.onDestroyView();
    }


    @Override
    public void onSuccess(String target, Object data) {
        switch (target) {
            case RSConstants.LOAD_CATEGORY:
                currentCategory = new Gson().fromJson(new Gson().toJson(data), Category.class);
                if (from.equals(RSConstants.FRAGMENT_SIGN_UP)) {
                    addReviewPresenter.loadMyEval(userId, rsAddReview.getItemId());
                } else {
                    rsLoader.dismiss();
                    initCriteriasList(currentCategory);
                }
                break;
            case RSConstants.ADD_REVIEW:
                navigateToItemDetails(rsAddReview.getItemId(), "Review added successfully");
                break;
            case RSConstants.UPDATE_REVIEW:
                navigateToItemDetails(rsAddReview.getItemId(), "Review updated successfully");
                break;
            case RSConstants.ADD_ITEM:
                ResponseAddItem responseAddItem = new Gson().fromJson(new Gson().toJson(data), ResponseAddItem.class);
                navigateToItemDetails(responseAddItem.getId(), "Item added successfully");
                break;
            case RSConstants.LOAD_MY_EVAL:
                myEval = new Gson().fromJson(new Gson().toJson(data), Evaluation.class);
                if (myEval == null)
                    myEval = new Evaluation();
                initCriteriasList(currentCategory);
                break;
        }
    }

    private void navigateToItemDetails(String itemId, String message) {
        fragmentActionListener.startFragment(ItemDetailsFragment.getInstance(itemId), RSConstants.FRAGMENT_ITEM_DETAILS);
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailure(String target) {
        switch (target) {
            case RSConstants.LOAD_CATEGORY:
                break;
            case RSConstants.ADD_REVIEW:
                break;
            case RSConstants.ADD_ITEM:
                break;
        }
    }

    @Override
    public void onError(String target) {

    }

    @Override
    public void showProgressBar(String target) {
        switch (target) {
            case RSConstants.LOAD_CATEGORY:
                rsLoader.show(getFragmentManager(), RSLoader.TAG);
                break;
            case RSConstants.LOAD_MY_EVAL:
                break;
            case RSConstants.ADD_REVIEW:
            case RSConstants.UPDATE_REVIEW:
            case RSConstants.ADD_ITEM:
                rsLoader.show(getFragmentManager(), RSLoader.TAG);
                break;
        }
    }

    @Override
    public void hideProgressBar(String target) {
        switch (target) {
            case RSConstants.LOAD_CATEGORY:
                rsLoader.dismiss();
                break;
            case RSConstants.LOAD_MY_EVAL:
                rsLoader.dismiss();
                break;
            case RSConstants.ADD_REVIEW:
            case RSConstants.UPDATE_REVIEW:
            case RSConstants.ADD_ITEM:
                rsLoader.dismiss();
                break;
        }
    }

    @Override
    public void showMessage(String target, String message) {
        //Toast.makeText(getContext(), ""+ message, Toast.LENGTH_LONG).show();
        //commentInput.setText(message);
    }

    @Override
    public void onOffLine() {
        Toast.makeText(getContext(), offlineMsg, Toast.LENGTH_LONG).show();
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

    private FileCompressor mCompressor;
    private File mPhotoFile;
    private Uri imageUri;

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
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        //imageFilePath = image.getAbsolutePath();

        return image;
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

    private void addPicToList(Uri imageUri) {
        if (listPics.size() == 0)
            addPixTV.setVisibility(View.GONE);
        listPics.add(imageUri);
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
