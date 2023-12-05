package com.puzzlemaker.model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "session")
public class Session {

    @Id
    private String id;

    @NotNull
    @Indexed(unique = true)
    private String userLogin;
}
