package Senaryo7;

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

public class Senaryo7_Test2_IkinciEvSahibiHataTest {

    private WebDriver driver;

    private static final String BASE_URL = "http://localhost:1313";

    static final String OWNER2_FIRST = "S7";
    static final String OWNER2_LAST = "Owner2";
    static final String OWNER2_EMAIL = "s7_owner2@mail.com";
    static final String OWNER2_PHONE = "05500000019";

    private static final String TARGET_FLAT_TEXT_CONTAINS = "Kapı 2";

    @BeforeEach
    void setup() { driver = new ChromeDriver(); }

    @AfterEach
    void teardown() { if (driver != null) driver.quit(); }

    private WebElement inputByLabel(String labelText) {
        return driver.findElement(By.xpath("//label[.//div[normalize-space() = '" + labelText + "']]//input"));
    }

    private WebElement selectByLabel(String labelText) {
        return driver.findElement(By.xpath("//label[.//div[normalize-space() = '" + labelText + "']]//select"));
    }

    @Test
    void ayniDaireyeIkinciEvSahibiEklenememeli() {
        driver.get(BASE_URL + "/sakin/kayit");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Sakin Kaydı')]")));

        inputByLabel("Ad").sendKeys(OWNER2_FIRST);
        inputByLabel("Soyad").sendKeys(OWNER2_LAST);
        inputByLabel("E-posta").sendKeys(OWNER2_EMAIL);
        inputByLabel("Telefon").sendKeys(OWNER2_PHONE);

        new Select(selectByLabel("Tür")).selectByVisibleText("Ev sahibi");

        Select flat = new Select(selectByLabel("Daire"));
        boolean selected = false;
        for (WebElement opt : flat.getOptions()) {
            if (opt.getText() != null && opt.getText().contains(TARGET_FLAT_TEXT_CONTAINS)) {
                flat.selectByVisibleText(opt.getText());
                selected = true;
                break;
            }
        }
        assertTrue(selected);

        driver.findElement(By.xpath("//button[normalize-space()='Kayıt Ol']")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZİÖÜŞĞÇ','abcdefghijklmnopqrstuvwxyzıöüşğç'), 'owner') or contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZİÖÜŞĞÇ','abcdefghijklmnopqrstuvwxyzıöüşğç'), 'yasal sahibi')]")
        ));

        String body = driver.findElement(By.tagName("body")).getText().toLowerCase();
        assertTrue(body.contains("owner") || body.contains("yasal sahibi"), "Tek ev sahibi kuralı hatası görünmeli");
    }
}
