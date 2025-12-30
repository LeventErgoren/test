package BagimsizTestler;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LandingSayfasiGorunuyorTest {

    private WebDriver driver;

    @BeforeEach
    void setup() {
        driver = new ChromeDriver();
    }

    @AfterEach
    void teardown() {
        if (driver != null) driver.quit();
    }

    @Test
    void landingSayfasiAciliyorVeGirisLinkleriVar() {
        driver.get("http://localhost:1313/");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Admin girişi') or contains(text(),'Yönetici') or contains(text(),'Sakin')]")));

        String body = driver.findElement(By.tagName("body")).getText();
        assertTrue(body.contains("Admin girişi") || body.contains("Yönetici"), "Landing sayfasında admin girişi metni olmalı");
        assertTrue(body.contains("Sakin girişi") || body.contains("Sakin"), "Landing sayfasında sakin girişi metni olmalı");
    }
}
