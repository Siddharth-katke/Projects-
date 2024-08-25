package com.project.journalApp.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "config_journal_app")
@Data
@NoArgsConstructor
//This pojo is used to access api_key for external api of weather which is manually stored in database
public class ConfigJournalAppEntity {
    private String key;
    private String value;
}
