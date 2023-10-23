package com.puzzlemaker.model;

import com.puzzlemaker.comparison.ComparableRecord;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "activeGame")
public class ActiveGame {

    @Id
    private String id;

    @NotNull
    private String title;

    @NotNull
    private ComparableRecord correctGuess;

    @NotNull
    private List<ComparableRecord> gameData;

}
