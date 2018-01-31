package com.testingtestingtesting.ashley.quickmentiontest;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.sql.Time;
import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Ashley on 1/31/2018.
 */

@Entity
public class Task {

    @Id
    private Long id;

    private String name;
    private Date startDate;
    //private Time startTime;
    private Date endDate;
    //private Time endTime;
    private Boolean isReoccurring;
    //need to add how often it will repeat
    private Long templateId;

    @Generated(hash = 638321980)
    public Task(Long id, String name, Date startDate, Date endDate,
            Boolean isReoccurring, Long templateId) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isReoccurring = isReoccurring;
        this.templateId = templateId;
    }
    @Generated(hash = 733837707)
    public Task() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Date getStartDate() {
        return this.startDate;
    }
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    public Date getEndDate() {
        return this.endDate;
    }
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    public Boolean getIsReoccurring() {
        return this.isReoccurring;
    }
    public void setIsReoccurring(Boolean isReoccurring) {
        this.isReoccurring = isReoccurring;
    }
    public Long getTemplateId() {
        return this.templateId;
    }
    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }
}
