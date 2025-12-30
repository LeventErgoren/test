package BagimsizTestler;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ResidentLoginSayfasiGorunuyorTest {

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
    void sakinGirisSayfasiAciliyorKayitOlLinkiVar() {
        driver.get("http://localhost:1313/sakin/giris");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Sakin Girişi')]")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Kayıt ol")));

        String body = driver.findElement(By.tagName("body")).getText();
        Assertions.assertTrue(body.contains("Sakin Girişi"), "Sakin giriş sayfası görünmeli");
    }
}
