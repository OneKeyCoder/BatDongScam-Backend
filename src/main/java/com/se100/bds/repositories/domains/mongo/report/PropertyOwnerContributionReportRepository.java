package com.se100.bds.repositories.domains.mongo.report;

import com.se100.bds.models.schemas.report.PropertyOwnerContributionReport;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PropertyOwnerContributionReportRepository extends MongoRepository<PropertyOwnerContributionReport, String> {
}
