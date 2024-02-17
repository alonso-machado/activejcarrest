import com.alonso.active4j.example.http.model.dto.CarRecord;
import io.activej.inject.module.AbstractModule;
import io.activej.test.ActiveJRunner;
import io.activej.test.UseModules;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.Test;


import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.util.Collections;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.text.IsEmptyString.emptyString;

@RunWith(ActiveJRunner.class)
@UseModules({TestMain.class})
public class TestMain extends AbstractModule {

	@Test
	public void testListAllCars() {
		//List all, should have all some of the cars the database has initially (import.sql)
		Response response = given()
				.when()
				.get("/car/")
				.then()
				.statusCode(HttpStatus.SC_OK)
				.contentType("application/json")
				.extract().response();
		assertThat(response.jsonPath().getList("name"), Matchers.hasItems("Diablo", "Enzo", "Uno Mille", "x1"));
	}

	@Test
	public void testEntityNotFoundForDelete() {
		given()
				.when()
				.delete("/car/999")
				.then()
				.statusCode(HttpStatus.SC_NOT_FOUND)
				.body(emptyString());
	}

	@Test
	public void testEntityNotFoundForUpdate() {
		CarRecord carRecord = new CarRecord("Fusion", "Ford", BigDecimal.valueOf(99000), "Hatchback");
		given()
				.when()
				.body(carRecord)
				.contentType("application/json")
				.put("/car/6767")
				.then()
				.statusCode(HttpStatus.SC_NOT_FOUND)
				.body(emptyString());
	}

	@Test
	public void testEntityNotFoundForGetId() {
		given()
				.when()
				.contentType("application/json")
				.get("/car/6767")
				.then()
				.statusCode(HttpStatus.SC_NOT_FOUND)
				.body(emptyString());
	}

	@Test
	public void testEntityNotFoundForGetName() {
		given()
				.when()
				.contentType("application/json")
				.get("/car/name/Onix")
				.then()
				.statusCode(HttpStatus.SC_NO_CONTENT)
				.body(emptyString());
	}

	@Test
	public void testEntityFoundForGetName() {
		Response response = given()
				.when()
				.contentType("application/json")
				.get("/car/name/x5")
				.then()
				.statusCode(HttpStatus.SC_OK)
				.contentType("application/json")
				.extract().response();
		assertThat(response.jsonPath().get("name"), Matchers.equalTo("x5"));
		assertThat(response.jsonPath().get("brand"), Matchers.equalTo("BMW"));
	}

	@Test
	public void testGetCarByBrandName() {
		// Given
		String carBrand = "Ferrari";

		// When/Then
		Response response = given()
				.when().get("/car/brand/{carBrand}", carBrand)
				.then()
				.statusCode(HttpStatus.SC_OK).contentType("application/json")
				.extract().response();
		assertThat(response.jsonPath().getList("name"), Matchers.hasItems("Enzo", "F50"));
		assertThat(response.jsonPath().getList("brand"), Matchers.everyItem(Matchers.equalTo("Ferrari")));
	}

	@Test
	public void testGetCarByBrandNameNonExistent() {
		// Given
		String carBrand = "TestNonExistent";

		// When/Then
		given()
				.when().get("/car/brand/{carBrand}", carBrand)
				.then()
				.statusCode(HttpStatus.SC_OK).contentType("application/json")
				.body("", equalTo(Collections.emptyList()));
		;
	}

	@Test
	public void testUpdateCar() {
		// Given
		long carId = 1L;
		CarRecord carRecord = new CarRecord("Fusion", "Ford", BigDecimal.valueOf(70000), "Hatchback");

		// When/Then
		given().header("Content-type", "application/json")
				.and()
				.body(carRecord)
				.when().put("/car/{id}", carId)
				.then()
				.statusCode(HttpStatus.SC_ACCEPTED);
	}

	@Test
	public void testDeleteCar() {
		// Given
		long carId = 1L;

		// When/Then
		given().header("Content-type", "application/json")
				.when().delete("/car/{id}", carId)
				.then()
				.statusCode(HttpStatus.SC_NO_CONTENT);
	}

	@Test
	public void testPostCar() {
		// Given
		CarRecord carRecord = new CarRecord("Ka", "Ford", BigDecimal.valueOf(25000), "Mini Car");

		// When / Then
		given().header("Content-type", "application/json")
				.and()
				.body(carRecord)
				.when().post("/car")
				.then()
				.statusCode(HttpStatus.SC_CREATED);
	}

	@Test
	public void testGetCarByRangeNotPossible() {
		// Given
		Double startPrice = 0.0;
		Double finalPrice = 1.0;

		// When/Then
		given()
				.when().get("/car/price-range/?startPrice={startPrice}&finalPrice={finalPrice}", startPrice, finalPrice)
				.then()
				.statusCode(HttpStatus.SC_OK)
				.body("", equalTo(Collections.emptyList()));
	}

	@Test
	public void testGetCarByRange() {
		// Given
		Double startPrice = 9500.0;
		Double finalPrice = 110000.0;

		// When/Then
		Response response = given()
				.when().get("/car/price-range/?startPrice={startPrice}&finalPrice={finalPrice}", startPrice, finalPrice)
				.then()
				.statusCode(HttpStatus.SC_OK)
				.contentType("application/json")
				.extract().response();
		assertThat(response.jsonPath().getList("name"), Matchers.hasItems("Uno Mille"));
	}

	@Test
	public void testGetCarById() {
		// Given
		Long carId = 3L;

		// When/Then
		Response response = given()
				.when().get("/car/{id}", carId)
				.then()
				.statusCode(HttpStatus.SC_OK).contentType("application/json")
				.extract().response();
		assertThat(response.jsonPath().get("id").toString(), Matchers.equalTo(carId.toString()));
	}

	@Test
	public void testListAllCarsPaged() {
		//List all, should have all some of the cars the database has initially (import.sql)
		Response response = given()
				.when()
				.get("/car/?pageIndex=0&pageSize=10")
				.then()
				.statusCode(HttpStatus.SC_OK)
				.contentType("application/json")
				.extract().response();
		assertThat(response.jsonPath().getList("name"), Matchers.hasItems("Uno Mille", "x1"));
	}

	@Test
	public void testListAllCarsPagedWrongDefaultingToListAll() {
		//List all, with wrong page, so it will default to List all elements without paging
		Response response = given()
				.when()
				.get("/car/?pageIndex=5")
				.then()
				.statusCode(HttpStatus.SC_OK)
				.contentType("application/json")
				.extract().response();
		assertThat(response.jsonPath().getList("name"), Matchers.hasItems("Uno Mille", "x1"));
	}

}
