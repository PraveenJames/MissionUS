package mission;

import cucumber.api.DataTable;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;

import java.util.*;

public class StepDefinition {
    private static final String BASE_URL = "https://reqres.in/api";
    private Response response;
    private int totalUsers;
    private int totalPages;
    private List<Integer> userIDs = new ArrayList<>();

    @Given("^I am on the home page$")
    public void iAmOnTheHomePage() {
        HomePage.homePage();
    }

    @Given("I get the default list of users for on 1st page")
    public void iGetTheDefaultListofusers() {
        response = RestAssured.given().header("User-Agent", "Mozilla/5.0").get(BASE_URL + "/users?page=1");
        Assert.assertEquals(response.getStatusCode(), 200, "Status code when getting default users list is not 200.");

        JsonPath jsonPath = response.jsonPath();
        totalUsers = jsonPath.getInt("total");
        totalPages = jsonPath.getInt("total_pages");
    }

    @When("I get the list of all users within every page")
    public void iGetTheListOfAllUsers() {
        userIDs.clear();
        for(int page = 1; page <= totalPages; page++) {
            Response pageResponse = RestAssured.get(BASE_URL + "/users?page=" + page);
            Assert.assertEquals(pageResponse.getStatusCode(), 200);
            JsonPath jsonPath = pageResponse.jsonPath();
            userIDs.addAll(jsonPath.getList("data.id"));
        }
    }

    @Then("I should see total users count equals the number of user ids")
    public void iShouldMatchTotalCount() {
        Assert.assertEquals(totalUsers, userIDs.size());
    }

    @Given("^I make a search for user (.*)$")
    public void iMakeASearchForUser(String sUserID) {
        response = RestAssured.get(BASE_URL + "/users/" + sUserID);
    }

    @Then("I should see the following user data")
    public void iShouldSeeFollowingUserData(DataTable dt) {
        Assert.assertEquals(response.getStatusCode(), 200);
        Map<String, String> userData = dt.asMaps(String.class, String.class).get(0);
        JsonPath jsonPath = response.jsonPath();

        for (Map.Entry<String, String> entry : userData.entrySet()) {
            String actual = jsonPath.getString("data." + entry.getKey());
            Assert.assertEquals(actual, entry.getValue());
        }
    }

    @Then("^I receive error code (\\d+) in response$")
    public void iReceiveErrorCodeInResponse(int responseCode) {
        Assert.assertEquals(response.getStatusCode(), responseCode);
    }

    @Given("^I create a user with following (.*) (.*)$")
    public void iCreateUserWithFollowing(String sUsername, String sJob) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("name", sUsername);
        payload.put("job", sJob);

        response = RestAssured.given().contentType("application/json").body(payload).post(BASE_URL + "/users");
        Assert.assertEquals(response.getStatusCode(), 201);
    }

    @Then("response should contain the following data")
    public void iVerifyResponseData(DataTable dt) {
        Map<String, String> userData = dt.asMaps(String.class, String.class).get(0);
        JsonPath jsonPath = response.jsonPath();

        for(Map.Entry<String, String> entry : userData.entrySet()) {
            String actual = jsonPath.getString(entry.getKey());
            Assert.assertEquals(actual, entry.getValue());
        }
    }

    @Given("I login unsuccessfully with the following data")
    public void iLoginSuccessfullyWithFollowingData(DataTable dt) {
        Map<String, String> data = dt.asMaps(String.class, String.class).get(0);
        Map<String, Object> payload = new HashMap<>();
        payload.put("email", data.get("Email"));

        if(data.get("Password") != null) {
            payload.put("password", data.get("Password"));
        }

        response = RestAssured.given()
                .contentType("application/json")
                .body(payload)
                .post(BASE_URL + "/login");
    }

    @Given("I wait for the user list to load")
    public void iWaitForUserListToLoad() {
        response = RestAssured.get(BASE_URL + "/users?delay=3");
        Assert.assertEquals(response.getStatusCode(), 200);
        userIDs = response.jsonPath().getList("data.id");
    }

    @Then("I should see that every user has a unique id")
    public void iShouldSeeThatEveryUserHasAUniqueID() {
        Set<Integer> uniqueIds = new HashSet<>(userIDs);
        Assert.assertEquals(uniqueIds.size(), userIDs.size());
    }

    @Then("^I should get a response code of (\\d+)$")
    public void iShouldGetAResponseCodeOf(int responseCode) {
        Assert.assertEquals(response.getStatusCode(), responseCode);
    }

    @And("^I should see the following response message:$")
    public void iShouldSeeTheFollowingResponseMessage(String expectedMessage) {
        String actualBody = response.getBody().asString();
        Assert.assertTrue(actualBody.contains(expectedMessage));
    }
}