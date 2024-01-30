package org.apache.camel;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A Camel Java DSL Router
 */
public class MyRouteBuilder extends RouteBuilder {
    private static final String HOSTNAME = "localhost";
    private static final int PORT = 8080;
    private static final String FOLDER = "web";

    /**
     * Let's configure the Camel routing rules using Java code...
     */
    @SuppressWarnings("unchecked")
    public void configure() {

        fromF("jetty:http://%s:%d/%s?matchOnUriPrefix=true", HOSTNAME, PORT, FOLDER)
                .process(new StaticProcessor(FOLDER));
        restConfiguration()
                .host(HOSTNAME)
                .port(PORT)
                .component("jetty")
                .contextPath("/api");
        rest("/players")
                .produces("application/json")
                .get("/")
                .to("direct:searchPlayer");
        from("direct:searchPlayer")
                .choice()
                .when(simple("${header[\"X-Filter-Type\"]} == \"search\""))
                .to("direct:searchPlayerByLastname")
                .when(simple("${header[\"X-Filter-Type\"]} == \"gender\""))
                .to("direct:searchPlayerByGender");
        from("direct:searchPlayerByLastname")
                .removeHeaders("CamelHttp*")
                .setHeader("Accept")
                .constant("application/json")
                .setHeader("Accept-Encoding")
                .constant("deflate")
                .setBody(constant(""))
                .setHeader(Exchange.HTTP_QUERY)
                .simple("per_page=900")
                .to("https://portal.frsah.ro/api/public/players")
                .convertBodyTo(String.class)
                .unmarshal()
                .json(JsonLibrary.Jackson)
                .bean(Player.class, "extractPlayers")
                .process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        String filterValue = exchange.getIn().getHeader("X-Filter-Value", String.class).toLowerCase();
                        List<Player> players = exchange.getIn().getBody(List.class);
                        List<Player> filteredPlayers = players.stream()
                                .filter(player -> player.getFirstname().toLowerCase().contains(filterValue))
                                .collect(Collectors.toList());
                        exchange.getIn().setBody(filteredPlayers);
                    }
                })
                .marshal()
                .json(JsonLibrary.Jackson);

        from("direct:searchPlayerByGender")
                .removeHeaders("CamelHttp*")
                .setHeader("Accept")
                .constant("application/json")
                .setHeader("Accept-Encoding")
                .constant("deflate")
                .setBody(constant(""))
                .setHeader(Exchange.HTTP_QUERY)
                .simple("per_page=900")
                .to("https://portal.frsah.ro/api/public/players")
                .convertBodyTo(String.class)
                .unmarshal()
                .json(JsonLibrary.Jackson)
                .bean(Player.class, "extractPlayers")
                .process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        String filterValue = exchange.getIn().getHeader("X-Filter-Value", String.class);
                        List<Player> players = exchange.getIn().getBody(List.class);
                        List<Player> filteredPlayers = players.stream()
                                .filter(player -> player.getGender().equalsIgnoreCase(filterValue))
                                .collect(Collectors.toList());
                        exchange.getIn().setBody(filteredPlayers);
                    }
                })
                .marshal()
                .json(JsonLibrary.Jackson);

        rest("/airlines")
                .produces("application/json")
                .get("/{airline}")
                .to("direct:searchAirline")
                .get("/{airline}/flights")
                .to("direct:searchFlights");
        rest("/flights")
                .produces("application/json")
                .get("/{airline}")
                .to("direct:searchFlights");
        from("direct:searchAirline")
                .removeHeaders("CamelHttp*")
                .setHeader("Accept")
                .constant("application/json")
                .setHeader("Accept-Encoding")
                .constant("deflate")
                .setBody(constant(""))
                .setHeader(Exchange.HTTP_QUERY)
                .simple("bridgeEndpoint=true&type=operator&query=${header.airline}")
                .to("https://www.flightradar24.com/v1/search/web/find")
                .convertBodyTo(String.class)
                .unmarshal()
                .json(JsonLibrary.Jackson)
                .bean(Airline.class, "extractAirlines")
                .marshal()
                .json(JsonLibrary.Jackson);
        from("direct:searchFlights")
                .removeHeaders("CamelHttp*")
                .setHeader("Accept")
                .constant("application/json")
                .setHeader("Accept-Encoding")
                .constant("deflate")
                .setHeader(Exchange.HTTP_QUERY)
                .simple("bridgeEndpoint=true&airline=${header.airline}")
                .setBody(constant(""))
                .to("https://data-cloud.flightradar24.com/zones/fcgi/feed.js")
                .convertBodyTo(String.class)
                .unmarshal()
                .json(JsonLibrary.Jackson)
                .bean(Flight.class, "extractFlights")
                .setHeader("size")
                .simple("${body.size}")
                .split()
                .body()
                .enrich("direct:searchFlightDetails", (oldExchange, newExchange) -> {
                    oldExchange.getIn()
                            .getBody(Flight.class)
                            .setDetails((Map<String, Object>) newExchange.getIn()
                                    .getBody(Map.class));
                    return oldExchange;
                })
                .aggregate(constant(true), (oldExchange, newExchange) -> {
                    Flight flight = newExchange.getIn()
                            .getBody(Flight.class);
                    if (oldExchange == null) {
                        List<Flight> flights = new ArrayList<>();
                        flights.add(flight);
                        newExchange.getIn()
                                .setBody(flights);
                        return newExchange;
                    } else {
                        List<Flight> flights = oldExchange.getIn()
                                .getBody(List.class);
                        flights.add(flight);
                        return oldExchange;
                    }
                })
                .completionSize(simple("${header.size}"))
                .marshal()
                .json(JsonLibrary.Jackson);
        from("direct:searchFlightDetails")
                .removeHeaders("CamelHttp*")
                .setHeader("Accept")
                .constant("application/json")
                .setHeader("Accept-Encoding")
                .constant("deflate")
                .setHeader(Exchange.HTTP_QUERY)
                .simple("bridgeEndpoint=true&flight=${body.code}")
                .setBody(constant(""))
                .to("https://data-live.flightradar24.com/clickhandler/")
                .convertBodyTo(String.class)
                .unmarshal()
                .json(JsonLibrary.Jackson)
                .bean(Flight.class, "extractFlightDetails");
    }

}
