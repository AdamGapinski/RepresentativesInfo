package com.adam58.model;

import com.google.gson.*;

import java.io.BufferedReader;
import java.io.IOException;
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
public class ParallelWebApiRequester implements WebApiRequester {
    private WebApiConsumer webApiConsumer = new WebApiConsumer();

    @Override
    public List<Representative> fetchRepresentativesByTermOfOffice(int termOfOffice) {
        String conditions = webApiConsumer.termOfOfficeToUrlParameter(termOfOffice);
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

        IntStream.range(1, 16).parallel().forEach(page -> {
            try {
                String urlString = String.format("%s&%s", webApiConsumer.getUrlStringForPage(page), conditions);
                BufferedReader reader = webApiConsumer.getJsonBufferedReaderForUrl(urlString);

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
        BufferedReader jsonReader = webApiConsumer.getJsonBufferedReaderForId(id);
        String line;

        Representative representative = null;

        if ((line = jsonReader.readLine()) != null) {
            representative = webApiConsumer.parseJsonToRepresentative(line);
        }

        return representative;
    }
}

