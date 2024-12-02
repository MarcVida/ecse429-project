import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // Use OrderAnnotation to enforce order
public class CategoryTest {

    private static final String dummyTitle = "buy groceries";
    private static final String dummyDescription = "get apples, bananas, oranges";

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://localhost:4567/categories";
    }

    @Test
    @Order(1) // Test runs first
    public void testDelete1() {
        Response response = 
            given()
            .when()
                .delete("/2");
        
        System.out.println("Transaction time DELETE 1: " + response.time() + " ms");

        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

        double processCpuLoad = osBean.getProcessCpuLoad() * 100;
        System.out.printf("Process CPU Usage DELETE 1: %.2f%%\n", processCpuLoad);

        long freeMemory = osBean.getFreePhysicalMemorySize();
        System.out.printf("Available Memory DELETE 1: %.2f MB\n", freeMemory / (1024.0 * 1024.0));
    }

    @Test
    @Order(2) // Test runs second
    public void testGet1() {
        Response response = 
            given()
            .when()
                .get("");

        System.out.println("\nTransaction time GET 1: " + response.time() + " ms");

        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

        double processCpuLoad = osBean.getProcessCpuLoad() * 100;
        System.out.printf("Process CPU Usage GET 1: %.2f%%\n", processCpuLoad);

        long freeMemory = osBean.getFreePhysicalMemorySize();
        System.out.printf("Available Memory GET 1: %.2f MB\n", freeMemory / (1024.0 * 1024.0));
    }

    @Test
    @Order(3) // Test runs third
    public void testPut1() {
        Response response = 
            given()
            .when()
                .body("{ \"title\": \"buy even more food\", \"description\": \"get apples, bananas, oranges\" }")
                .put("/1");
        
        System.out.println("\nTransaction time PUT 1: " + response.time() + " ms");

        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

        double processCpuLoad = osBean.getProcessCpuLoad() * 100;
        System.out.printf("Process CPU Usage PUT 1: %.2f%%\n", processCpuLoad);

        long freeMemory = osBean.getFreePhysicalMemorySize();
        System.out.printf("Available Memory PUT 1: %.2f MB\n", freeMemory / (1024.0 * 1024.0));
    }

    @Test
    @Order(4) 
    public void testDeleteWithID1() {
        Response response = 
            given()
            .when()
                .delete("/1");
    }

    
    @Test
    @Order(5)
    public void testPost1() {
        // Start tracking system metrics
        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

        // Capture transaction time for POST request
        Response postResponse = given()
            .when()
                .body("{ \"title\": \"buy food\", \"description\": \"get apples, bananas, oranges\" }")
                .post("");

        // Print POST transaction time
        System.out.println("\nTransaction time POST 1: " + postResponse.time() + " ms");

        // Track system metrics after POST
        double postCpuLoad = osBean.getProcessCpuLoad() * 100;
        System.out.printf("Process CPU Usage POST 1: %.2f%%\n", postCpuLoad);

        long postFreeMemory = osBean.getFreePhysicalMemorySize();
        System.out.printf("Available Memory POST 1: %.2f MB\n", postFreeMemory / (1024.0 * 1024.0));

        // Extract the ID to be deleted 
        String idString = postResponse.then()
            .extract().path("id");

        given()
            .when()
                .delete("/" + idString);
    }


    @Test
    @Order(6)
    public void testPost10() {
        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

        long startTime = System.currentTimeMillis();

        for (int i = 1; i <= 10; i++) {
            given()
                .when()
                    .body("{ \"title\": \"buy food " + i + "\", \"description\": \"get apples, bananas, oranges\" }")
                    .post("");
        }

        long endTime = System.currentTimeMillis();
        double endCpuLoad = osBean.getProcessCpuLoad() * 100;
        long endFreeMemory = osBean.getFreePhysicalMemorySize();

        System.out.println("\nTotal Time for POST 10 requests: " + (endTime - startTime) + " ms");
        System.out.printf("CPU Usage Change (POST 10): %.2f%%\n", endCpuLoad);
        System.out.printf("Memory Change (POST 10): %.2f MB\n", (endFreeMemory) / (1024.0 * 1024.0));
    }


    @Test
    @Order(7)
    public void testPut10() {
        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

        long startTime = System.currentTimeMillis();
        double startCpuLoad = osBean.getProcessCpuLoad() * 100;
        long startFreeMemory = osBean.getFreePhysicalMemorySize();

        for (int i = 1; i <= 10; i++) {
            given()
                .when()
                    .body("{ \"title\": \"update food " + i + "\", \"description\": \"get grapes, pineapples, watermelons\" }")
                    .put("/" + i);
        }

        long endTime = System.currentTimeMillis();
        double endCpuLoad = osBean.getProcessCpuLoad() * 100;
        long endFreeMemory = osBean.getFreePhysicalMemorySize();

        System.out.println("\nTotal Time for PUT 10 requests: " + (endTime - startTime) + " ms");
        System.out.printf("CPU Usage Change (PUT): %.2f%%\n", endCpuLoad - startCpuLoad);
        System.out.printf("Memory Change (PUT): %.2f MB\n", (startFreeMemory - endFreeMemory) / (1024.0 * 1024.0));
    }


}
