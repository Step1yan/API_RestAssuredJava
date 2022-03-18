package API;

import com.codeborne.selenide.commands.As;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.Assert;
import org.junit.Test;

import java.time.Clock;
import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.requestSpecification;

public class ReqresTest {
    private final static String URL = "https://reqres.in/";

    @Test
    public void CheckAvatAndIdTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpec200());
        List<UserData> users = given()
                .when()
                .get("api/users?page=2")
                .then().log().all()
                .extract().body().jsonPath().getList("user", UserData.class); //todo get request

        //stugum e userneri avatary u id-in
        users.forEach(x -> Assert.assertTrue(x.getAvatar().contains(x.getId().toString())));
        //todo stuguma te ardyoq mer bolor userneri mailery verjanum en @reqres.inov
        Assert.assertTrue(users.stream().allMatch(x -> x.getEmail().endsWith("@reqres.in")));
        //todo sarqum enq 2 hat list u hamematum irar het useri id-in u avatary
        List<String> avatars = users.stream().map(UserData::getAvatar).collect(Collectors.toList());
        List<String> ids = users.stream().map(x -> x.getId().toString()).collect(Collectors.toList());
        for (int i = 0; i < avatars.size(); i++) {
            Assert.assertTrue(avatars.get(i).contains(ids.get(i)));
        }
    }

    @Test
    public void SuccessRegisterTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpec200());
        Integer id = 4;
        String token = "QpwL5tke4Pnpja7X4";
        Register user = new Register("eve.holt@reqres.in", "pistol");
        SuccessRegister succsessRegister = given()
                .body(user)
                .when()
                .post("api/register")
                .then().log().all()
                .extract().as(SuccessRegister.class);
        Assert.assertNotNull(succsessRegister.getId());
        Assert.assertNotNull(succsessRegister.getToken());
        Assert.assertEquals(id, succsessRegister.getId());
        Assert.assertEquals(token, succsessRegister.getToken());
    }
    @Test
    public void unSuccessRegTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpec400());
        Register user = new Register("sydney@fife", "invalidPassword");
        UnSuccessReg unSuccessReg = given()
                .body(user)
                .post("api/register")
                .then().log().all()
                .extract().as(UnSuccessReg.class);
        Assert.assertEquals("Note: Only defined users succeed registration", unSuccessReg.getError());
    }
    @Test
    public void sortedYearsTest(){
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpec200());
        List<ColorsData> colors = given()
                .when()
                .get("api/unknown")
                .then().log().all()
                .extract().body().jsonPath().getList("data", ColorsData.class);
        List<Integer> years = colors.stream().map(ColorsData::getYear).collect(Collectors.toList());
        List<Integer> soertedYears = years.stream().sorted().collect(Collectors.toList());
        Assert.assertEquals(soertedYears, years);
    }
    @Test
    public void deleteTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecUniq(204));
        given()
                .when()
                .delete("api/users/2")
                .then().log().all();
    }
    @Test
    public void timeTest(){
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpec200());
        UserTime user = new UserTime("morpheus", "zion resident");
        UserTimeResponse response = given()
                .body(user)
                .when()
                .put("api/users/2")
                .then().log().all()
                .extract().as(UserTimeResponse.class);
        String regex = "(.{5})$";
        String currentTime = Clock.systemUTC().instant().toString().replaceAll(regex, "");
        System.out.println(currentTime +"***************");
       // Assert.assertEquals(currentTime, response.getUpdatedAt().replaceAll(regex, ""));
        System.out.println(response.getUpdatedAt().replaceAll(regex, ""+"************"));
    }
}
