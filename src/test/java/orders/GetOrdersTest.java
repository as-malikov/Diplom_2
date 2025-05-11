package orders;

import api.IngredientsApi;
import api.OrderApi;
import api.UserApi;
import com.google.gson.Gson;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.Ingredients;
import model.IngredientsHashList;
import model.User;
import model.UserCredential;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static util.OrderGenerate.getRandomIngredientsList;
import static util.UserGenerator.getRandomUser;

public class GetOrdersTest {
    public static final String EMPTY_TOKEN = "";

    private ValidatableResponse createUserResponse;
    private UserApi userApi;
    private UserCredential userCredential;
    private Gson gson;
    private OrderApi orderApi;

    @Before
    public void init() {
        User user = getRandomUser();
        userApi = new UserApi();
        IngredientsApi ingredientsApi = new IngredientsApi();
        gson = new Gson();
        orderApi = new OrderApi();
        createUserResponse = userApi.createUser(user);
        userCredential = getUserCredentialByResponse(createUserResponse);
        Ingredients ingredients = getIngredientsResponse(ingredientsApi.getIngredients());
        IngredientsHashList ingredientsHashList = getRandomIngredientsList(ingredients);
        orderApi.createOrder(ingredientsHashList,
                userCredential.getAccessToken());
    }

    @Test
    @DisplayName("Get orders with valid token")
    public void getOrdersWithTokenReturn200OkTest() {
        ValidatableResponse getOrderResponse = orderApi.getOrder(userCredential.getAccessToken());
        getOrderResponse.log().all()
                .assertThat()
                .statusCode(SC_OK)
                .body("success", is(true))
                .body("total", notNullValue())
                .body("orders", notNullValue())
                .body("totalToday", notNullValue());
    }

    @Test
    @DisplayName("Get orders with valid token")
    public void getOrdersNoTokenReturn401UnauthorisedTest() {
        ValidatableResponse getOrderResponse = orderApi.getOrder(EMPTY_TOKEN);
        getOrderResponse.log().all()
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", is(false))
                .body("message", is("You should be authorised"));
    }

    @After
    public void deleteUser() {
        if (createUserResponse.extract().statusCode() == SC_OK) {
            ValidatableResponse deleteUserResponse = userApi.deleteUser(userCredential.getAccessToken());
            deleteUserResponse.log().all()
                    .assertThat()
                    .statusCode(SC_ACCEPTED);
        }
    }

    @Step("Get user credential")
    protected UserCredential getUserCredentialByResponse(ValidatableResponse response) {
        return gson.fromJson(response.extract().body().asString(), UserCredential.class);
    }

    @Step("Get ingredients list")
    protected Ingredients getIngredientsResponse(ValidatableResponse response) {
        return gson.fromJson(response.extract().body().asString(), Ingredients.class);
    }
}
