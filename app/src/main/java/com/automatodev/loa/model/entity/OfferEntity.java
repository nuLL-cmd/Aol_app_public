package com.automatodev.loa.model.entity;

import java.util.Objects;

public class OfferEntity {
    private Long idOffer;
    private long dateOffer;
    private double price;
    private String status;
    private UserEntity user;
    private ItemEntity item;

    public OfferEntity(double price,String status, UserEntity user, ItemEntity item) {
        this.price = price;
        this.user = user;
        this.item = item;
        this.status = status;
    }

    public Long getIdOffer() {
        return idOffer;
    }

    public long getDateOffer() {
        return dateOffer;
    }

    public double getPrice() {
        return price;
    }

    public String getStatus(){
        return this.status;
    }
    public UserEntity getUser() {
        return user;
    }

    public ItemEntity getItem() {
        return item;
    }

    public void setIdOffer(Long idOffer) {
        this.idOffer = idOffer;
    }

    public void setDateOffer(long dateOffer) {
        this.dateOffer = dateOffer;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setStatus(String status){
        this.status = status;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public void setItem(ItemEntity item) {
        this.item = item;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OfferEntity)) return false;
        OfferEntity that = (OfferEntity) o;
        return getIdOffer().equals(that.getIdOffer());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdOffer());
    }

}
