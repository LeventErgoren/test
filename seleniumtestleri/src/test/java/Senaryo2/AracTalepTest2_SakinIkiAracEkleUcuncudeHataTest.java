package Senaryo2;

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

public class AracTalepTest2_SakinIkiAracEkleUcuncudeHataTest {

    private WebDriver driver;

    private static final String BASE_URL = "http://localhost:1313";

    private static final String RESIDENT_EMAIL = AracTalepTest1_SakinKayitOlusturTest.RESIDENT_EMAIL;
    private static final String RESIDENT_PHONE = AracTalepTest1_SakinKayitOlusturTest.RESIDENT_PHONE;

    private static final String PLATE_1 = "34SEL001";
    private static final String PLATE_2 = "34SEL002";
    private static final String PLATE_3 = "34SEL003";

    private static final String EXPECTED_LIMIT_ERROR_CONTAINS = "en fazla 2 araç";

    @BeforeEach
    void setup() {
        driver = new ChromeDriver();
    }

    @AfterEach
    void teardown() {
        if (driver != null) driver.quit();
    }

    private WebElement inputByLabel(String labelText) {
        return driver.findElement(By.xpath("//label[.//div[normalize-space() = '" + labelText + "']]//input"));
    }

    private void residentLogin() {
        driver.get(BASE_URL + "/sakin/giris");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Sakin Girişi')]")));

        inputByLabel("E-posta").sendKeys(RESIDENT_EMAIL);
        inputByLabel("Telefon").sendKeys(RESIDENT_PHONE);

        driver.findElement(By.xpath("//button[normalize-space()='Giriş Yap']")).click();

        wait.until(ExpectedConditions.urlContains("/sakin"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Sakin Paneli')]")));
    }

    private void addVehicle(String plate, String brand, String model) {
        inputByLabel("Plaka").clear();
        inputByLabel("Plaka").sendKeys(plate);
        inputByLabel("Marka").clear();
        inputByLabel("Marka").sendKeys(brand);
        inputByLabel("Model").clear();
        inputByLabel("Model").sendKeys(model);

        driver.findElement(By.xpath("//button[normalize-space()='Ekle']")).click();
    }

    @Test
    void sakinIkiAracEklemeliUcuncudeHataGormeli() {
        residentLogin();

        driver.get(BASE_URL + "/sakin/araclarim");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Araçlarım']")));

        addVehicle(PLATE_1, "Selenium", "Car1");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Araç eklendi.')]")));

        addVehicle(PLATE_2, "Selenium", "Car2");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Araç eklendi.')]")));

        String bodyAfter2 = driver.findElement(By.tagName("body")).getText();
        assertTrue(bodyAfter2.contains(PLATE_1), "1. araç listede görünmeli");
        assertTrue(bodyAfter2.contains(PLATE_2), "2. araç listede görünmeli");

        addVehicle(PLATE_3, "Selenium", "Car3");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'), '" + EXPECTED_LIMIT_ERROR_CONTAINS + "')]")));

        String bodyAfter3 = driver.findElement(By.tagName("body")).getText().toLowerCase();
        assertTrue(bodyAfter3.contains(EXPECTED_LIMIT_ERROR_CONTAINS), "3. araç eklemede limit hatası görünmeli");
    }
}
