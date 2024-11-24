package fpl.md37.genz_fashion.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;


public class OrderRequest {

    @SerializedName("id_client")
    private String idClient;

    @SerializedName("payment_method")
    private String paymentMethod;

    @SerializedName("products")
    private List<ProducItem> products;

    public OrderRequest(String idClient, String paymentMethod, List<ProducItem> products) {
        this.idClient = idClient;
        this.paymentMethod = paymentMethod;
        this.products = products;
    }

    public String getIdClient() {
        return idClient;
    }

    public void setIdClient(String idClient) {
        this.idClient = idClient;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public List<ProducItem> getProducts() {
        return products;
    }

    public void setProducts(List<ProducItem> products) {
        this.products = products;
    }
}

