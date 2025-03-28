import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class AuthIntegrationTest {
    @BeforeAll
    static void setUpBeforeClass(){
        RestAssured.baseURI = "http://localhost:8084";

    }
    @Test
    public void shouldReturnOKWithValidToken() {
        String loginPayload = """
                    {
                      "email": "john.doe@example.com",
                      "password": "password123"
                    }   
                """;

        Response response = RestAssured.given()
                .contentType("application/json")
                .body(loginPayload)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .body("token", Matchers.notNullValue())
                .extract()
                .response();
        System.out.println("Generated token: \n" + response.getBody().prettyPrint());
    }

    @Test
    public void shouldReturnUnauthorizedOnInvalidLogin() {
        String loginPayload = """
                    {
                      "email": "invalid@example.com",
                      "password": "password"
                    }   
                """;

        RestAssured.given()
                .contentType("application/json")
                .body(loginPayload)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(401);
    }
}
