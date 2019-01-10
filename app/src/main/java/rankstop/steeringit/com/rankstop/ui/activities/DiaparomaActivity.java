package rankstop.steeringit.com.rankstop.ui.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rankstop.steeringit.com.rankstop.MVP.model.PresenterItemImpl;
import rankstop.steeringit.com.rankstop.MVP.presenter.RSPresenter;
import rankstop.steeringit.com.rankstop.MVP.view.RSView;
import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.data.model.db.Picture;
import rankstop.steeringit.com.rankstop.data.model.network.RSRequestItemData;
import rankstop.steeringit.com.rankstop.data.model.network.RSResponseItemData;
import rankstop.steeringit.com.rankstop.ui.fragments.SlidePhotoFragment;
import rankstop.steeringit.com.rankstop.utils.RSConstants;

public class DiaparomaActivity extends AppCompatActivity implements RSView.StandardView {

    private Unbinder unbinder;

    @BindView(R.id.view_pager)
    ViewPager viewPager;

    private static int MAX_PAGES = 3;
    private static int MAX_FIELD_TO_LOAD = RSConstants.MAX_FIELD_TO_LOAD;

    private PagerAdapter mPagerAdapter;

    private List<Picture> filteredPictures;
    private RSRequestItemData rsRequestItemData;
    private int position, countPages, currentPages;
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
        position = getIntent().getIntExtra(RSConstants.POSITION, 0);
        countPages = getIntent().getIntExtra(RSConstants.COUNT_PAGES, 0);
        filter = getIntent().getIntExtra(RSConstants.FILTER, 0);
        if (filteredPictures.get(filteredPictures.size() - 1).getPictureEval() == null)
            filteredPictures.remove(filteredPictures.size() - 1);

        //Log.i("TAG_PHOTO", "filteredPictures = " + filteredPictures.size() + ", pictures = " + nbrPic + ", url = " + filteredPictures.get(filteredPictures.size() - 1).getPictureEval());

        int rest = nbrPic % RSConstants.MAX_FIELD_TO_LOAD;
        if (rest == 0) {
            currentPages = nbrPic / RSConstants.MAX_FIELD_TO_LOAD;
            if (currentPages == countPages) {
                isLastPage = true;
            }
        } else {
            isLastPage = true;
        }

        //Toast.makeText(this, "size = "+filteredPictures.size()+"\nisLastPage = "+isLastPage, Toast.LENGTH_LONG).show();

        itemPresenter = new PresenterItemImpl(this);

        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), filteredPictures);
        viewPager.setAdapter(mPagerAdapter);
        viewPager.setCurrentItem(position);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                //Log.i("TAG_PHOTO","i = "+i+", i1 = "+i1);
            }

            @Override
            public void onPageSelected(int i) {
                //Log.i("TAG_PHOTO", "i = " + i);
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
            itemPresenter.loadItemPixByUser(rsRequestItemData);
        else if (from.equals(RSConstants.ALL_PIX))
            itemPresenter.loadItemPix(rsRequestItemData);
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


    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        List<Picture> filteredPictures;

        public ScreenSlidePagerAdapter(FragmentManager fm, List<Picture> pictures) {
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

        unbinder.unbind();
        itemPresenter.onDestroyItem();

        super.onDestroy();
    }
}
