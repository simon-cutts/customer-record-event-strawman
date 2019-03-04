package com.sighware.customer.query;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.apache.log4j.Logger;
import com.sighware.customer.event.CustomerEvent;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Event query to retrieve all the events of a customer
 *
 * @author Simon Cutts
 */
public class EventQuery {

    private static final Logger LOG = Logger.getLogger(EventQuery.class);
    private final ZonedDateTime startTime = ZonedDateTime.parse("1900-01-01T00:00:01.000Z");
    private DynamoDBMapper mapper;
    private ZonedDateTime endTime = ZonedDateTime.now(ZoneOffset.UTC);
    private String customerId;


    public EventQuery(DynamoDBMapper mapper, ZonedDateTime endTime, String customerId) {
        this.mapper = mapper;
        this.endTime = endTime;
        this.customerId = customerId;
    }

    /**
     * Get the customer events
     */
    public List<CustomerEvent> get() {

        // Retrieve the CustomerEvents
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":customerId", new AttributeValue().withS(customerId));
        eav.put(":startTime", new AttributeValue().withS(startTime.format(DateTimeFormatter.ISO_INSTANT)));
        eav.put(":endTime", new AttributeValue().withS(endTime.format(DateTimeFormatter.ISO_INSTANT)));

        DynamoDBQueryExpression<CustomerEvent> queryExpression = new DynamoDBQueryExpression<CustomerEvent>()
                .withKeyConditionExpression("customerId = :customerId and createTime BETWEEN :startTime AND :endTime")
                .withExpressionAttributeValues(eav);

        LOG.info("Query customer events with customerId " + customerId);
        List<CustomerEvent> events = mapper.query(CustomerEvent.class, queryExpression);

        return events;
    }
}
