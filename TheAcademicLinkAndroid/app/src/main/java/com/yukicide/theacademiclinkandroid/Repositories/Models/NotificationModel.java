package com.yukicide.theacademiclinkandroid.Repositories.Models;

import com.yukicide.theacademiclinkandroid.Repositories.Fixed.NotificationRank;

import java.util.Date;

public class NotificationModel {
    String id, title, details;
    NotificationRank rank;
    boolean isEvent;
    Date date;

    public boolean isEvent() {
        return isEvent;
    }

    public void setEvent(boolean event) {
        isEvent = event;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    Date eventDate;

    public NotificationModel(String title, Date date, String details, NotificationRank rank) {
        this.title = title;
        this.date = date;
        this.details = details;
        this.rank = rank;
    }

    public NotificationModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public NotificationRank getRank() {
        return rank;
    }

    public void setRank(NotificationRank rank) {
        this.rank = rank;
    }
}
