package com.tericcabrel.parking.listeners;

import com.tericcabrel.parking.models.dbs.BaseModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Since all documents have createdAt and updatedAt properties
 * We listen for the event before the data being persisted in the database and we
 * set their value to now
 */
@Component
@Slf4j
public class ModelPersistListener extends AbstractMongoEventListener<BaseModel> {
    @Override
    public void onBeforeConvert(BeforeConvertEvent<BaseModel> event) {
        super.onBeforeConvert(event);

        Date dateNow = new Date();

        event.getSource().setCreatedAt(dateNow);
        event.getSource().setUpdatedAt(dateNow);

        log.info("BeforeConvertEvent - Saving class : " + event.getSource().getClass().getName());
    }
}
