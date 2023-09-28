package com.puzzlemaker.comparison;

import com.puzzlemaker.comparison.fields.ComparableDouble;
import com.puzzlemaker.comparison.fields.ComparableInteger;
import com.puzzlemaker.comparison.fields.ComparableList;
import com.puzzlemaker.comparison.fields.ComparableString;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class ComparableFieldFactory {

    public static ComparableDouble fromDouble(Double value) {
        return new ComparableDouble(value);
    }

    public static ComparableInteger fromInteger(Integer value) {
        return new ComparableInteger(value);
    }

    public static ComparableString fromString(String value) {
        return new ComparableString(value);
    }

    public static ComparableList fromList(List<String> values) {
        return new ComparableList(values);
    }

}
