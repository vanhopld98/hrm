package vn.com.humanresourcesmanagement.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.com.humanresourcesmanagement.aop.Secured;
import vn.com.humanresourcesmanagement.common.enums.RoleEnum;

@RestController
@RequestMapping("/health")
@RequiredArgsConstructor
public class HealthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HealthController.class);

    @Secured(roles = RoleEnum.NON)
    @GetMapping()
    public ResponseEntity<String> healthCheck() {
        LOGGER.info("[HEALTH] Health Check Success!!!");
        return ResponseEntity.ok("OK");
    }

}
