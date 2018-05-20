package com.example.frank.final_project.Model;

/**
 * Created by Frank on 2018/4/12.
 */

public class Cake {

    private int id;
    private String name;
    private int rank;
    private String size;
    private String description;
    private String ingredients;
    private int price;

    public Cake(){

    }

    public Cake(int id, String name, int rank, String size, String description, String ingredients, int price){
        this.id = id;
        this.name = name;
        this.rank = rank;
        this.size = size;
        this.description = description;
        this.ingredients = ingredients;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
