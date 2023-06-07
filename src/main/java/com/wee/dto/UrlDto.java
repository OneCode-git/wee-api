package com.wee.dto;

import com.wee.entity.Url;

public class UrlDto {
    private Url url;
    private String metadata;

    public Url getUrl() {
        return url;
    }
    public void setUrl(Url url) {
        this.url = url;
    }
    public String getMetadata() {
        return metadata;
    }
    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

}