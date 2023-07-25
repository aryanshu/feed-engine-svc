package com.feed.engine.feed;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Setter
@Getter
@Document(indexName = "userdata")
public class RecommendedUsers {
    @Id
    private String id;

    private String preferance;

    private String profile_score;

    private Integer age;

    private String global;

    private Integer higher_range;

    private Integer lower_range;

    private List<Integer> similar_user;

}

