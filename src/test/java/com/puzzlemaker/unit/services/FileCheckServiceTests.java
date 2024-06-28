package com.puzzlemaker.unit.services;

import com.mongodb.assertions.Assertions;
import com.puzzlemaker.model.dto.FileCheckDTO;
import com.puzzlemaker.service.FileCheckService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileCheckServiceTests {
    FileCheckService fileCheckService = new FileCheckService();
    private static final Path resourceDirectory = Paths.get(System.getProperty("user.dir"), "src","test", "resources");
    private static final List<String> fileNamesFault = List.of(
            "false1.csv",
            "false2.csv",
            "false3.csv",
            "false4.csv",
            "false5.csv",
            "false6.csv"
    );

    private static final List<String> fileNamesCorrect = List.of(
            "correctAll.csv",
            "correctAll5.csv",
            "correctAll1000.csv",
            "correctDouble.csv",
            "correctInt.csv",
            "correctString.csv"
    );
    private static final String fileNameShort = "short.csv";
    private static final String fileNameLong = "long.csv";
    private static final List<String> wrongHeaderNames = List.of(
            "headerDouble.csv",
            "headerInt.csv");
    private static final List<String> wrongRowSizeNames = List.of(
            "shortHeader.csv",
            "longHeader.csv",
            "longRow.csv",
            "shortRow.csv");
    private static final List<String> wrongColumnTypes = List.of(
            "wrongColumnStringDouble.csv",
            "wrongColumnStringInt.csv",
            "wrongColumnDoubleString.csv",
            "wrongColumnDoubleInt.csv",
            "wrongColumnIntString.csv",
            "wrongColumnIntDouble.csv"
    );
    private static final List<MultipartFile> testFilesFault = new ArrayList<>();
    private static final List<MultipartFile> testFilesCorrect= new ArrayList<>();
    private static final List<MultipartFile> testFilesWrongHeader= new ArrayList<>();
    private static final List<MultipartFile> testFilesWrongRowSize= new ArrayList<>();
    private static final List<MultipartFile> testFilesWrongColumnType = new ArrayList<>();
    private static MultipartFile testFileShort;
    private static MultipartFile testFileLong;
    @BeforeAll
    public static void setUpAll(){
        try{
            for(String  fileName: fileNamesFault){
                testFilesFault.add(new MockMultipartFile(fileName, new FileInputStream(Paths.get(resourceDirectory.toString(),fileName).toString())));
            }
            for(String  fileName: wrongHeaderNames){
                testFilesWrongHeader.add(new MockMultipartFile(fileName, new FileInputStream(Paths.get(resourceDirectory.toString(),fileName).toString())));
            }
            for(String  fileName: wrongRowSizeNames){
                testFilesWrongRowSize.add(new MockMultipartFile(fileName, new FileInputStream(Paths.get(resourceDirectory.toString(),fileName).toString())));
            }
            for(String  fileName: fileNamesCorrect){
                testFilesCorrect.add(new MockMultipartFile(fileName, new FileInputStream(Paths.get(resourceDirectory.toString(),fileName).toString())));
            }
            for(String  fileName: wrongColumnTypes){
                testFilesWrongColumnType.add(new MockMultipartFile(fileName, new FileInputStream(Paths.get(resourceDirectory.toString(),fileName).toString())));
            }
            testFileShort = new MockMultipartFile(fileNameShort, new FileInputStream(Paths.get(resourceDirectory.toString(),fileNameShort).toString()));
            testFileLong = new MockMultipartFile(fileNameLong, new FileInputStream(Paths.get(resourceDirectory.toString(),fileNameLong).toString()));
            System.setProperty("csv","done");
        }catch (Exception e){
            System.setProperty("csv","skip");
        }
    }

    @BeforeEach
    public void setUpService(){
        this.fileCheckService = new FileCheckService();
    }

    @Test
    @EnabledIfSystemProperty(named = "csv", matches = "done")
    public void foulLanguageTest(){
        //fault words checks
        for (MultipartFile testFileFault : testFilesFault){
            FileCheckDTO fileFaultCheckDTO = fileCheckService.checkFile(testFileFault,',');
            Assertions.assertFalse(fileFaultCheckDTO.foul_language());
            Assertions.assertFalse(fileFaultCheckDTO.all_fields_checked());
            Assertions.assertTrue(fileFaultCheckDTO.minimum_5());
            Assertions.assertTrue(fileFaultCheckDTO.maximum_1000());
            Assertions.assertTrue(fileFaultCheckDTO.all_string_first_row());
            Assertions.assertTrue(fileFaultCheckDTO.same_size_rows());
            Assertions.assertTrue(fileFaultCheckDTO.same_type_columns());
        }



    }
    @Test
    @EnabledIfSystemProperty(named = "csv", matches = "done")
    public void correctFileTest(){
        //correct csv
        for(MultipartFile testFileNoFault: testFilesCorrect){
            FileCheckDTO fileFaultCheckDTO = fileCheckService.checkFile(testFileNoFault,',');
            Assertions.assertTrue(fileFaultCheckDTO.foul_language());
            Assertions.assertTrue(fileFaultCheckDTO.minimum_5());
            Assertions.assertTrue(fileFaultCheckDTO.maximum_1000());
            Assertions.assertTrue(fileFaultCheckDTO.all_string_first_row());
            Assertions.assertTrue(fileFaultCheckDTO.same_size_rows());
            Assertions.assertTrue(fileFaultCheckDTO.same_type_columns());
            Assertions.assertTrue(fileFaultCheckDTO.all_fields_checked());
        }
    }
    @Test
    @EnabledIfSystemProperty(named = "csv", matches = "done")
    public void shortFileTest(){
        //short csv
        FileCheckDTO fileFaultCheckDTO = fileCheckService.checkFile(testFileShort,',');
        Assertions.assertTrue(fileFaultCheckDTO.foul_language());
        Assertions.assertFalse(fileFaultCheckDTO.all_fields_checked());
        Assertions.assertFalse(fileFaultCheckDTO.minimum_5());
        Assertions.assertTrue(fileFaultCheckDTO.maximum_1000());
        Assertions.assertTrue(fileFaultCheckDTO.all_string_first_row());
        Assertions.assertTrue(fileFaultCheckDTO.same_type_columns());
        Assertions.assertTrue(fileFaultCheckDTO.same_size_rows());
    }
    @Test
    @EnabledIfSystemProperty(named = "csv", matches = "done")
    public void longFileTest(){
        //too long csv
        FileCheckDTO fileFaultCheckDTO = fileCheckService.checkFile(testFileLong,',');
        Assertions.assertTrue(fileFaultCheckDTO.foul_language());
        Assertions.assertFalse(fileFaultCheckDTO.all_fields_checked());
        Assertions.assertTrue(fileFaultCheckDTO.minimum_5());
        Assertions.assertFalse(fileFaultCheckDTO.maximum_1000());
        Assertions.assertTrue(fileFaultCheckDTO.all_string_first_row());
        Assertions.assertTrue(fileFaultCheckDTO.same_type_columns());
        Assertions.assertTrue(fileFaultCheckDTO.same_size_rows());
    }
    @Test
    @EnabledIfSystemProperty(named = "csv", matches = "done")
    public void wrongHeaderTypeTest(){
        //column not a string test
        for(MultipartFile testFileNoFault: testFilesWrongHeader){
            FileCheckDTO fileFaultCheckDTO = fileCheckService.checkFile(testFileNoFault,',');
            Assertions.assertTrue(fileFaultCheckDTO.foul_language());
            Assertions.assertFalse(fileFaultCheckDTO.all_fields_checked());
            Assertions.assertTrue(fileFaultCheckDTO.minimum_5());
            Assertions.assertTrue(fileFaultCheckDTO.maximum_1000());
            Assertions.assertFalse(fileFaultCheckDTO.all_string_first_row());
            Assertions.assertTrue(fileFaultCheckDTO.same_type_columns());
            Assertions.assertTrue(fileFaultCheckDTO.same_size_rows());
        }
    }
    @Test
    @EnabledIfSystemProperty(named = "csv", matches = "done")
    public void wrongRowSizeTest(){
        //wrong row sizes
        for(MultipartFile testFileNoFault: testFilesWrongRowSize){
            FileCheckDTO fileFaultCheckDTO = fileCheckService.checkFile(testFileNoFault,',');
            Assertions.assertTrue(fileFaultCheckDTO.foul_language());
            Assertions.assertTrue(fileFaultCheckDTO.minimum_5());
            Assertions.assertTrue(fileFaultCheckDTO.maximum_1000());
            Assertions.assertTrue(fileFaultCheckDTO.all_string_first_row());
            Assertions.assertFalse(fileFaultCheckDTO.same_size_rows());
            Assertions.assertFalse(fileFaultCheckDTO.same_type_columns());
            Assertions.assertFalse(fileFaultCheckDTO.all_fields_checked());
        }
    }
    @Test
    @EnabledIfSystemProperty(named = "csv", matches = "done")
    public void wrongColumnTypesTest(){
        //wrong row sizes
        for(MultipartFile testFileNoFault: testFilesWrongColumnType){
            FileCheckDTO fileFaultCheckDTO = fileCheckService.checkFile(testFileNoFault,',');
            Assertions.assertTrue(fileFaultCheckDTO.foul_language());
            Assertions.assertTrue(fileFaultCheckDTO.minimum_5());
            Assertions.assertTrue(fileFaultCheckDTO.maximum_1000());
            Assertions.assertTrue(fileFaultCheckDTO.all_string_first_row());
            Assertions.assertTrue(fileFaultCheckDTO.same_size_rows());
            Assertions.assertFalse(fileFaultCheckDTO.same_type_columns());
            Assertions.assertFalse(fileFaultCheckDTO.all_fields_checked());
        }
    }
}
