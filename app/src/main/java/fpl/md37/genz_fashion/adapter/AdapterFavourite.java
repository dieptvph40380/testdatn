package fpl.md37.genz_fashion.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.genz_fashion.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import fpl.md37.genz_fashion.api.HttpRequest;
import fpl.md37.genz_fashion.handel.Item_Handel_delete;
import fpl.md37.genz_fashion.models.FavouriteItem;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterFavourite extends RecyclerView.Adapter<AdapterFavourite.ViewHolder> {
    private Context context;
    private List<FavouriteItem> products;
    private HttpRequest httpRequest; // Đối tượng gọi API
    private Item_Handel_delete items;

    // Constructor nhận context và khởi tạo danh sách trống
    public AdapterFavourite(Context context,Item_Handel_delete items) {
        this.context = context;
        this.products = new ArrayList<>();  // Khởi tạo danh sách trống
        this.items=items;
    }

    // Cập nhật danh sách sản phẩm yêu thích
    public void setProducts(List<FavouriteItem> products) {
        this.products = products;
        notifyDataSetChanged();  // Cập nhật lại RecyclerView
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout cho mỗi item trong danh sách yêu thích
        View view = LayoutInflater.from(context).inflate(R.layout.item_wishlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FavouriteItem productItem = products.get(position);
        httpRequest=new HttpRequest();
        // Log thông tin sản phẩm
        Log.d("FavouriteItem", "Product Name: " + productItem.getProductId().getProduct_name());
        Log.d("FavouriteItem", "Product Price: " + productItem.getProductId().getPrice());

        // Cập nhật tên sản phẩm
        holder.nameFavourite.setText(productItem.getProductId().getProduct_name());

        // Cập nhật giá sản phẩm
        holder.priceFavourite.setText(productItem.getProductId().getPrice() + " VND");

        // Hiển thị ảnh sản phẩm nếu có
        String imageUrl = productItem.getProductId().getImage().get(0); // Lấy ảnh đầu tiên trong danh sách ảnh
        if (imageUrl != null && !imageUrl.isEmpty()) {
            // Log URL ảnh
            Log.d("FavouriteItem", "Image URL: " + imageUrl);

            // Thay thế localhost bằng IP máy ảo của Android Emulator nếu cần
            if (imageUrl.startsWith("http://localhost")) {
                imageUrl = imageUrl.replace("http://localhost", "http://10.0.2.2");
            }
            // Tải ảnh sản phẩm vào ImageView sử dụng Glide
            Glide.with(holder.imgFavourite.getContext())
                    .load(imageUrl)
                    .into(holder.imgFavourite);
        } else {
            Log.d("FavouriteItem", "No image available");
        }

        // Ở đây bạn có thể thêm sự kiện click vào icon wishlist nếu cần
        holder.imgHeart.setOnClickListener(v -> {
            // Tạo AlertDialog xác nhận xóa
            new android.app.AlertDialog.Builder(context)
                    .setMessage("Do you want to remove this product from your favourites?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, id) -> {
                        // Xóa sản phẩm khỏi yêu thích khi người dùng nhấn Yes
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        if (currentUser != null) {
                            String userId = currentUser.getUid();
                            String favouriteId = productItem.getProductId().getId();
                            Log.d("FavouriteItem", "User ID: " + userId);
                            Log.d("FavouriteItem", "Product ID to remove: " + favouriteId);// Lấy ID của sản phẩm yêu thích
                            if (favouriteId != null) {
                                items.removeFromFavourite(userId, favouriteId); // Gọi hàm xóa sản phẩm
                            }
                        }
                    })
                    .setNegativeButton("No", (dialog, id) -> dialog.cancel()) // Hủy bỏ khi người dùng chọn No
                    .show();
        });

    }





    @Override
    public int getItemCount() {
        return products.size();  // Trả về số lượng sản phẩm trong danh sách
    }

    // ViewHolder để ánh xạ các thành phần trong item layout
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFavourite, imgHeart;
        TextView nameFavourite, priceFavourite;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFavourite = itemView.findViewById(R.id.product_image);
            imgHeart = itemView.findViewById(R.id.wishlist_icon);
            nameFavourite = itemView.findViewById(R.id.product_name);
            priceFavourite = itemView.findViewById(R.id.product_price);
        }
    }
}
