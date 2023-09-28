package com.puzzlemaker.repository;

import com.puzzlemaker.model.Game;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameRepository extends MongoRepository<Game, String> {

    Optional<Game> findGameByUserId(String userId);

    List<Game> findGamesByUserId(String userId);
}
