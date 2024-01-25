package org.apache.camel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.Field;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class Flight {
    @JsonIgnore
    private final String code;

    @JsonProperty
    private String number;

    @JsonProperty
    private String aircraft;

    @JsonProperty
    private String origin;

    @JsonProperty
    private String destination;

    private Flight(String code) {
        this.code = code;
    }

    @SuppressWarnings("unchecked")
    public static List<Flight> extractFlights(Exchange exchange) {
        List<Flight> flights = new ArrayList<Flight>();
        Map<String, Object> body = exchange.getIn().getBody(Map.class);
        for (Entry<String, Object> entry : body.entrySet()) {
            if (entry.getValue() instanceof List) {
                flights.add(new Flight(entry.getKey()));
            }
        }
        return flights;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> extractFlightDetails(Exchange exchange) {
        Map<String, Object> details = new HashMap<String, Object>();
        Map<String, Object> body = exchange.getIn().getBody(Map.class);
        var identification = (Map<String, Object>) body.get("identification");
        var number = (Map<String, Object>) identification.get("number");
        var aircraft = (Map<String, Object>) body.get("aircraft");
        var model = (Map<String, Object>) aircraft.get("model");
        var airport = (Map<String, Object>) body.get("airport");
        var origin = (Map<String, Object>) airport.get("origin");
        var destination = (Map<String, Object>) airport.get("destination");
        details.put("number", number.get("default"));
        details.put("aircraft", model.get("text"));
        details.put("origin", origin.get("name"));
        details.put("destination", destination.get("destination"));
        var status = (Map<String, Object>) body.get("status");
        details.put("status", status.get("text"));
        return details;
    }

    public String getCode() {
        return code;
    }

    public String getNumber() {
        return number;
    }

    public String getAircraft() {
        return aircraft;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDetails(Map<String, Object> details) {
        var fields = Arrays.stream(getClass().getDeclaredFields()).collect(Collectors.toMap(Field::getName, field -> field));
        for (var entry : fields.entrySet()) {
            if (details.containsKey(entry.getKey())) {
                entry.getValue().setAccessible(true);
                try {
                    entry.getValue().set(this, details.get(entry.getKey()));
                } catch (IllegalArgumentException | IllegalAccessException ignored) {
                }
            }
        }
    }

    @Override
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return super.toString();
        }
    }
}
