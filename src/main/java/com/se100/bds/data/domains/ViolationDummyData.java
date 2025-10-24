package com.se100.bds.data.domains;

import com.se100.bds.models.entities.property.Property;
import com.se100.bds.models.entities.user.User;
import com.se100.bds.models.entities.violation.ViolationReport;
import com.se100.bds.models.schemas.report.BaseReportData;
import com.se100.bds.models.schemas.report.ViolationReportDetails;
import com.se100.bds.repositories.domains.mongo.report.ViolationReportDetailsRepository;
import com.se100.bds.repositories.domains.property.PropertyRepository;
import com.se100.bds.repositories.domains.user.UserRepository;
import com.se100.bds.repositories.domains.violation.ViolationRepository;
import com.se100.bds.utils.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@RequiredArgsConstructor
@Service
public class ViolationDummyData {

    private final ViolationRepository violationRepository;
    private final ViolationReportDetailsRepository violationReportDetailsRepository;
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final Random random = new Random();

    public void createDummy() {
        createDummyViolations();
        createDummyViolationReportDetails();
    }

    private void createDummyViolations() {
        log.info("Creating dummy violation reports");

        List<User> users = userRepository.findAll();
        List<Property> properties = propertyRepository.findAll();

        if (users.isEmpty()) {
            log.warn("Cannot create violations - no users found");
            return;
        }

        List<ViolationReport> violations = new ArrayList<>();

        // Create 20 violation reports (small number, as violations should be rare)
        for (int i = 0; i < 20; i++) {
            User user = users.get(random.nextInt(users.size()));
            Property property = !properties.isEmpty() && random.nextBoolean()
                    ? properties.get(random.nextInt(properties.size()))
                    : null;

            String[] violationTypes = {
                    "Fraudulent Listing",
                    "Misrepresentation of Property",
                    "Spam or Duplicate Listing",
                    "Inappropriate Content",
                    "Non-compliance with Terms",
                    "Failure to Disclose Information"
            };

            String[] severities = {"MINOR", "MODERATE", "SERIOUS", "CRITICAL"};
            String[] statuses = {"REPORTED", "UNDER_REVIEW", "RESOLVED", "DISMISSED"};

            String violationType = violationTypes[random.nextInt(violationTypes.length)];
            String severity = severities[random.nextInt(severities.length)];
            String status = statuses[random.nextInt(statuses.length)];

            ViolationReport violation = ViolationReport.builder()
                    .user(user)
                    .property(property)
                    .violationType(violationType)
                    .description(generateViolationDescription(violationType))
                    .status(status)
                    .resolutionNotes(status.equals("RESOLVED") ? generateResolutionNotes() : null)
                    .resolvedAt(status.equals("RESOLVED") ? LocalDateTime.now().minusDays(random.nextInt(30)) : null)
                    .build();

            violations.add(violation);
        }

        violationRepository.saveAll(violations);
        log.info("Saved {} violation reports to database", violations.size());
    }

    private String generateViolationDescription(String violationType) {
        switch (violationType) {
            case "Fraudulent Listing":
                return "Property listing contains false information and misleading claims.";
            case "Misrepresentation of Property":
                return "Property details do not match the actual condition or specifications.";
            case "Spam or Duplicate Listing":
                return "Multiple identical listings posted for the same property.";
            case "Inappropriate Content":
                return "Listing contains inappropriate images or offensive language.";
            case "Non-compliance with Terms":
                return "User violated platform terms and conditions.";
            case "Failure to Disclose Information":
                return "Critical property information was not disclosed to potential buyers.";
            default:
                return "Violation of platform policies.";
        }
    }

    private String generateResolutionNotes() {
        String[] notes = {
                "Warning issued to user. Listing has been corrected.",
                "User account temporarily suspended. Violation resolved after compliance.",
                "Listing removed. User educated on platform policies.",
                "False report. No violation found after investigation.",
                "User provided correct information. Case closed."
        };
        return notes[random.nextInt(notes.length)];
    }

    private void createDummyViolationReportDetails() {
        log.info("Creating dummy violation report details");

        YearMonth currentMonth = YearMonth.now();
        List<ViolationReportDetails> reportDetailsList = new ArrayList<>();

        // Create reports for the last 22 months
        for (int i = 0; i < 22; i++) {
            YearMonth reportMonth = currentMonth.minusMonths(i);
            int year = reportMonth.getYear();
            int month = reportMonth.getMonthValue();

            // Calculate statistics for this month
            int totalViolations = 15 + random.nextInt(25); // 15-40 violations
            int currentMonthViolations = i == 0 ? (10 + random.nextInt(15)) : 0; // Only current month has this value
            int avgResolutionTime = 24 + random.nextInt(96); // 24-120 hours (1-5 days)
            int accountsSuspended = random.nextInt(8); // 0-7 accounts suspended
            int propertiesRemoved = random.nextInt(12); // 0-11 properties removed

            // Create base report data
            BaseReportData baseReportData = new BaseReportData();
            baseReportData.setReportType(Constants.ReportTypeEnum.VIOLATION);
            baseReportData.setMonth(month);
            baseReportData.setYear(year);
            baseReportData.setTitle("Violation Report - " + reportMonth.getMonth() + " " + year);
            baseReportData.setDescription("Monthly violation statistics and resolution metrics");
            baseReportData.setStartDate(reportMonth.atDay(1).atStartOfDay());
            baseReportData.setEndDate(reportMonth.atEndOfMonth().atTime(23, 59, 59));
            baseReportData.setFilePath("/reports/violation/" + year + "/" + month + "/violation-report.pdf");
            baseReportData.setFileName("violation-report-" + year + "-" + String.format("%02d", month) + ".pdf");
            baseReportData.setFileFormat("PDF");

            // Create violation report details
            ViolationReportDetails reportDetails = ViolationReportDetails.builder()
                    .totalViolationReports(totalViolations)
                    .totalViolationReportsCurrentMonth(currentMonthViolations)
                    .avgResolutionTimeHours(avgResolutionTime)
                    .accountsSuspended(accountsSuspended)
                    .propertiesRemoved(propertiesRemoved)
                    .build();

            reportDetails.setBaseReportData(baseReportData);
            reportDetailsList.add(reportDetails);

            log.info("Created violation report for {}/{}: {} total violations, {} avg resolution hours, {} accounts suspended, {} properties removed",
                    month, year, totalViolations, avgResolutionTime, accountsSuspended, propertiesRemoved);
        }

        violationReportDetailsRepository.saveAll(reportDetailsList);
        log.info("Saved {} violation report details to MongoDB", reportDetailsList.size());
    }
}
