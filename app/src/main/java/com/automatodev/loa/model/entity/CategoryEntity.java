package com.automatodev.loa.model.entity;

import com.automatodev.loa.R;

import java.util.ArrayList;

import lombok.Getter;

@Getter
public class CategoryEntity {
    private int image;
    private String option;

    public CategoryEntity(int image, String option) {
        this.image = image;
        this.option = option;
    }

    public static ArrayList<CategoryEntity> getCategory(){
        ArrayList<CategoryEntity> spinnerOptions = new ArrayList<>();
        spinnerOptions.add(new CategoryEntity(R.drawable.ic_eletronic,"Eletrônicos"));
        spinnerOptions.add(new CategoryEntity(R.drawable.ic_computer,"Informatica"));
        spinnerOptions.add(new CategoryEntity(R.drawable.ic_work,"Serviços"));
        spinnerOptions.add(new CategoryEntity(R.drawable.ic_business,"Imoveis"));
        spinnerOptions.add(new CategoryEntity(R.drawable.ic_car,"Veiculos"));
        spinnerOptions.add(new CategoryEntity(R.drawable.ic_eletro,"Eletrodomesticos"));
        spinnerOptions.add(new CategoryEntity(R.drawable.ic_home,"Para sua casa"));

        return spinnerOptions;
    }
}
