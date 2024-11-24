package fpl.md37.genz_fashion.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.genz_fashion.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import fpl.md37.genz_fashion.handel.Item_Handel_check;
import fpl.md37.genz_fashion.models.ProducItem;
import fpl.md37.genz_fashion.models.Product;
import fpl.md37.genz_fashion.models.Size;

public class AdapterCart extends RecyclerView.Adapter<AdapterCart.ViewHolder> {
    private Context context;
    private List<ProducItem> products;
    private Item_Handel_check listener;

    public AdapterCart(Context context, Item_Handel_check listener) {
        this.context = context;
        this.products = new ArrayList<>();
        this.listener = listener;
    }

    public void setProducts(List<ProducItem> products) {
        this.products = products;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AdapterCart.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterCart.ViewHolder holder, int position) {
        ProducItem product = products.get(position);

        // Cập nhật tên sản phẩm
        holder.cart_name.setText(product.getProductId().getProduct_name());

        // Cập nhật giá sản phẩm
        holder.cart_price.setText(product.getProductId().getPrice() + " VND");

        // Cập nhật số lượng
        int quantity = product.getQuantity();
        if (quantity <= 0) {
            quantity = 1; // Đảm bảo số lượng không dưới 1
        }
        holder.cart_quantity.setText(String.valueOf(quantity));
        Log.d("QuantityCheck", "Product quantity: " + product.getQuantity());
        // Cập nhật trạng thái checkbox
        holder.checkBox_cart.setChecked(product.isSelected());

        // Lắng nghe sự kiện khi checkbox thay đổi
        holder.checkBox_cart.setOnCheckedChangeListener((buttonView, isChecked) -> {
            product.setSelected(isChecked);
            if (listener != null) {
                listener.onProductChecked(product, isChecked);
            }
        });

        holder.btn_minus.setOnClickListener(view -> {
            int currentQuantity = product.getQuantity();
            if (currentQuantity > 1) {
                product.setQuantity(currentQuantity - 1);
                holder.cart_quantity.setText(String.valueOf(product.getQuantity()));
                notifyItemChanged(position);

                // Cập nhật dữ liệu server hoặc cơ sở dữ liệu nếu cần
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    String userId = currentUser.getUid();
                    listener.updateQuantity(userId, product.getProductId().getId(), product.getSizeId().getId(), "decrease");
                }
            } else {
                Toast.makeText(context, "Quantity cannot be less than 1", Toast.LENGTH_SHORT).show();
            }
        });
        holder.item_cart.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    String userId = currentUser.getUid();
                    String favouriteId = product.getProductId().getId();
                    if (favouriteId != null) {
                        listener.removeCart(userId,favouriteId); // Gọi hàm xóa sản phẩm
                    }
                }
                return false;
            }
        });
        holder.btn_add.setOnClickListener(view -> {
            product.setQuantity(product.getQuantity() + 1);
            holder.cart_quantity.setText(String.valueOf(product.getQuantity()));
            notifyItemChanged(position);

            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                String userId = currentUser.getUid();
                listener.updateQuantity(userId, product.getProductId().getId(), product.getSizeId().getId(), "increase");
            }
        });

        // Hiển thị ảnh sản phẩm
        String imageUrl = product.getProductId().getImage().get(0);
        if (imageUrl != null && !imageUrl.isEmpty()) {
            if (imageUrl.startsWith("http://localhost")) {
                imageUrl = imageUrl.replace("http://localhost", "http://10.0.2.2");
            }
            Log.d("ImageURL", "Image URL: " + imageUrl);
            Glide.with(holder.img_cart.getContext())
                    .load(imageUrl)
                    .into(holder.img_cart);
        }

        // Hiển thị kích thước sản phẩm
        if (product.getSizeId() != null) {
            holder.cart_size.setText("Size: " + product.getSizeId().getName());
        } else {
            holder.cart_size.setText("No size available");
        }
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img_cart;
        CheckBox checkBox_cart;
        ImageView btn_minus, btn_add;

        LinearLayout item_cart;
        TextView cart_name, cart_size, cart_price, cart_quantity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item_cart=itemView.findViewById(R.id.item_cart);
            img_cart = itemView.findViewById(R.id.cart_IMG);
            cart_name = itemView.findViewById(R.id.cart_name);
            cart_size = itemView.findViewById(R.id.cart_size);
            cart_price = itemView.findViewById(R.id.cart_price);
            checkBox_cart = itemView.findViewById(R.id.checkbox);
            cart_quantity = itemView.findViewById(R.id.quantity_cart);
            btn_minus = itemView.findViewById(R.id.nexttypeproductupload);
            btn_add = itemView.findViewById(R.id.addtypeproductupload);
        }
    }
}
