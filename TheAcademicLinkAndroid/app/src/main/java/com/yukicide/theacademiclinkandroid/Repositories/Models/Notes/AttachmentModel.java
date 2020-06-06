package com.yukicide.theacademiclinkandroid.Repositories.Models.Notes;

public class AttachmentModel {
    private String url;
    private String name;
    private boolean doc;

    public AttachmentModel(String name, String url, boolean doc) {
        this.name = name;
        this.url = url;
        this.doc = doc;
    }

    public AttachmentModel() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isDoc() {
        return doc;
    }

    public void setDoc(boolean doc) {
        this.doc = doc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

