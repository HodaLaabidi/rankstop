package rankstop.steeringit.com.rankstop.ui.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.customviews.RSTVMedium;
import rankstop.steeringit.com.rankstop.data.model.network.CategoryFilter;

public class SpinnerCategoryFilterAdapter extends ArrayAdapter<CategoryFilter> {

    private Context mContext;
    private List<CategoryFilter> countriesList;

    public SpinnerCategoryFilterAdapter(@NonNull Context context, @NonNull List<CategoryFilter> countriesList) {
        super(context, 0, countriesList);
        this.mContext = context;
        this.countriesList = countriesList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_list_category,parent,false);
        }
        RSTVMedium categoryNameTV = convertView.findViewById(R.id.text_view);

        CategoryFilter currentCategory = countriesList.get(position);

        if (currentCategory != null)
            categoryNameTV.setText(currentCategory.getCategory().getName());

        return convertView;

    }
}
