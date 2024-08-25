package com.project.journalApp.service;

import com.project.journalApp.Constants.Placeholders;
import com.project.journalApp.cache.AppCache;
import com.project.journalApp.entity.WeatherEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {

     @Value("${weather.api.key}")
     private String api_Key;

     //This string is saved in database with the help of app cache in config_journal_app collection and been accessed from there
     //private static final String API= "http://api.weatherapi.com/v1/current.json?key=API_KEY&q=CITY&aqi=no";

     @Autowired
     private RestTemplate restTemplate;

     @Autowired
     private AppCache appCache;

     @Autowired
     private RedisService redisService;

     public WeatherEntry getWeather(String city){
          WeatherEntry weatherEntry = redisService.get("Weather_of_" + city, WeatherEntry.class);
          if(weatherEntry!=null){
               return weatherEntry;
          }else {
               String finalApi = appCache.appCache.get(AppCache.keys.WEATHER_API.toString()).replace(Placeholders.API_KEY, api_Key).replace(Placeholders.CITY, city);
               ResponseEntity<WeatherEntry> response = restTemplate.exchange(finalApi, HttpMethod.GET, null, WeatherEntry.class);
               WeatherEntry body = response.getBody();
               if(body!=null) {
                    redisService.set("Weather_of_" + city, body, 300L);
               }
               return body;
          }
     }
}
