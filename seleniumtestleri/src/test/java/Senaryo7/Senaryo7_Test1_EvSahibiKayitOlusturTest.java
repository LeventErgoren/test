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

public class Senaryo7_Test1_EvSahibiKayitOlusturTest {

    private WebDriver driver;

    private static final String BASE_URL = "http://localhost:1313";

    static final String OWNER1_FIRST = "S7";
    static final String OWNER1_LAST = "Owner";
    static final String OWNER1_EMAIL = "s7_owner1@mail.com";
    static final String OWNER1_PHONE = "05500000018";

    private static final String TARGET_FLAT_TEXT_CONTAINS = "Kapı 2";

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

    private WebElement selectByLabel(String labelText) {
        return driver.findElement(By.xpath("//label[.//div[normalize-space() = '" + labelText + "']]//select"));
    }

    @Test
    void evSahibiKayitOlusturabilmeli() {
        driver.get(BASE_URL + "/sakin/kayit");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Sakin Kaydı')]")));

        inputByLabel("Ad").sendKeys(OWNER1_FIRST);
        inputByLabel("Soyad").sendKeys(OWNER1_LAST);
        inputByLabel("E-posta").sendKeys(OWNER1_EMAIL);
        inputByLabel("Telefon").sendKeys(OWNER1_PHONE);

        new Select(selectByLabel("Tür")).selectByVisibleText("Ev sahibi");

        WebElement flatSelect = selectByLabel("Daire");
        Select select = new Select(flatSelect);
        boolean selected = false;
        for (WebElement opt : select.getOptions()) {
            if (opt.getText() != null && opt.getText().contains(TARGET_FLAT_TEXT_CONTAINS)) {
                select.selectByVisibleText(opt.getText());
                selected = true;
                break;
            }
        }
        assertTrue(selected, "Kayıt ekranında '" + TARGET_FLAT_TEXT_CONTAINS + "' bulunan bir daire seçilemedi.");

        driver.findElement(By.xpath("//button[normalize-space()='Kayıt Ol']")).click();

        wait.until(ExpectedConditions.urlContains("/sakin"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Sakin Paneli')]")));

        assertTrue(driver.getCurrentUrl().contains("/sakin"));
    }
}
