package com.wiradipa.fieldOwners.ApiHelper;
import com.wiradipa.fieldOwners.Model.ResponseData;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BaseApiService {

    @FormUrlEncoded
    @POST("owners")
    Call<ResponseBody> registerOwner(@Field("username") String username,
                                     @Field("email") String email,
                                     @Field("password") String password,
                                     @Field("password_confirmation") String passwordConfirmation,
                                     @Field("phone_number") String phoneNumber,
                                     @Field("name") String name);

    @FormUrlEncoded
    @POST("owners/sign_in")
    Call<ResponseBody> ownersLogin(@Field("username") String username,
                                   @Field("password") String password);

    @FormUrlEncoded
    @POST("owners/sign_out")
    Call<ResponseBody> signOutOwner(@Field("auth_token") String token);

    @FormUrlEncoded
    @POST("owners/forgot_password")
    Call<ResponseBody> forgotPasswordOwner(@Field("phone_number") String phoneNumber);

    @FormUrlEncoded
    @POST("owners/activate_account")
    Call<ResponseBody> activationCode(@Field("phone_number") String phoneNumber,
                                      @Field("activation_number") String activatioNumber);

    @GET("facilities")
    Call<ResponseData> listFacilities();

    @GET("areas")
    Call<ResponseData> listAreas();

    @GET("grass_types")
    Call<ResponseData> typesGrass();

    @GET("field_types")
    Call<ResponseData> typeField();

    @FormUrlEncoded
    @GET("fields/[:id]/edit")
    Call<ResponseBody> formEditField(@Field("auth_token") String token);

    @FormUrlEncoded
    @PUT("fields")
    Call<ResponseBody> updateField(@Field("auth_token") String token,
                                   @Field("name") String name,
                                   @Field("description") String description,
                                   @Field("field_owner_id") String fieldOwnerID,
                                   @Field("grass_type_id") String grassTypeID,
                                   @Field("pitch_size") String pitchSize,
                                   @Field("picture") String picture);

    @Multipart
    @POST("fields")
    Call<ResponseBody> createField(@Part("auth_token") RequestBody token,
                                   @Part("name") RequestBody name,
                                   @Part("description") RequestBody description,
                                   @Part("field_owner_id") int fieldOwnerID,
                                   @Part("grass_type_id") int grassTypeID,
                                   @Part("field_type_id") int fieldTypeID,
                                   @Part("pitch_size") RequestBody pitchSize,
                                   @Part("field_tariffs") RequestBody fieldTarif,
                                   @Part MultipartBody.Part picture,
                                   @Part MultipartBody.Part title);

    @FormUrlEncoded
    @GET("field_owners/[:id]/edit")
    Call<ResponseBody> formEditVenue(@Field("auth_token") String token);

    @FormUrlEncoded
    @PUT("field_owners")
    Call<ResponseBody> updateVenue(@Field("auth_token") String token,
                                   @Field("name") String name,
                                   @Field("description") String description,
                                   @Field("address") String address,
                                   @Field("latitude") String latitude,
                                   @Field("longitude") String longitude,
                                   @Field("start_hour") String startHour,
                                   @Field("end_hour") String endHour,
                                   @Field("owner_id") String ownerID,
                                   @Field("picture") String picture);

    @Multipart
    @POST("field_owners")
    Call<ResponseBody> createVenue(@Part("auth_token") RequestBody token,
                                   @Part("name") RequestBody name,
                                   @Part("description") RequestBody description,
                                   @Part("address") RequestBody address,
                                   @Part("latitude") RequestBody latitude,
                                   @Part("longitude") RequestBody longitude,
                                   @Part("start_hour") int startHour,
                                   @Part("end_hour") int endHour,
                                   @Part("field_owners_facilities") RequestBody facilityId,
                                   @Part("field_owners_areas") RequestBody fieldAreas,
                                   @Part MultipartBody.Part picture,
                                   @Part MultipartBody.Part fieldPhoto);

    @GET("fields/{id}")
    Call<ResponseBody> detailField(@Path("id") String id);

    @GET("fields")
    Call<ResponseBody> listFields(@Query("auth_token") String token);

    @GET("fields")
    Call<ResponseBody> listField(@Query("auth_token") String token,
                                 @Query("field_owner_id") String fieldOwnerID);

    @GET("field_owners/{id}")
    Call<ResponseBody> detailVenue(@Path("id") String id);


    @GET("field_owners")
    Call<ResponseBody> listVenue(@Query("auth_token") String token);

    @GET("fields/schedule")
    Call<ResponseBody> listSchedule(@Query("auth_token") String token,
                                    @Query("rental_date") String date,
                                    @Query("field_id") String fieldId);

    @FormUrlEncoded
    @POST("field_rentals")
    Call<ResponseBody> createPeminjaman(@Field("auth_token") String token,
                                        @Field("rental_date") String rental_date,
                                        @Field("start_hour") String startHour,
                                        @Field("end_hour") String endHour,
                                        @Field("field_id") String field_id,
                                        @Field("payment_status") int paymentStatus);

    @FormUrlEncoded
    @POST("field_rentals/cancel")
    Call<ResponseBody> cancelPeminjaman(@Field("auth_token") String token,
                                        @Field("rental_date") String date,
                                        @Field("start_hour") String startHour,
                                        @Field("end_hour") String endHour,
                                        @Field("field_id") String fieldId);
}
