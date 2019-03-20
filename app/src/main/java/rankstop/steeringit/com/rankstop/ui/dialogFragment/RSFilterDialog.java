package rankstop.steeringit.com.rankstop.ui.dialogFragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rankstop.steeringit.com.rankstop.MVP.model.PresenterFilterSearchImpl;
import rankstop.steeringit.com.rankstop.MVP.presenter.RSPresenter;
import rankstop.steeringit.com.rankstop.MVP.view.RSView;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.RankStop;
import rankstop.steeringit.com.rankstop.data.model.network.CategoryFilter;
import rankstop.steeringit.com.rankstop.data.model.network.RSRequestFilter;
import rankstop.steeringit.com.rankstop.data.model.network.RSResponseCategoryFilter;
import rankstop.steeringit.com.rankstop.ui.adapter.SpinnerCategoryFilterAdapter;
import rankstop.steeringit.com.rankstop.ui.callbacks.FilterDialogListener;
import rankstop.steeringit.com.rankstop.utils.RSConstants;



public class RSFilterDialog extends BottomSheetDialogFragment implements RSView.SearchView {

    public static final String TAG = "FILTER_DIALOG";
    private Unbinder unbinder;
    private View view;
    private RSPresenter.SearchFilterPresenter presenter;
    private RSResponseCategoryFilter categoriesList;

    private CategoryFilter selectedCategory;

    private FilterDialogListener callback;

    @BindView(R.id.category_spinner)
    AppCompatSpinner categorySpinner;

    /*@BindView(R.id.country_spinner)
    AppCompatSpinner countrySpinner;*/

    @OnClick(R.id.btn_filter)
    void filter() {
        dismiss();
        RSRequestFilter data = new RSRequestFilter();
        data.setCatId(selectedCategory.getCategory().get_id());
        callback.onfilterClicked(data);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            callback = (FilterDialogListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling Fragment must implement OnAddFriendListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.filter_dialog, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        presenter = new PresenterFilterSearchImpl(RSFilterDialog.this);
        presenter.loadCategories(RankStop.getDeviceLanguage());

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = (CategoryFilter) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public void onDestroyView() {
        view = null;
        if (unbinder != null)
            unbinder.unbind();
        super.onDestroyView();
    }

    @Override
    public void onSuccess(String target, Object data) {
        switch (target){
            case RSConstants.LOAD_CATEGORIES_USED_BY_LOCATION:
                categoriesList = new Gson().fromJson(new Gson().toJson(data), RSResponseCategoryFilter.class);
                initCategoryList(categoriesList);
                break;
        }
    }

    private void initCategoryList(RSResponseCategoryFilter categoriesList) {
        SpinnerCategoryFilterAdapter spinnerCountryAdapter = new SpinnerCategoryFilterAdapter(getContext(), categoriesList.getCategories());
        categorySpinner.setAdapter(spinnerCountryAdapter);
    }

    @Override
    public void onFailure(String target) {

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
    public void onOffLine() {

    }
}
