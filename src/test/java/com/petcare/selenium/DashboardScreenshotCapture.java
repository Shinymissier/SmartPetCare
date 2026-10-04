package com.petcare.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Selenium WebDriver test suite for capturing dashboard screenshots.
 * 
 * Prerequisites:
 *   1. Application must be deployed and running on Tomcat (http://localhost:8080/petcare)
 *   2. A demo user must exist (admin@petcare.com / admin123) or modify credentials below.
 *   3. Chrome browser must be installed.
 * 
 * Usage:
 *   mvn test -Dtest=DashboardScreenshotCapture
 *   
 * Screenshots are saved to: target/screenshots/
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DashboardScreenshotCapture {

    private static WebDriver driver;
    private static WebDriverWait wait;

    // Configuration - modify these for your environment
    private static final String BASE_URL = "http://localhost:8080/petcare";
    private static final String LOGIN_EMAIL = "admin@petcare.com";
    private static final String LOGIN_PASSWORD = "admin123";
    private static final String SCREENSHOTS_DIR = "target/screenshots";

    @BeforeAll
    static void setUp() {
        // Auto-setup ChromeDriver
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--force-device-scale-factor=1");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        // Uncomment for headless mode:
        // options.addArguments("--headless=new");

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // Create screenshots directory
        new File(SCREENSHOTS_DIR).mkdirs();

        System.out.println("=======================================================");
        System.out.println("  Smart Pet Care - Dashboard Screenshot Capture Suite");
        System.out.println("  Target: " + BASE_URL);
        System.out.println("  Screenshots: " + SCREENSHOTS_DIR + "/");
        System.out.println("=======================================================");
    }

    @AfterAll
    static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
        System.out.println("\n>>> All screenshots saved to: " + SCREENSHOTS_DIR + "/");
    }

    @Test
    @Order(1)
    @DisplayName("01 - Capture Login Page")
    void captureLoginPage() {
        driver.get(BASE_URL + "/login");
        sleep(1500);
        takeScreenshot("01_login_page");
        System.out.println("[CAPTURED] Login page screenshot saved.");
    }

    @Test
    @Order(2)
    @DisplayName("02 - Login and Capture Dashboard")
    void loginAndCaptureDashboard() {
        driver.get(BASE_URL + "/login");
        sleep(1000);

        // Fill login form
        try {
            WebElement emailField = driver.findElement(By.name("email"));
            WebElement passwordField = driver.findElement(By.name("password"));
            emailField.clear();
            emailField.sendKeys(LOGIN_EMAIL);
            passwordField.clear();
            passwordField.sendKeys(LOGIN_PASSWORD);
            takeScreenshot("02_login_filled");

            // Submit
            WebElement submitBtn = driver.findElement(By.cssSelector("button[type='submit'], input[type='submit']"));
            submitBtn.click();
            sleep(3000);
        } catch (NoSuchElementException e) {
            System.out.println("[INFO] Login form not found, navigating directly to dashboard...");
            driver.get(BASE_URL + "/dashboard");
            sleep(3000);
        }

        takeScreenshot("03_dashboard_full");
        System.out.println("[CAPTURED] Full dashboard screenshot saved.");
    }

    @Test
    @Order(3)
    @DisplayName("03 - Capture Dashboard Real-Time Elements")
    void captureDashboardRealTimeElements() {
        driver.get(BASE_URL + "/dashboard");
        sleep(3000);

        // Capture KPI stat cards
        try {
            WebElement statsGrid = driver.findElement(By.cssSelector(".stats-grid"));
            takeElementScreenshot(statsGrid, "04_kpi_stat_cards");
            System.out.println("[CAPTURED] KPI stat cards screenshot.");
        } catch (NoSuchElementException e) {
            System.out.println("[SKIPPED] Stats grid not found.");
        }

        // Capture the real-time status bar
        try {
            WebElement realtimeBar = driver.findElement(By.cssSelector(".realtime-bar"));
            takeElementScreenshot(realtimeBar, "05_realtime_status_bar");
            System.out.println("[CAPTURED] Real-time status bar screenshot.");
        } catch (NoSuchElementException e) {
            System.out.println("[SKIPPED] Real-time bar not found.");
        }

        // Wait for live clock to tick
        sleep(2000);
        takeScreenshot("06_dashboard_live_clock_ticked");
        System.out.println("[CAPTURED] Dashboard with live clock update.");
    }

    @Test
    @Order(4)
    @DisplayName("04 - Capture Charts Section")
    void captureChartsSection() {
        driver.get(BASE_URL + "/dashboard");
        sleep(3000);

        // Scroll to charts
        JavascriptExecutor js = (JavascriptExecutor) driver;
        try {
            WebElement chartSection = driver.findElement(By.id("vaccinationChart"));
            js.executeScript("arguments[0].scrollIntoView({block: 'center', behavior: 'smooth'});", chartSection);
            sleep(2000);
            takeScreenshot("07_charts_section");
            System.out.println("[CAPTURED] Charts section screenshot.");
        } catch (NoSuchElementException e) {
            System.out.println("[SKIPPED] Charts not found, capturing scroll position.");
            js.executeScript("window.scrollTo(0, 500);");
            sleep(1000);
            takeScreenshot("07_charts_scroll");
        }
    }

    @Test
    @Order(5)
    @DisplayName("05 - Capture Vaccination Queue")
    void captureVaccinationQueue() {
        driver.get(BASE_URL + "/dashboard");
        sleep(2000);

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
        sleep(1500);
        takeScreenshot("08_vaccination_queue_and_sidebar");
        System.out.println("[CAPTURED] Vaccination queue and sidebar screenshot.");
    }

    @Test
    @Order(6)
    @DisplayName("06 - Capture Pet Management Page")
    void capturePetManagement() {
        driver.get(BASE_URL + "/pets");
        sleep(2000);
        takeScreenshot("09_pets_management");
        System.out.println("[CAPTURED] Pet management page screenshot.");
    }

    @Test
    @Order(7)
    @DisplayName("07 - Capture Vaccinations Page")
    void captureVaccinationsPage() {
        driver.get(BASE_URL + "/vaccinations");
        sleep(2000);
        takeScreenshot("10_vaccinations_page");
        System.out.println("[CAPTURED] Vaccinations page screenshot.");
    }

    @Test
    @Order(8)
    @DisplayName("08 - Capture XML Management Page")
    void captureXmlManagement() {
        driver.get(BASE_URL + "/xml-management");
        sleep(2000);
        takeScreenshot("11_xml_management");
        System.out.println("[CAPTURED] XML management page screenshot.");
    }

    @Test
    @Order(9)
    @DisplayName("09 - Full Page Scrolling Dashboard Capture")
    void captureFullPageDashboard() {
        driver.get(BASE_URL + "/dashboard");
        sleep(3000);

        JavascriptExecutor js = (JavascriptExecutor) driver;

        // Capture top section
        js.executeScript("window.scrollTo(0, 0);");
        sleep(1000);
        takeScreenshot("12_dashboard_top");

        // Capture middle section
        Long pageHeight = (Long) js.executeScript("return document.body.scrollHeight;");
        js.executeScript("window.scrollTo(0, " + (pageHeight / 3) + ");");
        sleep(1000);
        takeScreenshot("13_dashboard_middle");

        // Capture bottom section
        js.executeScript("window.scrollTo(0, " + (2 * pageHeight / 3) + ");");
        sleep(1000);
        takeScreenshot("14_dashboard_bottom");

        System.out.println("[CAPTURED] Full scrolling dashboard captures complete.");
    }

    // ============ Helper Methods ============

    /**
     * Takes a full-page screenshot and saves it.
     */
    private void takeScreenshot(String filename) {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Path destination = Paths.get(SCREENSHOTS_DIR, filename + "_" + timestamp + ".png");
            Files.copy(screenshot.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("  -> Saved: " + destination);
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to save screenshot: " + e.getMessage());
        }
    }

    /**
     * Takes a screenshot of a specific WebElement.
     */
    private void takeElementScreenshot(WebElement element, String filename) {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            File screenshot = element.getScreenshotAs(OutputType.FILE);
            Path destination = Paths.get(SCREENSHOTS_DIR, filename + "_" + timestamp + ".png");
            Files.copy(screenshot.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("  -> Element saved: " + destination);
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to save element screenshot: " + e.getMessage());
        }
    }

    /**
     * Simple sleep helper for waiting on animations.
     */
    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
