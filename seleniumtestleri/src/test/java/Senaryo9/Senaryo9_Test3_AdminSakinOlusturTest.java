package Senaryo9;

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

public class Senaryo9_Test3_AdminSakinOlusturTest {

    private WebDriver driver;
    private static final String BASE_URL = "http://localhost:1313";

    static final String RES_EMAIL = "s9_resident@mail.com";
    static final String RES_PHONE = "05500000021";

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
    void adminSakinOlusturabilmeli() {
        adminLogin();
        driver.get(BASE_URL + "/admin/sakinler");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Sakinler']")));

        inputByLabel("Ad").sendKeys("S9");
        inputByLabel("Soyad").sendKeys("Resident");
        inputByLabel("Telefon").sendKeys(RES_PHONE);
        inputByLabel("E-posta").sendKeys(RES_EMAIL);
        new Select(selectByLabel("Tür")).selectByVisibleText("Kiracı");

        // Daire seç: Kapı 2 (seeder'dan)
        Select flatSelect = new Select(selectByLabel("Daire"));
        boolean selected = false;
        for (WebElement opt : flatSelect.getOptions()) {
            if (opt.getText() != null && opt.getText().contains("Kapı 2")) {
                flatSelect.selectByVisibleText(opt.getText());
                selected = true;
                break;
            }
        }
        assertTrue(selected, "Kapı 2 daire seçilemedi");

        driver.findElement(By.xpath("//button[normalize-space()='Ekle']")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), '" + RES_EMAIL + "')]")));
        assertTrue(driver.findElement(By.tagName("body")).getText().contains(RES_EMAIL));
    }
}
