package com.puzzlemaker.repository;

import com.puzzlemaker.model.ActiveGame;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActiveGameRepository extends MongoRepository<ActiveGame, String> {
}
