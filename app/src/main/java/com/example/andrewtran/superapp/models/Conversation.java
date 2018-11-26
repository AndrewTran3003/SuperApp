package com.example.andrewtran.superapp.models;

public class Conversation {
    private String person;
    private String lastText;
    private String talkID;
    public Conversation (String TalkID, String Person, String LastText){
        talkID = TalkID;
        person = Person;
        lastText = LastText;
    }

    public String getPerson() {
        return person;
    }

    public String getLastText() {
        return lastText;

    }

    public String getTalkID() {
        return talkID;
    }

    public void setLastText(String lastText) {
        this.lastText = lastText;
    }
}


