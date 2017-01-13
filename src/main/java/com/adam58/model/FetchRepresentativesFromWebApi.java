package com.adam58.model;

import com.google.gson.*;
import com.google.gson.annotations.SerializedName;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Adam Gapiński
 */
public class FetchRepresentativesFromWebApi implements IFetchRepresentativesData {
    private String webApiUrl = "https://api-v3.mojepanstwo.pl/dane/";


    @Override
    public List<Representative> fetchRepresentativesByTermOfOffice(int termOfOffice) {
        String conditions = termOfOfficeToUrlParameter(termOfOffice);
        return fetchRepsByConditionsAndFilter(conditions, (x) -> true);
    }

    @Override
    public Representative fetchRepresentativeByName(String name,
                                                    String surname) {
        String nameUpper = name.toUpperCase();
        String surnameUpper = surname.toUpperCase();

        List<Representative> representatives = fetchRepsByConditionsAndFilter("",
                repInfo -> repInfo.data.name.toUpperCase().equals(nameUpper) &&
                        repInfo.data.surname.toUpperCase().equals(surnameUpper) &&
                        (repInfo.data.secondName == null || repInfo.data.secondName.equals("")));

        if (representatives.size() == 0) {
            throw new RepresentativeNotFoundException(String.format("Representative for given name: %s %s not found",
                    name, surname));
        } else {
            return representatives.get(0);
        }
    }

    @Override
    public Representative fetchRepresentativeByName(String name,
                                                    String secondName,
                                                    String surname) {
        String nameUpper = name.toUpperCase();
        String surnameUpper = surname.toUpperCase();
        String secondNameUpper = secondName.toUpperCase();

        List<Representative> representatives = fetchRepsByConditionsAndFilter("",
                repInfo -> repInfo.data.name.toUpperCase().equals(nameUpper) &&
                        repInfo.data.surname.toUpperCase().equals(surnameUpper) &&
                        repInfo.data.secondName.toUpperCase().equals(secondNameUpper));

        if (representatives.size() == 0) {
            throw new RepresentativeNotFoundException(String.format("Representative for given name: %s %s %s not found",
                    name, secondName, surname));
        }

        return representatives.get(0);
    }

    private List<Representative> fetchRepsByConditionsAndFilter(
            String conditions,
            Predicate<? super JsonDataObject.RepresentativeBasicInfo> filter) {
        List<Representative> representatives = new CopyOnWriteArrayList<>();


        IntStream.range(1,16).parallel().forEach(page -> {
            try {
                String urlString = String.format("%s&%s", getUrlStringForPage(page), conditions);
                BufferedReader reader = getJsonBufferedReaderForUrl(urlString);

                Gson gson = new Gson();

                String line;

                while ((line = reader.readLine()) != null) {

                    JsonDataObject dto = gson.fromJson(line, JsonDataObject.class);

                    if (dto != null) {
                        representatives.addAll(parallelRequestIds(dto.Dataobject
                                .stream()
                                .filter(filter)
                                .map(idDTO -> idDTO.id)
                                .collect(Collectors.toList())));
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return representatives;
    }

    private String termOfOfficeToUrlParameter(int termOfOffice) {
        return String.format("conditions[poslowie.kadencja]=%d", termOfOffice);
    }

    private String getUrlStringForPage(int page) {
        return String.format("%sposlowie.json?_type=objects&page=%d", webApiUrl, page);
    }

    private List<Representative> parallelRequestIds(List<Integer> representativeIds) {
        ExecutorService serviceRequestRepId = Executors.newCachedThreadPool();
        List<Representative> representatives = new CopyOnWriteArrayList<>();

        representativeIds.parallelStream()
                .forEach((id) -> serviceRequestRepId.submit(() -> {
                    try {
                        representatives.add(fetchRepresentative(id));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }));

        waitForExecutorServices(serviceRequestRepId);

        return representatives;
    }

    private void waitForExecutorServices(ExecutorService servicePageRequest) {
        try {
            servicePageRequest.shutdown();
            servicePageRequest.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Representative fetchRepresentative(int id) throws IOException {
        BufferedReader jsonReader = getJsonBufferedReaderForId(id);
        String line;

        Representative representative = null;

        if ((line = jsonReader.readLine()) != null) {
            representative = parseJsonToRepresentative(line);
        }

        return representative;
    }

    private Representative parseJsonToRepresentative(String line) {
        Representative representative = new Representative();

        JsonElement jsonElement = new JsonParser().parse(line);

        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonObject data = jsonObject.getAsJsonObject("data");
        representative.setName(data.getAsJsonPrimitive("poslowie.imie_pierwsze").getAsString());
        representative.setSecondName(data.getAsJsonPrimitive("poslowie.imie_drugie").getAsString());
        representative.setSurname(data.getAsJsonPrimitive("poslowie.nazwisko").getAsString());
        representative.setNameDopelniacz(data.getAsJsonPrimitive("poslowie.dopelniacz").getAsString());

        for(JsonElement termJson : data.getAsJsonArray("poslowie.kadencja")) {
            representative.addTerm(termJson.getAsInt());
        }

        List<Integer> termsOfOffice = new ArrayList<>();

        for (JsonElement e : data.getAsJsonArray("poslowie.kadencja")) {
            termsOfOffice.add(e.getAsInt());
        }

        JsonObject layers = jsonObject.getAsJsonObject("layers");
        JsonObject expenses = layers.getAsJsonObject("wydatki");
        JsonArray punkty = expenses.getAsJsonArray("punkty");

        int number = 0;
        for (JsonElement e : punkty) {
            JsonObject tuple = e.getAsJsonObject();
            String title = tuple.getAsJsonPrimitive("tytul").getAsString();

            if (title.equals("Koszty drobnych napraw i remontów lokalu biura poselskiego")) {
                number = tuple.getAsJsonPrimitive("numer").getAsInt();
            }
        }
        JsonArray roczniki = expenses.getAsJsonArray("roczniki");

        double renovationExpenses = 0;
        double totalExpenses = 0;

        if (number != 0) {
            for (JsonElement e : roczniki) {
                JsonArray pola = e.getAsJsonObject().getAsJsonArray("pola");
                renovationExpenses += pola.get(number - 1).getAsDouble();

                for (JsonElement p : pola) {
                    totalExpenses += p.getAsDouble();
                }
            }
        }

        representative.setMinorRenovationExp(renovationExpenses);
        representative.setTotalExpenses(totalExpenses);

        JsonElement businessTripsEl = layers.get("wyjazdy");

        if (businessTripsEl.isJsonArray()) {

            JsonArray businessTrips = businessTripsEl.getAsJsonArray();

            for(JsonElement e : businessTrips) {
                JsonObject jsonTrip = e.getAsJsonObject();

                BusinessTrip businessTrip = new BusinessTrip();
                businessTrip.setCountry(jsonTrip.getAsJsonPrimitive("kraj").getAsString());
                businessTrip.setDays(jsonTrip.getAsJsonPrimitive("liczba_dni").getAsInt());

                businessTrip.setTotalExpense(jsonTrip.getAsJsonPrimitive("koszt_suma").getAsDouble());

                representative.addBusinessTrip(businessTrip);
            }
        }

        return representative;
    }


    private BufferedReader getJsonBufferedReaderForId(int id) throws IOException {
        String urlString = new StringBuilder()
                .append(webApiUrl)
                .append("poslowie/")
                .append(id)
                .append(".json")
                .append("?layers[]=wydatki")
                .append("&layers[]=wyjazdy")
                .toString();

        return getJsonBufferedReaderForUrl(urlString);
    }

    private BufferedReader getJsonBufferedReaderForUrl(String urlString) throws IOException{
        URL url = new URL(urlString);
        InputStream contentStream = (InputStream) url.getContent();

        return new BufferedReader(new InputStreamReader(contentStream));
    }
}

class JsonDataObject {
    class RepresentativeBasicInfo {
        class Data {
            @SerializedName("poslowie.imie_pierwsze")
            public String name;

            @SerializedName("poslowie.imie_drugie")
            public String secondName;

            @SerializedName("poslowie.nazwisko")
            public String surname;
        }

        public int id;
        public Data data;
    }

    public List<RepresentativeBasicInfo> Dataobject;
}
