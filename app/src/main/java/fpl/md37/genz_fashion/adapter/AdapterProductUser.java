package fpl.md37.genz_fashion.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.genz_fashion.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import fpl.md37.genz_fashion.UserScreen.DetailUser;
import fpl.md37.genz_fashion.UserScreen.HomeFragment;
import fpl.md37.genz_fashion.handel.Item_Handle_MyWishlist;
import fpl.md37.genz_fashion.handel.Item_Handle_Product;
import fpl.md37.genz_fashion.models.Product;

public class AdapterProductUser extends RecyclerView.Adapter<AdapterProductUser.ViewHolder>{
    private Context context;
    private ArrayList<Product> listProduct;
    Item_Handle_MyWishlist items;

    public AdapterProductUser(Context context, ArrayList<Product> listProduct, Item_Handle_MyWishlist items) {
        this.context = context;
        this.listProduct = listProduct;
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        View view = inflater.inflate(R.layout.item_products,parent,false);
        return new AdapterProductUser.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = listProduct.get(position);
        String imageUrl = product.getImage().get(0);
        if (imageUrl != null && !imageUrl.isEmpty()) {
            if (imageUrl.startsWith("http://localhost")) {

                imageUrl = imageUrl.replace("http://localhost", "http://192.168.102.23 ");
            }
            Log.d("ImageURL", "Image URL: " + imageUrl);
            Glide.with(holder.image.getContext())
                    .load(imageUrl)
                    .into(holder.image);
        }
        holder.name.setText(product.getProduct_name());
        holder.price.setText("$"+product.getPrice());
//        if (product.isState()) {
//            holder.status.setText("Còn hàng");
//            holder.status.setTextColor(context.getResources().getColor(R.color.green));
//        } else {
//            holder.status.setText("Hết hàng");
//            holder.status.setTextColor(context.getResources().getColor(R.color.red));
//        }

        // Kiểm tra trạng thái yêu thích

        // Xử lý sự kiện nhấn trái tim
        holder.heartIcon.setOnClickListener(view -> {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                String userId = currentUser.getUid();
                holder.heartIcon.setImageResource(R.drawable.heart);
                items.addToFavourite(userId,product);
            }



        });





        holder.show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailUser.class);
                intent.putExtra("product", product); // Truyền đối tượng product sang Activity chi tiết
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listProduct.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView name, price, status;
        ImageView image,heartIcon;
        LinearLayout show;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            show = itemView.findViewById(R.id.show_product);
            image=itemView.findViewById(R.id.img_product);
            name=itemView.findViewById(R.id.tvProduct_name);
            price=itemView.findViewById(R.id.tvProduct_price);
//            status=itemView.findViewById(R.id.tvProduct_status);
            heartIcon = itemView.findViewById(R.id.wishlist_icon); // Khởi tạo trái tim

        }
    }
}
