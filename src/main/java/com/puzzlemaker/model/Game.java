package com.puzzlemaker.model;

import com.puzzlemaker.comparison.ComparableRecord;
import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;
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

    private List<Pair<String, Integer>> ratings = List.of();

    @NotNull
    private String userId;

    @NotNull
    private String title;

    @NotNull
    private String description;

    @NotNull
    private List<ComparableRecord> gameData;

}
