package com.masterisehomes.geometryapi.geojson;

import java.util.List;

import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashMap;

@ToString
public class Property<T> {
    private String key;
    private T value;

    public Property() {}
}