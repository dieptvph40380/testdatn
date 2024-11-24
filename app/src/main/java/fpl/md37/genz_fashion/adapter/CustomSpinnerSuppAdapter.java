package fpl.md37.genz_fashion.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.genz_fashion.R;

import java.util.ArrayList;

import fpl.md37.genz_fashion.models.Suppliers;

public class CustomSpinnerSuppAdapter extends ArrayAdapter<Suppliers> {
    private Context context;
    private ArrayList<Suppliers> suppliers;

    public CustomSpinnerSuppAdapter(Context context, ArrayList<Suppliers> suppliers) {
        super(context, R.layout.spinner_item_suppliers, suppliers);
        this.context = context;
        this.suppliers = suppliers;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    private View getCustomView(int position, View convertView, ViewGroup parent) {
        View row = LayoutInflater.from(context).inflate(R.layout.spinner_item_suppliers, parent, false);
        TextView textView = row.findViewById(R.id.spinner_tex_supp);
        ImageView imageView = row.findViewById(R.id.spinner_image_supp);

        Suppliers suppliers1 = suppliers.get(position);
        textView.setText(suppliers1.getName());

        if (suppliers1.getImage() != null) {
            Glide.with(context)
                    .load(suppliers1.getImage())
                    .into(imageView);
        }

        return row;
    }
}
