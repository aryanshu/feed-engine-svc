package com.feed.engine.swipe;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Swipe {
    @SequenceGenerator(
            name = "swipe_sequence",
            sequenceName = "swipe_sequence",
            allocationSize = 1
    )
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "swipe_sequence"
    )
    private Long swipeId;

    private Long userId;

    private Long likedUserId;

    public Swipe(Long userId, Long likedUserId) {
        this.userId = userId;
        this.likedUserId = likedUserId;
    }
}
