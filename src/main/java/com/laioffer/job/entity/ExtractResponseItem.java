package com.laioffer.job.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
// Do not need all the properties in the response
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExtractResponseItem {
    public List<Extraction> extractions;
}
