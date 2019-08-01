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
import rankstop.steeringit.com.rankstop.data.model.db.Country;

public class SpinnerCountryAdapter extends ArrayAdapter<Country> {

    private Context mContext;
    private List<Country> countriesList;

    public SpinnerCountryAdapter(@NonNull Context context, @NonNull List<Country> countriesList) {
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

        Country currentCountry = countriesList.get(position);

        if (currentCountry != null)
            categoryNameTV.setText(currentCountry.getCountryName());

        return convertView;

    }

}
