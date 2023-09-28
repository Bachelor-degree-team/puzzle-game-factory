package com.puzzlemaker.comparison.fields;

import com.puzzlemaker.comparison.ComparableField;
import com.puzzlemaker.comparison.ComparisonResult;
import com.puzzlemaker.comparison.utils.ComparisonUtils;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

@RequiredArgsConstructor
public class ComparableList implements ComparableField<List<String>> {

    @NonNull
    @Getter(onMethod_ = @Override)
    private List<String> value;

    @Override
    public ComparisonResult compareTo(ComparableField<?> comparableField) {
        if (!(comparableField instanceof ComparableList)) {
            return ComparisonResult.TYPE_MISMATCH;
        }

        @NonNull
        ComparableList comparableList = ComparisonUtils.ensureList(comparableField);

        if (value.equals(comparableList.getValue())) {
            return ComparisonResult.MATCH;
        }

        if (numberOfCommonElements(comparableList) > 0) {
            return ComparisonResult.PARTIAL_MATCH;
        }

        return ComparisonResult.NO_MATCH;
    }

    private int numberOfCommonElements(ComparableField<List<String>> comparableField) {
        return CollectionUtils.intersection(value, comparableField.getValue()).size();
    }
}
