package fpl.md37.genz_fashion.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseUtil {


    public static String currentUserId() {
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) {
            throw new IllegalStateException("User is not logged in.");
        }
        return userId;
    }

    public static boolean isLoggedIn() {
        return currentUserId() != null;
    }


    // Truy xuất thông tin người dùng hiện tại từ Firestore
    public static DocumentReference currentUserDetails() {
        if (!isLoggedIn()) {
            throw new IllegalStateException("User is not logged in.");
        }
        return FirebaseFirestore.getInstance()
                .collection("Client")
                .document(currentUserId());
    }
    public static CollectionReference allUserCollectionReference(){
        return FirebaseFirestore.getInstance().collection("Client");
    }
    // Lấy StorageReference của ảnh đại diện người dùng hiện tại
    public static StorageReference getCurrentProfilePicStorageRef() {
        String userId = currentUserId();
        return FirebaseStorage.getInstance()
                .getReference()
                .child("profile_pic")
                .child(userId);
    }

    // Lấy StorageReference của ảnh đại diện người dùng khác
    public static StorageReference getOtherProfilePicStorageRef(String otherUserId) {
        if (otherUserId == null || otherUserId.isEmpty()) {
            throw new IllegalArgumentException("Invalid user ID.");
        }
        return FirebaseStorage.getInstance()
                .getReference()
                .child("profile_pic")
                .child(otherUserId);
    }

}