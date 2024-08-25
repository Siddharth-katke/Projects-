package com.project.journalApp.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WeatherEntry {
    private Current current;

    @Getter
    @Setter
    public class Current{
        @JsonProperty("feelslike_c")
        private double feelsLikeInCelcius;
    }

}
