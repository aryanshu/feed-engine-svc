package com.feed.engine.swipe;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/swipe")
public class SwipeController {

    private final SwipeService swipeService;

    @PostMapping
    ResponseEntity<Swipe> swipeDetails(@RequestBody Swipe swipe){
        swipeService.saveSwipeDetails(swipe);
        return new ResponseEntity<>(swipe, HttpStatus.OK);
    }
}
