package com.puzzlemaker.parsing;

import com.opencsv.CSVReader;
import com.puzzlemaker.comparison.ComparableField;
import com.puzzlemaker.comparison.ComparableRecord;
import com.puzzlemaker.comparison.fields.ComparableDouble;
import com.puzzlemaker.comparison.fields.ComparableInteger;
import com.puzzlemaker.comparison.fields.ComparableString;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@UtilityClass
public class CsvFileParser {

    private static final int FIRST_ITEM = 0;

    public static List<ComparableRecord> readCsvFromRequest(MultipartFile file, char separator) {

        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()), separator)) {
            List<String[]> rows = reader.readAll();

            return rows.stream()
                    .map(CsvFileParser::toPairs)
                    .map(CsvFileParser::toRecord)
                    .toList();
        } catch (IOException ioe) {
            throw new RuntimeException("Failed reading csv file contents: ", ioe);
        }

    }

    private static ComparableRecord toRecord(Pair<String, List<ComparableField<?>>> pair) {
        return new ComparableRecord(pair.getLeft(), pair.getRight());
    }

    private static Pair<String, List<ComparableField<?>>> toPairs(String[] csvDataRow) {
        return Pair.of(csvDataRow[FIRST_ITEM], toComparableFields(csvDataRow));
    }

    private static List<ComparableField<?>> toComparableFields(String[] csvDataRow) {
        List<String> values = new ArrayList<>(List.of(csvDataRow));
        List<ComparableField<?>> result = new ArrayList<>();

        values.remove(FIRST_ITEM);

        values.forEach(value ->
                result.add(toComparableField(value))
        );

        return result;
    }

    private static ComparableField<?> toComparableField(String value) {
        if (!NumberUtils.isCreatable(value)) {
            log.debug("Value not numeric - casting to string.");
            return new ComparableString(value);
        }

        log.debug("Value numeric - trying to cast to integer");
        try {
            int intValue = Integer.parseInt(value);
            return new ComparableInteger(intValue);
        } catch (NumberFormatException e) {
            log.debug("Value not integer - trying to cast to double");
        }

        try {
            double doubleValue = Double.parseDouble(value);
            return new ComparableDouble(doubleValue);
        } catch (NumberFormatException e) {
            log.debug("Value not double!");
        }

        log.error("Read value was not matched to any of the primitive types, aborting!");
        throw new IllegalArgumentException("No type for value matched.");
    }

}
