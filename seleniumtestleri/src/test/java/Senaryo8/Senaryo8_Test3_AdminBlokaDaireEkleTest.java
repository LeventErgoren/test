package Senaryo8;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class Senaryo8_Test3_AdminBlokaDaireEkleTest {

    private WebDriver driver;
    private static final String BASE_URL = "http://localhost:1313";

    static final String DOOR_NUMBER = "808";

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

    private WebElement inputByLabel(String labelText) {
        return driver.findElement(By.xpath("//label[.//div[normalize-space() = '" + labelText + "']]//input"));
    }

    private WebElement selectByLabel(String labelText) {
        return driver.findElement(By.xpath("//label[.//div[normalize-space() = '" + labelText + "']]//select"));
    }

    @Test
    void adminBlokaDaireEkleyebilmeli() {
        adminLogin();
        driver.get(BASE_URL + "/admin/daireler");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Daireler']")));

        inputByLabel("Kapı No").sendKeys(DOOR_NUMBER);
        inputByLabel("Kat").sendKeys("1");
        new Select(selectByLabel("Durum")).selectByVisibleText("Boş");

        new Select(selectByLabel("Blok")).selectByVisibleText(Senaryo8_Test1_AdminBlokOlusturTest.BLOCK_NAME);

        // Daire tipi: ilk seçenek yeterli
        Select typeSelect = new Select(selectByLabel("Daire tipi"));
        if (!typeSelect.getOptions().isEmpty()) {
            typeSelect.selectByIndex(0);
        }

        driver.findElement(By.xpath("//button[normalize-space()='Ekle']")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), '" + DOOR_NUMBER + "')]")));
        assertTrue(driver.findElement(By.tagName("body")).getText().contains(DOOR_NUMBER));
    }
}
