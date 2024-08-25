package com.project.journalApp.controller;

import com.project.journalApp.entity.User;
import com.project.journalApp.entity.WeatherEntry;
import com.project.journalApp.repository.UserRepository;
import com.project.journalApp.service.UserService;
import com.project.journalApp.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WeatherService weatherService;

    @PutMapping
    public ResponseEntity<?> updateUser(@RequestBody User user){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User userInDb = userService.findByUserName(userName);
        userInDb.setUserName(user.getUserName());
        userInDb.setPassword(user.getPassword());
        userService.saveNewEntry(userInDb);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteUser(@RequestBody User user){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        userRepository.deleteByUserName(authentication.getName());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> Greetings(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        WeatherEntry weatherEntry = weatherService.getWeather("Mumbai");
        String greetings="";
        if(weatherEntry!= null){
            greetings=" Weather feels like " + weatherEntry.getCurrent().getFeelsLikeInCelcius();
        }
        return new ResponseEntity<>("Hii " + authentication.getName() + greetings,HttpStatus.OK);
    }
}
