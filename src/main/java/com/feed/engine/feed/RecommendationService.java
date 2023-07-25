package com.feed.engine.feed;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.feed.engine.userprofile.*;
import com.feed.engine.utils.Constants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
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


    public RecommendedUsers getRecommendationForUserUsingShards(String userId) throws IOException {
        String routing = getShardsNumber(userId);
        SearchResponse searchResponse = searchElasticsearch("userdata",userId,routing);
        SearchHit hit = searchResponse.getHits().getHits()[0];
        return objectMapper.readValue(hit.getSourceAsString(), RecommendedUsers.class);
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
