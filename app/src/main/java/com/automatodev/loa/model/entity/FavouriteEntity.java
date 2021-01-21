package com.automatodev.loa.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FavouriteEntity  implements Parcelable {
    private long idFavourite;
    private UserEntity userEntity;
    private ItemEntity announcementEntity;

    public FavouriteEntity(UserEntity userEntity, ItemEntity announcementEntity) {
        this.userEntity = userEntity;
        this.announcementEntity = announcementEntity;
    }

    protected FavouriteEntity(Parcel in) {
        idFavourite = in.readLong();
        userEntity = in.readParcelable(UserEntity.class.getClassLoader());
        announcementEntity = in.readParcelable(ItemEntity.class.getClassLoader());
    }

    public static final Creator<FavouriteEntity> CREATOR = new Creator<FavouriteEntity>() {
        @Override
        public FavouriteEntity createFromParcel(Parcel in) {
            return new FavouriteEntity(in);
        }

        @Override
        public FavouriteEntity[] newArray(int size) {
            return new FavouriteEntity[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(idFavourite);
        dest.writeParcelable(userEntity, flags);
        dest.writeParcelable(announcementEntity, flags);
    }
    @Override
    public int describeContents() {
        return 0;
    }

}
