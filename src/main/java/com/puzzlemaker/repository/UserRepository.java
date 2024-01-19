package com.puzzlemaker.repository;

import com.puzzlemaker.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findUserByLogin(String login);

    Optional<User> findUserByGamesIdsContaining(String gameId);

}
