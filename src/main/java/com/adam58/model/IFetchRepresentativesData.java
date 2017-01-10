package com.adam58.model;

import java.util.List;

/**
 * @author Adam Gapiński
 */
public interface IFetchRepresentativesData {
    List<Representative> fetchRepresentativesByTermOfOffice(int termOfOffice);
    List<Representative> fetchAllRepresentatives();
    Representative fetchRepresentativeByName(String name, String surname);
}
