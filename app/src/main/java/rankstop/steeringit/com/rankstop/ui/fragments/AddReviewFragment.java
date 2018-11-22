package rankstop.steeringit.com.rankstop.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.ui.activities.ContainerActivity;
import rankstop.steeringit.com.rankstop.ui.activities.TakePictureActivity;
import rankstop.steeringit.com.rankstop.ui.adapter.EvalCriteriasAdapter;
import rankstop.steeringit.com.rankstop.ui.adapter.ReviewPixAdapter;
import rankstop.steeringit.com.rankstop.ui.callbacks.CriteriaEvalListener;
import rankstop.steeringit.com.rankstop.ui.callbacks.FragmentActionListener;
import rankstop.steeringit.com.rankstop.ui.callbacks.RecyclerViewClickListener;
import rankstop.steeringit.com.rankstop.data.model.Criteria;
import rankstop.steeringit.com.rankstop.utils.HorizontalSpace;
import rankstop.steeringit.com.rankstop.utils.VerticalSpace;

public class AddReviewFragment extends Fragment {

    private MaterialButton takePicBtn;
    private RecyclerView recyclerViewEvalCriteria, recyclerViewPix;

    private ReviewPixAdapter reviewPixAdapter;
    private TextView addPixTV;
    private Toolbar toolbar;
    private View rootView;

    List<byte[]> listPics = new ArrayList<>();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_add_review, container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        bindViews();

        toolbar.setTitle(getResources().getString(R.string.title_add_review));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initCriteriasList();
        initPixList();

        takePicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getContext(), TakePictureActivity.class), 1);
            }
        });

    }

    private void bindViews() {
        recyclerViewEvalCriteria = rootView.findViewById(R.id.recycler_view_eval_criteria);
        recyclerViewPix = rootView.findViewById(R.id.recycler_view_pix);
        takePicBtn = rootView.findViewById(R.id.btn_take_pic);
        addPixTV = rootView.findViewById(R.id.tv_add_pix);
        toolbar = rootView.findViewById(R.id.toolbar);

        setFragmentActionListener((ContainerActivity)getActivity());
    }

    private void initCriteriasList() {
        List<Criteria> listCriterias = new ArrayList<>();
        listCriterias.add(new Criteria("Qualité", "id"));
        listCriterias.add(new Criteria("Service", "id"));
        listCriterias.add(new Criteria("Prix", "id"));

        int[] tabNote = new int[3];

        CriteriaEvalListener listener = new CriteriaEvalListener() {
            @Override
            public void onNoteChanged(int note, int position) {
                tabNote[position] = note;
                Toast.makeText(getContext(), "" + note + " at position " + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onImportanceChanged(int importance, int position) {
                Toast.makeText(getContext(), "importance = " + importance + " at position " + position, Toast.LENGTH_SHORT).show();
            }
        };
        recyclerViewEvalCriteria.setLayoutManager(new LinearLayoutManager(recyclerViewEvalCriteria.getContext()));
        recyclerViewEvalCriteria.setAdapter(new EvalCriteriasAdapter(listCriterias, listener, getContext()));
        recyclerViewEvalCriteria.addItemDecoration(new VerticalSpace(10, 1));
    }

    private void initPixList() {
        recyclerViewPix.setVisibility(View.VISIBLE);
        RecyclerViewClickListener listener = (view, position) -> {
            listPics.remove(position);
            reviewPixAdapter.notifyDataSetChanged();
            if (listPics.size() == 0)
                addPixTV.setVisibility(View.VISIBLE);
        };
        reviewPixAdapter = new ReviewPixAdapter(listPics, listener, getContext());
        recyclerViewPix.setLayoutManager(new LinearLayoutManager(recyclerViewPix.getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewPix.setAdapter(reviewPixAdapter);
        recyclerViewPix.addItemDecoration(new HorizontalSpace(getResources().getInteger(R.integer.m_card_view)));
        recyclerViewPix.setNestedScrollingEnabled(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1) {
            if (data != null) {
                byte[] chartData = data.getByteArrayExtra("byte_array");
                if (listPics.size() == 0)
                    addPixTV.setVisibility(View.GONE);
                listPics.add(chartData);
                reviewPixAdapter.notifyDataSetChanged();
            }
        }
    }


    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.profile_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case android.R.id.home:
                getActivity().onBackPressed();
                break;
            case R.id.setting:
                fragmentActionListener.startFragment(SettingsFragment.getInstance());
                break;
            case R.id.logout:
                /*RSSession.removeToken(getContext());
                ((ContainerActivity)getActivity()).manageSession(false);*/
                break;
            case R.id.history:
                fragmentActionListener.startFragment(HistoryFragment.getInstance());
                break;
            case R.id.contact:
                fragmentActionListener.startFragment(ContactFragment.getInstance());
                break;
            case R.id.notifications:
                fragmentActionListener.startFragment(ListNotifFragment.getInstance());
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    private FragmentActionListener fragmentActionListener;
    public void setFragmentActionListener(FragmentActionListener fragmentActionListener) {
        this.fragmentActionListener = fragmentActionListener;
    }
    private static AddReviewFragment instance;

    public static AddReviewFragment getInstance() {
        if (instance == null) {
            instance = new AddReviewFragment();
        }
        return instance;
    }

    @Override
    public void onDestroyView() {
        instance = null;
        fragmentActionListener = null;
        rootView=null;
        listPics.clear();
        super.onDestroyView();
    }
}
