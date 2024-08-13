package com.arthurpv15.apimanagement.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

import com.arthurpv15.apimanagement.enums.IncomeStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;


@Entity
@Table(name = "tb_receita")
public class Income implements Serializable{
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    /*
     * Serve to generate ID in the moment of adding entity in the databases.
     */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private Double value;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy HH:mm:ss", timezone = "GMT-3")
    private Instant date;
    private Integer status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @ManyToOne
    @JoinColumn(name = "category_income_id")
    private Category categoryIncome;

    public Income() {
    }
    
    public Income(String title, Double value, Instant date, IncomeStatus status, User user, Category categoryIncome){
        super();
        this.title = title;
        this.value = value;
        this.date = date;
        setStatus(status);
        this.user = user;
        this.categoryIncome = categoryIncome;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public IncomeStatus getStatus() {
        return IncomeStatus.valueOfCode(status);
    }

    public void setStatus(IncomeStatus status) {
        if(status != null){
            this.status = status.getCode();
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Category getCategoryIncome() {
        return categoryIncome;
    }

    public void setCategoryIncome(Category categoryIncome) {
        this.categoryIncome = categoryIncome;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Income income = (Income) obj;
        return id.equals(income.id);
        
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}