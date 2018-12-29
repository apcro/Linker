package com.alienpants.numberlink.libraries;

import com.alienpants.numberlink.responsemodels.BaseResponse;

import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiInterface {

    // POST methods
//    @POST("login")
//    Call<LoginResponse> login(
//            @Query("username") String username,
//            @Query("password") String password,
//            @Query("type") String type);
//
//    @POST("logout")
//    Call<BaseResponse> logout(
//            @Header("Token") String token
//    );
//
//    @POST("register")
//    Call<RegisterResponse> register(@Query("email") String email,
//                                    @Query("password") String password,
//                                    @Query("name") String name,
//                                    @Query("locale") String locale);
//
//    @POST("updateProfile")
//    Call<BaseResponse> updateProfile(
//            @Header("Token") String token,
//            @Query("username") String username,
//            @Query("firstname") String firstName,
//            @Query("lastname") String lastName,
//            @Query("email") String emailaddress,
//            @Query("home_airport") String home_airport,
//            @Query("latitude") float lat,
//            @Query("longitude") float lon
//
//    );
//
//
//    @POST("updateSettings")
//    Call<BaseResponse> updateSettings(
//            @Header("Token") String token,
//            @Query("budget") int budget,
//            @Query("people") int people,
//            @Query("split") int split,
//            @Query("dates") String dates,
//            @Query("leaveday") String leaveday,
//            @Query("returnday") String returnday,
//            @Query("filters") String filters,
//            @Query("currency") String currency,
//            @Query("locale") String locale
//    );
//
//    @GET("userSettings")
//    Call<UserSettings> userSettings(
//            @Header("Token") String token
//
//    );
//
//    @POST("saveItem")
//    Call<BaseResponse> saveItem(
//            @Header("Token") String token,
//            @Query("cityId") Integer cityId,
//            @Query("hotelId") Integer hotelId,
//            @Query("hotelprice") Integer hotelprice,
//            @Query("flightprice") String flightprice,
//            @Query("packageid") Integer packageid,
//            @Query("eventid") String eventid
//    );
//
//    @POST("sendFeedback")
//    Call<BaseResponse> sendFeedback(
//            @Header("Token") String token,
//            @Query("feedback") String feedback
//    );
//
//
//    @POST("deleteSavedItem")
//    Call<BaseResponse> deleteSavedItem(
//            @Header("Token") String token,
//            @Query("cityId") Integer cityId,
//            @Query("hotelId") Integer hotelId
//    );
//
    @POST("updateDeviceToken")
    Call<BaseResponse> updateDeviceToken(
            @Header("Token") String token,
            @Query("deviceToken") String deviceToken,
            @Query("deviceType") String deviceType
    );
//
//
//    @Multipart
//    @POST("updateAvatar")
//    Call<AvatarResponse> updateAvatar(
//            @Header("Token") String token,
//            @Part MultipartBody.Part avatar
//    );
//
//    @POST("emailTrip")
//    Call<BaseResponse> emailTrip(
//            @Header("Token") String token,
//            @Query("cityId") Integer cityId,
//            @Query("hotelId") Integer hotelId
//    );
//
//    // GET methods
//    @GET("checkToken")
//    Call<BaseResponse> checkToken(
//            @Header("Token") String token
//    );
//
//
//    @GET("findDestination")
//    Call<DestinationsResponse> findDestination(
//            @Header("Token") String token,
//            @Query("data") Integer count
//    );
//
//    @GET("findHotel")
//    Call<Hotels> findHotel(
//            @Header("Token") String token,
//            @Query("city_id") Integer cityId,
//            @Query("count") Integer count,
//            @Query("extraHotelId") Integer extraHotelId,
//            @Query("flightcost") String flightcost);
//
//    //    'bookWeekend',
////    'shareWeekend',		// share by email
//
//    @GET("getCityDetails")
//    Call<SingleDestination> getCityDetails(@Header("Token") String token, @Query("cityid") int cityid);
//
//    @GET("getHotelDetails")
//    Call<HotelDetails> getHotelDetails(@Header("Token") String token, @Query("hotelid") int hotelid);
//
//    @GET("getRoomDetails")
//    Call<HotelDetails> getRoomDetails(@Header("Token") String token, @Query("hotelid") int hotelid);
//
//    @GET("userLoad")
//    Call<UserResponse> userLoad(@Header("Token") String token);
//
////    @GET("userSettings")
////    Call<BaseResponse> userSettings(@Header("Token") String token);
//
//    @GET("getAirports")
//    Call<Airports> getAirports(@Query("lat") String lat, @Query("lon") String lon);
//
//    @GET("getNearestAirport")
//    Call<NearestAirportResponse> getNearestAirport(@Header("Token") String token,
//                                                   @Query("lat") String lat,
//                                                   @Query("lon") String lon);
//
//    @GET("getAirportDetails")
//    Call<SingleAirport> getAirportDetails(@Header("Token") String token, @Query("iata") String iata);
//
//    @GET("loadSavedItems")
//    Call<SavedItemsResponse> loadSavedItems(@Header("Token") String token);
//
//    @GET("loadSavedItem")
//    Call<OneSavedItemResponse> loadSavedItem(@Header("Token") String token,
//                                             @Query("cityId") int cityId,
//                                             @Query("hotelId") int hotelId);
//
//    @GET("checkHotelAvailability")
//    Call<HotelAvailabilities> checkHotelAvailability(
//            @Header("Token") String token,
//            @Query("hotel_ids") String hotelids);
//
//    @GET("getCityInformation")
//    Call<CityResponse> getCityInformation(
//            @Header("Token") String token,
//            @Query("cityid") String cityid);
//
//    // Analytics
//    @POST("logEvent")
//    Call<BaseResponse> logEvent(
//            @Header("Token") String token,
//            @Query("eventname") String name,
//            @Query("evendescription") String string,
//            @Query("userid") int userId,
//            @Query("cityid") int cityId,
//            @Query("hotelid") int hotelId
//    );
}
