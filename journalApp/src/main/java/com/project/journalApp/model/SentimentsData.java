package com.project.journalApp.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SentimentsData {

    public String email;
    public String sentiment;

}
