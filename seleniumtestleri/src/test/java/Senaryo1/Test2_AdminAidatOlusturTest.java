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

public class Test2_AdminAidatOlusturTest {

    private WebDriver driver;

    private static final String BASE_URL = "http://localhost:1313";

    // Test1'de seçtiğimiz daireyle aynı hedef
    private static final String TARGET_FLAT_TEXT_CONTAINS = "Kapı 2";

    // Test3-4'te kontrol edilecek dönem/tutar
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

    private WebElement selectByLabel(String labelText) {
        return driver.findElement(By.xpath("//label[.//div[normalize-space() = '" + labelText + "']]//select"));
    }

    private WebElement inputByLabel(String labelText) {
        return driver.findElement(By.xpath("//label[.//div[normalize-space() = '" + labelText + "']]//input"));
    }

    @Test
    void adminAidatOlusturabilmeli() {
        adminLogin();

        // Menüden Aidatlar
        driver.findElement(By.linkText("Aidatlar")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("/admin/aidatlar"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[contains(text(),'Aidatlar')]")));

        // Daire seç (Kapı 2)
        Select flatSelect = new Select(selectByLabel("Daire"));
        boolean selected = false;
        for (WebElement opt : flatSelect.getOptions()) {
            if (opt.getText() != null && opt.getText().contains(TARGET_FLAT_TEXT_CONTAINS)) {
                flatSelect.selectByVisibleText(opt.getText());
                selected = true;
                break;
            }
        }
        assertTrue(selected, "Aidat ekranında '" + TARGET_FLAT_TEXT_CONTAINS + "' bulunan bir daire seçilemedi.");

        WebElement monthInput = inputByLabel("Ay");
        WebElement yearInput = inputByLabel("Yıl");
        WebElement amountInput = inputByLabel("Tutar");

        monthInput.clear();
        monthInput.sendKeys(DUES_MONTH);
        yearInput.clear();
        yearInput.sendKeys(DUES_YEAR);
        amountInput.clear();
        amountInput.sendKeys(DUES_AMOUNT);

        // Oluştur
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Listeye düşmesini kontrol et
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'" + DUES_MONTH + "/" + DUES_YEAR + "')]")));
        String body = driver.findElement(By.tagName("body")).getText();
        assertTrue(body.contains(DUES_MONTH + "/" + DUES_YEAR), "Oluşturulan aidat dönemi listede görünmeli");
    }
}
