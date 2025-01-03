package com.example.search.model;

import java.util.List;

public class Shop {
    private String category; // ジャンル
    private String name;     // 店名
    private String type;     // タイプ (例: 高級、カジュアル)
    private String description; // 説明
    private String address;  // 住所
    private double latitude; // 緯度
    private double longitude; // 経度
    private List<String> keywords; // キーワード

    // コンストラクタ
    public Shop(String category, String name, String type, String description, String address, double latitude, double longitude, List<String> keywords) {
        this.category = category;
        this.name = name;
        this.type = type;
        this.description = description;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.keywords = keywords;
    }

    // ゲッターメソッド
    public String getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public String getAddress() {
        return address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    // オーバーライド toString メソッド
    @Override
    public String toString() {
        return "店名: " + name + "\n" +
                "ジャンル: " + category + "\n" +
                "タイプ: " + type + "\n" +
                "説明: " + description + "\n" +
                "住所: " + address;
    }
}
