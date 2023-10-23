package com.puzzlemaker.comparison;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.util.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Getter
@RequiredArgsConstructor
public class ComparableRecord {

    private static final String FIELDS_LENGTH_MISMATCH = "field_length_mismatch";
    private static final String GAME_WON_INDICATOR = "game_won";

    @NotNull
    private String name;

    @NotNull
    private List<ComparableField<?>> fields;

    public Pair<Map<String, String>, Boolean> compareTo(@NotNull ComparableRecord trueValue) {
        if (fields.size() != trueValue.fields.size()) {
            log.warn("Fields size mismatch between guesses {} and {}, this usually shouldn't happen.", name, trueValue.getName());
            return Pair.of(Map.of(FIELDS_LENGTH_MISMATCH, "true"), false);
        }

        Map<String, String> result = new HashMap<>();

        for (int i = 0; i < fields.size(); i++) {
            var comparison = fields.get(i).compareTo(trueValue.getFields().get(i));
            result.put(fields.get(i).getValue().toString(), comparison.toString());
        }

        if (result.values().stream().allMatch(matchResult -> ComparisonResult.MATCH.toString().equals(matchResult))) {
            result.put(GAME_WON_INDICATOR, "true");
            return Pair.of(result, true);
        }

        result.put(GAME_WON_INDICATOR, "false");
        return Pair.of(result, false);
    }
}
