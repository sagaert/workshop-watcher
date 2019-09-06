package org.salex.raspberry.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer implements InitializingBean {
    private static Logger LOG = LoggerFactory.getLogger(DatabaseInitializer.class);

    private WorkshopRepository repository;

    public DatabaseInitializer(WorkshopRepository repository) {
        this.repository = repository;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if(repository.count() > 0) {
            LOG.info("Skipping database initialization due to data already present.");
        } else {
            LOG.info("Start initializing database");
            repository.save(new Measurement());
            LOG.info("Finished initializing database");
        }
    }
}
