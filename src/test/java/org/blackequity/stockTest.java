package org.blackequity;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class stockTest {
    @Test
    void testHelloEndpoint() {
        given()
          .when().get("/stock")
          .then()
             .statusCode(200)
             .body(is("Hello from Quarkus REST"));
    }

}