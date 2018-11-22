package rankstop.steeringit.com.rankstop.data.webservices;

import rankstop.steeringit.com.rankstop.data.model.User;
import rankstop.steeringit.com.rankstop.data.model.custom.RSFollow;
import rankstop.steeringit.com.rankstop.data.model.custom.RSRequestListItem;
import rankstop.steeringit.com.rankstop.data.model.custom.RSResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

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

    // load list of items created by user
    @POST("items/getItemAllByUser")
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
}
