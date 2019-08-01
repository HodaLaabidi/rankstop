package rankstop.steeringit.com.rankstop.ui.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.customviews.RSTVMedium;

public class SpinnerPublicNameAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private String[] publicNameArray;

    public SpinnerPublicNameAdapter(@NonNull Context context, @NonNull String[] publicNameArray) {
        super(context, 0, publicNameArray);
        this.mContext = context;
        this.publicNameArray = publicNameArray;
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

        String publicName = publicNameArray[position];

        if (publicName != null)
            categoryNameTV.setText(publicName);

        return convertView;

    }
}
