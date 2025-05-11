package users;

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
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static util.UserGenerator.*;


@RunWith(Parameterized.class)
public class CreateUserParamTest {
    private static UserApi userApi;
    private final User user;
    private final int expectedStatus;
    ValidatableResponse createUserResponse;

    public CreateUserParamTest(User user, int expectedStatus) {
        this.user = user;
        this.expectedStatus = expectedStatus;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> canBeCreated() {
        return Arrays.asList(
                new Object[]{getRandomUser(), SC_OK},
                new Object[]{getRandomUser(), SC_OK},
                new Object[]{getRandomUser(), SC_OK},
                new Object[]{new User(getRandomName(), null, null), SC_FORBIDDEN},
                new Object[]{new User(null, getRandomPassword(), null), SC_FORBIDDEN},
                new Object[]{new User(null, null, getRandomEmail()), SC_FORBIDDEN}
        );
    }

    @Before
    public void init() {
        userApi = new UserApi();

    }

    @Test
    @DisplayName("Check user can be created")
    public void userCanBeCreatedTest() {
        if (expectedStatus == SC_OK) {
            createUserResponse = userApi.createUser(user);
            createUserResponse.log().all()
                    .assertThat()
                    .statusCode(expectedStatus)
                    .body("success", is(true))
                    .and()
                    .body("user.email", is(user.getEmail()))
                    .and()
                    .body("user.name", is(user.getName()))
                    .and()
                    .body("accessToken", notNullValue())
                    .and()
                    .body("refreshToken", notNullValue());
            userApi.createUser(user).log().all()
                    .assertThat()
                    .statusCode(SC_FORBIDDEN)
                    .body("success", is(false))
                    .and()
                    .body("message", is("User already exists"));
        } else if (expectedStatus == SC_FORBIDDEN) {
            userApi.createUser(user).log().all()
                    .assertThat()
                    .statusCode(expectedStatus)
                    .body("success", is(false))
                    .and()
                    .body("message", is("Email, password and name are required fields"));
        }
    }

    @After
    @Step("Delete user after test")
    public void deleteUser() {
        if (expectedStatus == SC_OK) {
            Gson gson = new Gson();
            String jsonResponse = createUserResponse.extract().body().asString();
            UserCredential userCredential = gson.fromJson(jsonResponse, UserCredential.class);

            ValidatableResponse deleteUserResponse = userApi.deleteUser(userCredential.getAccessToken());
            deleteUserResponse.log().all()
                    .assertThat()
                    .statusCode(SC_ACCEPTED);
        }
    }
}
