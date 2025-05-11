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
import static util.OrderGenerate.*;
import static util.UserGenerator.getRandomUser;


public class CreateOrderTest {
    public static final String EMPTY_TOKEN = "";

    private ValidatableResponse createUserResponse;
    private UserApi userApi;
    private UserCredential userCredential;
    private Gson gson;
    private OrderApi orderApi;
    private Ingredients ingredients;


    @Before
    public void init() {
        User user = getRandomUser();
        userApi = new UserApi();
        IngredientsApi ingredientsApi = new IngredientsApi();
        gson = new Gson();
        orderApi = new OrderApi();
        createUserResponse = userApi.createUser(user);
        userCredential = getUserCredentialByResponse(createUserResponse);
        ingredients = getIngredientsResponse(ingredientsApi.getIngredients());
    }

    @Test
    @DisplayName("Create order with ingredients and with valid token")
    public void createOrderWithIngredientsAndTokenReturn200OkTest() {
        IngredientsHashList ingredientsHashList = getRandomIngredientsList(ingredients);
        ValidatableResponse createOrderResponse = orderApi.createOrder(ingredientsHashList,
                userCredential.getAccessToken());
        createOrderResponse.log().all()
                .assertThat()
                .statusCode(SC_OK)
                .body("success", is(true))
                .body("name", notNullValue())
                .body("order", notNullValue())
                .body("order.ingredients", notNullValue());
    }

    @Test
    @DisplayName("Create order without ingredients and with valid token")
    public void createOrderNoIngredientsAndTokenReturn400BadRequestTest() {
        IngredientsHashList emptyIngredientsHashList = getEmptyIngredientsList();
        ValidatableResponse createOrderResponse = orderApi.createOrder(emptyIngredientsHashList,
                userCredential.getAccessToken());
        createOrderResponse.log().all()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body("success", is(false))
                .body("message", is("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Create order with ingredients and without token")
    public void createOrderWithIngredientsAndNoTokenReturn200OkTest() {
        IngredientsHashList ingredientsHashList = getRandomIngredientsList(ingredients);
        ValidatableResponse createOrderResponse = orderApi.createOrder(ingredientsHashList, EMPTY_TOKEN);
        createOrderResponse.log().all()
                .assertThat()
                .statusCode(SC_OK)
                .body("success", is(true))
                .body("name", notNullValue())
                .body("order", notNullValue());
    }

    @Test
    @DisplayName("Create order without ingredients and without valid token")
    public void createOrderNoIngredientsAndNoTokenReturn400BadRequestTest() {
        IngredientsHashList emptyIngredientsHashList = getEmptyIngredientsList();
        ValidatableResponse createOrderResponse = orderApi.createOrder(emptyIngredientsHashList,
                EMPTY_TOKEN);
        createOrderResponse.log().all()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body("success", is(false))
                .body("message", is("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Create order without incorrect ingredients and with valid token")
    public void createOrderIncorrectIngredients500InternalServerErrorTest() {
        IngredientsHashList incorrectIngredientsHashList = getIncorrectIngredientsList();
        ValidatableResponse createOrderResponse = orderApi.createOrder(incorrectIngredientsHashList,
                userCredential.getAccessToken());
        createOrderResponse.log().all()
                .assertThat()
                .statusCode(SC_INTERNAL_SERVER_ERROR);
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
