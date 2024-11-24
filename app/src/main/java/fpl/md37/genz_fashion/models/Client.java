package fpl.md37.genz_fashion.models;

import java.util.HashMap;

public class Client {
    private String password;
    private String name;
    private String email;
    private String phone;
    private String avatar;
    private String address;
    private String userId;
    private String fcmToken;

    // Phương thức chuyển đổi đối tượng thành HashMap
    public HashMap<String, Object> convertHashMap() {
        HashMap<String, Object> clientMap = new HashMap<>();
        clientMap.put("name", name);
        clientMap.put("email", email);
        clientMap.put("password", password);
        clientMap.put("phone", phone);
        clientMap.put("avatar", avatar);
        clientMap.put("address", address);
        return clientMap;
    }

    // Khởi tạo mặc định
    public Client() {}

    // Khởi tạo với tất cả các tham số
    public Client(String address, String avatar, String phone, String email, String name, String password) {
        this.address = address;
        this.avatar = avatar;
        this.phone = phone;
        this.email = email;
        this.name = name;
        this.password = password;

    }

    // Getter và Setter với kiểm tra hợp lệ
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
            this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
            this.name = name;

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
            this.email = email;

    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {

            this.phone = phone;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
            this.address = address;

    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    // Phương thức toString() để hiển thị thông tin đối tượng
    @Override
    public String toString() {
        return "Client{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", avatar='" + avatar + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
