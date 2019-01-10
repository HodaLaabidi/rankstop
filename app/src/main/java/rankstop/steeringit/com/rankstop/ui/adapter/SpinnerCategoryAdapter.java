package rankstop.steeringit.com.rankstop.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.customviews.RSTVMedium;
import rankstop.steeringit.com.rankstop.data.model.db.Category;

public class SpinnerCategoryAdapter extends ArrayAdapter<Category> {

    private Context mContext;
    private List<Category> categoriesList;

    public SpinnerCategoryAdapter(@NonNull Context context, List<Category> list) {
        super(context, 0, list);
        mContext = context;
        categoriesList = list;
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

        Category currentCategory = categoriesList.get(position);

        if (currentCategory != null)
            categoryNameTV.setText(currentCategory.getName());

        return convertView;

    }
}
