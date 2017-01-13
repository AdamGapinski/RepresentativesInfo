package com.adam58.model;

import java.util.List;

/**
 * @author Adam Gapiński
 */
public interface WebApiRequester {
    List<Representative> fetchRepresentativesByTermOfOffice(int termOfOffice);
    Representative fetchRepresentativeByName(String name, String surname)
            throws RepresentativeNotFoundException;
    Representative fetchRepresentativeByName(String name, String secondName, String surname)
            throws RepresentativeNotFoundException;
}
