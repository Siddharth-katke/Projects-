package com.project.journalApp.Schedular;

import com.project.journalApp.cache.AppCache;
import com.project.journalApp.entity.JournalEntry;
import com.project.journalApp.entity.User;
import com.project.journalApp.enums.Sentiment;
import com.project.journalApp.model.SentimentsData;
import com.project.journalApp.repository.UserRepositoryImpl;
import com.project.journalApp.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UserSchedular {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepositoryImpl userRepository;

    @Autowired
    private AppCache appCache;

    @Autowired
    private KafkaTemplate<String, SentimentsData> kafkaTemplate;

    //@Scheduled(cron = "0 0 9 * * SUN")
    ////@Scheduled(cron ="0 * * ? * *"
    public void fetchUsersAndSendSAMail(){
        List<User> users = userRepository.getUserForSA();
        for (User user : users) {
            List<JournalEntry> journalEntries = user.getJournalEntries();
            List<Sentiment> sentiments = journalEntries.stream().filter(x -> x.getDate()
                            .isAfter(LocalDateTime.now().minusDays(7)))
                            .map(x -> x.getSentiment()).collect(Collectors.toList());

            Map<Sentiment, Integer> sentimentCounts = new HashMap<>();
            for (Sentiment sentiment : sentiments) {
                if(sentiment!=null){
                    sentimentCounts.put(sentiment , sentimentCounts.getOrDefault(sentiment, 0)+1);
                }
            }
            Sentiment mostfrequentSentiments = null;
            int maxCount = 0;
            for (Map.Entry<Sentiment, Integer> entry: sentimentCounts.entrySet()) {
                if (entry.getValue() > maxCount){
                    maxCount = entry.getValue();
                    mostfrequentSentiments = entry.getKey();
                }
            }

            if(mostfrequentSentiments!=null){
                SentimentsData sentimentsData = SentimentsData.builder().email(user.getEmail()).sentiment("Sentiment for last 7 days" + mostfrequentSentiments).build();
                try {
                    kafkaTemplate.send("weekly-sentiments", sentimentsData.getEmail(), sentimentsData);
                }catch (Exception e){
                    emailService.sendMail(sentimentsData.getEmail(), "Sentiment for last 7 days", sentimentsData.getSentiment());
                }
                //emailService.sendMail(user.getEmail(),"Sentiment for last 7 days", mostfrequentSentiments.toString());
            }
        }
    }

    //@Scheduled(cron = "0 0/10 * ? * *")
    public void clearAppCache(){
        appCache.init();
    }
}
