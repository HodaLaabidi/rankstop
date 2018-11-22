package rankstop.steeringit.com.rankstop.MVP.presenter;

import rankstop.steeringit.com.rankstop.data.model.User;
import rankstop.steeringit.com.rankstop.data.model.custom.RSFollow;
import rankstop.steeringit.com.rankstop.data.model.custom.RSRequestListItem;

public interface RSPresenter {

    interface LoginPresenter {
        void performLogin(User user);
        void onDestroyLogin();
    }

    interface RegisterPresenter {
        void performRegister(User user);
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
        void followItem(RSFollow rsFollow);
        void unfollowItem(RSFollow rsFollow);
        void onDestroyItem();
    }


}
