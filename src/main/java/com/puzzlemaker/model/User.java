package com.puzzlemaker.model;

import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "user")
public class User {

    @Id
    private String id;

    @NonNull
    @Indexed(unique = true)
    private String login;

    @NonNull
    private String description;

    @NonNull
    private List<String> gamesIds;
}
