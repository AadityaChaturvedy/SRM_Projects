package com.srmist.academia;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class JdbcDemo implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("✅ Testing JDBC connection to Supabase...");

        try {
            // Just run a simple query (replace with your table name)
            List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT * FROM users LIMIT 5");

            if (rows.isEmpty()) {
                System.out.println("⚠️ No data found in Supabase table.");
            } else {
                System.out.println("✅ Data retrieved from Supabase:");
                rows.forEach(System.out::println);
            }
        } catch (Exception e) {
            System.err.println("❌ Error connecting to Supabase:");
            e.printStackTrace();
        }
    }
}
