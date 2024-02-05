# Documentația Aplicației de Integrare Camel

## Cuprins

- [Metode și Implementări Specifice](#metode-și-implementări-specifice)
    - [onException(Exception.class)](#onexceptionexceptionclass)
    - [fromF și restConfiguration](#fromf-și-restconfiguration)
    - [rest("/players")](#restplayers)
    - [from("direct:searchPlayer")](#fromdirectsearchplayer)
    - [process, choice, when, to](#process-choice-when-to)
    - [convertBodyTo, unmarshal, json, bean, marshal](#convertbodyto-unmarshal-json-bean-marshal)
    - [aggregate](#aggregate)
    - [enrich](#enrich)
    - [split și filter](#split-și-filter)
    - [toD](#tod)
- [Aspecte Implementate](#aspecte-implementate)
    - [Utilizarea a cel puțin două tipuri de canale pentru comunicarea între aplicații](#utilizarea-a-cel-puțin-două-tipuri-de-canale-pentru-comunicarea-între-aplicații)
    - [Îmbogățirea conținutului, sortarea, filtrarea, agregarea mesajelor](#îmbogățirea-conținutului-sortarea-filtrarea-agregarea-mesajelor)
    - [Rutarea mesajelor între aplicații](#rutarea-mesajelor-între-aplicații)
    - [Transformarea structurii mesajelor](#transformarea-structurii-mesajelor)

## Metode și Implementări Specifice

### onException(Exception.class)

Această metodă este folosită pentru a gestiona excepțiile la nivelul aplicației. Oricare excepție de tipul `Exception`
este interceptată, procesată, și marcata ca "handled", prevenind propagarea ei.

### fromF și restConfiguration

Aceste metode configurează endpoint-uri HTTP pentru a expune API-uri și a primi cereri HTTP. `fromF` este folosit pentru
a defini un endpoint HTTP cu un URI configurabil, iar `restConfiguration` este utilizat pentru a seta host-ul, portul,
componenta și context path-ul pentru REST API.

### rest("/players")

Definește rute REST pentru diferite operații legate de jucători, cum ar fi căutarea, descărcarea și obținerea detaliilor
unui jucător.

### from("direct:searchPlayer")

Implementează un canal de tip punct la punct care preia mesajele din ruta `direct:searchPlayer` și folosește
un `choice()` pentru a determina ruta ulterioară pe baza unui header specific.

### process, choice, when, to

Aceste metode sunt folosite pentru a procesa mesaje, a implementa logica condițională, a filtra mesaje bazate pe
header-e și a direcționa mesajele către alte rute sau servicii externe.

### convertBodyTo, unmarshal, json, bean, marshal

Aceste metode sunt folosite pentru a transforma structura mesajelor. Convertesc body-ul mesajului în diferite formate,
deserializează JSON în obiecte Java, apelează metode pe bean-uri și serializează obiecte Java înapoi în JSON.

### aggregate

Metoda `aggregate` este folosită pentru a agregă mesajele folosind o strategie de agregare, cum ar
fi `GenderAggregationStrategy`, pentru a combina mesaje într-un singur mesaj care conține un rezumat sau o colecție de
date agregate.

### enrich

Metoda `enrich` este utilizată pentru a îmbogăți un mesaj cu date suplimentare. În exemplu, este folosită pentru a
calcula vârsta unui jucător și a determina dacă poate să se înregistreze singur.

### split și filter

Aceste metode sunt folosite pentru a împărți mesajele în mesaje mai mici și a filtra aceste mesaje în funcție de anumite
criterii, cum ar fi genul jucătorilor.

### toD

Metoda `toD` este o variantă dinamică a `to`, care permite construirea unui endpoint URI din expresii sau header-e din
mesaj.

## Aspecte Implementate

### Utilizarea a cel puțin două tipuri de canale pentru comunicarea între aplicații

- Canale de tip punct la punct: Sunt utilizate prin intermediul direct routes (`from("direct:...")`).
- Canale HTTP REST: Sunt configurate prin `restConfiguration` și `rest("/...")`.

### Îmbogățirea conținutului, sortarea, filtrarea, agregarea mesajelor

- Îmbogățirea: Realizată prin `enrich("direct:calculateAge", new AgeEnrichmentAggregationStrategy())`.
- Sortarea: Implementată în metodele `process` unde se sortează listele de jucători.
- Filtrarea: Realizată cu `filter` în `from("direct:countPlayersByGender")`.
- Agregarea: Efectuată cu `aggregate(new GenderAggregationStrategy())`.

### Rutarea mesajelor între aplicații

Mesajele sunt rute între diferite endpoint-uri folosind `to`, `toD` și rute directe (`from("direct:...")`).

### Transformarea structurii mesajelor

Transformarea se face cu `unmarshal().json(JsonLibrary.Jackson)` pentru a converti JSON în obiecte Java
și `marshal().json(JsonLibrary.Jackson)` pentru a converti obiectele Java înapoi în JSON.
