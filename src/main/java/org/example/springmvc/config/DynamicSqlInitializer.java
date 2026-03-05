package org.example.springmvc.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Component
public class DynamicSqlInitializer implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ResourceLoader resourceLoader;

    private boolean initialized = false;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // Only run once, after the root context is refreshed (schema is created)
        if (!initialized && event.getApplicationContext().getParent() == null) {
            initializeDatabase();
            initialized = true;
        }
    }

    private void initializeDatabase() {
        try {
            // Find all SQL files in the data/ directory
            Resource[] resources = ResourcePatternUtils.getResourcePatternResolver(resourceLoader)
                    .getResources("classpath:data/*.sql");

            // Sort files by name to ensure consistent execution order
            List<Resource> sortedResources = new ArrayList<>(Arrays.asList(resources));
            sortedResources.sort(Comparator.comparing(Resource::getFilename));

            // Execute each SQL file
            for (Resource resource : sortedResources) {
                if (resource.exists() && resource.isReadable()) {
                    try (InputStream inputStream = resource.getInputStream()) {
                        String sql = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
                        
                        // Split by semicolon and execute each statement
                        String[] statements = sql.split(";");
                        for (String statement : statements) {
                            String trimmed = statement.trim();
                            if (!trimmed.isEmpty() && !trimmed.startsWith("--")) {
                                jdbcTemplate.execute(trimmed);
                            }
                        }
                        
                        System.out.println("Executed SQL file: " + resource.getFilename());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error initializing database with SQL files: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

