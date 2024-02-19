package com.puzzlemaker.unit.services;

import com.puzzlemaker.service.FileCheckService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FileCheckServiceTests {
    FileCheckService fileCheckService;
    Path resourceDirectory = Paths.get(System.getProperty("user.dir"), "test", "com", "puzzlemaker", "resources");
    @BeforeEach
    public void setUp(){
        this.fileCheckService = new FileCheckService();
    }

    @Test
    public void checkFileTest(){

    }
}
