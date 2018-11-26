package com.example.andrewtran.superapp.models;

import java.util.ArrayList;

public class Talks {

    private String talkID;
    private ArrayList<Talk> talks;
    private ArrayList<String> people;
    public Talks(){
        talks = new ArrayList<>();
        people = new ArrayList<>();

    }
    public void addTalk (Talk talk){
        talks.add(talk);
    }
    public void addPeople (ArrayList <String> People){
        people = People;
    }

    public ArrayList<Talk> getTalks() {
        return talks;
    }

    public ArrayList<String> getPeople() {
        return people;
    }

    public String getTalkID() {
        return talkID;
    }

    public void setTalkID(String talkID) {
        this.talkID = talkID;
    }
}
