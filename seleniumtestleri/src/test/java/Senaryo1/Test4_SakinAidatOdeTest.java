package Senaryo1;

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

public class Test4_SakinAidatOdeTest {

    private WebDriver driver;

    private static final String BASE_URL = "http://localhost:1313";

    // Test1'de oluşturulan sakin
    private static final String RESIDENT_EMAIL = "selenium1@mail.com";
    private static final String RESIDENT_PHONE = "05500000001";

    // Test2'de oluşturulan aidat
    private static final String DUES_MONTH = "1";
    private static final String DUES_YEAR = "2026";
    private static final String DUES_AMOUNT = "100";

    @BeforeEach
    void setup() {
        driver = new ChromeDriver();
    }

    @AfterEach
    void teardown() {
        if (driver != null) driver.quit();
    }

    private void residentLogin() {
        driver.get(BASE_URL + "/sakin/giris");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement emailInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[autocomplete='email']"))
        );
        WebElement phoneInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[autocomplete='tel']"))
        );

        emailInput.sendKeys(RESIDENT_EMAIL);
        phoneInput.sendKeys(RESIDENT_PHONE);
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/sakin"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Sakin Paneli')]")));
    }

    private WebElement selectByLabel(String labelText) {
        return driver.findElement(By.xpath("//label[.//div[normalize-space() = '" + labelText + "']]//select"));
    }

    private WebElement inputByLabel(String labelText) {
        return driver.findElement(By.xpath("//label[.//div[normalize-space() = '" + labelText + "']]//input"));
    }

    @Test
    void sakinAidatiOdeyebilmeli() {
        residentLogin();

        // Ödeme Yap
        driver.findElement(By.linkText("Ödeme Yap")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("/sakin/odeme"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[contains(text(),'Ödeme Yap')]")));

        // Aidat seç (1/2026)
        Select duesSelect = new Select(selectByLabel("Aidat"));
        boolean selected = false;
        for (WebElement opt : duesSelect.getOptions()) {
            if (opt.getText() != null && opt.getText().contains(DUES_MONTH + "/" + DUES_YEAR)) {
                duesSelect.selectByVisibleText(opt.getText());
                selected = true;
                break;
            }
        }
        assertTrue(selected, "Ödeme ekranında " + DUES_MONTH + "/" + DUES_YEAR + " aidatı seçilemedi.");

        // Tutar
        WebElement amountInput = inputByLabel("Tutar");
        amountInput.clear();
        amountInput.sendKeys(DUES_AMOUNT);

        // Gönder
        driver.findElement(By.xpath("//button[normalize-space()='Ödemeyi Gönder']")).click();

        // Başarılı mesajı
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Ödeme kaydedildi.')]")));

        // Aidatlarım ekranında artık "Ödenmiş Aidatlar" içinde görünmeli
        driver.findElement(By.linkText("Aidatlarım")).click();
        wait.until(ExpectedConditions.urlContains("/sakin/aidatlarim"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Ödenmiş Aidatlar')]")));

        String body = driver.findElement(By.tagName("body")).getText();
        assertTrue(body.contains(DUES_MONTH + "/" + DUES_YEAR), "Ödenen aidat döneminin listede görünmesi beklenir");
    }
}
