    package fpl.md37.genz_fashion.models;

    import com.google.gson.annotations.SerializedName;
    import java.io.Serializable;
    import java.util.List;

    public class Order implements Serializable {
        @SerializedName("_id")
        private String id;
        @SerializedName("id_client")
        private String idClient;       // id_client
        @SerializedName("products")
        private List<ProducItem> products;
        private int state;             // state: 0 = Chưa xử lý, 1 = Đã thanh toán, 2 = Hoàn thành
        @SerializedName("payment_method")
        private String paymentMethod;  // payment_method
        @SerializedName("total_amount")
        private double totalAmount;    // total_amount
        @SerializedName("order_time")
        private String timeOrder;

        public Order() {
        }

        public Order(String id, String idClient, List<ProducItem> products, int state, String paymentMethod, double totalAmount, String timeOrder) {
            this.id = id;
            this.idClient = idClient;
            this.products = products;
            this.state = state;
            this.paymentMethod = paymentMethod;
            this.totalAmount = totalAmount;
            this.timeOrder = timeOrder;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getIdClient() {
            return idClient;
        }

        public void setIdClient(String idClient) {
            this.idClient = idClient;
        }

        public List<ProducItem> getProducts() {
            return products;
        }

        public void setProducts(List<ProducItem> products) {
            this.products = products;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public String getPaymentMethod() {
            return paymentMethod;
        }

        public void setPaymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
        }

        public double getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(double totalAmount) {
            this.totalAmount = totalAmount;
        }

        public String getTimeOrder() {
            return timeOrder;
        }

        public void setTimeOrder(String timeOrder) {
            this.timeOrder = timeOrder;
        }
    }