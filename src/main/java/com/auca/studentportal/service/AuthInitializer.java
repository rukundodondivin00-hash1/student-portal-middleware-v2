package com.auca.studentportal.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthInitializer implements ApplicationRunner {

    private final AuthService authService;

    @Override
    @Async
    public void run(ApplicationArguments args) {
        log.info("Initializing async authentication with AUCA...");
        authService.signIn();
        log.info("Async authentication completed");
    }
}
