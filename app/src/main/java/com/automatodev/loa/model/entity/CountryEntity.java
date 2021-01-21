package com.automatodev.loa.model.entity;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public class CountryEntity {
    private String sigla;
    private int id;

    public CountryEntity(String sigla, int id) {
        this.sigla = sigla;
        this.id = id;
    }

    public static List<CountryEntity> populateUf(){
        List<CountryEntity> entities = new ArrayList<>();
        entities.add(new CountryEntity("AC",12));
        entities.add(new CountryEntity("AL",28));
        entities.add(new CountryEntity("AP",16));
        entities.add(new CountryEntity("AM",13));
        entities.add(new CountryEntity("BA",29));
        entities.add(new CountryEntity("CE",23));
        entities.add(new CountryEntity("DF",53));
        entities.add(new CountryEntity("ES",32));
        entities.add(new CountryEntity("GO",52));
        entities.add(new CountryEntity("MA",21));
        entities.add(new CountryEntity("MT",51));
        entities.add(new CountryEntity("MS",50));
        entities.add(new CountryEntity("MG",31));
        entities.add(new CountryEntity("PA",15));
        entities.add(new CountryEntity("PB",25));
        entities.add(new CountryEntity("PR",41));
        entities.add(new CountryEntity("PE",26));
        entities.add(new CountryEntity("PI",22));
        entities.add(new CountryEntity("RJ",33));
        entities.add(new CountryEntity("RN",24));
        entities.add(new CountryEntity("RS",43));
        entities.add(new CountryEntity("RO",11));
        entities.add(new CountryEntity("RR",14));
        entities.add(new CountryEntity("SC",42));
        entities.add(new CountryEntity("SP",35));
        entities.add(new CountryEntity("SE",28));
        entities.add(new CountryEntity("TO",17));
        return entities;

    }

}
