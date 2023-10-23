package com.puzzlemaker.model;

import com.puzzlemaker.comparison.ComparableRecord;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "game")
public class Game {

    @Id
    private String id;

    private final boolean isPublic;

    @NotNull
    private String userId;

    @NotNull
    private String title;

    @NotNull
    private String description;

    @NotNull
    private List<ComparableRecord> gameData;

}
