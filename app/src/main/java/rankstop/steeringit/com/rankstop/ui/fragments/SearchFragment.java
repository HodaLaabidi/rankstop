package rankstop.steeringit.com.rankstop.ui.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindColor;
import butterknife.BindInt;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import rankstop.steeringit.com.rankstop.MVP.model.PresenterSearchImpl;
import rankstop.steeringit.com.rankstop.MVP.presenter.RSPresenter;
import rankstop.steeringit.com.rankstop.MVP.view.RSView;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.RankStop;
import rankstop.steeringit.com.rankstop.customviews.RSBTNMedium;
import rankstop.steeringit.com.rankstop.customviews.RSTVBold;
import rankstop.steeringit.com.rankstop.customviews.RSTVRegular;
import rankstop.steeringit.com.rankstop.data.model.db.Category;
import rankstop.steeringit.com.rankstop.data.model.db.Item;
import rankstop.steeringit.com.rankstop.data.model.db.ItemDetails;
import rankstop.steeringit.com.rankstop.data.model.network.RSRequestFilter;
import rankstop.steeringit.com.rankstop.data.model.network.RSRequestItemByCategory;
import rankstop.steeringit.com.rankstop.data.model.network.RSResponseListingItem;
import rankstop.steeringit.com.rankstop.data.model.network.RSResponseSearch;
import rankstop.steeringit.com.rankstop.session.RSSession;
import rankstop.steeringit.com.rankstop.ui.activities.ContainerActivity;
import rankstop.steeringit.com.rankstop.ui.adapter.DataFetchedAdapter;
import rankstop.steeringit.com.rankstop.ui.adapter.ItemsAdapter;
import rankstop.steeringit.com.rankstop.ui.adapter.ItemsFetchedAdapter;
import rankstop.steeringit.com.rankstop.ui.callbacks.FilterDialogListener;
import rankstop.steeringit.com.rankstop.ui.callbacks.FragmentActionListener;
import rankstop.steeringit.com.rankstop.ui.callbacks.ItemPieListener;
import rankstop.steeringit.com.rankstop.ui.callbacks.RecyclerViewClickListener;
import rankstop.steeringit.com.rankstop.ui.dialogFragment.RSFilterDialog;
import rankstop.steeringit.com.rankstop.utils.EndlessScrollListener;
import rankstop.steeringit.com.rankstop.utils.RSConstants;
import rankstop.steeringit.com.rankstop.utils.RSNetwork;
import rankstop.steeringit.com.rankstop.utils.RxSearchObservable;
import rankstop.steeringit.com.rankstop.utils.VerticalSpace;

public class SearchFragment extends Fragment implements RSView.SearchView, FilterDialogListener {
    //views
    private View rootView;
    private Unbinder unbinder;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.search_view)
    SearchView searchView;

    @BindView(R.id.tv_category_title)
    RSTVBold categoryTitleTV;
    @BindView(R.id.rv_categories)
    RecyclerView categoriesRV;

    @BindView(R.id.tv_items_title)
    RSTVBold itemsTitleTV;
    @BindView(R.id.rv_items)
    RecyclerView itemsRV;
    @BindView(R.id.tv_titre_nosearch)
    RSTVRegular msgNoshearch;
    @BindView(R.id.btn_add_item_shearch)
    RSBTNMedium btnAdditem;
    @BindView(R.id.rv_items_fetched)
    RecyclerView itemsByCategoryRV;

    @BindColor(R.color.colorLightGray)
    int colorGray;

    @BindInt(R.integer.m_card_view)
    int marginCardView;
    @BindInt(R.integer.count_item_per_row)
    int countItemPerRow;
    @BindString(R.string.off_line)
    String offlineMsg;

    // variables
    private static SearchFragment instance;
    private RSPresenter.SearchPresenter searchPresenter;
    // categories fetched
    private List<Category> categoriesFetched;
    private DataFetchedAdapter dataFetchedAdapter;
    // items fetched
    private List<ItemDetails> itemsFetched;
    private List<Item> itemsList = new ArrayList<>();
    private ItemsFetchedAdapter itemsFetchedAdapter;
    // items fetched by category
    private ItemsAdapter itemsAdapter;
    private RecyclerViewClickListener itemsListener;
    private RSRequestItemByCategory rsRequestItemData;
    private Category currentCategory;
    // panigation variables
    private int currentPage = 1;
    private boolean isLastPage = false;
    private boolean isLoading = false;
    private int PAGES_COUNT = 1;
    private String query = "";

    private RSRequestFilter dataFiltered;

    public static SearchFragment getInstance() {
        //Bundle args = new Bundle();
        //args.putSerializable(RSConstants.RS_ITEM_DETAILS, itemDetails);
        if (instance == null) {
            instance = new SearchFragment();
        }
        //instance.setArguments(args);
        return instance;
    }

    public static SearchFragment getInstance(Category category) {
        Bundle args = new Bundle();
        args.putSerializable(RSConstants.CATEGORY, category);
        if (instance == null) {
            instance = new SearchFragment();
        }
        instance.setArguments(args);
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
        rootView = inflater.inflate(R.layout.fragment_search, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        rsRequestItemData = new RSRequestItemByCategory();
        rsRequestItemData.setPerPage(RSConstants.MAX_FIELD_TO_LOAD);
        rsRequestItemData.setLang(RankStop.getDeviceLanguage());
        rsRequestItemData.setPage(currentPage);
        if (RSSession.isLoggedIn())
            rsRequestItemData.setUserId(RSSession.getCurrentUser().get_id());

//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                if (!"".equals(query)) {
//
//                }
//                Log.i("tsize", categoriesFetched.size()+"t");
//                return true;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                return true;
//            }
//        });
        bindViews();
        initSearchListener();
        initItemsList();

    }

    @OnClick(R.id.btn_add_item_shearch)
    public void AddItem() {
        replaceFragment(AddItemFragment.getInstance(), RSConstants.FRAGMENT_ADD_ITEM);
    }

    private void replaceFragment(Fragment fragment, String tag) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
    }

    private void initItemsList() {
        ItemPieListener itemsListener = new ItemPieListener() {
            @Override
            public void onFollowChanged(int position) {

            }

            @Override
            public void onClick(View view, int position) {
                if (RSNetwork.isConnected(getContext())) {
                    fragmentActionListener.startFragment(ItemDetailsFragment.getInstance(itemsList.get(position).getItemDetails().get_id()), RSConstants.FRAGMENT_ITEM_DETAILS);
                } else {
                    onOffLine();
                }
            }
        };
        GridLayoutManager layoutManager = new GridLayoutManager(itemsByCategoryRV.getContext(), countItemPerRow);
        itemsAdapter = new ItemsAdapter(itemsListener, false);
        itemsByCategoryRV.setLayoutManager(layoutManager);
        itemsByCategoryRV.setAdapter(itemsAdapter);
        itemsByCategoryRV.addItemDecoration(new VerticalSpace(marginCardView, countItemPerRow));
        itemsByCategoryRV.addOnScrollListener(new EndlessScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;
                rsRequestItemData.setPage(currentPage);
                loadItems(rsRequestItemData);
            }

            @Override
            public int getTotalPageCount() {
                return PAGES_COUNT;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
    }

    private void initSearchListener() {
        RxSearchObservable.fromView(searchView)
                .debounce(300, TimeUnit.MILLISECONDS)
                .filter(text -> {
                    if (text.isEmpty()) {
                        categoryTitleTV.post(() -> categoryTitleTV.setVisibility(View.GONE));
                        categoriesRV.post(() -> categoriesRV.setVisibility(View.GONE));
                        itemsTitleTV.post(() -> itemsTitleTV.setVisibility(View.GONE));
                        itemsRV.post(() -> itemsRV.setVisibility(View.GONE));
                        return false;
                    } else {
                        return true;
                    }
                })
                .distinctUntilChanged()
                .switchMap((Function<String, ObservableSource<RSResponseSearch>>) query -> dataFromNetwork(query))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    //bind data here
                });

    }

    private void bindViews() {

        setFragmentActionListener((ContainerActivity) getActivity());
        toolbar.setTitle("Search");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        //((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        searchPresenter = new PresenterSearchImpl(SearchFragment.this);

        // Use a custom search icon for the SearchView in AppBar
        int searchImgId = android.support.v7.appcompat.R.id.search_button;
        ImageView v = searchView.findViewById(searchImgId);
        //v.setImageResource(R.drawable.search_btn);
        // Customize searchview text and hint colors
        int searchEditId = android.support.v7.appcompat.R.id.search_src_text;
        EditText et = searchView.findViewById(searchEditId);
        et.setTextColor(Color.WHITE);
        et.setHintTextColor(colorGray);
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/montserrat_regular.ttf");
        et.setTypeface(tf);

        // expand search view
        searchView.onActionViewExpanded();

        try {
            currentCategory = (Category) getArguments().getSerializable(RSConstants.CATEGORY);
            loadItems(rsRequestItemData);
        } catch (Exception e) {
        }

        // categories fetched
        categoriesFetched = new ArrayList<>();
        RecyclerViewClickListener categoriesListener = (view, position) -> {
            currentCategory = categoriesFetched.get(position);
            if (RSNetwork.isConnected(getContext())) {
                loadItems(rsRequestItemData);
            } else {
                onOffLine();
            }
        };
        dataFetchedAdapter = new DataFetchedAdapter(categoriesListener);
        categoriesRV.setLayoutManager(new GridLayoutManager(categoriesRV.getContext(), 1));
        categoriesRV.setAdapter(dataFetchedAdapter);

        // items fetched
        itemsFetched = new ArrayList<>();
        RecyclerViewClickListener itemsFetchedListener = (view, position) -> {
            if (RSNetwork.isConnected(getContext())) {
                fragmentActionListener.startFragment(ItemDetailsFragment.getInstance(itemsFetched.get(position).get_id()), RSConstants.FRAGMENT_ITEM_DETAILS);
            } else {
                onOffLine();
            }
        };
        itemsFetchedAdapter = new ItemsFetchedAdapter(itemsFetchedListener);
        itemsRV.setLayoutManager(new GridLayoutManager(itemsRV.getContext(), 1));
        itemsRV.setAdapter(itemsFetchedAdapter);
    }

    private void loadItems(RSRequestItemByCategory rsRequestItemData) {
        rsRequestItemData.setCatId(currentCategory.get_id());
        rsRequestItemData.setQ(query);
        searchPresenter.searchItems(rsRequestItemData, getContext());
    }

    /**
     * Simulation of network data
     */
    private Observable<RSResponseSearch> dataFromNetwork(final String query) {

        this.query = query;
        searchView.setOnCloseListener(() -> false);
        searchPresenter.search(query, RankStop.getDeviceLanguage(), getContext());
        itemsByCategoryRV.post(() -> itemsByCategoryRV.setVisibility(View.GONE));

        return Observable.just(true)
                //.delay(200, TimeUnit.MILLISECONDS)
                .map(value -> new RSResponseSearch());
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
//        MenuItem searchItem = menu.findItem(R.id.filter);
//        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
//        // Use a custom search icon for the SearchView in AppBar
//        int searchImgId = android.support.v7.appcompat.R.id.search_button;
//        ImageView v = (ImageView) searchView.findViewById(searchImgId);
//        //v.setImageResource(R.drawable.search_btn);
//        // Customize searchview text and hint colors
//        int searchEditId = android.support.v7.appcompat.R.id.search_src_text;
//        EditText et = (EditText) searchView.findViewById(searchEditId);
//        et.setTextColor(Color.BLACK);
//        et.setHintTextColor(Color.BLACK);
//        searchItem.expandActionView();
//        searchView.requestFocus();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case android.R.id.home:
                getActivity().onBackPressed();
                break;
            case R.id.filter:
                //ouvre
                RSFilterDialog dialog = new RSFilterDialog();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                dialog.setTargetFragment(this, 0);
                Bundle bundle = new Bundle();
                bundle.putSerializable("data", dataFiltered);
                dialog.setArguments(bundle);
                dialog.show(ft, RSFilterDialog.TAG);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private FragmentActionListener fragmentActionListener;

    public void setFragmentActionListener(FragmentActionListener fragmentActionListener) {
        this.fragmentActionListener = fragmentActionListener;
    }

    @Override
    public void onDestroyView() {

        currentPage = 1;
        isLastPage = false;
        isLoading = false;
        PAGES_COUNT = 1;
        query = "";

        if (unbinder != null)
            unbinder.unbind();
        instance = null;
        if (searchPresenter != null)
            searchPresenter.onDestroy(getContext());
        rootView = null;
        super.onDestroyView();
    }

    @Override
    public void onSuccess(String target, Object data) {
        switch (target) {
            case RSConstants.SEARCH:
                RSResponseSearch rsResponseSearch = new Gson().fromJson(new Gson().toJson(data), RSResponseSearch.class);
                bindDataFetched(rsResponseSearch);
                break;
            case RSConstants.SEARCH_ITEMS:
            case RSConstants.SEARCH_ITEMS_FILTERED:
                RSResponseListingItem rsResponse = new Gson().fromJson(new Gson().toJson(data), RSResponseListingItem.class);
                itemsList.addAll(rsResponse.getItems());
                if (rsResponse.getCurrent() == 1) {
                    itemsAdapter.clear();
                    PAGES_COUNT = rsResponse.getPages();
                } else if (rsResponse.getCurrent() > 1) {
                    itemsAdapter.removeLoadingFooter();
                    isLoading = false;
                }
                itemsAdapter.addAll(rsResponse.getItems());
                if (currentPage < PAGES_COUNT) {
                    itemsAdapter.addLoadingFooter();
                    isLastPage = false;
                } else {
                    isLastPage = true;
                }

                categoriesRV.setVisibility(View.GONE);
                categoryTitleTV.setVisibility(View.GONE);
                itemsRV.setVisibility(View.GONE);
                itemsTitleTV.setVisibility(View.GONE);
                itemsByCategoryRV.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void bindDataFetched(RSResponseSearch rsResponseSearch) {

        itemsFetched = rsResponseSearch.getItem();
        categoriesFetched = rsResponseSearch.getCategory();

        if (categoriesFetched.size() > 0) {

            categoriesRV.setVisibility(View.VISIBLE);
            categoryTitleTV.setVisibility(View.VISIBLE);
            msgNoshearch.setVisibility(View.GONE);
            btnAdditem.setVisibility(View.GONE);
            dataFetchedAdapter.refreshData(categoriesFetched);
        } else {
            msgNoshearch.setVisibility(View.VISIBLE);
            btnAdditem.setVisibility(View.VISIBLE);
            categoriesRV.setVisibility(View.GONE);
            categoryTitleTV.setVisibility(View.GONE);
        }

        if (itemsFetched.size() > 0) {

            itemsRV.setVisibility(View.VISIBLE);
            itemsTitleTV.setVisibility(View.VISIBLE);

            itemsFetchedAdapter.refreshData(itemsFetched);
        } else {
            itemsRV.setVisibility(View.GONE);
            itemsTitleTV.setVisibility(View.GONE);
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
        Toast.makeText(getContext(), offlineMsg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onfilterClicked(RSRequestFilter data) {
        dataFiltered = data;
        //searchPresenter.searchItemsFiltered(data);
        currentCategory = new Category();
        currentCategory.set_id(data.getCatId());

        rsRequestItemData.setCatId(data.getCatId());
        rsRequestItemData.setQ(query);
        rsRequestItemData.setPage(1);
        searchPresenter.searchItems(rsRequestItemData, getContext());
    }
}
