package fpl.md37.genz_fashion.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.genz_fashion.R;

import java.util.ArrayList;

import fpl.md37.genz_fashion.models.CartData;
import fpl.md37.genz_fashion.models.Order;
import fpl.md37.genz_fashion.models.ProducItem;
import fpl.md37.genz_fashion.models.Product;
import fpl.md37.genz_fashion.models.Size;

public class AdapterOderActive extends RecyclerView.Adapter<AdapterOderActive.OrderActiveViewHolder> {

    private ArrayList<Order> orderList;
    private Context context;

    // Constructor
    public AdapterOderActive(ArrayList<Order> orderList, Context context) {
        this.orderList = orderList;
        this.context = context;
    }

    // Update data in the adapter
    public void setOrderList(ArrayList<Order> orderList) {
        this.orderList = orderList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderActiveViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_active, parent, false);
        return new OrderActiveViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderActiveViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.timeOrder.setText(order.getTimeOrder());

        ArrayList<ProducItem> productList = new ArrayList<>(order.getProducts());
        Log.d("AdapterOderActive", "Number of products: " + productList.size());
        if (!productList.isEmpty()) {
            ProductAdapter productAdapter = new ProductAdapter(productList, context);
            holder.rvProductList.setAdapter(productAdapter);
        } else {
            Log.d("AdapterOderActive", "Product list is empty.");
        }


        holder.btnTrackOrder.setOnClickListener(v -> {

        });
    }

    @Override
    public int getItemCount() {
        return orderList == null ? 0 : orderList.size();
    }

    // ViewHolder class
    public static class OrderActiveViewHolder extends RecyclerView.ViewHolder {
        TextView timeOrder;
        RecyclerView rvProductList;
        Button btnTrackOrder;

        public OrderActiveViewHolder(@NonNull View itemView) {
            super(itemView);
            rvProductList = itemView.findViewById(R.id.rvProductList_order);
            btnTrackOrder = itemView.findViewById(R.id.btnTrackOrder);
            timeOrder = itemView.findViewById(R.id.timeorder);
            rvProductList.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
        }
    }
}