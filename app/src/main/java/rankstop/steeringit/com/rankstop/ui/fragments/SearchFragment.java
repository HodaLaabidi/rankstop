package rankstop.steeringit.com.rankstop.ui.fragments;

import android.graphics.Color;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import rankstop.steeringit.com.rankstop.MVP.model.PresenterSearchImpl;
import rankstop.steeringit.com.rankstop.MVP.presenter.RSPresenter;
import rankstop.steeringit.com.rankstop.MVP.view.RSView;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.data.model.db.Category;
import rankstop.steeringit.com.rankstop.data.model.db.ItemDetails;
import rankstop.steeringit.com.rankstop.data.model.network.RSRequestItemByCategory;
import rankstop.steeringit.com.rankstop.data.model.network.RSResponseListingItem;
import rankstop.steeringit.com.rankstop.data.model.network.RSResponseSearch;
import rankstop.steeringit.com.rankstop.session.RSSession;
import rankstop.steeringit.com.rankstop.ui.activities.ContainerActivity;
import rankstop.steeringit.com.rankstop.ui.adapter.DataFetchedAdapter;
import rankstop.steeringit.com.rankstop.ui.adapter.ItemsAdapter;
import rankstop.steeringit.com.rankstop.ui.adapter.ItemsFetchedAdapter;
import rankstop.steeringit.com.rankstop.ui.callbacks.FragmentActionListener;
import rankstop.steeringit.com.rankstop.ui.callbacks.ItemPieListener;
import rankstop.steeringit.com.rankstop.ui.callbacks.RecyclerViewClickListener;
import rankstop.steeringit.com.rankstop.utils.EndlessScrollListener;
import rankstop.steeringit.com.rankstop.utils.RSConstants;
import rankstop.steeringit.com.rankstop.utils.RxSearchObservable;
import rankstop.steeringit.com.rankstop.utils.VerticalSpace;

public class SearchFragment extends Fragment implements RSView.SearchView {
    //views
    private View rootView;
    private Unbinder unbinder;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.search_view)
    SearchView searchView;

    @BindView(R.id.tv_category_title)
    TextView categoryTitleTV;
    @BindView(R.id.rv_categories)
    RecyclerView categoriesRV;

    @BindView(R.id.tv_items_title)
    TextView itemsTitleTV;
    @BindView(R.id.rv_items)
    RecyclerView itemsRV;

    @BindView(R.id.rv_items_fetched)
    RecyclerView itemsByCategoryRV;

    @BindColor(R.color.colorLightGray)
    int colorGray;
    // variables
    private static SearchFragment instance;
    private RSPresenter.SearchPresenter searchPresenter;
    // categories fetched
    private List<Category> categoriesFetched;
    private DataFetchedAdapter dataFetchedAdapter;
    private RecyclerViewClickListener categoriesListener;
    // items fetched
    private List<ItemDetails> itemsFetched;
    private ItemsFetchedAdapter itemsFetchedAdapter;
    private RecyclerViewClickListener itemsFetchedListener;
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
        rsRequestItemData.setPage(currentPage);
        if (RSSession.isLoggedIn(getContext()))
            rsRequestItemData.setUserId(RSSession.getCurrentUser(getContext()).get_id());

        bindViews();
        initSearchListener();
        initItemsList();

    }

    private void initItemsList() {
        ItemPieListener itemsListener = new ItemPieListener() {
            @Override
            public void onFollowChanged(boolean isFollow, int position) {

            }

            @Override
            public void onFollowChanged(int position) {

            }

            @Override
            public void onClick(View view, int position) {
                fragmentActionListener.startFragment(ItemDetailsFragment.getInstance(itemsFetched.get(position).get_id()), RSConstants.FRAGMENT_ITEM_DETAILS);
            }
        };
        GridLayoutManager layoutManager = new GridLayoutManager(itemsByCategoryRV.getContext(), getResources().getInteger(R.integer.count_item_per_row));
        itemsAdapter = new ItemsAdapter(itemsListener, getContext(), false);
        itemsByCategoryRV.setLayoutManager(layoutManager);
        itemsByCategoryRV.setAdapter(itemsAdapter);
        itemsByCategoryRV.addItemDecoration(new VerticalSpace(getResources().getInteger(R.integer.m_card_view), getResources().getInteger(R.integer.count_item_per_row)));
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
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(String text) {
                        if (text.isEmpty()) {
                            categoryTitleTV.post(new Runnable() {
                                @Override
                                public void run() {
                                    categoryTitleTV.setVisibility(View.GONE);
                                }
                            });
                            categoriesRV.post(new Runnable() {
                                @Override
                                public void run() {
                                    categoriesRV.setVisibility(View.GONE);
                                }
                            });
                            itemsTitleTV.post(new Runnable() {
                                @Override
                                public void run() {
                                    itemsTitleTV.setVisibility(View.GONE);
                                }
                            });
                            itemsRV.post(new Runnable() {
                                @Override
                                public void run() {
                                    itemsRV.setVisibility(View.GONE);
                                }
                            });
                            return false;
                        } else {
                            return true;
                        }
                    }
                })
                .distinctUntilChanged()
                .switchMap(new Function<String, ObservableSource<RSResponseSearch>>() {
                    @Override
                    public ObservableSource<RSResponseSearch> apply(String query) {
                        return dataFromNetwork(query);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<RSResponseSearch>() {
                    @Override
                    public void accept(RSResponseSearch result) {
                        //bind data here
                    }
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

        // expand search view
        searchView.onActionViewExpanded();

        try {
            currentCategory = (Category) getArguments().getSerializable(RSConstants.CATEGORY);
            loadItems(rsRequestItemData);
        } catch (Exception e) {
        }

        // categories fetched
        categoriesFetched = new ArrayList<>();
        categoriesListener = (view, position) -> {
            currentCategory = categoriesFetched.get(position);
            loadItems(rsRequestItemData);
        };
        dataFetchedAdapter = new DataFetchedAdapter(getContext(), categoriesListener);
        categoriesRV.setLayoutManager(new GridLayoutManager(categoriesRV.getContext(), 1));
        categoriesRV.setAdapter(dataFetchedAdapter);

        // items fetched
        itemsFetched = new ArrayList<>();
        itemsFetchedListener = (view, position) -> {
            fragmentActionListener.startFragment(ItemDetailsFragment.getInstance(itemsFetched.get(position).get_id()), RSConstants.FRAGMENT_ITEM_DETAILS);
        };
        itemsFetchedAdapter = new ItemsFetchedAdapter(getContext(), itemsFetchedListener);
        itemsRV.setLayoutManager(new GridLayoutManager(itemsRV.getContext(), 1));
        itemsRV.setAdapter(itemsFetchedAdapter);
    }

    private void loadItems(RSRequestItemByCategory rsRequestItemData) {
        rsRequestItemData.setCatId(currentCategory.get_id());
        rsRequestItemData.setQ(query);
        searchPresenter.searchItems(rsRequestItemData);
    }

    /**
     * Simulation of network data
     */
    private Observable<RSResponseSearch> dataFromNetwork(final String query) {

        this.query = query;
        searchPresenter.search(query);
        itemsByCategoryRV.post(new Runnable() {
            @Override
            public void run() {
                itemsByCategoryRV.setVisibility(View.GONE);
            }
        });

        return Observable.just(true)
                //.delay(200, TimeUnit.MILLISECONDS)
                .map(new Function<Boolean, RSResponseSearch>() {
                    @Override
                    public RSResponseSearch apply(@io.reactivex.annotations.NonNull Boolean value) {
                        RSResponseSearch user = new RSResponseSearch();
                        return user;
                    }
                });
    }

    /*public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        // Use a custom search icon for the SearchView in AppBar
        int searchImgId = android.support.v7.appcompat.R.id.search_button;
        ImageView v = (ImageView) searchView.findViewById(searchImgId);
        //v.setImageResource(R.drawable.search_btn);
        // Customize searchview text and hint colors
        int searchEditId = android.support.v7.appcompat.R.id.search_src_text;
        EditText et = (EditText) searchView.findViewById(searchEditId);
        et.setTextColor(Color.BLACK);
        et.setHintTextColor(Color.BLACK);
        searchItem.expandActionView();
        searchView.requestFocus();
    }*/

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

    private FragmentActionListener fragmentActionListener;

    public void setFragmentActionListener(FragmentActionListener fragmentActionListener) {
        this.fragmentActionListener = fragmentActionListener;
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        instance = null;
        searchPresenter.onDestroy();
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
                RSResponseListingItem rsResponse = new Gson().fromJson(new Gson().toJson(data), RSResponseListingItem.class);
                //itemsList.addAll(rsResponse.getItems());
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

            dataFetchedAdapter.refreshData(categoriesFetched);
        } else {
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
}
