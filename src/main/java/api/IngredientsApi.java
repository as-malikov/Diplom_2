package api;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class IngredientsApi extends RestApi {
    public static final String API_INGREDIENTS = "/api/ingredients";

    @Step("Get ingredients list")
    public ValidatableResponse getIngredients() {
        return given().spec(requestSpecification())
                .when()
                .get(API_INGREDIENTS)
                .then();
    }
}
