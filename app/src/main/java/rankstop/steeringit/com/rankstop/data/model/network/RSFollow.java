package rankstop.steeringit.com.rankstop.data.model.network;

public class RSFollow {

    private String follower, following;

    public RSFollow(String follower, String following) {
        this.follower = follower;
        this.following = following;
    }

    public RSFollow() {
    }

    public String getFollower() {
        return follower;
    }

    public void setFollower(String follower) {
        this.follower = follower;
    }

    public String getFollowing() {
        return following;
    }

    public void setFollowing(String following) {
        this.following = following;
    }
}
