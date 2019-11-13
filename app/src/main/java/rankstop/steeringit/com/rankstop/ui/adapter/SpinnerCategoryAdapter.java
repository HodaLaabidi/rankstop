package rankstop.steeringit.com.rankstop.ui.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.RankStop;
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
    public boolean isEnabled(int position){
        if(position == 0)
        {
            // Disable the first item from Spinner
            // First item will be use for hint
            return false;
        }
        else
        {
            return true;
        }
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = initView(position, convertView, parent);
        TextView tv = (TextView) view;
        if(position == 0){
            // Set the hint text color gray
            tv.setTextColor(Color.GRAY);
        }
        else {
            tv.setTextColor(Color.BLACK);
        }
        return view;
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_list_category,parent,false);
        }
        categoryNameTV = convertView.findViewById(R.id.text_view);
        Category currentCategory;
        currentCategory = categoriesList.get(position);
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
