package org.apache.camel;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
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
    @JsonProperty
    private Integer age;
    @JsonProperty
    private Boolean canCheckInAlone;

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
            String avatar = (String) result.get("avatar");
            if (avatar == null) {
                avatar = "https://chesscoders.fra1.digitaloceanspaces.com/frsah-portal/default-avatar.webp";
            }
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
                    avatar));
        }
        return players;
    }

    @SuppressWarnings("unchecked")
    public static Player extractPlayer(Exchange exchange) {
        Map<String, Object> body = exchange.getIn().getBody(Map.class);
        Map<String, Object> club = (Map<String, Object>) body.get("club");
        String avatar = (String) body.get("avatar");
        if (avatar == null) {
            avatar = "https://chesscoders.fra1.digitaloceanspaces.com/frsah-portal/default-avatar.webp";
        }
        return new Player(
                (String) body.get("_id"),
                (String) club.get("name"),
                (String) body.get("status"),
                (String) body.get("firstname"),
                (String) body.get("lastname"),
                (Integer) body.get("year"),
                (String) body.get("gender"),
                (String) body.get("fide"),
                (String) body.get("title"),
                avatar);
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

    public void setAge(Integer age) {
        this.age = age;
    }

    public void setCanCheckInAlone(Boolean canCheckInAlone) {
        this.canCheckInAlone = canCheckInAlone;
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

    public void calculateAgeAndCheckInStatus() {
        if (this.year != null) {
            this.age = LocalDate.now().getYear() - this.year;
            this.canCheckInAlone = this.age >= 18;
        } else {
            this.age = null;
            this.canCheckInAlone = null;
        }
    }
}
