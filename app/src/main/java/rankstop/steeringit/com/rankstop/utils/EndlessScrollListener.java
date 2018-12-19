package rankstop.steeringit.com.rankstop.utils;

import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

public abstract class EndlessScrollListener extends RecyclerView.OnScrollListener {

    GridLayoutManager layoutManager;

    public EndlessScrollListener(GridLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        Log.i("TAG_PIX", "from listener");

        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

        Log.i("TAG_PIX", "from listener visibleItemCount = "+visibleItemCount);
        Log.i("TAG_PIX", "from listener totalItemCount = "+totalItemCount);
        Log.i("TAG_PIX", "from listener firstVisibleItemPosition = "+ firstVisibleItemPosition);
        Log.i("TAG_PIX", "from listener getTotalPageCount() = "+ getTotalPageCount());
        if (!isLoading() && !isLastPage()) {
            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                loadMoreItems();
            }
        }
    }

    protected abstract void loadMoreItems();

    public abstract int getTotalPageCount();

    public abstract boolean isLastPage();

    public abstract boolean isLoading();
}
