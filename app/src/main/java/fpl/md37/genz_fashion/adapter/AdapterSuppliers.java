package fpl.md37.genz_fashion.adapter;

import android.app.Activity;
import android.content.Context;
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

import java.util.ArrayList;
import java.util.List;

import fpl.md37.genz_fashion.ManagerScreen.SupplierFragment;
import fpl.md37.genz_fashion.handel.Item_Handel_Suppliers;
import fpl.md37.genz_fashion.models.Suppliers;

public class AdapterSuppliers extends RecyclerView.Adapter<AdapterSuppliers.ViewHolder> {

    private Context context;
    private ArrayList<Suppliers> listsuppliers;
    private Item_Handel_Suppliers items;




    public AdapterSuppliers(Context context, ArrayList<Suppliers> listsuppliers, SupplierFragment items) {
        this.context = context;
        this.listsuppliers = listsuppliers;
        this.items =  items;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=((Activity)context).getLayoutInflater();
        View view=inflater.inflate(R.layout.itemsuppliers,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Suppliers suppliers=listsuppliers.get(position);
        String imageUrl = suppliers.getImage();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            if (imageUrl.startsWith("http://localhost")) {

                imageUrl = imageUrl.replace("http://localhost", "http://10.0.2.2");
            }
            Log.d("ImageURL", "Image URL: " + imageUrl);
            Glide.with(holder.imageView.getContext())
                    .load(imageUrl)
                    .into(holder.imageView);
        }
        holder.name.setText(suppliers.getName());
        holder.phone.setText(suppliers.getPhone());
        holder.email.setText(suppliers.getEmail());
        holder.description.setText(suppliers.getDescription());

        holder.itemDelete.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                items.Delete(suppliers);
                return false;
            }
        });

        holder.btnedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                items.Update(suppliers);
            }
        });
    }




    @Override
    public int getItemCount() {
        return listsuppliers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView name, phone,email,description;
        ImageButton btnedit;
        LinearLayout itemDelete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.imageSupplier);
            name=itemView.findViewById(R.id.textSupplierName);
            phone=itemView.findViewById(R.id.textSupplierContact);
            email=itemView.findViewById(R.id.textSupplierEmail);
            description=itemView.findViewById(R.id.textSupplierDescreption);

            btnedit=itemView.findViewById(R.id.btnEditS);

            itemDelete=itemView.findViewById(R.id.itemDelete);


        }
    }
}

