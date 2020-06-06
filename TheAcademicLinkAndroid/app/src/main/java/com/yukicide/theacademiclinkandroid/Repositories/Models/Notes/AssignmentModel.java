package com.yukicide.theacademiclinkandroid.Repositories.Models.Notes;

import java.util.Date;

public class AssignmentModel {
    private String id;
    private String name;
    private String details;
    private String subjectId;
    private Date date;
    private AttachmentModel documents;
    private String teacherID;

    public AssignmentModel(String name, String details, String subjectId, Date date, AttachmentModel documents, String teacherID) {
        this.name = name;
        this.details = details;
        this.subjectId = subjectId;
        this.date = date;
        this.documents = documents;
        this.teacherID = teacherID;
    }

    public AssignmentModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public AttachmentModel getDocuments() {
        return documents;
    }

    public void setDocuments(AttachmentModel documents) {
        this.documents = documents;
    }

    public String getTeacherID() {
        return teacherID;
    }

    public void setTeacherID(String teacherID) {
        this.teacherID = teacherID;
    }
}
