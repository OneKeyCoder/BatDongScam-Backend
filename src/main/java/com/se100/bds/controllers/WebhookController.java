package com.se100.bds.controllers;

import com.se100.bds.controllers.base.AbstractBaseController;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/webhooks")
@Tag(name = "40. Webhooks", description = "Webhook to receive from payment APIs")
public class WebhookController extends AbstractBaseController {
}
