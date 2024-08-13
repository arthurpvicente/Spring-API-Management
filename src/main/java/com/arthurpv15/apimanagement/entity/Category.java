package com.arthurpv15.apimanagement.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "tb_category")
public class Category implements Serializable{
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    /*
     * Serve to generate ID in the moment of adding the entity in the databases.
     */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;

    @OneToMany(mappedBy = "categoryIncome")
    @JsonIgnore
    private List<Income> incomes = new ArrayList<>();
    @OneToMany(mappedBy = "categoryOutgoing")
    @JsonIgnore
    private List<Outgoing> outgoings = new ArrayList<>();

    public Category(){
    }

    public Category(String title){
        this.title = title;
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

    public List<Income> getIncomes() {
        return incomes;
    }

    public List<Outgoing> getOutcomes() {
        return outgoings;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() !=obj.getClass())
            return false;
            Category category = (Category) obj;
            return id.equals(category.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }   
}