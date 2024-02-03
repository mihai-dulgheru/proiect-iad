package org.apache.camel.aggregationStrategies;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.apache.camel.Player;

import java.util.Map;

public class AgeEnrichmentAggregationStrategy implements AggregationStrategy {
    @SuppressWarnings("rawtypes")
    @Override
    public Exchange aggregate(Exchange original, Exchange resource) {
        Player originalPlayer = original.getIn().getBody(Player.class);
        Map ageInfo = resource.getIn().getBody(Map.class);

        if (originalPlayer != null && ageInfo != null) {
            originalPlayer.setAge((Integer) ageInfo.get("age"));
            originalPlayer.setCanCheckInAlone((Boolean) ageInfo.get("canCheckInAlone"));
        }

        return original;
    }
}
