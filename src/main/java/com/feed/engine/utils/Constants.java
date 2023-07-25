package com.feed.engine.utils;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;



public class Constants {
    public final List<String> hardMatches = new ArrayList<>(List.of("dob","gender","global","higher_range","lower_range","maximum_distance"));
    public static final double EARTH_RADIUS_KM = 6371.0;
}
