package dev.gagnon.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class StartupService implements ApplicationRunner {
    
    private final DataSynchronizationService dataSynchronizationService;
    
    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Bot service started successfully!");
        log.info("OpenAI-dependent features are disabled due to API quota limitations");
        log.info("Basic marketplace assistant functionality is available");
        
        // Commented out to avoid OpenAI API calls during startup
        /*
        try {
            // Perform initial sync on startup
            dataSynchronizationService.performFullSync();
            log.info("Initial data synchronization completed successfully");
        } catch (Exception e) {
            log.error("Error during initial data synchronization", e);
            // Don't fail startup, just log the error
        }
        */
    }
}