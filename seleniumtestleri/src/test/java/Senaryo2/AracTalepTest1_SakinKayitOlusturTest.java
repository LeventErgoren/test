package Senaryo2;

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

public class AracTalepTest1_SakinKayitOlusturTest {

    private WebDriver driver;

    private static final String BASE_URL = "http://localhost:1313";

    // Bu değerler AracTalepTest2-3-5'te de kullanılacak
    static final String RESIDENT_FIRST_NAME = "Selenium";
    static final String RESIDENT_LAST_NAME = "AracTalep";
    static final String RESIDENT_EMAIL = "selenium_arac_talep@mail.com";
    static final String RESIDENT_PHONE = "05500000009";

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
    void sakinKayitOlusturVeSakinPaneleGirmeli() {
        driver.get(BASE_URL + "/sakin/kayit");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Sakin Kaydı')]")));

        inputByLabel("Ad").sendKeys(RESIDENT_FIRST_NAME);
        inputByLabel("Soyad").sendKeys(RESIDENT_LAST_NAME);
        inputByLabel("E-posta").sendKeys(RESIDENT_EMAIL);
        inputByLabel("Telefon").sendKeys(RESIDENT_PHONE);

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

        assertTrue(driver.getCurrentUrl().contains("/sakin"), "Kayıt sonrası /sakin'e yönlenmeli");
    }
}
