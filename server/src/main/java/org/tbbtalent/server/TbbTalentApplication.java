package org.tbbtalent.server;

import org.flywaydb.core.Flyway;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TbbTalentApplication { 

    public static void main(String[] args) {
        SpringApplication.run(TbbTalentApplication.class, args);
    }

   @Bean
   @ConditionalOnProperty(name="flyway.repair", havingValue="true")
   public FlywayMigrationStrategy fixFlyway() {
       return new FlywayMigrationStrategy() {
           @Override
           public void migrate(Flyway flyway) {
               try {
                   System.out.println("************* Starting flyway repair ***********************");
                   flyway.repair();
                   System.out.println("************* Finished flyway repair ***********************");
                   flyway.migrate();
               } catch (Exception e) {
                   System.out.println("ERROR: unable to repair flyway");
                   e.printStackTrace();
               }
           }
       };
   }

}