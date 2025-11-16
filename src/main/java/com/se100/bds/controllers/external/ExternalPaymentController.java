package com.se100.bds.controllers.external;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/external/payment")
@Tag(name = "200. External Payment", description = "Account API")
@Slf4j
public class ExternalPaymentController {
}
