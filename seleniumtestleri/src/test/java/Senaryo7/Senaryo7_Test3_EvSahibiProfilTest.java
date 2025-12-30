package Senaryo7;

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

public class Senaryo7_Test3_EvSahibiProfilTest {

    private WebDriver driver;
    private static final String BASE_URL = "http://localhost:1313";

    private static final String EMAIL = Senaryo7_Test1_EvSahibiKayitOlusturTest.OWNER1_EMAIL;
    private static final String PHONE = Senaryo7_Test1_EvSahibiKayitOlusturTest.OWNER1_PHONE;

    @BeforeEach
    void setup() { driver = new ChromeDriver(); }

    @AfterEach
    void teardown() { if (driver != null) driver.quit(); }

    private WebElement inputByLabel(String labelText) {
        return driver.findElement(By.xpath("//label[.//div[normalize-space() = '" + labelText + "']]//input"));
    }

    private void residentLogin() {
        driver.get(BASE_URL + "/sakin/giris");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Sakin Girişi')]")));

        inputByLabel("E-posta").sendKeys(EMAIL);
        inputByLabel("Telefon").sendKeys(PHONE);
        driver.findElement(By.xpath("//button[normalize-space()='Giriş Yap']")).click();

        wait.until(ExpectedConditions.urlContains("/sakin"));
    }

    @Test
    void evSahibiProfildeEvSahibiGorunmeli() {
        residentLogin();

        driver.get(BASE_URL + "/sakin/profil");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Profil']")));

        String body = driver.findElement(By.tagName("body")).getText();
        assertTrue(body.contains("Ev sahibi"), "Profilde Ev sahibi görünmeli");
        assertTrue(body.contains(EMAIL), "Profilde e-posta görünmeli");
    }
}
