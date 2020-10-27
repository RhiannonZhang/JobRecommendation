package com.laioffer.job.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

// History的reference是favorite
public class HistoryRequestBody {
    @JsonProperty("user_id")
    // 哪个user点的favorite
    public String userId;
    // 点的favorite的item是什么
    public Item favorite;
}
