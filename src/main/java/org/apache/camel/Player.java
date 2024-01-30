package org.apache.camel;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Player {
    @JsonProperty
    private String _id;
    @JsonProperty
    private String club;
    @JsonProperty
    private String status;
    @JsonProperty
    private String firstname;
    @JsonProperty
    private String lastname;
    @JsonProperty
    private Integer year;
    @JsonProperty
    private String gender;
    @JsonProperty
    private String fide;
    @JsonProperty
    private String title;
    @JsonProperty
    private String avatar;

    public Player(String _id, String club, String status, String firstname, String lastname, Integer year, String gender, String fide, String title, String avatar) {
        this._id = _id;
        this.club = club;
        this.status = status;
        this.firstname = firstname;
        this.lastname = lastname;
        this.year = year;
        this.gender = gender;
        this.fide = fide;
        this.title = title;
        this.avatar = avatar;
    }

    @SuppressWarnings("unchecked")
    public static List<Player> extractPlayers(Exchange exchange) {
        List<Player> players = new ArrayList<>();
        Map<String, Object> body = exchange.getIn().getBody(Map.class);
        List<Map<String, Object>> results = (List<Map<String, Object>>) body.get("pages");
        for (var result : results) {
            Map<String, Object> club = (Map<String, Object>) result.get("club");
            players.add(new Player(
                    (String) result.get("_id"),
                    (String) club.get("name"),
                    (String) result.get("status"),
                    (String) result.get("firstname"),
                    (String) result.get("lastname"),
                    (Integer) result.get("year"),
                    (String) result.get("gender"),
                    (String) result.get("fide"),
                    (String) result.get("title"),
                    (String) result.get("avatar")
            ));
        }
        return players;
    }

    public static List<Player> filterPlayers(List<Player> players, String filterType, String filterValue) {
        List<Player> filteredPlayers = new ArrayList<>();

        if ("search".equals(filterType)) {
            for (Player player : players) {
                if (player.getFirstname().toLowerCase().contains(filterValue.toLowerCase())
                        || player.getLastname().toLowerCase().contains(filterValue.toLowerCase())) {
                    filteredPlayers.add(player);
                }
            }
        } else if ("gender".equals(filterType)) {
            for (Player player : players) {
                if (player.getGender().equalsIgnoreCase(filterValue)) {
                    filteredPlayers.add(player);
                }
            }
        } else {
            filteredPlayers.addAll(players);
        }

        return filteredPlayers;
    }

    public String get_id() {
        return _id;
    }

    public String getClub() {
        return club;
    }

    public String getStatus() {
        return status;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public Integer getYear() {
        return year;
    }

    public String getGender() {
        return gender;
    }

    public String getFide() {
        return fide;
    }

    public String getTitle() {
        return title;
    }

    public String getAvatar() {
        return avatar;
    }

    @Override
    public String toString() {
        return "Player{" +
                "_id='" + _id + '\'' +
                ", club='" + club + '\'' +
                ", status='" + status + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", year=" + year +
                ", gender='" + gender + '\'' +
                ", fide='" + fide + '\'' +
                ", title='" + title + '\'' +
                ", avatar='" + avatar + '\'' +
                '}';
    }
}
