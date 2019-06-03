package rankstop.steeringit.com.rankstop.ui.dialogFragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rankstop.steeringit.com.rankstop.MVP.model.PresenterFilterSearchImpl;
import rankstop.steeringit.com.rankstop.MVP.presenter.RSPresenter;
import rankstop.steeringit.com.rankstop.MVP.view.RSView;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.RankStop;
import rankstop.steeringit.com.rankstop.customviews.RSTVBold;
import rankstop.steeringit.com.rankstop.data.model.db.Category;
import rankstop.steeringit.com.rankstop.data.model.db.Country;
import rankstop.steeringit.com.rankstop.data.model.network.CategoryFilter;
import rankstop.steeringit.com.rankstop.data.model.network.LocationFilter;
import rankstop.steeringit.com.rankstop.data.model.network.RSRequestFilter;
import rankstop.steeringit.com.rankstop.data.model.network.RSResponseCategoryFilter;
import rankstop.steeringit.com.rankstop.ui.adapter.SpinnerCategoryFilterAdapter;
import rankstop.steeringit.com.rankstop.ui.adapter.SpinnerCountryFilterAdapter;
import rankstop.steeringit.com.rankstop.ui.callbacks.FilterDialogListener;
import rankstop.steeringit.com.rankstop.utils.RSConstants;


public class RSFilterDialog extends BottomSheetDialogFragment implements RSView.SearchView {

    public static final String TAG = "FILTER_DIALOG";
    private Unbinder unbinder;
    private View view;
    private RSPresenter.SearchFilterPresenter presenter;
    private RSResponseCategoryFilter categoriesList;

    private CategoryFilter selectedCategory;
    private LocationFilter selectedLocation;
    private RSRequestFilter rsRequestFilter;

    private FilterDialogListener callback;

    @BindView(R.id.category_spinner)
    AppCompatSpinner categorySpinner;

    @BindView(R.id.tv_country)
    RSTVBold countryTV;
    @BindView(R.id.country_spinner)
    AppCompatSpinner countrySpinner;

    @BindView(R.id.tv_city)
    RSTVBold cityTV;
    @BindView(R.id.cities_container)
    LinearLayout citiesContainer;

    @BindString(R.string.other)
    String otherCity;
    @BindString(R.string.all)
    String allCountry;

    @OnClick(R.id.btn_filter)
    void filter() {
        dismiss();
        RSRequestFilter data = new RSRequestFilter();
        data.setCatId(selectedCategory.getCategory().get_id());
        String[] countryCode = new String[1];
       if (selectedLocation != null){
           if (selectedLocation.getCountry().getCountryName().equals(allCountry)) {
               countryCode[0] = "";
           } else {
               List<String> listCityt = new ArrayList<>();
               countryCode[0] = selectedLocation.getCountry().getCountryCode();
               for (int i = 0; i < citiesContainer.getChildCount(); i++) {
                   if (citiesContainer.getChildAt(i) instanceof CheckBox) {
                       CheckBox checkBox = (CheckBox) citiesContainer.getChildAt(i);
                       if (checkBox.isChecked()) {
                           if (checkBox.getText().toString().equals(otherCity)) {
                               listCityt.add("");
                           } else {
                               listCityt.add(checkBox.getText().toString());
                           }

                           //Toast.makeText(getContext(), ""+checkBox.getText().toString(), Toast.LENGTH_SHORT).show();
                       }
                   }
               }
               data.setCity(listCityt);
           }
           data.setCodeCountry(countryCode);
       }


        callback.onfilterClicked(data);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            callback = (FilterDialogListener) getTargetFragment();
            rsRequestFilter = (RSRequestFilter) getArguments().getSerializable("data");
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
        presenter.loadCategories(RankStop.getDeviceLanguage(), getContext());

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = (CategoryFilter) parent.getItemAtPosition(position);
                if (selectedCategory.getCategory().isLocation()) {
                    countryTV.setVisibility(View.VISIBLE);
                    cityTV.setVisibility(View.VISIBLE);
                    citiesContainer.setVisibility(View.VISIBLE);
                    countrySpinner.setVisibility(View.VISIBLE);
                    //Toast.makeText(getContext(), selectedCategory.getLocation().get(0).getCountry().getCountryCode()+"", Toast.LENGTH_SHORT).show();
                    initCountryList(selectedCategory);
                } else {
                    countrySpinner.setVisibility(View.GONE);
                    countryTV.setVisibility(View.GONE);
                    cityTV.setVisibility(View.GONE);
                    citiesContainer.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedLocation = (LocationFilter) parent.getItemAtPosition(position);
                if (selectedLocation.getCountry().getCountryName().equals(allCountry)) {
                    cityTV.setVisibility(View.GONE);
                    citiesContainer.setVisibility(View.GONE);
                } else {
                    initCitiesList(selectedLocation.getCities());
                    cityTV.setVisibility(View.VISIBLE);
                    citiesContainer.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void initCitiesList(String[] cities) {
        citiesContainer.removeAllViews();
        for (int i = 0; i < cities.length; i++) {
            CheckBox checkBox = new CheckBox(getContext());
            checkBox.setText(cities[i]);
            checkBox.setId(i);

            if (i == 0 && cities[0].equals(""))
                checkBox.setText(otherCity);
            citiesContainer.addView(checkBox);
        }
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
        switch (target) {
            case RSConstants.LOAD_CATEGORIES_USED_BY_LOCATION:
                categoriesList = new Gson().fromJson(new Gson().toJson(data), RSResponseCategoryFilter.class);
                initCategoryList(categoriesList);
                break;
        }
    }

    private void initCategoryList(RSResponseCategoryFilter categoriesList) {
        SpinnerCategoryFilterAdapter spinnerCategoryAdapter = new SpinnerCategoryFilterAdapter(getContext(), categoriesList.getCategories());
        categorySpinner.setAdapter(spinnerCategoryAdapter);
        if (rsRequestFilter != null) {
            int index = findCategory(rsRequestFilter.getCatId());
            if (index != -1) {
                categorySpinner.setSelection(index);
            }
        }
    }

    private void initCountryList(CategoryFilter category) {
//        Country country = new Country(allCountry);
//
//        LocationFilter location = new LocationFilter(country);
//        category.getLocation().add(0, location);
        SpinnerCountryFilterAdapter spinnerCountryAdapter = new SpinnerCountryFilterAdapter(getContext(), category.getLocation());
        countrySpinner.setAdapter(spinnerCountryAdapter);
        if (rsRequestFilter != null) {
            if (rsRequestFilter.getCodeCountry()[0] != null) {
//                Toast.makeText(getContext(), category.getLocation().get(0).getCountry().getCountryCode() + "", Toast.LENGTH_SHORT).show();
                int index = findCountry(rsRequestFilter.getCodeCountry()[0], category);
                if (index != -1) {
                    countrySpinner.setSelection(index);
                }
            }
        }
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

    private int findCategory(String categoryId) {
        if (categoryId != null)
            for (int i = 0; i < categoriesList.getCategories().size(); i++) {
                if (categoriesList.getCategories().get(i).getCategory().get_id().toLowerCase().trim().equals(categoryId.toLowerCase().trim()))
                    return i;
            }
        return -1;
    }

    private int findCountry(String countryCode, CategoryFilter category) {
        if (countryCode != null) {
            for (int i = 0; i < category.getLocation().size(); i++) {
                if (category.getCategory().isLocation()) {
//                    Toast.makeText(getContext(), category.getLocation().get(0).getCountry().getCountryCode()+"", Toast.LENGTH_SHORT).show();
                    if (category.getLocation().get(i).getCountry().getCountryCode().toLowerCase().trim().equals(countryCode.toLowerCase().trim()))
                        return i;
                }
            }
        }
        return -1;
    }
}
