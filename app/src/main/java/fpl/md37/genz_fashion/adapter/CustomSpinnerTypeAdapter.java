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

import fpl.md37.genz_fashion.models.TypeProduct;

public class CustomSpinnerTypeAdapter extends ArrayAdapter<TypeProduct> {
    private Context context;
    private ArrayList<TypeProduct> typeProducts;

    public CustomSpinnerTypeAdapter(Context context, ArrayList<TypeProduct> typeProducts) {
        super(context, R.layout.spinner_item_type, typeProducts);
        this.context = context;
        this.typeProducts = typeProducts;
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
        View row = LayoutInflater.from(context).inflate(R.layout.spinner_item_type, parent, false);
        TextView textView = row.findViewById(R.id.spinner_text);
        ImageView imageView = row.findViewById(R.id.spinner_image);

        TypeProduct typeProduct = typeProducts.get(position);
        textView.setText(typeProduct.getName());

        if (typeProduct.getImage() != null) {
            Glide.with(context)
                    .load(typeProduct.getImage())
                    .into(imageView);
        }

        return row;
    }
}


