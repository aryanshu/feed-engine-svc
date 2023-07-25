package com.feed.engine.feed;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@RestController
@AllArgsConstructor
@RequestMapping("api/v1/recommendation")
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping
    public ResponseEntity<RecommendedUsers> getRecommendation(@RequestParam(value = "userid") String userId) {
        return new ResponseEntity<>(recommendationService.getRecommendationForUser(userId),HttpStatus.OK);
    }

    @GetMapping(path = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<MatchedUserProfile> getEventsAlternativeOption(@RequestParam(value = "userid") String userId) throws IOException {

        RecommendedUsers recommendedUsers = recommendationService.getRecommendationForUser(userId);

        List<MatchedUserProfile> matchedUserProfileList = new ArrayList<>();

        for(Integer user:recommendedUsers.getSimilar_user()){
            if(recommendationService.isSimilar(Integer.valueOf(userId),user)){
                MatchedUserProfile matchedUserProfile = recommendationService.fetchDetails(user);
                System.out.println("matchedUserProfile:"+matchedUserProfile);
                if(matchedUserProfile!=null){
                    matchedUserProfileList.add(matchedUserProfile);
                }
            }
        }

        Stream<MatchedUserProfile> matchedUserProfileStream = matchedUserProfileList.stream();

        return Flux.fromStream(matchedUserProfileStream)
                .delayElements(Duration.ofMillis(300));

    }


    @GetMapping("/test")
    public ResponseEntity<RecommendedUsers> getRecommendationUsingShards2(@RequestParam(value = "userid") String userId) throws IOException {
        return new ResponseEntity<>(recommendationService.getRecommendationForUserUsingShards(userId),HttpStatus.OK);
    }




}
