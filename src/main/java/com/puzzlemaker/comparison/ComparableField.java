package com.puzzlemaker.comparison;

public interface ComparableField<T> {

    T getValue();
    ComparisonResult compareTo(ComparableField<?> comparableField);
}
