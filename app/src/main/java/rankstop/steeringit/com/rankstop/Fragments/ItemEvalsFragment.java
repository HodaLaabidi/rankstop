package rankstop.steeringit.com.rankstop.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import rankstop.steeringit.com.rankstop.Adapter.CriteriaAdapter;
import rankstop.steeringit.com.rankstop.Interface.RecyclerViewClickListener;
import rankstop.steeringit.com.rankstop.Model.Criteria;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.Utils.HorizontalSpace;
import rankstop.steeringit.com.rankstop.Utils.VerticalSpace;

public class ItemEvalsFragment extends Fragment {

    private RecyclerView recyclerViewCriterias;
    private List<Criteria> listCriterias;

    public ItemEvalsFragment() {
    }

    public static ItemEvalsFragment newInstance() {

        Bundle args = new Bundle();

        ItemEvalsFragment fragment = new ItemEvalsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_item_evals, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        recyclerViewCriterias = getActivity().findViewById(R.id.recycler_view_criterias);
        initBarChart();
    }

    private void initBarChart() {
        listCriterias = new ArrayList<>();

        listCriterias.add(new Criteria("1", "Quality", "2", "3", "1"));
        listCriterias.add(new Criteria("2", "Notoriety", "2", "1", "1"));
        listCriterias.add(new Criteria("3", "Price", "1", "1", "2"));
        listCriterias.add(new Criteria("4", "Service", "2", "2", "1"));

        RecyclerViewClickListener listener = (view, position) -> {
            //Toast.makeText(getContext(), "Position " + position, Toast.LENGTH_SHORT).show();
        };

        recyclerViewCriterias.setLayoutManager(new GridLayoutManager(recyclerViewCriterias.getContext(), getResources().getInteger(R.integer.count_bar_per_row)));
        recyclerViewCriterias.setAdapter(new CriteriaAdapter(listCriterias, listener, getContext()));
        recyclerViewCriterias.addItemDecoration(new VerticalSpace(getResources().getInteger(R.integer.m_card_view), getResources().getInteger(R.integer.count_bar_per_row)));
    }
}
