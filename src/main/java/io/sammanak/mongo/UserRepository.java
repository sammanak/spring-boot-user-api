package io.sammanak.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface UserRepository extends MongoRepository<User, String>{
    @Query("{ 'email' : ?0 }")
    List<User> findByEmail(String email);
}