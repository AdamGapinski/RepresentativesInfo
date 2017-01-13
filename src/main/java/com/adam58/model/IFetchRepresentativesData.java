package com.adam58.model;

import java.util.List;

/**
 * @author Adam Gapiński
 */
public interface IFetchRepresentativesData {
    List<Representative> fetchRepresentativesByTermOfOffice(int termOfOffice);
    Representative fetchRepresentativeByName(String name, String surname) throws Exception;
    Representative fetchRepresentativeByName(String name, String secondName, String surname) throws Exception;
}
