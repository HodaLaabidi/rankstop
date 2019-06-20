package rankstop.steeringit.com.rankstop.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.customviews.RSTVMedium;
import rankstop.steeringit.com.rankstop.data.model.network.LocationFilter;

public class SpinnerCountryFilterAdapter extends ArrayAdapter<LocationFilter> {

    private Context mContext;
    private List<LocationFilter> countriesList;

    public SpinnerCountryFilterAdapter(@NonNull Context context, @NonNull List<LocationFilter> countriesList) {
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
        RSTVMedium countryNameTV = convertView.findViewById(R.id.text_view);

        LocationFilter currentCoutry = countriesList.get(position);

        if (currentCoutry != null)
            if(currentCoutry.getCountry() != null){
                countryNameTV.setText(currentCoutry.getCountry().getCountryName());
            }

        return convertView;

    }

}
