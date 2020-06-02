package com.yukicide.theacademiclinkandroid.Repositories.Models;

import java.util.ArrayList;
import java.util.Date;

public class NotesModel {
    public NotesModel(String name, String details, ArrayList<String> documents, String subjectId, Date uploaded) {
        this.name = name;
        this.details = details;
        this.documents = documents;
        this.subjectId = subjectId;
        this.uploaded = uploaded;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public ArrayList<String> getDocuments() {
        return documents;
    }

    public void setDocuments(ArrayList<String> documents) {
        this.documents = documents;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public Date getUploaded() {
        return uploaded;
    }

    public void setUploaded(Date uploaded) {
        this.uploaded = uploaded;
    }

    private String id;
    private String name;
    private String details;
    private String subjectId;
    private Date uploaded;
    private ArrayList<String> documents = new ArrayList<>();
}
