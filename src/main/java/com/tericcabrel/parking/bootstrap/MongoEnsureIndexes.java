package com.tericcabrel.parking.bootstrap;

import com.tericcabrel.parking.models.dbs.*;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.stereotype.Component;

/**
 * Automatic index creation will be disabled by default as of Spring Data MongoDB 3.x.
 * It's recommended to set up indices manually in an application ready block.
 *
 * Here we define properties to be indexed in our model
 */
@Component
public class MongoEnsureIndexes implements ApplicationListener<ContextRefreshedEvent> {
    private MongoTemplate mongoTemplate;

    public MongoEnsureIndexes(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        // User model
        mongoTemplate.indexOps(User.class).ensureIndex(
                new Index().on("email", Sort.Direction.ASC).unique()
        );

        // Role model
        mongoTemplate.indexOps(Role.class).ensureIndex(
                new Index().on("name", Sort.Direction.ASC).unique()
        );

        // CarType model
        mongoTemplate.indexOps(CarType.class).ensureIndex(
            new Index().on("name", Sort.Direction.ASC).unique()
        );

        // Customer model
        mongoTemplate.indexOps(Customer.class).ensureIndex(
            new Index().on("name", Sort.Direction.ASC)
        );

        mongoTemplate.indexOps(Customer.class).ensureIndex(
            new Index().on("email", Sort.Direction.ASC).unique()
        );
    }
}
