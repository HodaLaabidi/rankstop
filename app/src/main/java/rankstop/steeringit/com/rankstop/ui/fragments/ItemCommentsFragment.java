package rankstop.steeringit.com.rankstop.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.ui.callbacks.FragmentActionListener;

public class ItemCommentsFragment extends Fragment {

    private RecyclerView recyclerViewComments;
    private View rootView;

    public ItemCommentsFragment() {
    }

    public static ItemCommentsFragment newInstance() {

        Bundle args = new Bundle();

        ItemCommentsFragment fragment = new ItemCommentsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_item_comments, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        recyclerViewComments = rootView.findViewById(R.id.recycler_view_comments);
        recyclerViewComments.setLayoutManager(new LinearLayoutManager(getContext()));
        //recyclerViewDemo.addItemDecoration(new Space(20, 1));
        //recyclerViewDemo.setAdapter(new DemoAdapter(feedItems(), getContext()));
    }

    private FragmentActionListener fragmentActionListener;
    public void setFragmentActionListener(FragmentActionListener fragmentActionListener) {
        this.fragmentActionListener = fragmentActionListener;
    }
    private static ItemCommentsFragment instance;

    public static ItemCommentsFragment getInstance() {
        if (instance == null) {
            instance = new ItemCommentsFragment();
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
