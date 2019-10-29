package rankstop.steeringit.com.rankstop.data.webservices;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import rankstop.steeringit.com.rankstop.data.model.db.CriteriaEval;
import rankstop.steeringit.com.rankstop.data.model.db.FakeUser;
import rankstop.steeringit.com.rankstop.data.model.db.RSContact;
import rankstop.steeringit.com.rankstop.data.model.db.RSRequestEditProfile;
import rankstop.steeringit.com.rankstop.data.model.db.RequestOwnership;
import rankstop.steeringit.com.rankstop.data.model.db.User;
import rankstop.steeringit.com.rankstop.data.model.network.GeoPluginResponse;
import rankstop.steeringit.com.rankstop.data.model.network.RSDeviceIP;
import rankstop.steeringit.com.rankstop.data.model.network.RSFollow;
import rankstop.steeringit.com.rankstop.data.model.network.RSRequestFilter;
import rankstop.steeringit.com.rankstop.data.model.network.RSRequestItemByCategory;
import rankstop.steeringit.com.rankstop.data.model.network.RSRequestItemData;
import rankstop.steeringit.com.rankstop.data.model.network.RSRequestListItem;
import rankstop.steeringit.com.rankstop.data.model.network.RSRequestReportAbuse;
import rankstop.steeringit.com.rankstop.data.model.network.RSRequestSocialLogin;
import rankstop.steeringit.com.rankstop.data.model.network.RSResponse;
import rankstop.steeringit.com.rankstop.session.RSSessionToken;
import rankstop.steeringit.com.rankstop.utils.RSConstants;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface API {

    @Headers({
            "Accept: application/json"
    })

    // vérifier l'existance d'un email
    @POST("users/signIn/findemail")
    Call<RSResponse> findEmail(@Body User user);

    @FormUrlEncoded
    @POST("users/signup/email/token")
    Call<RSResponse> findEmailByToken( @Field("token") String token);

    @POST("users/signIn")
    Call<RSResponse> loginUser(@Body User user);

    @POST("users/signIn")
    Call<RSResponse> REloginUser(@Body FakeUser user);

    // register by email and password
    @POST("users/signUp")
    Call<RSResponse> registerUser(@Body User user);

    // register by email and password
    @POST("users/socialLogin")
    Call<RSResponse> socialLogin(@Body RSRequestSocialLogin user);

    // register by email and password
    @Multipart
    @POST("users/updateUser")
    Call<RSResponse> updateUser(
            @Header(RSConstants.HEADER_TOKEN) String token,
            @Part MultipartBody.Part part,
            @Part("firstName") RequestBody firstName,
            @Part("lastName") RequestBody lastName,
            @Part("phone") RequestBody phone,
            @Part("gender") RequestBody gender,
            @Part("birthDate") RequestBody birthDate,
            @Part("username") RequestBody username,
            @Part("nameToUse") RequestBody nameToUse,
            @Part("city") RequestBody city,
            @Part("countryName") RequestBody countryName,
            @Part("countryCode") RequestBody countryCode,
            @Part("userId") RequestBody userId,
            @Part("oldPassword") RequestBody oldPassword,
            @Part("newPassword") RequestBody newPassword
    );

    // load user info
    @FormUrlEncoded
    @POST("users/userInfo")
    Call<RSResponse> loadUserInfo(@Header(RSConstants.HEADER_TOKEN) String token, @Field("id") String id);

    // load user history
    @POST("history/storiesByUser")
    Call<RSResponse> loadUserHistory(@Header(RSConstants.HEADER_TOKEN) String token, @Body RSRequestListItem rsRequestListItem);

    // load categories list
    @FormUrlEncoded
    @POST("categories/getCategory")
    Call<RSResponse> loadCategoriesList(@Header(RSConstants.HEADER_TOKEN) String token, @Field("lang") String lang);

    // load category by id
    @FormUrlEncoded
    @POST("categories/getCategoryById")
    Call<RSResponse> loadCategory(@Header(RSConstants.HEADER_TOKEN) String token, @Field("_id") String id, @Field("lang") String lang);

    // load list of items created by user
    @POST("items/getItemCreatedByUser")
    Call<RSResponse> loadItemCreated(@Header(RSConstants.HEADER_TOKEN) String token, @Body RSRequestListItem rsRequestListItem);

    // load list of items followed by user
    @POST("follows/listItemFollowersByUser")
    Call<RSResponse> loadItemFollowed(@Header(RSConstants.HEADER_TOKEN) String token, @Body RSRequestListItem rsRequestListItem);

    // load list of items owned by user
    @POST("items/getItemAllByUserOwner")
    Call<RSResponse> loadItemOwned(@Header(RSConstants.HEADER_TOKEN) String token, @Body RSRequestListItem rsRequestListItem);

    // load list of top ranked items
    @POST("items/getListItemByTopRank")
    Call<RSResponse> loadTopRankedItems(@Header(RSConstants.HEADER_TOKEN) String token, @Body RSRequestListItem rsRequestListItem);

    // load list of top viewed items
    @POST("items/getItemAllByTopView")
    Call<RSResponse> loadTopViewedItems(@Header(RSConstants.HEADER_TOKEN) String token, @Body RSRequestListItem rsRequestListItem);

    // load list of top commented items
    @POST("items/getListItemByTopComments")
    Call<RSResponse> loadTopCommentedItems(@Header(RSConstants.HEADER_TOKEN) String token, @Body RSRequestListItem rsRequestListItem);

    // load list of top followed items
    @POST("items/getListItemByTopFollowed")
    Call<RSResponse> loadTopFollowedItems(@Header(RSConstants.HEADER_TOKEN) String token, @Body RSRequestListItem rsRequestListItem);

    // load list of my evals
    @POST("items/getItemAllByUserEvaluated")
    Call<RSResponse> loadMyEvals(@Header(RSConstants.HEADER_TOKEN) String token, @Body RSRequestListItem rsRequestListItem);

    // follow item
    @POST("follows/follow")
    Call<RSResponse> followItem(@Header(RSConstants.HEADER_TOKEN) String token, @Body RSFollow rsFollow);

    // unfollow item
    @POST("follows/unfollow")
    Call<RSResponse> unfollowItem(@Header(RSConstants.HEADER_TOKEN) String token, @Body RSFollow rsFollow);

    // load item by id
    @FormUrlEncoded
    @POST("items/getItemByIdAndIdUser")
    Call<RSResponse> loadItem(@Header(RSConstants.HEADER_TOKEN) String token, @Field("itemId") String itemId, @Field("userId") String userId, @Field("lang") String lang);

    // load item comments
    @POST("eval/getComments")
    Call<RSResponse> loadItemComments(@Header(RSConstants.HEADER_TOKEN) String token, @Body RSRequestItemData rsRequestItemData);

    // load user's item comments
    @POST("eval/getCommentsByUser")
    Call<RSResponse> loadItemCommentsByUser(@Header(RSConstants.HEADER_TOKEN) String token, @Body RSRequestItemData rsRequestItemData);

    // load item pictures
    @POST("eval/getPictures")
    Call<RSResponse> loadItemPix(@Header(RSConstants.HEADER_TOKEN) String token, @Body RSRequestItemData rsRequestItemData);

    // load user's item pictures
    @POST("eval/getPicturesByUser")
    Call<RSResponse> loadItemPixByUser(@Header(RSConstants.HEADER_TOKEN) String token, @Body RSRequestItemData rsRequestItemData);

    // add item with review
    @Multipart
    @POST("eval/addReviews&Item")
    Call<RSResponse> addItem(
            @Header(RSConstants.HEADER_TOKEN) String token,
            @Part List<MultipartBody.Part> parts,
            @Part("userId") RequestBody userId,
            @Part("evalCri") List<CriteriaEval> evalCri,
            @Part("categoryId") RequestBody categoryId,
            @Part("description") RequestBody description,
            @Part("title") RequestBody title,
            @Part("barcode") RequestBody barcode,
            @Part("address") RequestBody address,
            @Part("phone") RequestBody phone,
            @Part("latitude") RequestBody latitude,
            @Part("longitude") RequestBody longitude,
            @Part("city") RequestBody city,
            @Part("governorate") RequestBody governorate,
            @Part("country") RequestBody country,
            @Part("comment") RequestBody comment
    );

    // add review
    @Multipart
    @POST("eval/addReviews")
    Call<RSResponse> addReview(
            @Header(RSConstants.HEADER_TOKEN) String token,
            @Part List<MultipartBody.Part> parts,
            @Part("comment") RequestBody comment,
            @Part("userId") RequestBody userId,
            @Part("itemId") RequestBody itemId,
            @Part("evalCri") List<CriteriaEval> evalCri
    );

    // add review
    @Multipart
    @POST("eval/updateReview")
    Call<RSResponse> updateReview(
            @Header(RSConstants.HEADER_TOKEN) String token,
            @Part List<MultipartBody.Part> parts,
            @Part("text") RequestBody comment,
            @Part("userId") RequestBody userId,
            @Part("itemId") RequestBody itemId,
            @Part("evalId") RequestBody evalId
    );

    // add pix to item
    @Multipart
    @POST("items/uploadGallery")
    Call<RSResponse> addItemPix(
            @Part List<MultipartBody.Part> parts,
            @Part("itemId") RequestBody itemId
    );

    // load user eval
    @FormUrlEncoded
    @POST("eval/getLastEvalUser")
    Call<RSResponse> loadMyEval(@Header(RSConstants.HEADER_TOKEN) String token, @Field("userId") String userId, @Field("itemId") String itemId);

    // load report abuses by langue
    @FormUrlEncoded
    @POST("reportAbuses/findAbuse")
    Call<RSResponse> loadAbusesList(@Header(RSConstants.HEADER_TOKEN) String token, @Field("lang") String langue);

    // report abuse
    @POST("reportAbuses/addReportAbuse")
    Call<RSResponse> reportAbuse(@Header(RSConstants.HEADER_TOKEN) String token, @Body RSRequestReportAbuse rsRequestReportAbuse);

    // add review
    @Multipart
    @POST("items/updateItemAndGallery")
    Call<RSResponse> updateItem(
            @Header(RSConstants.HEADER_TOKEN) String token,
            @Part List<MultipartBody.Part> parts,
            @Part("itemId") RequestBody itemId,
            @Part("urlFacebook") RequestBody urlFacebook,
            @Part("urlInstagram") RequestBody urlInstagram,
            @Part("urlTwitter") RequestBody urlTwitter,
            @Part("urlLinkedIn") RequestBody urlLinkedIn,
            @Part("urlGooglePlus") RequestBody urlGooglePlus,
            @Part("picDelete") ArrayList<String> picDelete,
            @Part("barcode")   RequestBody barcode
    );

    // search items
    @GET("items/searchKey")
    Call<RSResponse> search(@Header(RSConstants.HEADER_TOKEN) String token, @Query("q") String query, @Query("lang") String lang);

    // search items
    @FormUrlEncoded
    @POST("country/getAllCountry")
    Call<RSResponse> loadCountries(@Header(RSConstants.HEADER_TOKEN) String token, @Field("lang") String lang);

    // report abuse
    @POST("items/search")
    Call<RSResponse> searchItems(@Header(RSConstants.HEADER_TOKEN) String token, @Body RSRequestItemByCategory rsRequestSearch);

    // report abuse
    @POST("items/getListItemWithFilter")
    Call<RSResponse> searchItemsFiltered(@Header(RSConstants.HEADER_TOKEN) String token, @Body RSRequestFilter data);

    // send request ownership
    @POST("contact/sendInfoUserBuyItem")
    Call<RSResponse> requestOwnership(@Header(RSConstants.HEADER_TOKEN) String token, @Body RequestOwnership requestOwnership);

    // send request ownership
    @POST("contact/sendContact")
    Call<RSResponse> contact(@Header(RSConstants.HEADER_TOKEN) String token, @Body RSContact rsContact);

    //
    @GET("json.gp")
    Call<GeoPluginResponse> getAddressFromIP(@Query("ip") String ip);

    // ip finder
    @GET("/")
    Call<RSDeviceIP> getPublicIP(@Query("format") String format);

    // delete comment
    @FormUrlEncoded
    @POST("items/deleteComments")
    Call<RSResponse> deleteComment(@Header(RSConstants.HEADER_TOKEN) String token, @Field("commentId") String commentId, @Field("itemId") String itemId);

    // delete comment
    @FormUrlEncoded
    @POST("items/deletePictures")
    Call<RSResponse> deletePicture(@Header(RSConstants.HEADER_TOKEN) String token, @Field("pictureId") String pictureId, @Field("itemId") String itemId);

    // delete comment
    @FormUrlEncoded
    @POST("users/changeLanguage")
    Call<RSResponse> editDeviceLanguage(@Header(RSConstants.HEADER_TOKEN) String token, @Field("userId") String userId, @Field("lang") String lang);

    // load list notif
    @POST("notif/findNotification")
    Call<RSResponse> loadListNotif(@Header(RSConstants.HEADER_TOKEN) String token, @Body RSRequestListItem rsRequestListItem);

    // edit notif visibility
    @FormUrlEncoded
    @POST("notif/ChangeStatusNotification")
    Call<RSResponse> editNotifVisibility(@Header(RSConstants.HEADER_TOKEN) String token, @Field("notifId") String userId);

    // edit notif visibility
    @FormUrlEncoded
    @POST("users/forgot")
    Call<RSResponse> forgotPassword(@Field("email") String email);

    // load categories list used by locations
    @FormUrlEncoded
    @POST("items/getAllCategoriesUsedByLocations")
    Call<RSResponse> loadCategoriesUsedByLocations(@Header(RSConstants.HEADER_TOKEN) String token, @Field("lang") String langue);

    @FormUrlEncoded
    @POST("items/getItemByBarCode")
    Call<RSResponse> searchBarcode(@Header(RSConstants.HEADER_TOKEN) String token , @Field("barcode") String barcode);

    @POST("users/updateRegistrationToken")
    Call<RSResponse> updateRegistrationToken(String id, String token);
}
