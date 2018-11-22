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

import java.io.Serializable;
import java.util.List;

import rankstop.steeringit.com.rankstop.ui.activities.ContainerActivity;
import rankstop.steeringit.com.rankstop.ui.adapter.ItemEvalAdapter;
import rankstop.steeringit.com.rankstop.ui.callbacks.FragmentActionListener;
import rankstop.steeringit.com.rankstop.ui.callbacks.RecyclerViewClickListener;
import rankstop.steeringit.com.rankstop.data.model.CriteriaNote;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.utils.RSConstants;
import rankstop.steeringit.com.rankstop.utils.VerticalSpace;

public class ItemEvalsFragment extends Fragment {

    private RecyclerView recyclerViewCriterias;
    private View rootView;
    private List<CriteriaNote> listCriterias;

    public ItemEvalsFragment() {
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

        setFragmentActionListener((ContainerActivity) getActivity());

        recyclerViewCriterias = rootView.findViewById(R.id.recycler_view_criterias);
        listCriterias = (List<CriteriaNote>) getArguments().getSerializable(RSConstants.TAB_EVALS);
        initBarChart();
    }

    private void initBarChart() {
        RecyclerViewClickListener listener = (view, position) -> {
        };
        recyclerViewCriterias.setLayoutManager(new GridLayoutManager(recyclerViewCriterias.getContext(), getResources().getInteger(R.integer.count_bar_per_row)));
        recyclerViewCriterias.setAdapter(new ItemEvalAdapter(listCriterias, listener, getContext()));
        recyclerViewCriterias.addItemDecoration(new VerticalSpace(getResources().getInteger(R.integer.m_card_view), getResources().getInteger(R.integer.count_bar_per_row)));
    }

    private FragmentActionListener fragmentActionListener;

    public void setFragmentActionListener(FragmentActionListener fragmentActionListener) {
        this.fragmentActionListener = fragmentActionListener;
    }

    private static ItemEvalsFragment instance;

    public static ItemEvalsFragment getInstance(List<CriteriaNote> criteriaNote) {
        Bundle args = new Bundle();
        args.putSerializable(RSConstants.TAB_EVALS, (Serializable) criteriaNote);
        if (instance == null) {
            instance = new ItemEvalsFragment();
        }
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onDestroyView() {
        instance = null;
        rootView = null;
        fragmentActionListener = null;
        super.onDestroyView();
    }
}
