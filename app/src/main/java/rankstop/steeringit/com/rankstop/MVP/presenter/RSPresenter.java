package rankstop.steeringit.com.rankstop.MVP.presenter;

import android.content.Context;

import rankstop.steeringit.com.rankstop.data.model.db.RSContact;
import rankstop.steeringit.com.rankstop.data.model.db.RSRequestEditProfile;
import rankstop.steeringit.com.rankstop.data.model.db.RequestOwnership;
import rankstop.steeringit.com.rankstop.data.model.db.User;
import rankstop.steeringit.com.rankstop.data.model.network.RSAddReview;
import rankstop.steeringit.com.rankstop.data.model.network.RSFollow;
import rankstop.steeringit.com.rankstop.data.model.network.RSRequestFilter;
import rankstop.steeringit.com.rankstop.data.model.network.RSRequestItemByCategory;
import rankstop.steeringit.com.rankstop.data.model.network.RSRequestItemData;
import rankstop.steeringit.com.rankstop.data.model.network.RSRequestListItem;
import rankstop.steeringit.com.rankstop.data.model.network.RSRequestReportAbuse;
import rankstop.steeringit.com.rankstop.data.model.network.RSRequestSocialLogin;
import rankstop.steeringit.com.rankstop.data.model.network.RSUpdateItem;

public interface RSPresenter {

    interface LoginPresenter {
        void performLogin(User user, Context context);
        void followItem(RSFollow rsFollow, String target, Context context);
        void forgotPassword(String email, Context context);
        void onDestroyLogin( Context context);
    }

    interface RegisterPresenter {
        void performRegister(User user, Context context);
        void followItem(RSFollow rsFollow, String target, Context context);
        void getAddress(String ip, String target, Context context);
        void getPublicIP(String format, String target, Context context);
        void onDestroyRegister( Context context);
    }

    interface SignupPresenter {
        void performFindEmail(String email, Context context);
        void performSocialLogin(RSRequestSocialLogin user, Context context);
        void followItem(RSFollow rsFollow, String target, Context context);
        void getAddress(String ip, String target, Context context);
        void getPublicIP(String format, String target, Context context);
        void onDestroyFindEmail(Context context);
    }

    interface UserPresenter{
        void loadUserInfo(String id, Context context);
        void onDestroyUser( Context context);
    }

    interface ItemPresenter {
        void loadItem(String itemId, String userId, String lang, Context context);
        void loadTopRankedItems(RSRequestListItem rsRequestListItem, Context context);
        void loadTopViewedItems(RSRequestListItem rsRequestListItem, Context context);
        void loadTopCommentedItems(RSRequestListItem rsRequestListItem, Context context);
        void loadTopFollowedItems(RSRequestListItem rsRequestListItem, Context context);
        void loadItemCreated(RSRequestListItem rsRequestListItem, Context context);
        void loadItemOwned(RSRequestListItem rsRequestListItem, Context context);
        void loadItemFollowed(RSRequestListItem rsRequestListItem, Context context);
        void loadMyEvals(RSRequestListItem rsRequestListItem, Context context);
        void loadCategoriesList(String lang, Context context);
        void followItem(RSFollow rsFollow, Context context);
        void unfollowItem(RSFollow rsFollow, Context context);
        void loadItemComments(RSRequestItemData rsRequestItemData, Context context);
        void loadItemCommentsByUser(RSRequestItemData rsRequestItemData, Context context);
        void loadItemPix(RSRequestItemData rsRequestItemData, Context context);
        void loadItemPixByUser(RSRequestItemData rsRequestItemData, Context context);
        void deleteComment(String commentId, String itemId, Context context);
        void deletePicture(String pictureId, String itemId, Context context);
        void onDestroyItem( Context context);
    }

    interface AddReviewPresenter {
        void loadCategory(String id, String lang, Context context);
        void addReview(RSAddReview rsAddReview, Context context);
        void updateReview(RSAddReview rsAddReview, Context context);
        void addItem(RSAddReview rsAddReview, Context context);
        void loadMyEval(String userId, String itemId, Context context);
        void onDestroy( Context context);
    }

    interface UpdateItemPresenter{
        void updateItem(RSUpdateItem rsUpdateItem, Context context);
        void onDestroy( Context context);
    }

    interface abusePresenter{
        void loadAbusesList(String langue, Context context);
        void reportAbuse(RSRequestReportAbuse rsRequestReportAbuse, Context context);
        void onOkClick(Context context);
        void onDestroy( Context context);
    }

    interface SearchPresenter{
        void search(String query, String lang, Context context);
        void searchItems(RSRequestItemByCategory rsRequestSearch, Context context);
        void searchItemsFiltered(RSRequestFilter data, Context context);
        void onDestroy( Context context);
    }

    interface UpdateProfilePresenter{
        void editProfile(RSRequestEditProfile user, Context context);
        void loadCountriesList(String lang, Context context);
        void onDestroy( Context context);
    }

    interface EditDeviceLangPresenter {
        void editLang(String userId, String lang, Context context);
        void onDestroy( Context context);
    }

    interface ListNotifPresenter {
        void loadListNotif(RSRequestListItem rsRequestListItem, Context context);
        void editNotifVisibility(String notifId, String itemId, Context context);
        void onDestroy( Context context);
    }

    interface UserHistoryPresenter{
        void loadHistory(RSRequestListItem rsRequestListItem, Context context);
        void onDestroy( Context context);
    }

    interface ContactPresenter{
        void requestOwnership(RequestOwnership requestOwnership, Context context);
        void contact(RSContact rsContact, Context context);
        void onDestroy( Context context);
    }

    interface SearchFilterPresenter {
        void loadCategories(String lang, Context context);
        void onDestroy( Context context);
    }
}
