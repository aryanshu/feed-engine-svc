package com.feed.engine.feed;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import com.feed.engine.userprofile.*;
import com.feed.engine.utils.Constants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortMode;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;



@Service
@AllArgsConstructor
@Slf4j
public class RecommendationService {

    private final RecommendationRepo recommendationRepo;
    private final ProfileRepo profileRepo;
    private final UserInterestRepo userInterestRepo;

    private final RestHighLevelClient elasticsearchClient;

    private final ObjectMapper objectMapper;



    private final ElasticsearchOperations elasticsearchOperations;

    public RecommendedUsers getRecommendationForUser(String userId) {
        return recommendationRepo.findById(userId).orElseThrow(()->new RuntimeException("recommendation is not found for user"));
    }


    public RecommendedUsers getRecommendationForUserUsingGeoShards(String userId) throws IOException {
        String routing = getShardsNumber(userId);
        SearchResponse searchResponse = searchElasticsearch("userdata", userId, routing);
        SearchHit hit =null;
        RecommendedUsers recommendedUsers=null;
        if(searchResponse.getHits().getHits().length>0 && false){
            hit = searchResponse.getHits().getHits()[0];
            recommendedUsers = objectMapper.readValue(hit.getSourceAsString(), RecommendedUsers.class);
            recommendedUsers.setId(userId);
        }
        //TODO: Incase of new user or user which moved from old location, needs to generate recommendation
        else{
            recommendedUsers = generateRecommendationForUser("userdata", userId, routing);
        }

        return recommendedUsers;
    }

    private RecommendedUsers generateRecommendationForUser(String index, String userId, String routing) throws IOException {
        RecommendedUsers recommendedUsers = new RecommendedUsers();
        UserProfile userProfile = profileRepo.findById(Long.valueOf(userId)).orElseThrow(()->new RuntimeException("User is not found"));
        recommendedUsers.setId(userId);
        recommendedUsers.setAge(userProfile.getAge());
        recommendedUsers.setGlobal(userProfile.isGlobal()==true?"true":"false");
        recommendedUsers.setPreferance(String.valueOf(userProfile.getPreferance()));
        recommendedUsers.setHigher_range(userProfile.getHigherRange());
        recommendedUsers.setLower_range(userProfile.getLowerRange());
        recommendedUsers.setProfile_score(String.valueOf(userProfile.getProfileScore()));

        List<Integer> similarUsers = new ArrayList<>();

        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.sort(SortBuilders.fieldSort("profile_score").order(SortOrder.DESC).sortMode(SortMode.MAX));
        searchSourceBuilder.size(100);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = elasticsearchClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = searchResponse.getHits();
        for(SearchHit hit:hits){
            RecommendedUsers recommendedUsers1 = objectMapper.readValue(hit.getSourceAsString(), RecommendedUsers.class);
            similarUsers.add(Integer.valueOf(recommendedUsers1.getId()));
        }

        recommendedUsers.setSimilar_user(similarUsers);
        return recommendedUsers;

    }

    public SearchResponse searchElasticsearch(String index, String id, String preference) throws IOException {
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        searchSourceBuilder.query(QueryBuilders.termQuery("_id", id));

        if (preference != null && !preference.isEmpty()) {
            searchRequest.routing(preference);
        }

        searchRequest.source(searchSourceBuilder);

        return elasticsearchClient.search(searchRequest, RequestOptions.DEFAULT);
    }

    public String getShardsNumber(String userId){
        UserProfile userProfile = profileRepo.findById(Long.valueOf(userId)).orElseThrow(()->new RuntimeException("User is not found"));
        String userLocation = userProfile.getLocation();
        double latitude = Double.parseDouble(userLocation.split(" ")[0].replace("lat:",""));
        double longitude = Double.parseDouble(userLocation.split(" ")[1].replace("long:",""));

        String routing = locationMapper(latitude,longitude);
        return routing;

    }

    public boolean isWholeNumber(double num) {
        return num == Math.floor(num);
    }
    private String locationMapper(double latitude, double longitude) {
        int row=isWholeNumber(latitude)?(int) latitude-1-20:(int) latitude-20;
        int col=isWholeNumber(longitude)?(int) longitude-1-20:(int) longitude-20;

        return String.valueOf(row*(Constants.longitude.length-1)+col+1);

    }


    public boolean isSimilar(Integer userId, Integer recommendedUserId) {
        UserProfile userProfile = profileRepo.findById(Long.valueOf(userId)).orElseThrow(()->new RuntimeException("User is not found"));
        Optional<UserProfile> recommendedUserProfile = profileRepo.findById(recommendedUserId.longValue());

        if(recommendedUserProfile.isPresent()){
            Integer age = recommendedUserProfile.get().getAge();
            if(age>userProfile.getHigherRange() || age < userProfile.getLowerRange()){
                return false;
            }

            Preferance preferance = userProfile.getPreferance();
            if(!preferance.equals(Preferance.Everyone)){
                if(!preferance.equals(recommendedUserProfile.get().getPreferance())){
                    return false;
                }
            }


            if(!userProfile.isGlobal()){
                String userLocation = userProfile.getLocation();
                String recommendedUserLocation = recommendedUserProfile.get().getLocation();
                int maximumDistance = userProfile.getMaximumDistance();

                double lat1 = Double.parseDouble(userLocation.split(" ")[0].replace("lat:",""));
                double lat2 = Double.parseDouble(recommendedUserLocation.split(" ")[0].replace("lat:",""));

                double long1 = Double.parseDouble(userLocation.split(" ")[1].replace("long:",""));
                double long2 = Double.parseDouble(recommendedUserLocation.split(" ")[1].replace("long:",""));

                if(calculateDistance(lat1, long1, lat2, long2 ) > maximumDistance*1000){
                    return false;
                }

            }


        }


        return true;

    }


    public static double calculateDistance(double lat1, double long1, double lat2, double long2) {
        // Convert latitude and longitude from degrees to radians
        double lat1Rad = Math.toRadians(lat1);
        double long1Rad = Math.toRadians(long1);
        double lat2Rad = Math.toRadians(lat2);
        double long2Rad = Math.toRadians(long2);

        // Haversine formula
        double latDiff = lat2Rad - lat1Rad;
        double longDiff = long2Rad - long1Rad;
        double a = Math.pow(Math.sin(latDiff / 2), 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) * Math.pow(Math.sin(longDiff / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Calculate the distance
        double distance = Constants.EARTH_RADIUS_KM * c;
        return distance;
    }

    public MatchedUserProfile fetchDetails(Integer user) {
        MatchedUserProfile matchedUserProfile=null;
        try {
            UserProfile userProfile = profileRepo.findById(user.longValue()).orElseThrow(() -> new RuntimeException("profile is deleted"));
            UserInterests userInterests = userInterestRepo.findById(user.longValue()).orElseThrow(() -> new RuntimeException("profile is deleted"));

            matchedUserProfile = new MatchedUserProfile(userProfile, userInterests);
        }

        catch (Exception e){
            return matchedUserProfile;
        }

        return matchedUserProfile;
    }
}
