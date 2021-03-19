package com.kuliza.demo.model;

import com.kuliza.demo.implementations.Task;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "user_policy")
public class UserPolicy {

    @Id
    @Column(name = "policy_name")
    @NonNull
    private String policy_name;

    @Column(name = "user_name")
    private String user_name;

    @Column(name="status")
    private String status;

    @Column(name="enabled")
    @ColumnDefault("true")
    private boolean enabled;

    @Column(name="remedy_type")
    private String remedy_type;



    public UserPolicy(Long user_policyId, String policy_name, String user_name) {
        this.policy_name = policy_name;
        this.user_name = user_name;
    }
    public UserPolicy() {

    }

    public String getRemedy_type() {
        return remedy_type;
    }

    public void setRemedy_type(String remedy_type) {
        this.remedy_type = remedy_type;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPolicy_name() {
        return policy_name;
    }

    public void setPolicy_name(String policy_name) {
        this.policy_name = policy_name;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
}
