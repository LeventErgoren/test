package Senaryo1;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class Test3_SakinAidatGoruntuleTest {

    private WebDriver driver;

    private static final String BASE_URL = "http://localhost:1313";

    // Test1'de oluşturulan sakin
    private static final String RESIDENT_EMAIL = "selenium1@mail.com";
    private static final String RESIDENT_PHONE = "05500000001";

    // Test2'de oluşturulan aidat dönemi
    private static final String DUES_MONTH = "1";
    private static final String DUES_YEAR = "2026";

    @BeforeEach
    void setup() {
        driver = new ChromeDriver();
    }

    @AfterEach
    void teardown() {
        if (driver != null) driver.quit();
    }

    private void residentLogin() {
        driver.get(BASE_URL + "/sakin/giris");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement emailInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[autocomplete='email']"))
        );
        WebElement phoneInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[autocomplete='tel']"))
        );

        emailInput.sendKeys(RESIDENT_EMAIL);
        phoneInput.sendKeys(RESIDENT_PHONE);
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/sakin"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Sakin Paneli')]")));
    }

    @Test
    void sakinAidatiniGorebilmeli() {
        residentLogin();

        // Aidatlarım
        driver.findElement(By.linkText("Aidatlarım")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("/sakin/aidatlarim"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[contains(text(),'Aidatlarım')]")));

        // Oluşturulan dönem görünmeli
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'" + DUES_MONTH + "/" + DUES_YEAR + "')]")));
        String body = driver.findElement(By.tagName("body")).getText();
        assertTrue(body.contains(DUES_MONTH + "/" + DUES_YEAR), "Aidat dönemi sakin ekranında görünmeli");
    }
}
