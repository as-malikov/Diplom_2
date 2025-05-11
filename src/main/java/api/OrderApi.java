package api;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import model.IngredientsHashList;

import static io.restassured.RestAssured.given;

public class OrderApi extends RestApi {
    public static final String API_ORDERS = "/api/orders";

    @Step("Create order")
    public ValidatableResponse createOrder(IngredientsHashList ingredientsHashList, String accessToken) {
        return given().spec(requestSpecification())
                .header("Authorization", accessToken)
                .body(ingredientsHashList)
                .when()
                .post(API_ORDERS)
                .then();
    }

    @Step("Get orders")
    public ValidatableResponse getOrder(String accessToken) {
        return given().spec(requestSpecification())
                .header("Authorization", accessToken)
                .when()
                .get(API_ORDERS)
                .then();
    }
}
