package rankstop.steeringit.com.rankstop.ui.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.data.model.db.Gallery;
import rankstop.steeringit.com.rankstop.ui.fragments.SlideGalleryFragment;
import rankstop.steeringit.com.rankstop.utils.RSConstants;

public class ItemGalleryActivity extends BaseActivity {

    private Unbinder unbinder;

    @BindView(R.id.view_pager)
    ViewPager viewPager;

    private PagerAdapter mPagerAdapter;

    private List<Gallery> pictures;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_gallery);
        unbinder = ButterKnife.bind(this);

        pictures = (List<Gallery>) getIntent().getSerializableExtra(RSConstants.PICTURES);
        position = getIntent().getIntExtra(RSConstants.POSITION, 0);

        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), pictures);
        viewPager.setAdapter(mPagerAdapter);
        viewPager.setCurrentItem(position);
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        List<Gallery> pix;

        public ScreenSlidePagerAdapter(FragmentManager fm, List<Gallery> pictures) {
            super(fm);
            this.pix = pictures;
        }

        @Override
        public Fragment getItem(int position) {
            return SlideGalleryFragment.newInstance(pix.get(position));
        }

        @Override
        public int getCount() {
            return pix.size();
        }
    }


    @Override
    protected void onDestroy() {

        if (unbinder != null)
            unbinder.unbind();

        super.onDestroy();
    }
}
