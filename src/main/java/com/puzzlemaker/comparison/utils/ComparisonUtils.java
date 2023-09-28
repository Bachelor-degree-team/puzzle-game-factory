package com.puzzlemaker.comparison.utils;

import com.puzzlemaker.comparison.ComparableField;
import com.puzzlemaker.comparison.fields.ComparableDouble;
import com.puzzlemaker.comparison.fields.ComparableInteger;
import com.puzzlemaker.comparison.fields.ComparableList;
import com.puzzlemaker.comparison.fields.ComparableString;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ComparisonUtils {

    public static ComparableInteger ensureInteger(ComparableField<?> comparableField) {
        return comparableField instanceof ComparableInteger comparableInteger ? comparableInteger : null;
    }

    public static ComparableDouble ensureDouble(ComparableField<?> comparableField) {
        return comparableField instanceof ComparableDouble comparableInteger ? comparableInteger : null;
    }

    public static ComparableString ensureString(ComparableField<?> comparableField) {
        return comparableField instanceof ComparableString comparableInteger ? comparableInteger : null;
    }

    public static ComparableList ensureList(ComparableField<?> comparableField) {
        return comparableField instanceof ComparableList comparableInteger ? comparableInteger : null;
    }

}
