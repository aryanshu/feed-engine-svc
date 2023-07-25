package com.feed.engine.feed;

import com.feed.engine.userprofile.UserInterests;
import com.feed.engine.userprofile.UserProfile;
import lombok.Data;

@Data
public class MatchedUserProfile {
    private UserProfile userProfile;
    private UserInterests userInterests;

    public MatchedUserProfile(UserProfile userProfile, UserInterests userInterests) {
        this.userProfile = userProfile;
        this.userInterests = userInterests;
    }
}
