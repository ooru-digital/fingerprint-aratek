package com.aratek.trustfinger.bean;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by hecl on 2018/9/22.
 */

public class User implements Serializable, Comparable<User> {
    private int rank;
    private String id;
    private String firstName;
    private String lastName;
    private FingerData fingerData;
    private int similarity;

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getSimilarity() {
        return similarity;
    }

    public void setSimilarity(int similarity) {
        this.similarity = similarity;
    }

    public User() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public FingerData getFingerData() {
        return fingerData;
    }

    public void setFingerData(FingerData fingerData) {
        this.fingerData = fingerData;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", fingerData=" + fingerData +
                '}';
    }

    @Override
    public int compareTo(@NonNull User o) {
        return o.getSimilarity() - this.getSimilarity();
    }
}
