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

import com.bumptech.glide.Glide;  // Thêm thư viện Glide vào đây
import com.example.genz_fashion.R;

import java.util.ArrayList;

import fpl.md37.genz_fashion.models.Product; // Import đúng Product
import fpl.md37.genz_fashion.models.Size;   // Import đúng Size
import fpl.md37.genz_fashion.models.ProducItem; // Import đúng ProducItem

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private ArrayList<ProducItem> productItemList;
    private Context context;

    public ProductAdapter(ArrayList<ProducItem> productItemList, Context context) {
        this.productItemList = productItemList;
        this.context = context;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_product_order, parent, false); // Sử dụng layout item_product_order.xml cho sản phẩm
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        ProducItem productItem = productItemList.get(position);
        Product product = productItem.getProductId();
//        Size size = productItem.getSizeId();

        if (product != null) {
            holder.tvProductName.setText(product.getProduct_name());
            holder.tvProductPrice.setText(String.valueOf(product.getPrice()));


//            if (size != null) {
//                holder.tvProductSizeQty.setText("Size: " + size.getName() + " | Qty: " + productItem.getQuantity());
//            } else {
//                holder.tvProductSizeQty.setText("Size: N/A | Qty: " + productItem.getQuantity());
//            }
            // Kiểm tra xem có ảnh không trước khi load
            if (product.getImage() != null && !product.getImage().isEmpty()) {
                Glide.with(context).load(product.getImage().get(0)).into(holder.ivProductImage);
            } else {
                Glide.with(context).load(R.drawable.ic_launcher_background).into(holder.ivProductImage); // placeholder nếu không có ảnh
            }
        }
    }

    @Override
    public int getItemCount() {
        return productItemList != null ? productItemList.size() : 0;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvProductPrice, tvProductSizeQty;
        ImageView ivProductImage;

        public ProductViewHolder(View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvProductSizeQty = itemView.findViewById(R.id.tvProductSizeQty);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
        }
    }
}