package fpl.md37.genz_fashion.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.genz_fashion.R;

import java.util.ArrayList;
import java.util.List;

import fpl.md37.genz_fashion.handel.Item_Handel_check;
import fpl.md37.genz_fashion.models.ProducItem;

public class CheckOutAdapter extends RecyclerView.Adapter<CheckOutAdapter.ViewHolder>{
    private Context context;
    private List<ProducItem> products;
    private Item_Handel_check listener;

    public CheckOutAdapter(Context context) {
        this.context = context;
        this.products = new ArrayList<>();

    }
    public void setProducts(List<ProducItem> products) {
        this.products = products;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CheckOutAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_checkout, parent, false);
        return new CheckOutAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckOutAdapter.ViewHolder holder, int position) {
        ProducItem product = products.get(position);
        holder.tvProductName.setText(product.getProductId().getProduct_name());
        holder.tvProductPrice.setText(product.getProductId().getPrice()+" VND");
        holder.tvProductQuantity.setText(String.valueOf(product.getQuantity()));
        if (product.getSizeId() != null) {
            holder.tvProductSize.setText("Size: " + product.getSizeId().getName());
        } else {
            holder.tvProductSize.setText("No size available");
        }


        String imageUrl = product.getProductId().getImage().get(0);
        if (imageUrl != null && !imageUrl.isEmpty()) {
            if (imageUrl.startsWith("http://localhost")) {

                imageUrl = imageUrl.replace("http://localhost", "http://10.0.2.2");
            }
            Log.d("ImageURL", "Image URL: " + imageUrl);
            Glide.with(holder.imgProdcut.getContext())
                    .load(imageUrl)
                    .into(holder.imgProdcut);
        }
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProdcut;
        TextView tvProductName,tvProductSize,tvProductQuantity,tvProductPrice;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProdcut=itemView.findViewById(R.id.img_ClImage);
            tvProductName=itemView.findViewById(R.id.tv_ClNameProdcut);
            tvProductSize=itemView.findViewById(R.id.tv_ClProdctSize);
            tvProductQuantity=itemView.findViewById(R.id.tv_ClProductQty);
            tvProductPrice=itemView.findViewById(R.id.tv_ClProductPrice);

        }
    }
}
