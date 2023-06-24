package com.feed.engine.swipe;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class SwipeService {
    private final SwipeRepo swipeRepo;


    public void saveSwipeDetails(Swipe swipe) {
        swipeRepo.save(swipe);
    }
}
