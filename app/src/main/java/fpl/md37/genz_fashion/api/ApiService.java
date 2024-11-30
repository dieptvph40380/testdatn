package fpl.md37.genz_fashion.api;

import java.util.ArrayList;

import fpl.md37.genz_fashion.models.OrderResponse;
import fpl.md37.genz_fashion.models.CartResponseBody;
import fpl.md37.genz_fashion.models.FavouriteResponseBody;
import fpl.md37.genz_fashion.models.OrderRequest;
import fpl.md37.genz_fashion.models.Product;
import fpl.md37.genz_fashion.models.RemoveFavouriteRequest;
import fpl.md37.genz_fashion.models.Response;
import fpl.md37.genz_fashion.models.ResponseCart;
import fpl.md37.genz_fashion.models.ResponseFavourite;
import fpl.md37.genz_fashion.models.SelectProductRequest;
import fpl.md37.genz_fashion.models.Size;
import fpl.md37.genz_fashion.models.Suppliers;
import fpl.md37.genz_fashion.models.TypeProduct;
import fpl.md37.genz_fashion.models.UpdateQuantityRequest;
import fpl.md37.genz_fashion.models.Voucher;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    public static String BASE_URL="http://192.168.102.23:3000/api/";
    ApiService apiService  = new Retrofit.Builder()
            .baseUrl(ApiService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService.class);
    //listTypeProduct
    @GET("typeproduct")
    Call<Response<ArrayList<TypeProduct>>> getAlltypeproduct();
    //listSize
    @GET("get-list-size")
    Call<Response<ArrayList<Size>>> getAllSizes();
    //add type
    @Multipart
    @POST("add-type")
    Call<ResponseBody> addTypeProduct(
            @Part("name") RequestBody name,
            @Part("id_size") RequestBody sizes,
            @Part MultipartBody.Part image
    );
    //deleteTypeProduct
    @DELETE("delete-typeproduct-by-id/{id}")
    Call<Response<TypeProduct>>deleteTypeProduct(@Path("id") String id);
    //updateTypeProduct
    @Multipart
    @PUT("update-typeproduct/{id}")
    Call<ResponseBody> updateTypeProduct(
            @Path("id") String id,
            @Part("name") RequestBody name,
            @Part("id_size") RequestBody sizes,
            @Part MultipartBody.Part image
    );
    //list suppliers
    @GET("suppliers")
    Call<Response<ArrayList<Suppliers>>> getAllsuppliers();
    //add supplier
    @Multipart
    @POST("add-supplier")
    Call<ResponseBody> addSuppliers(
            @Part("name") RequestBody name,
            @Part("phone") RequestBody phone,
            @Part("email") RequestBody email,
            @Part("description") RequestBody description,
            @Part MultipartBody.Part image
    );

    //delete supplier
    @DELETE("delete-supplier-by-id/{id}")
    Call<Response<Suppliers>> deleteSuppliers(@Path("id") String id);
    @Multipart
    @PUT("update-supplier/{id}")
    Call<ResponseBody> updateSupplier(
            @Path("id") String id,
            @Part("name") RequestBody name,
            @Part("phone") RequestBody phone,
            @Part("email") RequestBody email,
            @Part("description") RequestBody description,
            @Part MultipartBody.Part image
    );

    //listProduct
    @GET("prodct")
    Call<Response<ArrayList<Product>>> getAllProducts();
    //add product
    @Multipart
    @POST("add-product")
    Call<Response<Product>> addProduct(
            @Part("product_name") RequestBody product_name,
            @Part("price") RequestBody price,
            @Part("quantity") RequestBody quantity,
            @Part("state") RequestBody state,
            @Part("description") RequestBody description,
            @Part("id_suppliers") RequestBody suppliers,
            @Part("id_producttype") RequestBody typeproducts,
            @Part("sizeQuantities") RequestBody sizeQuantities,
            @Part ArrayList<MultipartBody.Part> image
    );
     //update product
     @Multipart
     @PUT("update-product/{id}")
     Call<Response<Product>> updateProduct(
             @Path("id") String id,
             @Part("product_name") RequestBody product_name,
             @Part("price") RequestBody price,
             @Part("quantity") RequestBody quantity,
             @Part("state") RequestBody state,
             @Part("description") RequestBody description,
             @Part("id_suppliers") RequestBody suppliers,
             @Part("id_producttype") RequestBody typeproducts,
             @Part("sizeQuantities") RequestBody sizeQuantities,
             @Part ArrayList<MultipartBody.Part> image
     );

//    @GET("get-supplier-by-name")
//    Call<Response<ArrayList<Suppliers>>> searchSuppliers(
//            @Query("name") String name
//    );
    @GET("get_product/{id}")
    Call<Response<Product>> getProductById(@Path("id") String id);
    // Lấy thông tin chi tiết của nhà cung cấp dựa trên ID
    @GET("suppliers/{id}")
    Call<Response<Suppliers>> getSupplierById(@Path("id") String id);

    // Lấy thông tin chi tiết của loại sản phẩm dựa trên ID
    @GET("typeproduct/{id}")
    Call<Response<TypeProduct>> getTypeProductById(@Path("id") String id);

    //listVoucher
    @GET("get-list-voucher")
    Call<Response<ArrayList<Voucher>>> getAllVoucher();
    // add voucher
    @Multipart
    @POST("add-voucher")
    Call<ResponseBody> addVoucher(
            @Part("name") RequestBody name,
            @Part("description") RequestBody description,
            @Part("discountValue") RequestBody discountValue,
            @Part("discountType") RequestBody discountType,
            @Part("validFrom") RequestBody validFrom,
            @Part("validUntil") RequestBody validUntil,
            @Part("minimumOrderValue") RequestBody minimumOrderValue,
            @Part MultipartBody.Part image
    );
    @Multipart
    @PUT("update-voucher/{id}")
    Call<ResponseBody> updateVoucher(
            @Path("id") String voucherId,
            @Part("name") RequestBody name,
            @Part("description") RequestBody description,
            @Part("discountValue") RequestBody discountValue,
            @Part("discountType") RequestBody discountType,
            @Part("validFrom") RequestBody validFrom,
            @Part("validUntil") RequestBody validUntil,
            @Part("minimumOrderValue") RequestBody minimumOrderValue,
            @Part MultipartBody.Part image
    );
    //add to cart
    @POST("add-to-cart")
    Call<ResponseBody> addToCart(@Body CartResponseBody body);
    @GET("get-cart/{userId}")
    Call<ResponseCart> getCart(@Path("userId") String userId);
    @GET("get-order/{userId}")
    Call<ResponseCart> getOrder(@Path("userId") String userId);
    @POST("select-products")
    Call<ResponseBody> selectProducts(@Body SelectProductRequest request);
    @POST("update-quantity")
    Call<ResponseBody> updateProductQuantity(@Body UpdateQuantityRequest request);
    @POST("remove-product")
    Call<ResponseBody> removeCart(@Body RemoveFavouriteRequest request);
    //add to cart
    @POST("add-favourite")
    Call<ResponseBody> addToFavourite(@Body FavouriteResponseBody body);
    @GET("get-favourite/{userId}") // URL của bạn không có {userId} nữa
    Call<ResponseFavourite> getFavourite(@Path("userId") String userId);

    @POST("remove-favourite")
    Call<ResponseBody> removeFavourite(@Body RemoveFavouriteRequest request);
    //lay danh sach don hang
    @GET("orders")
    Call<OrderResponse> getOrders(@Query("clientId") String clientId, @Query("state") int state);
    @POST("add-order")
    Call<ResponseBody> addOrder(@Body OrderRequest body);
}
