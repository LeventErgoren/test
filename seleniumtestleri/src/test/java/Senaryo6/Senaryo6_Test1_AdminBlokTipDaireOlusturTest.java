package Senaryo6;

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

public class Senaryo6_Test1_AdminBlokTipDaireOlusturTest {

    private WebDriver driver;
    private static final String BASE_URL = "http://localhost:1313";

    static final String BLOCK_NAME = "S6 Blok";
    static final String TYPE_NAME = "S6 Tip";
    static final String DOOR_NUMBER = "606";

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

    private org.openqa.selenium.WebElement inputByLabel(String labelText) {
        return driver.findElement(By.xpath("//label[.//div[normalize-space() = '" + labelText + "']]//input"));
    }

    private org.openqa.selenium.WebElement selectByLabel(String labelText) {
        return driver.findElement(By.xpath("//label[.//div[normalize-space() = '" + labelText + "']]//select"));
    }

    @Test
    void adminBlokTipVeDaireOlusturabilmeli() {
        adminLogin();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Blok
        driver.get(BASE_URL + "/admin/bloklar");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Bloklar']")));
        inputByLabel("Blok adı").sendKeys(BLOCK_NAME);
        inputByLabel("Toplam kat").sendKeys("5");
        driver.findElement(By.xpath("//button[normalize-space()='Ekle']")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), '" + BLOCK_NAME + "')]")));

        // Tip
        driver.get(BASE_URL + "/admin/daire-tipleri");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Daire Tipleri']")));
        inputByLabel("Tip adı").sendKeys(TYPE_NAME);
        inputByLabel("Varsayılan aidat").sendKeys("100");
        driver.findElement(By.xpath("//button[normalize-space()='Ekle']")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), '" + TYPE_NAME + "')]")));

        // Daire
        driver.get(BASE_URL + "/admin/daireler");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Daireler']")));
        inputByLabel("Kapı No").sendKeys(DOOR_NUMBER);
        inputByLabel("Kat").sendKeys("2");
        new org.openqa.selenium.support.ui.Select(selectByLabel("Durum")).selectByVisibleText("Boş");
        new org.openqa.selenium.support.ui.Select(selectByLabel("Blok")).selectByVisibleText(BLOCK_NAME);
        new org.openqa.selenium.support.ui.Select(selectByLabel("Daire tipi")).selectByVisibleText(TYPE_NAME);
        driver.findElement(By.xpath("//button[normalize-space()='Ekle']")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), '" + DOOR_NUMBER + "')]")));
        assertTrue(driver.findElement(By.tagName("body")).getText().contains(DOOR_NUMBER));
    }
}
