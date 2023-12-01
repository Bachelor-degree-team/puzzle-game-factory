package com.puzzlemaker.controller;

import com.puzzlemaker.model.dto.ActiveGameDTO;
import com.puzzlemaker.service.ActiveGameService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/game")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ActiveGameController {

    @NotNull
    private final ActiveGameService activeGameService;

    @GetMapping("/active/get/{id}")
    public ResponseEntity<ActiveGameDTO> guess(@PathVariable("id") String id) {
        return ResponseEntity.of(activeGameService.getById(id));
    }

    @GetMapping("/{id}/guess/{name}")
    public ResponseEntity<Map<String, List<String>>> guess(@PathVariable("id") String id, @PathVariable("name") String name) {
        return ResponseEntity.of(activeGameService.guess(id, name));
    }

}
