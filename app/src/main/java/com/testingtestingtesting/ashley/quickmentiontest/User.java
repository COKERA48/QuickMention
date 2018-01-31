package com.testingtestingtesting.ashley.quickmentiontest;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Ashley on 1/30/2018.
 */
@Entity
public class User {
    @Id
    private Long id;
    @NotNull
    private String email;
    @Generated(hash = 1339009088)
    public User(Long id, @NotNull String email) {
        this.id = id;
        this.email = email;
    }
    @Generated(hash = 586692638)
    public User() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getEmail() {
        return this.email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

}
