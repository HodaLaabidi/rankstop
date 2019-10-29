package rankstop.steeringit.com.rankstop.ui.activities;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;

import com.google.gson.Gson;
import rankstop.steeringit.com.rankstop.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rankstop.steeringit.com.rankstop.MVP.model.PresenterItemImpl;
import rankstop.steeringit.com.rankstop.MVP.presenter.RSPresenter;
import rankstop.steeringit.com.rankstop.MVP.view.RSView;
import rankstop.steeringit.com.rankstop.customviews.RSCustomToast;
import rankstop.steeringit.com.rankstop.data.model.db.Picture;
import rankstop.steeringit.com.rankstop.data.model.network.RSRequestItemData;
import rankstop.steeringit.com.rankstop.data.model.network.RSResponseItemData;
import rankstop.steeringit.com.rankstop.ui.fragments.SlidePhotoFragment;
import rankstop.steeringit.com.rankstop.utils.RSConstants;

public class DiaporamaActivity extends BaseActivity implements RSView.StandardView , RSView.StandardView2{

    private Unbinder unbinder;

    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindString(R.string.off_line)
    String offlineMsg;

    private PagerAdapter mPagerAdapter;

    private List<Picture> filteredPictures;
    private RSRequestItemData rsRequestItemData;
    private int countPages;
    private int currentPages;
    private RSPresenter.ItemPresenter itemPresenter;
    private boolean isLastPage = false;
    private int filter, nbrPic;
    private String from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diaparoma);
        unbinder = ButterKnife.bind(this);

        from = getIntent().getStringExtra(RSConstants.FROM);

        nbrPic = getIntent().getIntExtra(RSConstants.PICTURES, 0);
        filteredPictures = (List<Picture>) getIntent().getSerializableExtra(RSConstants.FILTERED_PICTURES);
        rsRequestItemData = (RSRequestItemData) getIntent().getSerializableExtra(RSConstants.RS_REQUEST_ITEM_DATA);
        int position = getIntent().getIntExtra(RSConstants.POSITION, 0);
        countPages = getIntent().getIntExtra(RSConstants.COUNT_PAGES, 0);
        filter = getIntent().getIntExtra(RSConstants.FILTER, 0);
        if (filteredPictures.get(filteredPictures.size() - 1).getPictureEval() == null)
            filteredPictures.remove(filteredPictures.size() - 1);

        int rest = nbrPic % RSConstants.MAX_FIELD_TO_LOAD;
        if (rest == 0) {
            currentPages = nbrPic / RSConstants.MAX_FIELD_TO_LOAD;
            if (currentPages == countPages) {
                isLastPage = true;
            }
        } else {
            isLastPage = true;
        }

        itemPresenter = new PresenterItemImpl(this, this);

        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), filteredPictures);
        viewPager.setAdapter(mPagerAdapter);
        viewPager.setCurrentItem(position);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                if (!isLastPage) {
                    if (i == filteredPictures.size() - 1) {
                        currentPages++;
                        loadItemPix(currentPages);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    private void loadItemPix(int pageNumber) {
        rsRequestItemData.setPage(pageNumber);
        if (from.equals(RSConstants.MY_PIX))
            itemPresenter.loadItemPixByUser(rsRequestItemData, getBaseContext());
        else if (from.equals(RSConstants.ALL_PIX))
            itemPresenter.loadItemPix(rsRequestItemData, getBaseContext());
    }

    @Override
    public void onSuccess(String target, Object data) {
        switch (target) {
            case RSConstants.ITEM_PIX:
                RSResponseItemData rsResponseItemData = new Gson().fromJson(new Gson().toJson(data), RSResponseItemData.class);
                try {
                    managePicsList(rsResponseItemData);
                } catch (Exception e) {
                }
                break;
            case RSConstants.ITEM_PIX_BY_USER:
                RSResponseItemData response = new Gson().fromJson(new Gson().toJson(data), RSResponseItemData.class);
                try {
                    managePicsList(response);
                } catch (Exception e) {
                }
                break;
        }
    }

    @Override
    public void onFailure(String target) {

    }

    @Override
    public void onError(String target) {

    }

    @Override
    public void showProgressBar(String target) {

    }

    @Override
    public void hideProgressBar(String target) {

    }

    @Override
    public void showMessage(String target, String message) {

    }

    @Override
    public void onOffLine() {
        // Toast.makeText(getApplicationContext(), offlineMsg, Toast.LENGTH_LONG).show();
        new RSCustomToast(DiaporamaActivity.this, getResources().getString(R.string.error), offlineMsg, R.drawable.ic_error, RSCustomToast.ERROR).show();

    }

    private void managePicsList(RSResponseItemData rsResponseItemData) {
        nbrPic += rsResponseItemData.getPictures().size();
        switch (filter) {
            case R.id.all_comment:
                filteredPictures.addAll(rsResponseItemData.getPictures());
                break;
            case R.id.good_comment:
                filteredPictures.addAll(getFilterOutput(rsResponseItemData.getPictures(), RSConstants.PIE_GREEN));
                break;
            case R.id.neutral_comment:
                filteredPictures.addAll(getFilterOutput(rsResponseItemData.getPictures(), RSConstants.PIE_ORANGE));
                break;
            case R.id.bad_comment:
                filteredPictures.addAll(getFilterOutput(rsResponseItemData.getPictures(), RSConstants.PIE_RED));
                break;
        }
        mPagerAdapter.notifyDataSetChanged();
        if (rsResponseItemData.getCurrent() == countPages)
            isLastPage = true;
    }

    private List<Picture> getFilterOutput(List<Picture> pictures, int filter) {
        List<Picture> result = new ArrayList<>();
        for (Picture picture : pictures) {
            if (filter == picture.getColor()) {
                result.add(picture);
            }
        }
        return result;
    }

    @Override
    public void onSuccessRefreshItem(String target, String itemId, String message, Object data) {

    }


    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        List<Picture> filteredPictures;

        private ScreenSlidePagerAdapter(FragmentManager fm, List<Picture> pictures) {
            super(fm);
            this.filteredPictures = pictures;
        }

        @Override
        public Fragment getItem(int position) {
            return SlidePhotoFragment.newInstance(filteredPictures.get(position));
        }

        @Override
        public int getCount() {
            return filteredPictures.size();
        }
    }


    @Override
    protected void onDestroy() {

        if (unbinder != null)
            unbinder.unbind();
        if (itemPresenter != null)
            itemPresenter.onDestroyItem(getBaseContext());

        super.onDestroy();
    }
}
