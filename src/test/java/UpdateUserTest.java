import api.UserApi;
import com.google.gson.Gson;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.User;
import model.UserCredential;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static util.UserGenerator.*;
import static org.hamcrest.Matchers.is;

public class UpdateUserTest {

    public static final String EMPTY_STRING = "";
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
    @DisplayName("Update user login, password and email by correct token")
    public void updateUserByCorrectToken200OkTest() {
        User newUser = getNewUser();
        ValidatableResponse updateUserResponse = userApi.updateUserByToken(newUser, userCredential.getAccessToken());
        updateUserResponse.log().all()
                .assertThat()
                .statusCode(SC_OK)
                .body("success", is(true))
                .body("user.email", is(newUser.getEmail()))
                .body("user.name", is(newUser.getName()));
    }

    @Test
    @DisplayName("Update user login, password and email by empty token")
    public void updateUserByIncorrectTokenReturn401AuthorisedTest() {
        User newUser = getNewUser();
        ValidatableResponse updateUserResponse = userApi.updateUserByToken(newUser, EMPTY_STRING);
        updateUserResponse.log().all()
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

    @Step("Get new user")
    protected User getNewUser() {
        String newName = getRandomName();
        String newPassword = getRandomPassword();
        String newEmail = getRandomEmail();
        return new User(newName, newPassword, newEmail);
    }
}
