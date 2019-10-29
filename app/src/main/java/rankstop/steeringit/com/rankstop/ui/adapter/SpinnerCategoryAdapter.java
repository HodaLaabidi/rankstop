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
import rankstop.steeringit.com.rankstop.data.model.db.Category;

public class SpinnerCategoryAdapter extends ArrayAdapter<Category> {

    private Context mContext;
    private List<Category> categoriesList;

    private static  RSTVMedium categoryNameTV ;

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
         categoryNameTV = convertView.findViewById(R.id.text_view);

        Category currentCategory = categoriesList.get(position);

        if (currentCategory != null)
            categoryNameTV.setText(currentCategory.getName());

        return convertView;

    }

    public Category  refreshSpinner (String categoryId ){

        //Stream category = this.categoriesList.stream().filter(p -> p.get_id().equals("categoryId"));

        for (Category category : this.categoriesList) {
            if (categoryId.equals(category.get_id())) {
                Category  currentCategory = category ;
                categoryNameTV.setText(currentCategory.getName());
                return currentCategory ;
            }
        }

       return new Category();



    }





}
