package com.puzzlemaker.comparison.fields;

import com.puzzlemaker.comparison.ComparableField;
import com.puzzlemaker.comparison.ComparisonResult;
import com.puzzlemaker.comparison.utils.ComparisonUtils;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ComparableInteger implements ComparableField<Integer> {

    @NonNull
    @Getter(onMethod_ = @Override)
    private Integer value;

    @Override
    public ComparisonResult compareTo(ComparableField<?> comparableField) {
        if (!(comparableField instanceof ComparableInteger)) {
            return ComparisonResult.TYPE_MISMATCH;
        }

        @NonNull
        ComparableInteger comparableInteger = ComparisonUtils.ensureInteger(comparableField);

        if (value > comparableInteger.getValue()) {
            return ComparisonResult.LOWER;
        }

        if (value.equals(comparableInteger.getValue())) {
            return ComparisonResult.MATCH;
        }

        return ComparisonResult.HIGHER;
    }
}
