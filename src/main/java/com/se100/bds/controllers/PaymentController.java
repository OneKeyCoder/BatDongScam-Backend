package com.se100.bds.controllers;

import com.se100.bds.controllers.base.AbstractBaseController;
import com.se100.bds.dtos.requests.payment.CreateBonusPaymentRequest;
import com.se100.bds.dtos.requests.payment.CreateSalaryPaymentRequest;
import com.se100.bds.dtos.requests.payment.UpdatePaymentStatusRequest;
import com.se100.bds.dtos.responses.PageResponse;
import com.se100.bds.dtos.responses.SingleResponse;
import com.se100.bds.dtos.responses.payment.PaymentDetailResponse;
import com.se100.bds.dtos.responses.payment.PaymentListItem;
import com.se100.bds.services.domains.payment.PaymentService;
import com.se100.bds.utils.Constants.PaymentStatusEnum;
import com.se100.bds.utils.Constants.PaymentTypeEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static com.se100.bds.utils.Constants.SECURITY_SCHEME_NAME;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
@Tag(name = "013. Payment Controller", description = "RESTful payment resource for listing, querying, and updating payment records")
@Slf4j
public class PaymentController extends AbstractBaseController {

    private final PaymentService paymentService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    @Operation(
            summary = "List payments",
            description = "Query payment resources with pagination and filters. Payer/payee context is derived from linked contract/property/agent.",
            security = @SecurityRequirement(name = SECURITY_SCHEME_NAME)
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved payments",
            content = @Content(schema = @Schema(implementation = PageResponse.class))
    )
    public ResponseEntity<PageResponse<PaymentListItem>> getPayments(
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") int size,

            @Parameter(description = "Sort by field (e.g., createdAt, dueDate, amount)")
            @RequestParam(defaultValue = "createdAt") String sortBy,

            @Parameter(description = "Sort direction")
            @RequestParam(defaultValue = "DESC") Sort.Direction sortDirection,

            @Parameter(description = "Filter by payment types")
            @RequestParam(required = false) List<PaymentTypeEnum> paymentTypes,

            @Parameter(description = "Filter by payment statuses")
            @RequestParam(required = false) List<PaymentStatusEnum> statuses,

            @Parameter(description = "Filter by payer ID (customer or property owner)")
            @RequestParam(required = false) UUID payerId,

            @Parameter(description = "Filter by payee ID (agent for salary/bonus)")
            @RequestParam(required = false) UUID payeeId,

            @Parameter(description = "Filter by contract ID")
            @RequestParam(required = false) UUID contractId,

            @Parameter(description = "Filter by property ID")
            @RequestParam(required = false) UUID propertyId,

            @Parameter(description = "Filter by agent ID")
            @RequestParam(required = false) UUID agentId,

            @Parameter(description = "Filter by due date from (inclusive)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDateFrom,

            @Parameter(description = "Filter by due date to (inclusive)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDateTo,

            @Parameter(description = "Filter by paid date from (inclusive)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate paidDateFrom,

            @Parameter(description = "Filter by paid date to (inclusive)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate paidDateTo,

            @Parameter(description = "Filter overdue payments only (due date < today and not paid)")
            @RequestParam(required = false) Boolean overdue
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<PaymentListItem> payments = paymentService.getPayments(
                pageable, paymentTypes, statuses, payerId, payeeId,
                contractId, propertyId, agentId, dueDateFrom, dueDateTo,
                paidDateFrom, paidDateTo, overdue
        );

        return responseFactory.successPage(payments, "Payments retrieved successfully");
    }

    @GetMapping("/{paymentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    @Operation(
            summary = "Get payment details",
            description = "Retrieve a payment resource by ID with payer/payee context",
            security = @SecurityRequirement(name = SECURITY_SCHEME_NAME)
    )
    public ResponseEntity<SingleResponse<PaymentDetailResponse>> getPaymentById(
            @Parameter(description = "Payment ID", required = true)
            @PathVariable UUID paymentId
    ) {
        PaymentDetailResponse payment = paymentService.getPaymentById(paymentId);
        return responseFactory.successSingle(payment, "Payment retrieved successfully");
    }

    @PatchMapping("/{paymentId}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    @Operation(
            summary = "Update payment status",
            description = "Mark a payment as paid (SUCCESS) or set internal status after manual confirmation",
            security = @SecurityRequirement(name = SECURITY_SCHEME_NAME)
    )
    public ResponseEntity<SingleResponse<PaymentDetailResponse>> updatePaymentStatus(
            @Parameter(description = "Payment ID", required = true)
            @PathVariable UUID paymentId,
            @Valid @RequestBody UpdatePaymentStatusRequest request
    ) {
        PaymentDetailResponse payment = paymentService.updatePaymentStatus(paymentId, request);
        return responseFactory.successSingle(payment, "Payment status updated successfully");
    }

    @PostMapping("/salary")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    @Operation(
            summary = "Create salary payment",
            description = "Creates a salary payment record for a sales agent (manual payout flow).",
            security = @SecurityRequirement(name = SECURITY_SCHEME_NAME)
    )
    public ResponseEntity<SingleResponse<PaymentDetailResponse>> createSalaryPayment(
            @Valid @RequestBody CreateSalaryPaymentRequest request
    ) {
        PaymentDetailResponse payment = paymentService.createSalaryPayment(request);
        return responseFactory.successSingle(payment, "Salary payment created successfully");
    }

    @PostMapping("/bonus")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    @Operation(
            summary = "Create bonus payment",
            description = "Creates a bonus payment record for a sales agent (manual payout flow).",
            security = @SecurityRequirement(name = SECURITY_SCHEME_NAME)
    )
    public ResponseEntity<SingleResponse<PaymentDetailResponse>> createBonusPayment(
            @Valid @RequestBody CreateBonusPaymentRequest request
    ) {
        PaymentDetailResponse payment = paymentService.createBonusPayment(request);
        return responseFactory.successSingle(payment, "Bonus payment created successfully");
    }
}
