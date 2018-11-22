package rankstop.steeringit.com.rankstop.ui.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.data.model.Comment;
import rankstop.steeringit.com.rankstop.ui.activities.ContainerActivity;
import rankstop.steeringit.com.rankstop.ui.adapter.ItemCommentAdapter;
import rankstop.steeringit.com.rankstop.ui.callbacks.FragmentActionListener;
import rankstop.steeringit.com.rankstop.ui.callbacks.RecyclerViewClickListener;
import rankstop.steeringit.com.rankstop.utils.RSConstants;
import rankstop.steeringit.com.rankstop.utils.VerticalSpace;

public class ItemCommentsFragment extends Fragment {

    private RecyclerView recyclerViewComments;
    private View rootView;
    private List<Comment> comments;
    private RadioGroup filterToggle;
    private int lastCheckedId = R.id.all_comment;
    private ItemCommentAdapter itemCommentAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_item_comments, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        bindViews();

        comments= (List<Comment>) getArguments().getSerializable(RSConstants.TAB_COMMENTS);
        setFragmentActionListener((ContainerActivity)getActivity());

        RecyclerViewClickListener listener = (view, position) -> {
        };

        itemCommentAdapter = new ItemCommentAdapter(comments, listener, getContext());
        recyclerViewComments.setLayoutManager(new GridLayoutManager(recyclerViewComments.getContext(), getResources().getInteger(R.integer.count_item_per_row)));

        recyclerViewComments.addItemDecoration(new VerticalSpace(getResources().getInteger(R.integer.m_card_view), getResources().getInteger(R.integer.count_item_per_row)));

        setFilterListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        recyclerViewComments.setAdapter(itemCommentAdapter);
    }

    private void bindViews() {
        filterToggle = rootView.findViewById(R.id.filter_toggle);
        recyclerViewComments = rootView.findViewById(R.id.recycler_view_comments);
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
                        itemCommentAdapter.refreshData(comments);
                        break;
                    case R.id.good_comment:
                        filterToggle.setBackgroundResource(R.drawable.rs_filter_view_green);
                        itemCommentAdapter.refreshData(getFilterOutput(comments, RSConstants.PIE_GREEN));
                        break;
                    case R.id.neutral_comment:
                        filterToggle.setBackgroundResource(R.drawable.rs_filter_view_orange);
                        itemCommentAdapter.refreshData(getFilterOutput(comments, RSConstants.PIE_ORANGE));
                        break;
                    case R.id.bad_comment:
                        filterToggle.setBackgroundResource(R.drawable.rs_filter_view_red);
                        itemCommentAdapter.refreshData(getFilterOutput(comments, RSConstants.PIE_RED));
                        break;
                }
            }
        });
    }

    private List<Comment> getFilterOutput(List<Comment> comments, int filter) {
        List<Comment> result = new ArrayList<>();
        for (Comment comment : comments) {
            if (filter == comment.getColor()) {
                result.add(comment);
            }
        }
        //Toast.makeText(getContext(), ""+result.size(), Toast.LENGTH_SHORT).show();
        return result;
    }

    private FragmentActionListener fragmentActionListener;
    public void setFragmentActionListener(FragmentActionListener fragmentActionListener) {
        this.fragmentActionListener = fragmentActionListener;
    }
    private static ItemCommentsFragment instance;

    public static ItemCommentsFragment getInstance(List<Comment> comments) {

        Bundle args = new Bundle();
        args.putSerializable(RSConstants.TAB_COMMENTS, (Serializable) comments);

        if (instance == null) {
            instance = new ItemCommentsFragment();
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
