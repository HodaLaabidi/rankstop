package rankstop.steeringit.com.rankstop.ui.callbacks;

import androidx.fragment.app.Fragment;

public interface FragmentActionListener {
    void navigateTo(int fragmentId, String tag);
    void startFragment(Fragment fragment, String tag);
    void pop();
}
