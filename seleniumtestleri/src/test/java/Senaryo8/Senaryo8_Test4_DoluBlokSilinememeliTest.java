package Senaryo8;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class Senaryo8_Test4_DoluBlokSilinememeliTest {

    private WebDriver driver;
    private static final String BASE_URL = "http://localhost:1313";

    @BeforeEach
    void setup() { driver = new ChromeDriver(); }

    @AfterEach
    void teardown() { if (driver != null) driver.quit(); }

    private void adminLogin() {
        driver.get(BASE_URL + "/admin/giris");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[autocomplete='username']"))).sendKeys("admin");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[autocomplete='current-password']"))).sendKeys("1234");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(ExpectedConditions.urlContains("/admin"));
    }

    @Test
    void icindeDaireOlanBlokSilinememeli() {
        adminLogin();
        driver.get(BASE_URL + "/admin/bloklar");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Bloklar']")));

        driver.findElement(By.xpath("//tr[.//*[contains(normalize-space(.), '" + Senaryo8_Test1_AdminBlokOlusturTest.BLOCK_NAME + "')]]//button[normalize-space()='Sil']")).click();

        Alert confirm = wait.until(ExpectedConditions.alertIsPresent());
        confirm.accept();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZİÖÜŞĞÇ','abcdefghijklmnopqrstuvwxyzıöüşğç'), 'silinemez')]")
        ));

        String body = driver.findElement(By.tagName("body")).getText().toLowerCase();
        assertTrue(body.contains("silinemez"), "Dolu blok silinemez hatası görünmeli");
    }
}
