package com.project.journalApp.service;

import com.project.journalApp.model.SentimentsData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class SentimentConsumerService {

    @Autowired
    private EmailService emailService;

    @KafkaListener(topics = "weekly-sentiments",groupId = "weekly-sentiment-group")
    public void consume(SentimentsData sentimentsData){
        sendEmail(sentimentsData);
    }

    public void sendEmail(SentimentsData sentimentsData){
        emailService.sendMail(sentimentsData.getEmail(), "Sentiment for last 7 days", sentimentsData.getSentiment());
    }
}
