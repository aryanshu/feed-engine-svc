package com.feed.engine.userprofile;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;


@Setter
@Getter
@NoArgsConstructor
@Entity
public class UserProfile {

    @Id
    @Column(updatable = false)
    private Long id;
    @Column(nullable = false, updatable = false)
    private String email;
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column
    private String dob;

    @Transient
    private Integer age;

    @Enumerated(EnumType.STRING)
    private Gender gender;
    @Column(length = 1000)
    private String description;

    private String bio;

    private String workAt;
    private Integer lowerRange=18;
    private Integer higherRange=60;

    @Enumerated(EnumType.STRING)
    private Preferance preferance;

    private String phoneNumber;

    private String location;

    private boolean global;
    private Integer maximumDistance;

    private float profileScore;

    @Enumerated(EnumType.STRING)
    private RelationGoal relationGoal;
    @Enumerated(EnumType.STRING)
    private Education education;
    @Enumerated(EnumType.STRING)
    private StarSign starSign;
    @Enumerated(EnumType.STRING)
    private Children children;
    @Enumerated(EnumType.STRING)
    private Vaccinated vaccinated;
    @Enumerated(EnumType.STRING)
    private Personality personality;
    @Enumerated(EnumType.STRING)
    private Pets pets;
    @Enumerated(EnumType.STRING)
    private Smoke smoke;
    @Enumerated(EnumType.STRING)
    private Drink drink;
    @Enumerated(EnumType.STRING)
    private Exercise exercise;
    @Enumerated(EnumType.STRING)
    private Diet diet;
    @Enumerated(EnumType.STRING)
    private Status status;
    @Enumerated(EnumType.STRING)
    private Sleeping sleeping;


    public UserProfile(Long id, String email, String firstName, String lastName, String dob,
                       Gender gender, String description, String bio, String workAt, Preferance preferance, String phoneNumber, String location,
                       boolean global, Integer maximumDistance) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.gender = gender;
        this.description = description;
        this.bio = bio;
        this.workAt = workAt;
        this.preferance = preferance;
        this.phoneNumber = phoneNumber;
        this.location = location;
        this.global = global;
        this.maximumDistance = maximumDistance;
    }

    public UserProfile(Long id, String email, String firstName, String lastName, String dob, Gender gender,
                       String description, String bio, String workAt, Preferance preferance, String phoneNumber, String location,
                       boolean global, Integer maximumDistance,  RelationGoal relationGoal, Education education,
                       StarSign starSign, Children children, Vaccinated vaccinated, Personality personality, Pets pets,
                       Smoke smoke, Drink drink, Exercise exercise, Diet diet, Status status, Sleeping sleeping) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.gender = gender;
        this.description = description;
        this.bio = bio;
        this.workAt = workAt;
        this.preferance = preferance;
        this.phoneNumber = phoneNumber;
        this.location = location;
        this.global = global;
        this.maximumDistance = maximumDistance;
        this.relationGoal = relationGoal;
        this.education = education;
        this.starSign = starSign;
        this.children = children;
        this.vaccinated = vaccinated;
        this.personality = personality;
        this.pets = pets;
        this.smoke = smoke;
        this.drink = drink;
        this.exercise = exercise;
        this.diet = diet;
        this.status = status;
        this.sleeping = sleeping;
    }

    public Integer getAge() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(dob,formatter);
        return Period.between(date,LocalDate.now()).getYears();
    }
}
