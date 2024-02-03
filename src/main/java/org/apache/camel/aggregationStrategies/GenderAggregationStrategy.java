package org.apache.camel.aggregationStrategies;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.apache.camel.Player;

import java.util.HashMap;
import java.util.Map;

public class GenderAggregationStrategy implements AggregationStrategy {
    @SuppressWarnings("unchecked")
    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        String newGender = newExchange.getIn().getBody(Player.class).getGender();
        Map<String, Integer> genderCount;

        if (oldExchange == null) {
            genderCount = new HashMap<>();
            genderCount.put(newGender, 1);
            newExchange.getIn().setBody(genderCount);
            return newExchange;
        } else {
            genderCount = oldExchange.getIn().getBody(Map.class);
            genderCount.merge(newGender, 1, Integer::sum);
            return oldExchange;
        }
    }
}
