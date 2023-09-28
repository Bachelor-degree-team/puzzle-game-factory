package com.puzzlemaker.controller;

import com.puzzlemaker.service.ActiveGameService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/game")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ActiveGameController {

    @NonNull
    ActiveGameService activeGameService;

    @GetMapping("/{id}/guess/{name}")
    public ResponseEntity<Map<String, String>> guess(@PathVariable("id") String id, @PathVariable("name") String name) {
        return ResponseEntity.of(activeGameService.guess(id, name));
    }

}
