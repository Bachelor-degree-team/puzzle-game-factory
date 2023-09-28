package com.puzzlemaker.model;

import com.puzzlemaker.comparison.ComparableRecord;
import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "game")
public class Game {

    @Id
    private String id;

    private final boolean isPublic;

    @NonNull
    private String userId;

    @NonNull
    private String title;

    @NonNull
    private String description;

    @NonNull
    private List<ComparableRecord> gameData;

}
