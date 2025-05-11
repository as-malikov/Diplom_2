package api;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import model.User;

import static io.restassured.RestAssured.given;


public class UserApi extends RestApi {
    public static final String API_AUTH_REGISTER = "/api/auth/register";
    public static final String API_AUTH_LOGIN = "/api/auth/login";
    public static final String API_AUTH_USER = "/api/auth/user";
    public static final String API_AUTH_TOKEN = "/api/auth/token";

    @Step("Create user")
    public ValidatableResponse createUser(User user) {
        return given().spec(requestSpecification())
                .body(user)
                .when()
                .post(API_AUTH_REGISTER)
                .then();
    }

    @Step("Get token by user email and user password")
    public ValidatableResponse loginUser(User user) {
        return given().spec(requestSpecification())
                .body(user)
                .when()
                .post(API_AUTH_LOGIN)
                .then();
    }

    @Step("Get access token user by refresh token")
    public ValidatableResponse getUserByToken(String accessToken) {
        return given().spec(requestSpecification())
                .header("Authorization", accessToken)
                .when()
                .get(API_AUTH_USER)
                .then();
    }

    @Step("Get access token user by refresh token")
    public ValidatableResponse updateUserByToken(User user, String accessToken) {
        return given().spec(requestSpecification())
                .header("Authorization", accessToken)
                .body(user)
                .when()
                .patch(API_AUTH_USER)
                .then();
    }

    @Step("Delete user")
    public ValidatableResponse deleteUser(String accessToken) {
        return given().spec(requestSpecification())
                .header("Authorization", accessToken)
                .when()
                .delete(API_AUTH_USER)
                .then();
    }
}
