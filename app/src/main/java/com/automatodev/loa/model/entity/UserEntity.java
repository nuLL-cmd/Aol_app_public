package com.automatodev.loa.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserEntity implements Parcelable {

    private Long idUser;
    private String uid;
    private String firstName;
    private String lastName;
    private String phone;
    private String city;
    private String uf;
    private String email;
    private String urlPhoto;
    private long dateSince;
    private String catDefault;
    private double total;

    private List<ItemEntity> announces;

    public UserEntity(long idUser){
        this.idUser  = idUser;
    }
    public UserEntity(long idUser, String uid, String firstName
            , String lastName, String phone, String city, String uf, String email
            , String urlPhoto, long dateSince, String catDefault,double total, List<ItemEntity> announces) {
        this.idUser = idUser;
        this.uid = uid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.city = city;
        this.uf = uf;
        this.email = email;
        this.urlPhoto = urlPhoto;
        this.dateSince = dateSince;
        this.catDefault = catDefault;
        this.total = total;
        this.announces = announces;
    }
    public UserEntity(){

    }

    protected UserEntity(Parcel in) {
        idUser = in.readLong();
        uid = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        phone = in.readString();
        city = in.readString();
        uf = in.readString();
        email = in.readString();
        urlPhoto = in.readString();
        dateSince = in.readLong();
        catDefault = in.readString();
        total = in.readDouble();
        announces = in.createTypedArrayList(ItemEntity.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(idUser);
        dest.writeString(uid);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(phone);
        dest.writeString(city);
        dest.writeString(uf);
        dest.writeString(email);
        dest.writeString(urlPhoto);
        dest.writeLong(dateSince);
        dest.writeString(catDefault);
        dest.writeDouble(total);
        dest.writeTypedList(announces);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserEntity> CREATOR = new Creator<UserEntity>() {
        @Override
        public UserEntity createFromParcel(Parcel in) {
            return new UserEntity(in);
        }

        @Override
        public UserEntity[] newArray(int size) {
            return new UserEntity[size];
        }
    };

}

