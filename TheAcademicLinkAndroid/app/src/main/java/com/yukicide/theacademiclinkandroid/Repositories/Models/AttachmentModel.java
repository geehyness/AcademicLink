package com.yukicide.theacademiclinkandroid.Repositories.Models;

public class AttachmentModel {
    private String url;
    private boolean doc;

    public AttachmentModel(String url, boolean doc) {
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
}
