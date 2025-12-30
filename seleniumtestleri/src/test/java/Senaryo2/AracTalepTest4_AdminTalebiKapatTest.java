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

public class AracTalepTest4_AdminTalebiKapatTest {

    private WebDriver driver;

    private static final String BASE_URL = "http://localhost:1313";

    private static final String TICKET_TITLE = AracTalepTest3_SakinTalepOlusturTest.TICKET_TITLE;

    @BeforeEach
    void setup() {
        driver = new ChromeDriver();
    }

    @AfterEach
    void teardown() {
        if (driver != null) driver.quit();
    }

    private void adminLogin() {
        driver.get(BASE_URL + "/admin/giris");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement usernameInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[autocomplete='username']"))
        );
        WebElement passwordInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[autocomplete='current-password']"))
        );

        usernameInput.sendKeys("admin");
        passwordInput.sendKeys("1234");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/admin"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Yönetim Paneli')]")));
    }

    @Test
    void adminTalebiKapatabilmeli() {
        adminLogin();

        driver.get(BASE_URL + "/admin/talepler");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Talepler']")));

        WebElement closeButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//tr[.//*[contains(normalize-space(.), '" + TICKET_TITLE + "')]]//button[normalize-space()='Kapat']")
        ));
        closeButton.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//tr[.//*[contains(normalize-space(.), '" + TICKET_TITLE + "')]]//*[normalize-space()='Kapalı']")
        ));

        String body = driver.findElement(By.tagName("body")).getText();
        assertTrue(body.contains(TICKET_TITLE), "Kapatılan talep listede görünmeli");
        assertTrue(body.contains("Kapalı"), "Talep durumu Kapalı olmalı");
    }
}
