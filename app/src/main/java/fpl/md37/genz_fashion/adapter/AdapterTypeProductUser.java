package fpl.md37.genz_fashion.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.genz_fashion.R;

import java.util.ArrayList;

import fpl.md37.genz_fashion.handel.Item_Handel_click;
import fpl.md37.genz_fashion.models.TypeProduct;

public class AdapterTypeProductUser extends RecyclerView.Adapter<AdapterTypeProductUser.ViewHolder> {
    private Context context;
    private Item_Handel_click listener;
    private ArrayList<TypeProduct> typeProducts;
    private int selectedPosition = -1; // Biến lưu vị trí item được chọn

    public AdapterTypeProductUser(Context context, ArrayList<TypeProduct> typeProducts, Item_Handel_click listener) {
        this.context = context;
        this.typeProducts = typeProducts;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AdapterTypeProductUser.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View view = inflater.inflate(R.layout.item_typeproduct_btn, parent, false);
        return new AdapterTypeProductUser.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterTypeProductUser.ViewHolder holder, int position) {
        TypeProduct typeProduct = typeProducts.get(position);
        holder.name.setText(typeProduct.getName());

        // Kiểm tra nếu item này được chọn hay không
        if (position == selectedPosition) {
            holder.name.setBackgroundColor(context.getResources().getColor(R.color.blue)); // Màu khi được chọn
            holder.name.setTextColor(context.getResources().getColor(R.color.white));  // Màu chữ khi chọn
        } else {
            holder.name.setBackgroundColor(context.getResources().getColor(R.color.white)); // Màu mặc định
            holder.name.setTextColor(context.getResources().getColor(R.color.black));   // Màu chữ mặc định
        }

        holder.type.setOnClickListener(v -> {
            // Cập nhật vị trí được chọn và thông báo sự kiện
            selectedPosition = position;
            notifyDataSetChanged(); // Thông báo RecyclerView cập nhật giao diện

            if (listener != null) {
                listener.onTypeProductClick(typeProduct.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return typeProducts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        LinearLayout type;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            type=itemView.findViewById(R.id.itemType);
            name = itemView.findViewById(R.id.tvFilter);
        }
    }
}
