package util;

import io.qameta.allure.Step;
import model.Ingredients;
import model.IngredientsHashList;
import com.github.javafaker.Faker;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class OrderGenerate {

    public static final int INGREDIENTS_AMOUNT_4 = 4;
    private static final Faker faker = new Faker(new Locale("en"));

    @Step("Generate random ingredients hash list default")
    public static IngredientsHashList getRandomIngredientsList(Ingredients ingredients) {
        Random random = new Random();
        List<String> ingredientsHashList = new ArrayList<>(List.of());
        for (int i = 0; i < INGREDIENTS_AMOUNT_4; i++) {
            int randomIndex = random.nextInt(ingredients.getData().size());
            ingredientsHashList.add(ingredients.getData().get(randomIndex).get_id());
        }
        return new IngredientsHashList(ingredientsHashList);
    }

    @Step("Generate empty ingredients hash list default")
    public static IngredientsHashList getEmptyIngredientsList() {
        List<String> ingredientsHashList = new ArrayList<>(List.of());
        return new IngredientsHashList(ingredientsHashList);
    }

    @Step("Generate incorrect ingredients hash list default")
    public static IngredientsHashList getIncorrectIngredientsList() {
        List<String> ingredientsHashList = new ArrayList<>(List.of());
        for (int i = 0; i < INGREDIENTS_AMOUNT_4; i++) {
            String hashCode = faker.crypto().md5();
            ingredientsHashList.add(hashCode);
        }
        return new IngredientsHashList(ingredientsHashList);
    }
}
