package fpl.md37.genz_fashion.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.genz_fashion.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import fpl.md37.genz_fashion.ManagerScreen.ProfileCustomerFragment;
import fpl.md37.genz_fashion.models.Client;
import fpl.md37.genz_fashion.models.TypeProduct;

public class AdapterViewCustomer extends RecyclerView.Adapter<AdapterViewCustomer.ViewHolder> {
    private Context context;
    private ArrayList<Client> Client;
    FirebaseFirestore database;
    public AdapterViewCustomer(Context context, ArrayList<Client> Client,FirebaseFirestore database) {
        this.context = context;
        this.Client = Client;
        this.database = database;
    }
    public void updateList(ArrayList<Client> newList) {
        this.Client = newList; // Cập nhật danh sách khách hàng
        notifyDataSetChanged(); // Thông báo adapter để cập nhật
    }
    @NonNull
    @Override
    public AdapterViewCustomer.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        View view = inflater.inflate(R.layout.item_customer_information,parent,false);
        return new AdapterViewCustomer.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterViewCustomer.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.name.setText(Client.get(position).getName());
        holder.phone.setText(Client.get(position).getPhone());
        holder.email.setText(Client.get(position).getEmail());
        Glide.with(context).load(Client.get(position).getAvatar()).into(holder.avatar);

        holder.btnview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(view.getContext(), ProfileCustomerFragment.class);
                intent.putExtra("image",Client.get(position).getAvatar());
                intent.putExtra("name",Client.get(position).getName());
                intent.putExtra("phone",Client.get(position).getPhone());
                intent.putExtra("email",Client.get(position).getEmail());
                intent.putExtra("address",Client.get(position).getAddress());
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return Client.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView name, phone,email;
        Button btnview, btndelete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView); 
            avatar=itemView.findViewById(R.id.image_CusAvatar);
            name=itemView.findViewById(R.id.tv_CusName);
            phone=itemView.findViewById(R.id.tv_CusPhone);
            email=itemView.findViewById(R.id.tv_CusEmail);
            btnview=itemView.findViewById(R.id.btn_CusView);
        }
    }
}
