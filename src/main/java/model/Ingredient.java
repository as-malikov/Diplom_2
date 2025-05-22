package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
@Getter
public class Ingredient {
    private String _id;
    private String name;
    private String type;
    private int proteins;
    private int fat;
    private int carbohydrates;
    private int calories;
    private int price;
    private String image;
    private String image_mobile;
    private String image_large;
    private String __v;
}
