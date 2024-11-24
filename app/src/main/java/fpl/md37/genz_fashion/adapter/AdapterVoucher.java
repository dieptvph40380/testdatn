package fpl.md37.genz_fashion.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.genz_fashion.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import fpl.md37.genz_fashion.ManagerScreen.VoucherDetailActivity;
import fpl.md37.genz_fashion.handel.Item_Handle_Voucher;
import fpl.md37.genz_fashion.models.Voucher;

public class AdapterVoucher extends RecyclerView.Adapter<AdapterVoucher.ViewHolder> {
    private Context context;
    private ArrayList<Voucher> listVoucher;
    private Item_Handle_Voucher item;
    private Handler handler = new Handler();

    public AdapterVoucher(Context context, ArrayList<Voucher> listVoucher, Item_Handle_Voucher item) {
        this.context = context;
        this.listVoucher = listVoucher;
        this.item = item;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        View view = inflater.inflate(R.layout.item_voucher, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Voucher voucher = listVoucher.get(position);
        String imageUrl = voucher.getImage();

        if (imageUrl != null && !imageUrl.isEmpty()) {
            if (imageUrl.startsWith("http://localhost")) {
                imageUrl = imageUrl.replace("http://localhost", "http://10.0.2.2");
            }
            Log.d("ImageURL", "Image URL: " + imageUrl);
            Glide.with(holder.imageView.getContext())
                    .load(imageUrl)
                    .into(holder.imageView);
        }

        String validFrom = voucher.getValidFrom();
        String validUntil = voucher.getValidUntil();

        // Tính toán số ngày giữa validFrom và validUntil
        long daysDifference = calculateDaysDifference(validFrom, validUntil);
        if (daysDifference <= 0) {
            holder.date.setText("Đã hết hạn");
        } else {
            holder.date.setText("Hết hạn sau " + daysDifference + " ngày");
        }

        holder.name.setText(voucher.getName());
        holder.dis.setText(voucher.getDescription());

        holder.btnedit.setOnClickListener(view -> item.Update(voucher));

        holder.btndelete.setOnClickListener(view -> {

        });

        holder.detail_voucher.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), VoucherDetailActivity.class);
            intent.putExtra("name", voucher.getName());
            intent.putExtra("image", voucher.getImage());
            intent.putExtra("description", voucher.getDescription());
            intent.putExtra("discountValue", voucher.getDiscountValue());
            intent.putExtra("minimumOrderValue", voucher.getMinimumOrderValue());
            intent.putExtra("discountType", voucher.getDiscountType());
            intent.putExtra("validFrom", voucher.getValidFrom());
            intent.putExtra("validUntil", voucher.getValidUntil());
            view.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listVoucher.size();
    }

    // Cập nhật lại số ngày còn lại từ ngày hiện tại đến ngày hết hạn
    private long calculateDaysDifference(String validFrom, String validUntil) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy"); // Định dạng ngày tháng
        try {
            Date startDate = sdf.parse(validFrom);
            Date endDate = sdf.parse(validUntil);
            Date currentDate = new Date();

            // Nếu ngày hiện tại lớn hơn ngày kết thúc thì voucher đã hết hạn
            if (currentDate.after(endDate)) {
                return 0;
            }

            // Nếu ngày bắt đầu lớn hơn ngày hiện tại thì sử dụng ngày bắt đầu
            Date effectiveStartDate = currentDate.before(startDate) ? startDate : currentDate;

            // Tính toán số ngày giữa ngày hiệu lực và ngày hết hạn
            long differenceInMillis = endDate.getTime() - effectiveStartDate.getTime();
            return differenceInMillis / (1000 * 60 * 60 * 24);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    // Lặp lại cập nhật sau mỗi 24 giờ
    private void scheduleDailyUpdate() {
        handler.postDelayed(() -> {
            notifyDataSetChanged();
            scheduleDailyUpdate();
        }, 24 * 60 * 60 * 1000);
    }


    public void startDailyUpdate() {
        scheduleDailyUpdate();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView name, dis, date;
        ImageButton btnedit, btndelete;
        LinearLayout detail_voucher;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            detail_voucher = itemView.findViewById(R.id.liner_voucher);
            imageView = itemView.findViewById(R.id.ImageVoucher);
            name = itemView.findViewById(R.id.Namevoucher);
            dis = itemView.findViewById(R.id.discriptionvoucher);
            btnedit = itemView.findViewById(R.id.btnEdit);
            date = itemView.findViewById(R.id.date);
            btndelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
