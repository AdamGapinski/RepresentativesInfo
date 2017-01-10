package com.adam58.model;

import java.util.List;

/**
 * @author Adam Gapiński
 */
public interface IFetchRepresentativesData {
    List<Representative> fetchRepresentativesByTermOfOffice(int termOfOffice);
    Representative fetchRepresentativeByName(int termOfOffice, String names, String surname) throws Exception;
}
