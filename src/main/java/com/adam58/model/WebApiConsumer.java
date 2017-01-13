package com.adam58.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

/**
 * @author Adam Gapiński
 */
class WebApiConsumer {
    private String webApiUrl = "https://api-v3.mojepanstwo.pl/dane/";

    Representative parseJsonToRepresentative(String line) {
        Representative representative = new Representative();

        JsonElement jsonElement = new com.google.gson.JsonParser().parse(line);
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        parseAndSetBasicInfo(representative, jsonObject);

        JsonObject layers = jsonObject.getAsJsonObject("layers");

        parseAndSetExpenses(representative, layers);
        parseAndSetBusinessTrips(representative, layers);

        return representative;
    }

    private void parseAndSetBasicInfo(Representative representative, JsonObject jsonObject) {
        JsonObject data = jsonObject.getAsJsonObject("data");
        representative.setName(data.getAsJsonPrimitive("poslowie.imie_pierwsze").getAsString());
        representative.setSecondName(data.getAsJsonPrimitive("poslowie.imie_drugie").getAsString());
        representative.setSurname(data.getAsJsonPrimitive("poslowie.nazwisko").getAsString());
        representative.setNameDopelniacz(data.getAsJsonPrimitive("poslowie.dopelniacz").getAsString());

        for(JsonElement termJson : data.getAsJsonArray("poslowie.kadencja")) {
            representative.addTerm(termJson.getAsInt());
        }
    }

    private void parseAndSetExpenses(Representative representative, JsonObject layers) {
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
    }

    private void parseAndSetBusinessTrips(Representative representative, JsonObject layers) {
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
    }


    BufferedReader getJsonBufferedReaderForId(int id) throws IOException {
        String urlString = String.format("%sposlowie/%d.json?layers[]=wydatki&layers[]=wyjazdy", webApiUrl, id);

        return getJsonBufferedReaderForUrl(urlString);
    }

    BufferedReader getJsonBufferedReaderForUrl(String urlString) throws IOException{
        URL url = new URL(urlString);
        InputStream contentStream = (InputStream) url.getContent();

        return new BufferedReader(new InputStreamReader(contentStream));
    }

    String termOfOfficeToUrlParameter(int termOfOffice) {
        return String.format("conditions[poslowie.kadencja]=%d", termOfOffice);
    }

    String getUrlStringForPage(int page) {
        return String.format("%sposlowie.json?_type=objects&page=%d", webApiUrl, page);
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
