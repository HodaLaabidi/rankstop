package rankstop.steeringit.com.rankstop.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.ui.callbacks.FragmentActionListener;

public class MyEvaluationsFragment extends Fragment {

    final String TAG = "SETTINGS FRAGMENT";
    private Toolbar toolbar;
    private View rootView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("LIFE_CYCLE", "" + TAG + " onCreateView");
        rootView = inflater.inflate(R.layout.fragment_my_evaluations, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        bindViews();
    }

    private void bindViews() {
        toolbar = rootView.findViewById(R.id.toolbar);

        toolbar.setTitle(getResources().getString(R.string.title_my_evals));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.profile_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
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
    private static MyEvaluationsFragment instance;

    public static MyEvaluationsFragment getInstance() {
        if (instance == null) {
            instance = new MyEvaluationsFragment();
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





    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i("LIFE_CYCLE", "" + TAG + " onAttach");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i("LIFE_CYCLE", "" + TAG + " onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("LIFE_CYCLE", "" + TAG + " onResume");
    }

    @Override
    public void onPause() {
        Log.i("LIFE_CYCLE", "" + TAG + " onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.i("LIFE_CYCLE", "" + TAG + " onStop");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.i("LIFE_CYCLE", "" + TAG + " onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.i("LIFE_CYCLE", "" + TAG + " onDetach");
        super.onDetach();
    }
}
