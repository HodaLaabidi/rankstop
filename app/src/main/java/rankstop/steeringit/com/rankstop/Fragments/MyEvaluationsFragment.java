package rankstop.steeringit.com.rankstop.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import rankstop.steeringit.com.rankstop.R;

public class MyEvaluationsFragment extends Fragment {

    final String TAG = "SETTINGS FRAGMENT";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("LIFE_CYCLE",""+TAG+" onCreateView");
        return inflater.inflate(R.layout.fragment_my_evaluations, container, false);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i("LIFE_CYCLE",""+TAG+" onAttach");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("LIFE_CYCLE",""+TAG+" onCreate");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i("LIFE_CYCLE",""+TAG+" onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i("LIFE_CYCLE",""+TAG+" onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("LIFE_CYCLE",""+TAG+" onResume");
    }

    @Override
    public void onPause() {
        Log.i("LIFE_CYCLE",""+TAG+" onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.i("LIFE_CYCLE",""+TAG+" onStop");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Log.i("LIFE_CYCLE",""+TAG+" onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.i("LIFE_CYCLE",""+TAG+" onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.i("LIFE_CYCLE",""+TAG+" onDetach");
        super.onDetach();
    }
}
