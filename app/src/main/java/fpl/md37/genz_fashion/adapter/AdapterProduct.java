package fpl.md37.genz_fashion.adapter;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.genz_fashion.R;

import java.util.ArrayList;

import fpl.md37.genz_fashion.ManagerScreen.ProductsFragment;
import fpl.md37.genz_fashion.UserScreen.HomeFragment;
import fpl.md37.genz_fashion.handel.Item_Handle_Product;
import fpl.md37.genz_fashion.models.Product;

public class AdapterProduct extends RecyclerView.Adapter<AdapterProduct.ViewHolder> {
    private Context context;
    private ArrayList<Product> listProduct;
    Item_Handle_Product items;


    public AdapterProduct(Context context, ArrayList<Product> listProduct, ProductsFragment items) {
        this.context = context;
        this.listProduct = listProduct;
        this.items = items;
    }


    //Adapter màn home hiển thị danh sách sản phẩm cho người dùng
    public AdapterProduct(Context context, ArrayList<Product> listProduct, HomeFragment homeFragment) {
        this.context = context;
        this.listProduct = listProduct;
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        View view = inflater.inflate(R.layout.item_manager_products,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = listProduct.get(position);
        String imageUrl = product.getImage().get(0);
        if (imageUrl != null && !imageUrl.isEmpty()) {
            if (imageUrl.startsWith("http://localhost")) {

                imageUrl = imageUrl.replace("http://localhost", "http://10.0.2.2");
            }
            Log.d("ImageURL", "Image URL: " + imageUrl);
            Glide.with(holder.image.getContext())
                    .load(imageUrl)
                    .into(holder.image);
        }
        holder.name.setText(product.getProduct_name());
        holder.price.setText(product.getPrice());
        if (product.isState()) {
            holder.status.setText("Còn hàng");
            holder.status.setTextColor(context.getResources().getColor(R.color.green));
        } else {
            holder.status.setText("Hết hàng");
            holder.status.setTextColor(context.getResources().getColor(R.color.red));
        }
        holder.show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               items.ShowProduct(product);
            }
        });

    }

    @Override
    public int getItemCount() {
        return listProduct.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView name, price, status;
        ImageView image;
        LinearLayout show;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            show = itemView.findViewById(R.id.show_product);
            image=itemView.findViewById(R.id.img_product);
            name=itemView.findViewById(R.id.tvProduct_name);
            price=itemView.findViewById(R.id.tvProduct_price);
            status=itemView.findViewById(R.id.tvProduct_status);

        }
    }

}
