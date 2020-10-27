package com.laioffer.job.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ExtractRequestBody {
    //名字没换,所以不需要annotation
    public List<String> data;

    // 因为名字换了(从snake case换成了camel case)
    @JsonProperty("max_keywords")
    public int maxKeywords;

    public ExtractRequestBody(List<String> data, int maxKeywords) {
        this.data = data;
        this.maxKeywords = maxKeywords;
    }
}
