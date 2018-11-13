package rankstop.steeringit.com.rankstop.MVP.presenter;

import rankstop.steeringit.com.rankstop.data.model.User;
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

    interface HomePresenter {
        void loadTopRankedItems(RSRequestListItem rsRequestListItem);
        void loadTopViewedItems(RSRequestListItem rsRequestListItem);
        void loadTopCommentedItems(RSRequestListItem rsRequestListItem);
        void loadTopFollowedItems(RSRequestListItem rsRequestListItem);
        void onDestroy();
    }

    interface ProfilePresenter {
        void loadItemCreated(RSRequestListItem rsRequestListItem);
        void loadItemOwned(RSRequestListItem rsRequestListItem);
        void loadItemFollowed(RSRequestListItem rsRequestListItem);
        void loadUserInfo(String id);
        void onDestroy();
    }


}
