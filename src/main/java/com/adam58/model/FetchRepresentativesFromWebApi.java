package com.adam58.model;

import com.google.gson.Gson;
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
public class FetchRepresentativesFromWebApi implements IFetchRepresentativesData {
    private String webApiUrl = "https://api-v3.mojepanstwo.pl/dane/";

    @Override
    public List<Representative> fetchRepresentativesByTermOfOffice(int termOfOffice) {

        return null;
    }

    @Override
    public List<Representative> fetchAllRepresentatives() {
        return null;
    }

    @Override
    public Representative fetchRepresentativeByName(String name, String surname) {
        return null;
    }


    private Representative fetchRepresentative(int id) throws IOException {
        BufferedReader jsonReader = getJsonBufferedReader(id);

        String line;
        RepresentativeDTO dto = null;

        while((line = jsonReader.readLine()) != null) {
            Gson gson = new Gson();
            dto = gson.fromJson(line, RepresentativeDTO.class);
        }

        return new Representative(dto);
    }

    private BufferedReader getJsonBufferedReader(int id) throws IOException {
        String urlString = new StringBuilder()
                .append("poslowie/")
                .append(id)
                .append(".json")
                .append("?layers[]=wydatki")
                .append("&layers[]=wyjazdy")
                .toString();

        URL url = new URL(urlString);
        Object content = url.getContent();

        InputStream contentStream = null;
        if (content instanceof InputStream) {
            contentStream = (InputStream) content;
        }

        return new BufferedReader(new InputStreamReader(contentStream));
    }
}

class RepresentativeDTO {
    @SerializedName("poslowie.imie_pierwsze")
    public String name;

    @SerializedName("poslowie.imie_drugie")
    public String secondName;

    @SerializedName("poslowie.nazwisko")
    public String surname;


    class Layers {
        class Expenses {
            class Tuple {
                @SerializedName("tytul")
                public String title;
                @SerializedName("numer")
                public int number;
            }

            @SerializedName("punkty")
            List<Tuple> tuples;
        }

        @SerializedName("wydatki")
        public Expenses expenses;

    }

    public Layers layers;
}
