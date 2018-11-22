package rankstop.steeringit.com.rankstop.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.ui.adapter.PieAdapter;
import rankstop.steeringit.com.rankstop.ui.callbacks.ItemPieListener;
import rankstop.steeringit.com.rankstop.data.model.Item;
import rankstop.steeringit.com.rankstop.utils.VerticalSpace;

public class SearchActivity extends AppCompatActivity {

    private RecyclerView recyclerViewItems;
    private List<Item> listItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        bindViews();

        initListItems();
    }

    private void initListItems() {
        listItems = new ArrayList<>();
        ItemPieListener listener = new ItemPieListener() {
            @Override
            public void onFollowChanged(boolean isFollow, int position) {

            }

            @Override
            public void onClick(View view, int position) {

            }
        };
        recyclerViewItems.setLayoutManager(new GridLayoutManager(recyclerViewItems.getContext(), getResources().getInteger(R.integer.count_item_per_row)));
        recyclerViewItems.setAdapter(new PieAdapter(listItems, listener, this));
        recyclerViewItems.addItemDecoration(new VerticalSpace(getResources().getInteger(R.integer.m_card_view), getResources().getInteger(R.integer.count_item_per_row)));
    }

    private void bindViews() {
        recyclerViewItems = findViewById(R.id.recycler_view_items);
    }
}
