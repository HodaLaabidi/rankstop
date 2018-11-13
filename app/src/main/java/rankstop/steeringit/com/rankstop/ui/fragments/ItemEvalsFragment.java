package rankstop.steeringit.com.rankstop.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import rankstop.steeringit.com.rankstop.ui.adapter.CriteriaAdapter;
import rankstop.steeringit.com.rankstop.ui.callbacks.FragmentActionListener;
import rankstop.steeringit.com.rankstop.ui.callbacks.RecyclerViewClickListener;
import rankstop.steeringit.com.rankstop.data.model.CriteriaNote;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.utils.VerticalSpace;

public class ItemEvalsFragment extends Fragment {

    private RecyclerView recyclerViewCriterias;
    private View rootView;
    private List<CriteriaNote> listCriterias;

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
        rootView = inflater.inflate(R.layout.fragment_item_evals, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        recyclerViewCriterias = rootView.findViewById(R.id.recycler_view_criterias);
        initBarChart();
    }

    private void initBarChart() {
        listCriterias = new ArrayList<>();

        listCriterias.add(new CriteriaNote("1", "Quality", "2", "3", "1"));
        listCriterias.add(new CriteriaNote("2", "Notoriety", "2", "1", "1"));
        listCriterias.add(new CriteriaNote("3", "Price", "1", "1", "2"));
        listCriterias.add(new CriteriaNote("4", "Service", "2", "2", "1"));

        RecyclerViewClickListener listener = (view, position) -> {
            //Toast.makeText(getContext(), "Position " + position, Toast.LENGTH_SHORT).show();
        };

        recyclerViewCriterias.setLayoutManager(new GridLayoutManager(recyclerViewCriterias.getContext(), getResources().getInteger(R.integer.count_bar_per_row)));
        recyclerViewCriterias.setAdapter(new CriteriaAdapter(listCriterias, listener, getContext()));
        recyclerViewCriterias.addItemDecoration(new VerticalSpace(getResources().getInteger(R.integer.m_card_view), getResources().getInteger(R.integer.count_bar_per_row)));
    }

    private FragmentActionListener fragmentActionListener;
    public void setFragmentActionListener(FragmentActionListener fragmentActionListener) {
        this.fragmentActionListener = fragmentActionListener;
    }
    private static ItemEvalsFragment instance;

    public static ItemEvalsFragment getInstance() {
        if (instance == null) {
            instance = new ItemEvalsFragment();
        }
        return instance;
    }

    @Override
    public void onDestroyView() {
        instance = null;
        rootView=null;
        fragmentActionListener = null;
        super.onDestroyView();
    }
}
