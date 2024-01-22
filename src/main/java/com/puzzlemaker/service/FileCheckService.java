package com.puzzlemaker.service;

import com.puzzlemaker.comparison.ComparableField;
import com.puzzlemaker.comparison.ComparableRecord;
import com.puzzlemaker.comparison.fields.ComparableString;
import com.puzzlemaker.model.dto.FileCheckDTO;
import com.puzzlemaker.parsing.CsvFileParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FileCheckService {

    private static final List<String> FOUL_WORDS = List.of("fuck", "nigger", "cunt", "nigga", "bitch", "shit");

    public FileCheckDTO checkFile(MultipartFile csvFile, char separator) {
        List<String[]> rowData = CsvFileParser.readCsvToArrays(csvFile, separator);
        List<ComparableRecord> gameData = CsvFileParser.readCsvToGameData(csvFile, separator);

        return FileCheckDTO.fromConditions(
                foulLanguage(rowData),
                rowSize(rowData),
                columnTypes(gameData),
                firstRowString(gameData),
                min5(rowData),
                max1000(rowData)
        );
    }

    private static boolean foulLanguage(List<String[]> rowData) {
        String csvString = rowData.stream().map(Arrays::toString).collect(Collectors.joining());

        for (String badWord : FOUL_WORDS) {
            if (csvString.contains(badWord)) {
                return false;
            }
        }

        return true;
    }

    private static boolean rowSize(List<String[]> rowData) {
        int establishedRowSize = rowData.get(0).length;

        for (String[] row : rowData) {
            if (row.length != establishedRowSize) {
                return false;
            }
        }

        return true;
    }

    private static boolean columnTypes(List<ComparableRecord> gameData) {
        List<ComparableRecord> listWithoutFirstRow = new ArrayList<>(gameData);
        listWithoutFirstRow.remove(0);

        var establishedTypes = listWithoutFirstRow.get(0).getFields().stream().map(Object::getClass).toList();

        for (ComparableRecord record: listWithoutFirstRow) {
            if (!establishedTypes.equals(record.getFields().stream().map(Object::getClass).toList())) {
                return false;
            }
        }

        return true;
    }

    private static boolean firstRowString(List<ComparableRecord> gameData) {
        List<ComparableField<?>> firstRow = gameData.get(0).getFields();

        for (ComparableField<?> field : firstRow) {
            if (!(field instanceof ComparableString)) {
                return false;
            }
        }

        return true;
    }

    private static boolean min5(List<String[]> rowData) {
        return rowData.size() > 5;
    }

    private static boolean max1000(List<String[]> rowData) {
        return rowData.size() <= 1001;
    }

}
