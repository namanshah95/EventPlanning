package com.example.naman.eventplanning;

import android.support.annotation.NonNull;

import java.util.Date;

/**
 * Created by Naman on 3/4/2017.
 */

public class Message implements Comparable{
    private String text;
    private String sender;
    private Date datetime;

    public Message(String text, String sender) {
        this(text, sender, new Date());
    }

    public Message(String text, String sender, Date datetime) {
        this.text = text;
        this.sender = sender;
        this.datetime = datetime;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        Message other = (Message) o;
        return this.datetime.compareTo(other.getDatetime());
    }
}
