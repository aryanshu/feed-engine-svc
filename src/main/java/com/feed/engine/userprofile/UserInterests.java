package com.feed.engine.userprofile;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Setter
@Getter
@Entity
@NoArgsConstructor
public class UserInterests {

    @Id
    private Long userID;
    private boolean gym;
    private boolean sports;
    private boolean music;
    private boolean reading;
    private boolean hiking;
    private boolean cooking;
    private boolean dancing;
    private boolean photography;
    private boolean painting;
    private boolean gaming;
    private boolean traveling;
    private boolean movies;
    private boolean fashion;
    private boolean gardening;
    private boolean yoga;
    private boolean writing;
    private boolean cycling;
    private boolean swimming;
    private boolean pets;
    private boolean technology;
    private boolean meditation;
    private boolean food;
    private boolean volunteering;
    private boolean crafts;

    private boolean cricket;
    private boolean bollywood;
    private boolean ayurveda;
    private boolean classicalMusic;
    private boolean indianCuisine;
    private boolean historyAndCulture;
    private boolean spirituality;
    private boolean regionalDance;
}