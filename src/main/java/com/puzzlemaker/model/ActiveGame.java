package com.puzzlemaker.model;

import com.puzzlemaker.comparison.ComparableRecord;
import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "activeGame")
public class ActiveGame {

    @Id
    private String id;

    @NonNull
    private String title;

    @NonNull
    private ComparableRecord correctGuess;

    @NonNull
    private List<ComparableRecord> gameData;

}
