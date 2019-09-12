package com.steeringit.rankstop.ui.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.io.Serializable;
import java.util.List;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.steeringit.rankstop.ui.activities.ContainerActivity;
import com.steeringit.rankstop.ui.adapter.ItemEvalAdapter;
import com.steeringit.rankstop.ui.callbacks.FragmentActionListener;
import com.steeringit.rankstop.ui.callbacks.RecyclerViewClickListener;
import com.steeringit.rankstop.data.model.db.CriteriaNote;
import com.steeringit.rankstop.R;
import com.steeringit.rankstop.utils.RSConstants;
import com.steeringit.rankstop.utils.VerticalSpace;

public class ItemEvalsFragment extends Fragment {

    private View rootView;
    private Unbinder unbinder;
    private List<CriteriaNote> listCriterias;

    @BindView(R.id.recycler_view_criterias)
    RecyclerView recyclerViewCriterias;

    @BindView(R.id.layout_add_review)
    LinearLayout layoutAddReview;


    @BindInt(R.integer.count_bar_per_row)
    int countBarPerRow;
    @BindInt(R.integer.m_card_view)
    int marginCardView;

    public ItemEvalsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_item_evals, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setFragmentActionListener((ContainerActivity) getActivity());

        listCriterias = (List<CriteriaNote>) getArguments().getSerializable(RSConstants.TAB_EVALS);
        if (listCriterias.size() == 0)
            layoutAddReview.setVisibility(View.VISIBLE);
        else
            initBarChart();
    }

    private void initBarChart() {
        recyclerViewCriterias.setVisibility(View.VISIBLE);
        RecyclerViewClickListener listener = (view, position) -> {
        };
        recyclerViewCriterias.setLayoutManager(new GridLayoutManager(recyclerViewCriterias.getContext(), countBarPerRow));
        recyclerViewCriterias.setAdapter(new ItemEvalAdapter(listCriterias, listener));
        recyclerViewCriterias.addItemDecoration(new VerticalSpace(marginCardView, countBarPerRow));
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
        if (unbinder != null)
            unbinder.unbind();
        super.onDestroyView();
    }
}
