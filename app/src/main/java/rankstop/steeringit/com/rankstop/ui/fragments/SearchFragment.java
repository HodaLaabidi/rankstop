package rankstop.steeringit.com.rankstop.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.data.model.Item;
import rankstop.steeringit.com.rankstop.data.model.User;
import rankstop.steeringit.com.rankstop.ui.adapter.PieAdapter;
import rankstop.steeringit.com.rankstop.ui.callbacks.ItemPieListener;
import rankstop.steeringit.com.rankstop.utils.RxSearchObservable;
import rankstop.steeringit.com.rankstop.utils.VerticalSpace;

public class SearchFragment extends Fragment {

    //views
    private View rootView;
    private Unbinder unbinder;
    @BindView(R.id.recycler_view_items)
    RecyclerView recyclerViewItems;

    @BindView(R.id.search_view)
    SearchView searchView;
    @BindView(R.id.text)
    TextView textView;
    // variables
    private static SearchFragment instance;
    private List<Item> listItems;

    public static SearchFragment getInstance() {
        //Bundle args = new Bundle();
        //args.putSerializable(RSConstants.RS_ITEM_DETAILS, itemDetails);
        if (instance == null) {
            instance = new SearchFragment();
        }
        //instance.setArguments(args);
        return instance;
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

        //initListItems();

        RxSearchObservable.fromView(searchView)
                .debounce(100, TimeUnit.MILLISECONDS)
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(String text) {
                        if (text.isEmpty()) {
                            textView.post(new Runnable() {
                                @Override
                                public void run() {
                                    textView.setText("");
                                }
                            });
                            return false;
                        } else {
                            return true;
                        }
                    }
                })
                .distinctUntilChanged()
                .switchMap(new Function<String, ObservableSource<User>>() {
                    @Override
                    public ObservableSource<User> apply(String query) {
                        return dataFromNetwork(query);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<User>() {
                    @Override
                    public void accept(User result) {
                        textView.setText(result.getFullname());
                    }
                });
    }

    /**
     * Simulation of network data
     */
    private Observable<User> dataFromNetwork(final String query) {
        return Observable.just(true)
                //.delay(200, TimeUnit.MILLISECONDS)
                .map(new Function<Boolean, User>() {
                    @Override
                    public User apply(@io.reactivex.annotations.NonNull Boolean value) {
                        User user = new User();
                        user.setFullname(query);
                        return user;
                    }
                });
    }

    /*private void initListItems() {
        listItems = new ArrayList<>();
        ItemPieListener listener = new ItemPieListener() {
            @Override
            public void onFollowChanged(boolean isFollow, int position) {

            }

            @Override
            public void onFollowChanged(int position) {

            }

            @Override
            public void onClick(View view, int position) {

            }
        };
        recyclerViewItems.setLayoutManager(new GridLayoutManager(recyclerViewItems.getContext(), getResources().getInteger(R.integer.count_item_per_row)));
        recyclerViewItems.setAdapter(new PieAdapter(listItems, listener, getContext()));
        recyclerViewItems.addItemDecoration(new VerticalSpace(getResources().getInteger(R.integer.m_card_view), getResources().getInteger(R.integer.count_item_per_row)));
    }*/

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        instance = null;
        super.onDestroyView();
    }
}
