package rankstop.steeringit.com.rankstop.ui.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.data.model.Picture;
import rankstop.steeringit.com.rankstop.ui.activities.ContainerActivity;
import rankstop.steeringit.com.rankstop.ui.adapter.ItemPicsAdapter;
import rankstop.steeringit.com.rankstop.ui.callbacks.FragmentActionListener;
import rankstop.steeringit.com.rankstop.ui.callbacks.RecyclerViewClickListener;
import rankstop.steeringit.com.rankstop.utils.RSConstants;
import rankstop.steeringit.com.rankstop.utils.VerticalSpace;

public class ItemPicsFragment extends Fragment {

    private RecyclerView recyclerViewPics;
    private View rootView;
    private List<Picture> pictures;
    private RadioGroup filterToggle;
    private int lastCheckedId = R.id.all_comment;
    private ItemPicsAdapter itemPicsAdapter;

    public ItemPicsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_item_pics, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        pictures= (List<Picture>) getArguments().getSerializable(RSConstants.TAB_PIX);

        bindViews();

        setFragmentActionListener((ContainerActivity)getActivity());
        RecyclerViewClickListener listener = (view, position) -> {
        };
        itemPicsAdapter = new ItemPicsAdapter(pictures, listener, getContext());
        recyclerViewPics.setLayoutManager(new GridLayoutManager(recyclerViewPics.getContext(), getResources().getInteger(R.integer.count_item_per_row)));
        recyclerViewPics.setAdapter(itemPicsAdapter);
        recyclerViewPics.addItemDecoration(new VerticalSpace(getResources().getInteger(R.integer.m_card_view), getResources().getInteger(R.integer.count_item_per_row)));

        setFilterListener();
    }

    private void setFilterListener() {
        filterToggle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (lastCheckedId){
                    case R.id.all_comment:
                        ((RadioButton)rootView.findViewById(lastCheckedId)).setTextColor(ContextCompat.getColor(getContext(), R.color.colorGray));
                        break;
                    case R.id.good_comment:
                        ((RadioButton)rootView.findViewById(lastCheckedId)).setTextColor(ContextCompat.getColor(getContext(), R.color.colorGreenPie));
                        break;
                    case R.id.neutral_comment:
                        ((RadioButton)rootView.findViewById(lastCheckedId)).setTextColor(ContextCompat.getColor(getContext(), R.color.colorOrangePie));
                        break;
                    case R.id.bad_comment:
                        ((RadioButton)rootView.findViewById(lastCheckedId)).setTextColor(ContextCompat.getColor(getContext(), R.color.colorRedPie));
                        break;
                }
                lastCheckedId = checkedId;
                ((RadioButton)rootView.findViewById(checkedId)).setTextColor(Color.WHITE);
                switch (checkedId){
                    case R.id.all_comment:
                        filterToggle.setBackgroundResource(R.drawable.rs_filter_view_gray);
                        itemPicsAdapter.refreshData(pictures);
                        break;
                    case R.id.good_comment:
                        filterToggle.setBackgroundResource(R.drawable.rs_filter_view_green);
                        itemPicsAdapter.refreshData(getFilterOutput(pictures, RSConstants.PIE_GREEN));
                        break;
                    case R.id.neutral_comment:
                        filterToggle.setBackgroundResource(R.drawable.rs_filter_view_orange);
                        itemPicsAdapter.refreshData(getFilterOutput(pictures, RSConstants.PIE_ORANGE));
                        break;
                    case R.id.bad_comment:
                        filterToggle.setBackgroundResource(R.drawable.rs_filter_view_red);
                        itemPicsAdapter.refreshData(getFilterOutput(pictures, RSConstants.PIE_RED));
                        break;
                }
            }
        });
    }

    private List<Picture> getFilterOutput(List<Picture> pictures, int filter) {
        List<Picture> result = new ArrayList<>();
        for (Picture picture : pictures) {
            if (filter == picture.getColor()) {
                result.add(picture);
            }
        }
        //Toast.makeText(getContext(), ""+result.size(), Toast.LENGTH_SHORT).show();
        return result;
    }

    private void bindViews() {
        recyclerViewPics = rootView.findViewById(R.id.recycler_view_pics);
        filterToggle = rootView.findViewById(R.id.filter_toggle);
    }

    private FragmentActionListener fragmentActionListener;
    public void setFragmentActionListener(FragmentActionListener fragmentActionListener) {
        this.fragmentActionListener = fragmentActionListener;
    }
    private static ItemPicsFragment instance;

    public static ItemPicsFragment getInstance(List<Picture> pictures) {

        Bundle args = new Bundle();
        args.putSerializable(RSConstants.TAB_PIX, (Serializable) pictures);

        if (instance == null) {
            instance = new ItemPicsFragment();
        }
        instance.setArguments(args);
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
