package fpl.md37.genz_fashion.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.genz_fashion.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import fpl.md37.genz_fashion.UserScreen.ChatActivity;
import fpl.md37.genz_fashion.models.Client;
import fpl.md37.genz_fashion.utils.AndroidUtil;

public class SearchUserAdapter extends FirestoreRecyclerAdapter<Client, SearchUserAdapter.UserModelViewHolder> {

    private final Context context;

    // Constructor
    public SearchUserAdapter(@NonNull FirestoreRecyclerOptions<Client> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserModelViewHolder holder, int position, @NonNull Client model) {
        // Set username and phone
        holder.usernameText.setText(model.getName() != null ? model.getName() : "Unknown User");
        holder.phoneText.setText(model.getPhone() != null ? model.getPhone() : "Unknown Phone");

        // Get avatar URL
        String profilePicUrl = model.getAvatar(); // Replace with your actual field getter
        if (profilePicUrl != null && !profilePicUrl.isEmpty()) {
            Glide.with(context)
                    .load(profilePicUrl)
                    .placeholder(R.drawable.default_avatar) // Placeholder while loading
                    .error(R.drawable.default_avatar)      // Fallback if URL is invalid
                    .into(holder.profilePic);
        } else {
            // Set default avatar if URL is null or empty
            holder.profilePic.setImageResource(R.drawable.default_avatar);
            android.util.Log.e("SearchUserAdapter", "Avatar URL is null or empty for user: " + model.getName());
        }
        holder.itemView.setOnClickListener(v -> {
            //navigate to chat activity
            Intent intent = new Intent(context, ChatActivity.class);
            AndroidUtil.passUserModelAsIntent(intent,model);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }


    @NonNull
    @Override
    public UserModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_user_recycler_row, parent, false);
        return new UserModelViewHolder(view);
    }

    // ViewHolder class
    static class UserModelViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText;
        TextView phoneText;
        ImageView profilePic;

        public UserModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.user_name_text);
            phoneText = itemView.findViewById(R.id.phone_text);
            profilePic = itemView.findViewById(R.id.profile_pic_image_view);
        }
    }
}
