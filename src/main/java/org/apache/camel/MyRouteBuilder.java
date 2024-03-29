package org.apache.camel;

import org.apache.camel.aggregationStrategies.AgeEnrichmentAggregationStrategy;
import org.apache.camel.aggregationStrategies.GenderAggregationStrategy;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.cache.SimpleCache;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.util.UniqueIdentifierGenerator;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings({"DuplicatedCode", "unchecked"})
public class MyRouteBuilder extends RouteBuilder {
    private static final Integer PER_PAGE = 900;
    private static final Integer PORT = 8080;
    private static final String CONTEXT_PATH = "/api";
    private static final String FOLDER = "web/";
    private static final String HOSTNAME = "localhost";
    private static final String URI = "https://portal.frsah.ro/api/public/players";

    public void configure() {

        onException(Exception.class)
                .process(exchange -> {
                    Exception exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT,
                            Exception.class);
                    System.err.println("Error: " + exception.getMessage());
                })
                .handled(true);

        fromF("jetty:http://%s:%d/%s?matchOnUriPrefix=true", HOSTNAME, PORT, FOLDER)
                .process(new StaticProcessor(FOLDER));

        restConfiguration()
                .host(HOSTNAME)
                .port(PORT)
                .component("jetty")
                .contextPath(CONTEXT_PATH);

        rest("/players")
                .produces("application/json")
                .get("/")
                .to("direct:searchPlayer")
                .get("/download")
                .to("direct:downloadPlayers")
                .get("/{id}")
                .to("direct:searchPlayerById");

        from("direct:searchPlayer")
                .choice()
                .when(header("X-Query-Key").isEqualTo("search"))
                .to("direct:searchPlayerByLastname")
                .when(header("X-Query-Key").isEqualTo("gender"))
                .to("direct:searchPlayerByGender")
                .when(header("X-Query-Key").isEqualTo("sort"))
                .to("direct:sortPlayers")
                .otherwise()
                .to("direct:searchAllPlayers");

        from("direct:searchPlayerByLastname")
                .process(exchange -> {
                    String cacheKey = "playersByLastname_" + exchange.getIn()
                            .getHeader("X-Query-Value", String.class).toLowerCase();
                    List<Player> cachedData = (List<Player>) SimpleCache.get(cacheKey);
                    if (cachedData == null) {
                        exchange.getIn().setHeader("CacheMiss", true);
                        exchange.getIn().setHeader("CacheKey", cacheKey);
                        exchange.getIn().setHeader("NoCache",
                                UniqueIdentifierGenerator.generate());
                    } else {
                        exchange.getIn().setBody(cachedData);
                    }
                })
                .choice()
                .when(header("CacheMiss"))
                .removeHeaders("CamelHttp*")
                .setHeader("Accept", constant("application/json"))
                .setHeader("Accept-Encoding", constant("deflate"))
                .setBody(constant(""))
                .setHeader(Exchange.HTTP_QUERY, simple("per_page=" + PER_PAGE + "&no_cache=${header.NoCache}"))
                .to(URI)
                .convertBodyTo(String.class)
                .unmarshal()
                .json(JsonLibrary.Jackson)
                .bean(Player.class, "extractPlayers")
                .process(exchange -> {
                    String cacheKey = exchange.getIn().getHeader("CacheKey", String.class);
                    String filterValue = exchange.getIn().getHeader("X-Query-Value", String.class)
                            .toLowerCase();
                    List<Player> players = exchange.getIn().getBody(List.class);
                    List<Player> filteredPlayers = players.stream().filter(
                                    player -> player.getLastname().toLowerCase()
                                            .contains(filterValue))
                            .collect(Collectors.toList());
                    SimpleCache.put(cacheKey, filteredPlayers);
                    exchange.getIn().setBody(filteredPlayers);
                })
                .end()
                .marshal()
                .json(JsonLibrary.Jackson);

        from("direct:searchPlayerByGender")
                .process(exchange -> {
                    String cacheKey = "playersByGender_"
                            + exchange.getIn().getHeader("X-Query-Value", String.class);
                    List<Player> cachedData = (List<Player>) SimpleCache.get(cacheKey);
                    if (cachedData == null) {
                        exchange.getIn().setHeader("CacheMiss", true);
                        exchange.getIn().setHeader("CacheKey", cacheKey);
                        exchange.getIn().setHeader("NoCache",
                                UniqueIdentifierGenerator.generate());
                    } else {
                        exchange.getIn().setBody(cachedData);
                    }
                })
                .choice()
                .when(header("CacheMiss"))
                .removeHeaders("CamelHttp*")
                .setHeader("Accept", constant("application/json"))
                .setHeader("Accept-Encoding", constant("deflate"))
                .setBody(constant(""))
                .setHeader(Exchange.HTTP_QUERY, simple("per_page=" + PER_PAGE + "&no_cache=${header.NoCache}"))
                .to(URI)
                .convertBodyTo(String.class)
                .unmarshal()
                .json(JsonLibrary.Jackson)
                .bean(Player.class, "extractPlayers")
                .process(exchange -> {
                    String cacheKey = exchange.getIn().getHeader("CacheKey", String.class);
                    String filterValue = exchange.getIn().getHeader("X-Query-Value", String.class);
                    List<Player> players = exchange.getIn().getBody(List.class);
                    List<Player> filteredPlayers = players.stream()
                            .filter(player -> player.getGender()
                                    .equalsIgnoreCase(filterValue))
                            .collect(Collectors.toList());
                    SimpleCache.put(cacheKey, filteredPlayers);
                    exchange.getIn().setBody(filteredPlayers);
                })
                .end()
                .marshal()
                .json(JsonLibrary.Jackson);

        from("direct:sortPlayers")
                .process(exchange -> {
                    String cacheKey = "sortedPlayers_"
                            + exchange.getIn().getHeader("X-Query-Value", String.class);
                    List<Player> cachedData = (List<Player>) SimpleCache.get(cacheKey);
                    if (cachedData == null) {
                        exchange.getIn().setHeader("CacheMiss", true);
                        exchange.getIn().setHeader("CacheKey", cacheKey);
                        exchange.getIn().setHeader("NoCache",
                                UniqueIdentifierGenerator.generate());
                    } else {
                        exchange.getIn().setBody(cachedData);
                    }
                })
                .choice()
                .when(header("CacheMiss"))
                .removeHeaders("CamelHttp*")
                .setHeader("Accept", constant("application/json"))
                .setHeader("Accept-Encoding", constant("deflate"))
                .setBody(constant(""))
                .setHeader(Exchange.HTTP_QUERY, simple("per_page=" + PER_PAGE + "&no_cache=${header.NoCache}"))
                .to(URI)
                .convertBodyTo(String.class)
                .unmarshal()
                .json(JsonLibrary.Jackson)
                .bean(Player.class, "extractPlayers")
                .process(exchange -> {
                    String cacheKey = exchange.getIn().getHeader("CacheKey", String.class);
                    String filterValue = exchange.getIn().getHeader("X-Query-Value", String.class);
                    List<Player> players = exchange.getIn().getBody(List.class);
                    if ("asc".equalsIgnoreCase(filterValue)) {
                        players.sort(Comparator.comparing(Player::getLastname));
                    } else {
                        players.sort((p1, p2) -> p2.getLastname().compareTo(p1.getLastname()));
                    }
                    SimpleCache.put(cacheKey, players);
                    exchange.getIn().setBody(players);
                })
                .end()
                .marshal()
                .json(JsonLibrary.Jackson);

        from("direct:searchAllPlayers")
                .process(exchange -> {
                    String cacheKey = "allPlayers";
                    List<Player> cachedData = (List<Player>) SimpleCache.get(cacheKey);
                    if (cachedData == null) {
                        exchange.getIn().setHeader("CacheMiss", true);
                        exchange.getIn().setHeader("CacheKey", cacheKey);
                        exchange.getIn().setHeader("NoCache",
                                UniqueIdentifierGenerator.generate());
                    } else {
                        exchange.getIn().setBody(cachedData);
                    }
                })
                .choice()
                .when(header("CacheMiss"))
                .removeHeaders("CamelHttp*")
                .setHeader("Accept", constant("application/json"))
                .setHeader("Accept-Encoding", constant("deflate"))
                .setBody(constant(""))
                .setHeader(Exchange.HTTP_QUERY, simple("per_page=" + PER_PAGE + "&no_cache=${header.NoCache}"))
                .toD(URI)
                .convertBodyTo(String.class)
                .unmarshal()
                .json(JsonLibrary.Jackson)
                .bean(Player.class, "extractPlayers")
                .process(exchange -> {
                    String cacheKey = exchange.getIn().getHeader("CacheKey", String.class);
                    List<Player> responseBody = exchange.getIn().getBody(List.class);
                    SimpleCache.put(cacheKey, responseBody);
                })
                .end()
                .marshal()
                .json(JsonLibrary.Jackson);

        from("direct:downloadPlayers")
                .process(exchange -> exchange.getIn().setHeader("NoCache", UniqueIdentifierGenerator.generate()))
                .removeHeaders("CamelHttp*")
                .setHeader("Accept", constant("application/json"))
                .setHeader("Accept-Encoding", constant("deflate"))
                .setBody(constant(""))
                .setHeader(Exchange.HTTP_QUERY, simple("per_page=" + PER_PAGE + "&no_cache=${header.NoCache}"))
                .toD(URI)
                .convertBodyTo(String.class)
                .unmarshal()
                .json(JsonLibrary.Jackson)
                .bean(Player.class, "extractPlayers")
                .process(exchange -> {
                    String queryStringParameters = exchange.getIn().getHeader("X-Query-String-Parameters",
                            String.class);
                    Map<String, String> queryParams = parseQueryString(queryStringParameters);
                    List<Player> players = exchange.getIn().getBody(List.class);
                    if (queryParams.containsKey("search")) {
                        String filterValue = queryParams.get("search");
                        players = players.stream().filter(
                                        player -> player.getLastname().toLowerCase().contains(filterValue))
                                .collect(Collectors.toList());
                    }
                    if (queryParams.containsKey("gender")) {
                        String filterValue = queryParams.get("gender");
                        players = players.stream().filter(
                                        player -> player.getGender().equalsIgnoreCase(filterValue))
                                .collect(Collectors.toList());
                    }
                    exchange.getIn().setBody(players);
                })
                .marshal()
                .json(JsonLibrary.Jackson, true)
                .to("file:data/output?fileName=players.json&fileExist=Override");

        from("direct:searchPlayerById")
                .process(exchange -> {
                    String playerId = exchange.getIn().getHeader("id", String.class);
                    String cacheKey = "playerById_" + playerId;
                    Player cachedPlayer = (Player) SimpleCache.get(cacheKey);
                    if (cachedPlayer == null) {
                        exchange.getIn().setHeader("CacheMiss", true);
                        exchange.getIn().setHeader("CacheKey", cacheKey);
                        exchange.getIn().setHeader("NoCache", UniqueIdentifierGenerator.generate());
                    } else {
                        exchange.getIn().setBody(cachedPlayer);
                    }
                })
                .choice()
                .when(header("CacheMiss"))
                .removeHeaders("CamelHttp*")
                .setHeader("Accept", constant("application/json"))
                .setHeader("Accept-Encoding", constant("deflate"))
                .setHeader(Exchange.HTTP_QUERY, simple("no_cache=${header.NoCache}"))
                .toD(URI + "/${header.id}")
                .unmarshal()
                .json(JsonLibrary.Jackson)
                .bean(Player.class, "extractPlayer")
                .process(exchange -> {
                    Player player = exchange.getIn().getBody(Player.class);
                    exchange.getIn().setHeader("PlayerAgeInfo", player.getYear());
                })
                .enrich("direct:calculateAge", new AgeEnrichmentAggregationStrategy())
                .process(exchange -> {
                    String cacheKey = exchange.getIn().getHeader("CacheKey", String.class);
                    Player enrichedPlayer = exchange.getIn().getBody(Player.class);
                    SimpleCache.put(cacheKey, enrichedPlayer);
                })
                .end()
                .marshal()
                .json(JsonLibrary.Jackson);

        from("direct:calculateAge")
                .process(exchange -> {
                    Integer yearOfBirth = exchange.getIn().getHeader("PlayerAgeInfo", Integer.class);
                    if (yearOfBirth != null) {
                        int currentYear = LocalDate.now().getYear();
                        int age = currentYear - yearOfBirth;
                        boolean canCheckInAlone = age >= 18;
                        Map<String, Object> ageInfo = new HashMap<>();
                        ageInfo.put("age", age);
                        ageInfo.put("canCheckInAlone", canCheckInAlone);
                        exchange.getIn().setBody(ageInfo);
                    }
                });

        rest("/aggregate")
                .produces("application/json")
                .get("/")
                .to("direct:countPlayersByGender");

        from("direct:countPlayersByGender")
                .process(exchange -> {
                    String cacheKey = "allPlayers";
                    List<Player> cachedData = (List<Player>) SimpleCache.get(cacheKey);
                    exchange.getIn().setBody(cachedData);
                })
                .split(body())
                .filter(exchange -> {
                    Player player = exchange.getIn().getBody(Player.class);
                    return player.isGenderFM();
                })
                .resequence().body()
                .to("direct:aggregateGender");

        from("direct:aggregateGender")
                .aggregate(new GenderAggregationStrategy()).constant(true)
                .completionSize(PER_PAGE)
                .completionTimeout(1000)
                .process(exchange -> {
                    Map<String, Integer> aggregatedGenderCount = exchange.getIn().getBody(Map.class);
                    exchange.getIn().setBody(aggregatedGenderCount);
                })
                .marshal()
                .json(JsonLibrary.Jackson)
                .to("file:data/output?fileName=playerCountsByGender.json&fileExist=Override")
                .end();

    }

    private Map<String, String> parseQueryString(String queryString) {
        Map<String, String> queryMap = new HashMap<>();
        if (queryString == null || queryString.trim().isEmpty()) {
            return queryMap;
        }

        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            if (idx > 0 && idx < pair.length() - 1) {
                String key = pair.substring(0, idx).toLowerCase();
                String value = pair.substring(idx + 1);
                if (!value.isEmpty()) {
                    queryMap.put(key, value);
                }
            }
        }

        return queryMap;
    }

}
