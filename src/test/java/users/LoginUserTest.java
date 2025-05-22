package users;

import api.UserApi;
import com.google.gson.Gson;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.User;
import model.UserCredential;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.is;
import static util.UserGenerator.getRandomUser;

public class LoginUserTest {
    private ValidatableResponse createUserResponse;
    private UserApi userApi;
    private User user;
    private UserCredential userCredential;
    private Gson gson;

    @Before
    public void init() {
        user = getRandomUser();
        userApi = new UserApi();
        gson = new Gson();
        createUserResponse = userApi.createUser(user);
        userCredential = getUserCredentialByResponse(createUserResponse);
    }

    @Test
    @DisplayName("Get user token")
    public void loginUserByEmailAndPasswordReturn200OkTest() {
        ValidatableResponse getTokenResponse = userApi.loginUser(user).log().all()
                .assertThat()
                .statusCode(SC_OK)
                .body("success", is(true))
                .body("user.email", is(user.getEmail()))
                .body("user.name", is(user.getName()))
                .body("accessToken", CoreMatchers.notNullValue())
                .body("refreshToken", CoreMatchers.notNullValue());
        userCredential = getUserCredentialByResponse(getTokenResponse);
    }

    @Test
    @DisplayName("Get user token with incorrect user login and user password")
    public void loginUserByIncorrectEmailAndPasswordReturn401Unauthorized() {
        User newUser = getRandomUser();
        userApi.loginUser(newUser).log().all()
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", is(false))
                .body("message", is("email or password are incorrect"));
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
    public UserCredential getUserCredentialByResponse(ValidatableResponse response) {
        return gson.fromJson(response.extract().body().asString(), UserCredential.class);
    }
}
