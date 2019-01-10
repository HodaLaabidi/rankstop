package rankstop.steeringit.com.rankstop.ui.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import rankstop.steeringit.com.rankstop.MVP.model.PresenterAddReviewImpl;
import rankstop.steeringit.com.rankstop.MVP.presenter.RSPresenter;
import rankstop.steeringit.com.rankstop.MVP.view.RSView;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.data.model.db.Category;
import rankstop.steeringit.com.rankstop.data.model.db.CriteriaEval;
import rankstop.steeringit.com.rankstop.data.model.db.Evaluation;
import rankstop.steeringit.com.rankstop.data.model.network.RSAddReview;
import rankstop.steeringit.com.rankstop.data.model.network.ResponseAddItem;
import rankstop.steeringit.com.rankstop.session.RSSession;
import rankstop.steeringit.com.rankstop.ui.activities.ContainerActivity;
import rankstop.steeringit.com.rankstop.ui.activities.TakePictureActivity;
import rankstop.steeringit.com.rankstop.ui.adapter.EvalCriteriasAdapter;
import rankstop.steeringit.com.rankstop.ui.adapter.ReviewPixAdapter;
import rankstop.steeringit.com.rankstop.ui.callbacks.CriteriaEvalListener;
import rankstop.steeringit.com.rankstop.ui.callbacks.FragmentActionListener;
import rankstop.steeringit.com.rankstop.ui.callbacks.RecyclerViewClickListener;
import rankstop.steeringit.com.rankstop.data.model.db.Criteria;
import rankstop.steeringit.com.rankstop.utils.Helpers;
import rankstop.steeringit.com.rankstop.utils.HorizontalSpace;
import rankstop.steeringit.com.rankstop.utils.RSConstants;
import rankstop.steeringit.com.rankstop.utils.VerticalSpace;

public class AddReviewFragment extends Fragment implements RSView.StandardView {

    private MaterialButton takePicBtn, addReviewBtn;
    private RecyclerView recyclerViewEvalCriteria, recyclerViewPix;
    private TextInputEditText commentInput;

    private ReviewPixAdapter reviewPixAdapter;
    private TextView addPixTV;
    private Toolbar toolbar;
    private View rootView;

    private List<Uri> listPics = new ArrayList<>();
    private List<CriteriaEval> criteriaEvalList, myCriteriaEvalList;
    private String userId;

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
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        bindViews();
        userId = RSSession.getCurrentUser(getContext()).get_id();
        rsAddReview = (RSAddReview) getArguments().getSerializable(RSConstants.RS_ADD_REVIEW);
        myEval = (Evaluation) getArguments().getSerializable(RSConstants.MY_EVAL);
        from = getArguments().getString(RSConstants.FROM);

        loadCategoriesList(rsAddReview.getCategoryId());

        toolbar.setTitle(getResources().getString(R.string.title_add_review));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initPixList();

        takePicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getContext(), TakePictureActivity.class), RSConstants.REQUEST_CODE);
            }
        });

        addReviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rsAddReview.setComment(commentInput.getText().toString());
                rsAddReview.setUserId(userId);
                rsAddReview.setEvalCri(criteriaEvalList);
                rsAddReview.setFiles(listPics);

                if (rsAddReview.getItemId() == null) {
                    // add item with review
                    addReviewPresenter.addItem(rsAddReview);
                } else {
                    if (isEvalChanged(myCriteriaEvalList, criteriaEvalList)) {
                        // add review
                        addReviewPresenter.addReview(rsAddReview);
                    } else {
                        //update review
                        if (rsAddReview.getComment().equals("") && listPics.size() == 0){
                            Toast.makeText(getContext(), "you don't make any change", Toast.LENGTH_LONG).show();
                        }else {
                            rsAddReview.setEvalId(myEval.get_id());
                            addReviewPresenter.updateReview(rsAddReview);
                        }
                    }
                }
            }
        });

    }

    private boolean isEvalChanged(List<CriteriaEval> myCriteriaEvalList, List<CriteriaEval> criteriaEvalList) {
        for (int i = 0; i < myCriteriaEvalList.size(); i++) {
            if (myCriteriaEvalList.get(i).getNote() != criteriaEvalList.get(i).getNote() || myCriteriaEvalList.get(i).getCoefficient() != criteriaEvalList.get(i).getCoefficient())
                return true;
        }
        return false;
    }

    private void loadCategoriesList(String id) {
        addReviewPresenter.loadCategory(id);
    }

    private void bindViews() {

        addReviewPresenter = new PresenterAddReviewImpl(AddReviewFragment.this, getContext());
        recyclerViewEvalCriteria = rootView.findViewById(R.id.recycler_view_eval_criteria);
        recyclerViewPix = rootView.findViewById(R.id.recycler_view_pix);
        takePicBtn = rootView.findViewById(R.id.btn_take_pic);
        addReviewBtn = rootView.findViewById(R.id.btn_add_review);
        commentInput = rootView.findViewById(R.id.input_comment);
        addPixTV = rootView.findViewById(R.id.tv_add_pix);
        toolbar = rootView.findViewById(R.id.toolbar);

        setFragmentActionListener((ContainerActivity) getActivity());
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
                        myCriteriaEvalList.add(new CriteriaEval(-1, 1, criteria.get_id(), criteria.getName()));
                        criteriaEvalList.add(new CriteriaEval(-1, 1, criteria.get_id()));
                    } else {
                        myCriteriaEvalList.add(
                                new CriteriaEval(
                                        criteriaEval.getNote(),
                                        criteriaEval.getCoefficient(),
                                        criteria.get_id(),
                                        criteria.getName()));
                        criteriaEvalList.add(
                                new CriteriaEval(
                                        criteriaEval.getNote(),
                                        criteriaEval.getCoefficient(),
                                        criteria.get_id()));
                    }
                } else {
                    myCriteriaEvalList.add(new CriteriaEval(-1, 1, criteria.get_id(), criteria.getName()));
                    criteriaEvalList.add(new CriteriaEval(-1, 1, criteria.get_id()));
                }
            } else {
                myCriteriaEvalList.add(new CriteriaEval(-1, 1, criteria.get_id(), criteria.getName()));
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
        recyclerViewEvalCriteria.setAdapter(new EvalCriteriasAdapter(myCriteriaEvalList, listener, getContext()));
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
        reviewPixAdapter = new ReviewPixAdapter(listPics, listener, getContext());
        recyclerViewPix.setLayoutManager(new LinearLayoutManager(recyclerViewPix.getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewPix.setAdapter(reviewPixAdapter);
        recyclerViewPix.addItemDecoration(new HorizontalSpace(getResources().getInteger(R.integer.m_card_view)));
        recyclerViewPix.setNestedScrollingEnabled(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == RSConstants.REQUEST_CODE) {
            if (data != null) {
                byte[] chartData = data.getByteArrayExtra("byte_array");
                if (listPics.size() == 0)
                    addPixTV.setVisibility(View.GONE);
                listPics.add(Helpers.getImageUri(chartData, getContext()));
                reviewPixAdapter.notifyDataSetChanged();
            }
        }
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

    }

    @Override
    public void hideProgressBar(String target) {

    }

    @Override
    public void showMessage(String target, String message) {
        //Toast.makeText(getContext(), ""+ message, Toast.LENGTH_LONG).show();
        //commentInput.setText(message);
    }
}
