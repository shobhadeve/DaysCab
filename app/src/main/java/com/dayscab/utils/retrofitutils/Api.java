package com.dayscab.utils.retrofitutils;

import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface Api {

    @FormUrlEncoded
    @POST("change_password")
    Call<ResponseBody> changePass(@FieldMap Map<String, String> params);

    @POST("get_support")
    Call<ResponseBody> getSupportApi();

    @FormUrlEncoded
    @POST("get_my_order")
    Call<ResponseBody> myOrderApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("update_lat_lon")
    Call<Map<String, String>> updateLocation(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("forgot_password")
    Call<ResponseBody> forgotPass(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_booking_history")
    Call<ResponseBody> getTaxiHistory(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("update_online_status")
    Call<ResponseBody> updateOnOffApi(@FieldMap Map<String, String> params);

    @POST("get_category")
    Call<ResponseBody> getShopCategory();

    @POST("car_list")
    Call<ResponseBody> getCarList();

    @POST("get_make")
    Call<ResponseBody> getCarMakeList();

    @FormUrlEncoded
    @POST("get_profile")
    Call<ResponseBody> getProfileCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_model")
    Call<ResponseBody> getCarModelCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("login")
    Call<ResponseBody> loginApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("available_car_driver")
    Call<ResponseBody> getAvailableDrivers(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_available_driver")
    Call<ResponseBody> getAvailableCarDriversHome(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_car_type_list")
    Call<ResponseBody> getCarTypeListApi(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_dev_order")
    Call<ResponseBody> getDevOrdersApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("social_login")
    Call<ResponseBody> socialLogin(@FieldMap Map<String,String> params);

    @FormUrlEncoded
    @POST("add_bank_account")
    Call<ResponseBody> addBankAccount(@FieldMap Map<String,String> params);

    @FormUrlEncoded
    @POST("driver_accept_and_Cancel_request")
    Call<ResponseBody> acceptCancelOrderCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_current_booking")
    Call<ResponseBody> getCurrentTaxiBooking(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("booking_request")
    Call<ResponseBody> bookingRequestApi(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("driver_accept_and_Cancel_request")
    Call<ResponseBody> acceptCancelOrderCallTaxi(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("place_order")
    Call<ResponseBody> placeDevOrderApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_shop_items")
    Call<ResponseBody> getAllShopItems(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("delete_shop_items")
    Call<ResponseBody> deleteShopItems(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("delete_cart_data")
    Call<ResponseBody> deleteCartItems(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("signup")
    Call<ResponseBody> signUpApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_allshop_list_bycategory")
    Call<ResponseBody> getAllShopApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("delete_cart")
    Call<ResponseBody> deleteStoreItemApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_cart")
    Call<ResponseBody> getStoreCartApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("add_to_cart")
    Call<ResponseBody> addToCartApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("available_car_driver")
    Call<ResponseBody> getAvailableCarCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("add_to_cart")
    Call<ResponseBody> updateOrderStatusApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_count_cart")
    Call<ResponseBody> getCartCountApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_current_booking")
    Call<ResponseBody> getCurrentBooking(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_lat_lon")
    Call<ResponseBody> getLatLonDriver(@FieldMap Map<String, String> params);

    @Multipart
    @POST("add_document")
    Call<ResponseBody> addDriverDocumentApiCall(@Part("user_id") RequestBody user_id,
                                                @Part MultipartBody.Part file1,
                                                @Part MultipartBody.Part file2,
                                                @Part MultipartBody.Part file3);

    @Multipart
    @POST("signup")
    Call<ResponseBody> signUpDriverCallApi(@Part("first_name") RequestBody first_name,
                                           @Part("last_name") RequestBody last_name,
                                           @Part("email") RequestBody email,
                                           @Part("mobile") RequestBody mobile,
                                           @Part("city") RequestBody city,
                                           @Part("address") RequestBody address,
                                           @Part("register_id") RequestBody register_id,
                                           @Part("lat") RequestBody lat,
                                           @Part("lon") RequestBody lon,
                                           @Part("password") RequestBody password,
                                           @Part("type") RequestBody type,
                                           @Part("step") RequestBody step,
                                           @Part MultipartBody.Part file1);

    @Multipart
    @POST("add_shop_items")
    Call<ResponseBody> addShopsItems(@Part("user_id") RequestBody shop_id,
                                     @Part("item_name") RequestBody item_name,
                                     @Part("item_price") RequestBody item_price,
                                     @Part("item_description") RequestBody item_description,
                                     @Part MultipartBody.Part file1);

    @Multipart
    @POST("add_vehicle")
    Call<ResponseBody> addDriverVehicle(@Part("user_id") RequestBody user_id,
                                     @Part("car_type_id") RequestBody car_type,
                                     @Part("brand") RequestBody car_brand,
                                     @Part("car_model") RequestBody car_model,
                                     @Part("car_number") RequestBody carNumber,
                                     @Part("year_of_manufacture") RequestBody year_of_manufacture,
                                     @Part MultipartBody.Part file1);

}


