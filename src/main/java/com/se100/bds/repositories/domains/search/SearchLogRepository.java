package com.se100.bds.repositories.domains.search;

import com.se100.bds.entities.search.SearchLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchLogRepository extends MongoRepository<SearchLog, String> {
}
