package com.puzzlemaker.controller;

import com.puzzlemaker.model.dto.FileCheckDTO;
import com.puzzlemaker.model.dto.GameDTO;
import com.puzzlemaker.service.FileCheckService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/file")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FileCheckController {

    @NotNull
    private final FileCheckService fileCheckService;

    @PostMapping("/check")
    public ResponseEntity<FileCheckDTO> check(@RequestParam MultipartFile csv) {
        FileCheckDTO fileCheckDTO = fileCheckService.checkFile(csv);
        return ResponseEntity.of(Optional.of(fileCheckDTO));
    }

}
