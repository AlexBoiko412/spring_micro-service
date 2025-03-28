import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


public class PatientIntegrationTest {
    @BeforeAll
    static void setUpBeforeClass() {
        RestAssured.baseURI = "http://localhost:8084";
    }

    @Test
    public void shouldReturnPatientsWithValidToken(){
        String loginPayload = """
                    {
                      "email": "john.doe@example.com",
                      "password": "password123"
                    }   
                """;

        String token = RestAssured.given()
                .contentType("application/json")
                .body(loginPayload)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .get("token");

        Response patients = RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/patients")
                .then()
                .statusCode(200)
                .body(Matchers.notNullValue())
                .extract()
                .response();
        System.out.println(patients.prettyPrint());
    }
}
