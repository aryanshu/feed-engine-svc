package com.feed.engine.feed;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@Repository
public interface RecommendationRepo extends ElasticsearchRepository<RecommendedUsers, String> {
    Optional<RecommendedUsers> findById(String id);

    @Query("{\"match\": {\"_id\": \"?0\"}}")
    Optional<RecommendedUsers> findByUserIdWithShardPreference(@Param("shardNumber") int shardNumber,String userId);
}
