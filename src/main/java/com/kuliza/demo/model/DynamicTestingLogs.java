package com.kuliza.demo.model;

import javax.persistence.*;

@Entity
public class DynamicTestingLogs {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="testing_id")
    private Long testingId;


    @Column(name="policy_name")
    private String policy_name;

    @Column(name="risk_id")
    private Long risk_id;

    @Column(name="date")
    String date;

    @Column(name="time")
    String time;

    @Column(name="user_name")
    String user_name;

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public Long getTestingId() {
        return testingId;
    }

    public void setTestingId(Long testingId) {
        this.testingId = testingId;
    }

    public String getPolicy_name() {
        return policy_name;
    }

    public void setPolicy_name(String policy_name) {
        this.policy_name = policy_name;
    }

    public Long getRisk_id() {
        return risk_id;
    }

    public void setRisk_id(Long risk_id) {
        this.risk_id = risk_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
