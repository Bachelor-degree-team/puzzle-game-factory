package com.puzzlemaker.comparison.fields;

import com.puzzlemaker.comparison.ComparableField;
import com.puzzlemaker.comparison.ComparisonResult;
import com.puzzlemaker.comparison.utils.ComparisonUtils;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ComparableDouble implements ComparableField<Double> {

    @NonNull
    @Getter(onMethod_ = @Override)
    private Double value;

    @Override
    public ComparisonResult compareTo(ComparableField<?> comparableField) {
        if (!(comparableField instanceof ComparableDouble)) {
            return ComparisonResult.TYPE_MISMATCH;
        }

        @NonNull
        ComparableDouble comparableDouble = ComparisonUtils.ensureDouble(comparableField);

        if (value > comparableDouble.getValue()) {
            return ComparisonResult.LOWER;
        }

        if (value.equals(comparableDouble.getValue())) {
            return ComparisonResult.MATCH;
        }

        return ComparisonResult.HIGHER;
    }
}
