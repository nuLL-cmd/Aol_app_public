package com.automatodev.loa.model.entity;

import java.io.Serializable;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ImageEntity implements Serializable {
    private Long idImage;
    private String urlImage;
    private String name;


    public ImageEntity(Long idImage, String urlImage, String name) {
        this.idImage = idImage;
        this.urlImage = urlImage;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImageEntity)) return false;
        ImageEntity that = (ImageEntity) o;
        return getIdImage().equals(that.getIdImage());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdImage());
    }

    /*

    public Long getIdImage() {
        return idImage;
    }

    public void setIdImage(Long idImage) {
        this.idImage = idImage;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }
*/


}
