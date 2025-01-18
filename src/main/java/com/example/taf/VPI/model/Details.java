package com.example.taf.VPI.model;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import java.util.List;

@Embeddable
public class Details {

    @ElementCollection
    private List<String> common_names;
    private String name;

    private String url;
    private String description;
    private String image;

    @ElementCollection
    private List<String> synonyms;

    private String toxicity;

    public List<String> getCommon_names() {
        return common_names;
    }

    public void setCommon_names(List<String> common_names) {
        this.common_names = common_names;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<String> getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(List<String> synonyms) {
        this.synonyms = synonyms;
    }

    public String getToxicity() {
        return toxicity;
    }

    public void setToxicity(String toxicity) {
        this.toxicity = toxicity;
    }
}
