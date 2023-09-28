package com.puzzlemaker.comparison.fields;

import com.puzzlemaker.comparison.ComparableField;
import com.puzzlemaker.comparison.ComparisonResult;
import com.puzzlemaker.comparison.utils.ComparisonUtils;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ComparableString implements ComparableField<String> {

    @NonNull
    @Getter(onMethod_ = @Override)
    private String value;

    @Override
    public ComparisonResult compareTo(ComparableField<?> comparableField) {
        if (!(comparableField instanceof ComparableString)) {
            return ComparisonResult.TYPE_MISMATCH;
        }

        @NonNull
        ComparableString comparableString = ComparisonUtils.ensureString(comparableField);

        if (value.equals(comparableString.getValue())) {
            return ComparisonResult.MATCH;
        }

        return ComparisonResult.NO_MATCH;
    }
}
