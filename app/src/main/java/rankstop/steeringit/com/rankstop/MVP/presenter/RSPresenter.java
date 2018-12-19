package rankstop.steeringit.com.rankstop.MVP.presenter;

import rankstop.steeringit.com.rankstop.data.model.User;
import rankstop.steeringit.com.rankstop.data.model.custom.RSAddReview;
import rankstop.steeringit.com.rankstop.data.model.custom.RSFollow;
import rankstop.steeringit.com.rankstop.data.model.custom.RSRequestItemByCategory;
import rankstop.steeringit.com.rankstop.data.model.custom.RSRequestItemData;
import rankstop.steeringit.com.rankstop.data.model.custom.RSRequestListItem;
import rankstop.steeringit.com.rankstop.data.model.custom.RSRequestReportAbuse;
import rankstop.steeringit.com.rankstop.data.model.custom.RSUpdateItem;

public interface RSPresenter {

    interface LoginPresenter {
        void performLogin(User user);
        void followItem(RSFollow rsFollow, String target);
        void onDestroyLogin();
    }

    interface RegisterPresenter {
        void performRegister(User user);
        void followItem(RSFollow rsFollow, String target);
        void onDestroyRegister();
    }

    interface SignupPresenter {
        void performFindEmail(String email);
        void onDestroyFindEmail();
    }

    interface UserPresenter{
        void loadUserInfo(String id);
        void onDestroyUser();
    }

    interface ItemPresenter {
        void loadItem(String itemId, String userId);
        void loadTopRankedItems(RSRequestListItem rsRequestListItem);
        void loadTopViewedItems(RSRequestListItem rsRequestListItem);
        void loadTopCommentedItems(RSRequestListItem rsRequestListItem);
        void loadTopFollowedItems(RSRequestListItem rsRequestListItem);
        void loadItemCreated(RSRequestListItem rsRequestListItem);
        void loadItemOwned(RSRequestListItem rsRequestListItem);
        void loadItemFollowed(RSRequestListItem rsRequestListItem);
        void loadMyEvals(RSRequestListItem rsRequestListItem);
        void loadCategoriesList();
        void followItem(RSFollow rsFollow);
        void unfollowItem(RSFollow rsFollow);
        void loadItemComments(RSRequestItemData rsRequestItemData);
        void loadItemCommentsByUser(RSRequestItemData rsRequestItemData);
        void loadItemPix(RSRequestItemData rsRequestItemData);
        void loadItemPixByUser(RSRequestItemData rsRequestItemData);
        void onDestroyItem();
    }

    interface AddReviewPresenter {
        void loadCategory(String id);
        void addReview(RSAddReview rsAddReview);
        void updateReview(RSAddReview rsAddReview);
        void addItem(RSAddReview rsAddReview);
        void loadMyEval(String userId, String itemId);
        void onDestroy();
    }

    interface UpdateItemPresenter{
        void updateItem(RSUpdateItem rsUpdateItem);
        void onDestroy();
    }

    interface abusePresenter{
        void loadAbusesList(String langue);
        void reportAbuse(RSRequestReportAbuse rsRequestReportAbuse);
        void onOkClick();
        void onCancelClick();
        void onDestroy();
    }

    interface SearchPresenter{
        void search(String query);
        void searchItems(RSRequestItemByCategory rsRequestSearch);
        void onDestroy();
    }


}
