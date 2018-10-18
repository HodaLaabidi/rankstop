package rankstop.steeringit.com.rankstop.Utils;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class VerticalSpace extends RecyclerView.ItemDecoration {

    int space, countItemPerRow;

    public VerticalSpace(int space, int countItemPerRow) {
        this.space = space;
        this.countItemPerRow = countItemPerRow;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        if (parent.getChildLayoutPosition(view) < countItemPerRow) {
            outRect.top = space;
        }else {
            outRect.top = 0;
        }
        outRect.bottom = space;
    }
}
