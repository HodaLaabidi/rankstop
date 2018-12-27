package rankstop.steeringit.com.rankstop.data.webservices;

import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rankstop.steeringit.com.rankstop.data.model.CriteriaEval;
import rankstop.steeringit.com.rankstop.data.model.User;
import rankstop.steeringit.com.rankstop.data.model.custom.RSFollow;
import rankstop.steeringit.com.rankstop.data.model.custom.RSRequestItemByCategory;
import rankstop.steeringit.com.rankstop.data.model.custom.RSRequestItemData;
import rankstop.steeringit.com.rankstop.data.model.custom.RSRequestListItem;
import rankstop.steeringit.com.rankstop.data.model.custom.RSRequestReportAbuse;
import rankstop.steeringit.com.rankstop.data.model.custom.RSResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
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

    @POST("users/signIn")
    Call<RSResponse> loginUser(@Body User user);

    @POST("users/signUp")
    Call<RSResponse> registerUser(@Body User user);

    // load user info
    @FormUrlEncoded
    @POST("users/userInfo")
    Call<RSResponse> loadUserInfo(@Field("id") String id);

    @GET("criteria/getCriterias")
    Call<RSResponse> loadCriteriaList();

    // load categories list
    @GET("categories/getCategory")
    Call<RSResponse> loadCategoriesList();

    // load category by id
    @FormUrlEncoded
    @POST("categories/getCategoryById")
    Call<RSResponse> loadCategory(@Field("_id") String id);

    // load list of items created by user
    @POST("items/getItemCreatedByUser")
    Call<RSResponse> loadItemCreated(@Body RSRequestListItem rsRequestListItem);

    // load list of items followed by user
    @POST("follows/listItemFollowersByUser")
    Call<RSResponse> loadItemFollowed(@Body RSRequestListItem rsRequestListItem);

    // load list of items owned by user
    @POST("items/getItemAllByUserOwner")
    Call<RSResponse> loadItemOwned(@Body RSRequestListItem rsRequestListItem);

    // load list of top ranked items
    @POST("items/getListItemByTopRank")
    Call<RSResponse> loadTopRankedItems(@Body RSRequestListItem rsRequestListItem);

    // load list of top viewed items
    @POST("items/getItemAllByTopView")
    Call<RSResponse> loadTopViewedItems(@Body RSRequestListItem rsRequestListItem);

    // load list of top commented items
    @POST("items/getListItemByTopComments")
    Call<RSResponse> loadTopCommentedItems(@Body RSRequestListItem rsRequestListItem);

    // load list of top followed items
    @POST("items/getListItemByTopFollowed")
    Call<RSResponse> loadTopFollowedItems(@Body RSRequestListItem rsRequestListItem);

    // load list of my evals
    @POST("items/getItemAllByUserEvaluated")
    Call<RSResponse> loadMyEvals(@Body RSRequestListItem rsRequestListItem);

    // follow item
    @POST("follows/follow")
    Call<RSResponse> followItem(@Body RSFollow rsFollow);

    // unfollow item
    @POST("follows/unfollow")
    Call<RSResponse> unfollowItem(@Body RSFollow rsFollow);

    // load item by id
    @FormUrlEncoded
    @POST("items/getItemByIdAndIdUser")
    Call<RSResponse> loadItem(@Field("itemId") String itemId, @Field("userId") String userId);

    // load item comments
    @POST("eval/getComments")
    Call<RSResponse> loadItemComments(@Body RSRequestItemData rsRequestItemData);

    // load user's item comments
    @POST("eval/getCommentsByUser")
    Call<RSResponse> loadItemCommentsByUser(@Body RSRequestItemData rsRequestItemData);

    // load item pictures
    @POST("eval/getPictures")
    Call<RSResponse> loadItemPix(@Body RSRequestItemData rsRequestItemData);

    // load user's item pictures
    @POST("eval/getPicturesByUser")
    Call<RSResponse> loadItemPixByUser(@Body RSRequestItemData rsRequestItemData);

    // add item with review
    @Multipart
    @POST("eval/addReviews&Item")
    Call<RSResponse> addItem(
            @Part List<MultipartBody.Part> parts,
            @Part("userId") RequestBody userId,
            @Part("evalCri") List<CriteriaEval> evalCri,
            @Part("categoryId") RequestBody categoryId,
            @Part("description") RequestBody description,
            @Part("title") RequestBody title,
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
    Call<RSResponse> loadMyEval(@Field("userId") String userId, @Field("itemId") String itemId);

    // load report abuses by langue
    @FormUrlEncoded
    @POST("reportAbuses/findAbuse")
    Call<RSResponse> loadAbusesList(@Field("langue") String langue);

    // report abuse
    @POST("reportAbuses/addReportAbuse")
    Call<RSResponse> reportAbuse(@Body RSRequestReportAbuse rsRequestReportAbuse);

    // add review
    @Multipart
    @POST("items/updateItemAndGallery")
    Call<RSResponse> updateItem(
            @Part List<MultipartBody.Part> parts,
            @Part("itemId") RequestBody itemId,
            @Part("urlFacebook") RequestBody urlFacebook,
            @Part("urlInstagram") RequestBody urlInstagram,
            @Part("urlTwitter") RequestBody urlTwitter,
            @Part("urlLinkedIn") RequestBody urlLinkedIn,
            @Part("urlGooglePlus") RequestBody urlGooglePlus,
            @Part("picDelete") ArrayList<String> picDelete
    );

    // search items
    @GET("items/searchKey")
    Call<RSResponse> search(@Query("q") String query);

    // report abuse
    @POST("items/search")
    Call<RSResponse> searchItems(@Body RSRequestItemByCategory rsRequestSearch);
}
