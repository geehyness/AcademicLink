package com.yukicide.theacademiclinkandroid.Repositories.Models.Notes;

import java.util.ArrayList;
import java.util.Date;

public class NotesModel {
    public NotesModel(String name, String details, String subjectId, Date uploaded, ArrayList<AttachmentModel> documents, String teacherID) {
        this.name = name;
        this.details = details;
        this.subjectId = subjectId;
        this.uploaded = uploaded;
        this.documents = documents;
        this.teacherID = teacherID;
    }

    public NotesModel() {
    }

    public String getTeacherID() { return teacherID; }

    public void setTeacherID(String teacherID) { this.teacherID = teacherID; }

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

    public ArrayList<AttachmentModel> getDocuments() {
        return documents;
    }

    public void setDocuments(ArrayList<AttachmentModel> documents) {
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
    private ArrayList<AttachmentModel> documents = new ArrayList<>();
    private String teacherID;
}
