package com.adam58.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
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
    public Representative fetchRepresentativeByName(int termOfOffice,
                                                    String names,
                                                    String surname) throws Exception {
        String namesUpper = names.toUpperCase();
        String surnameUpper = surname.toUpperCase();

        List<Representative> representatives = fetchRepsByConditionsAndFilter(termOfOfficeToUrlParameter(termOfOffice),
                repInfo -> {
                    return repInfo.names.toUpperCase().equals(namesUpper) &&
                            repInfo.surname.toUpperCase().equals(surnameUpper);
                });

        if (representatives.size() == 0) {
            throw new Exception();
        }

        return representatives.get(0);
    }

    private List<Representative> fetchRepsByConditionsAndFilter(
            String conditions,
            Predicate<? super DataObject.RepresentativeBasicInfo> filter) {
        ExecutorService servicePageRequest = Executors.newCachedThreadPool();
        List<Representative> representatives = new CopyOnWriteArrayList<>();


        IntStream.range(1,16).parallel().forEach(page -> {
            try {
                String urlString = String.format("%s&%s", getUrlStringForPage(page), conditions);
                BufferedReader reader = getJsonBufferedReaderForUrl(urlString);

                Gson gson = new Gson();

                String line;
                while ((line = reader.readLine()) != null) {

                    DataObject dto = gson.fromJson(line, DataObject.class);
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

        waitForExecutorServices(servicePageRequest);

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
        RepresentativeDTO dto = null;

        while((line = jsonReader.readLine()) != null) {
            Gson gson = new Gson();
            dto = gson.fromJson(line, RepresentativeDTO.class);
        }

        return new Representative(dto);
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

class RepresentativeDTO {
    class Data {
        @SerializedName("poslowie.imie_pierwsze")
        public String name;

        @SerializedName("poslowie.imie_drugie")
        public String secondName;

        @SerializedName("poslowie.nazwisko")
        public String surname;

        @SerializedName("poslowie.imiona")
        public String names;

        @SerializedName("poslowie.kadencja")
        public List<Integer> termOfOffice;
    }

    @SerializedName("data")
    public Data data;

    class Layers {
        class Expenses {
            class Punkt {
                @SerializedName("tytul")
                public String title;
                @SerializedName("numer")
                public int number;
            }

            public List<Punkt> punkty;

            class Value {
                @SerializedName("pola")
                List<Integer> values;
            }

            @SerializedName("roczniki")
            public List<Value> values;

        }

        @SerializedName("wydatki")
        public Expenses expenses;
    }

    public Layers layers;
}

class DataObject {
    class RepresentativeBasicInfo {
        public int id;

        @SerializedName("poslowie.imiona")
        public String names;

        @SerializedName("poslowie.nazwisko")
        public String surname;
    }

    public List<RepresentativeBasicInfo> Dataobject;
}
