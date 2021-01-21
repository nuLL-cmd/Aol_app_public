package com.automatodev.loa.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ItemEntity implements Parcelable, Comparable<ItemEntity> {

    private Long idAnnouncement;
    private String title;
    private String description;
        private String situation;
        private Double price;
    private String phone;
    private String uf;
    private String city;
    private Long dateCad;
    private int view;
    private UserEntity userEntity;
    private List<ImageEntity> images;
    private String uid;
    private String category;
    private Double lat;
    private Double lang;
    private String statusItem;

    public ItemEntity(Long idAnnouncement) {
        this.idAnnouncement = idAnnouncement;
    }

    public ItemEntity() {
    }

    protected ItemEntity(Parcel in) {
        idAnnouncement = in.readLong();
        title = in.readString();
        description = in.readString();
        situation = in.readString();
        price = in.readDouble();
        phone = in.readString();
        uf = in.readString();
        city = in.readString();
        dateCad = in.readLong();
        userEntity = in.readParcelable(UserEntity.class.getClassLoader());
        uid = in.readString();
        category = in.readString();
        lat = in.readDouble();
        lang = in.readDouble();
        statusItem = in.readString();
    }

    public static final Creator<ItemEntity> CREATOR = new Creator<ItemEntity>() {
        @Override
        public ItemEntity createFromParcel(Parcel in) {
            return new ItemEntity(in);
        }

        @Override
        public ItemEntity[] newArray(int size) {
            return new ItemEntity[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(idAnnouncement);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(situation);
        dest.writeDouble(price);
        dest.writeString(phone);
        dest.writeString(uf);
        dest.writeString(city);
        dest.writeLong(dateCad);
        dest.writeParcelable(userEntity, flags);
        dest.writeString(uid);
        dest.writeString((category));
        dest.writeDouble(lat);
        dest.writeDouble(lang);
        dest.writeString(statusItem);
    }

    @Override
    public int compareTo(ItemEntity o) {
        if (getDateCad() == null || o.getDateCad() == null){
            return 0;
        }
        return getDateCad().compareTo(o.getDateCad());
    }

}
