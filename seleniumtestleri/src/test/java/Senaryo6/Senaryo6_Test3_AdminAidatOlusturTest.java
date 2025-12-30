package Senaryo6;

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

public class Senaryo6_Test3_AdminAidatOlusturTest {

    private WebDriver driver;
    private static final String BASE_URL = "http://localhost:1313";

    static final String DUES_MONTH = "1";
    static final String DUES_YEAR = "2027";
    static final String DUES_AMOUNT = "100";

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
    void adminAidatOlusturabilmeli() {
        adminLogin();
        driver.get(BASE_URL + "/admin/aidatlar");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Aidatlar']")));

        Select flatSelect = new Select(selectByLabel("Daire"));
        boolean selected = false;
        for (WebElement opt : flatSelect.getOptions()) {
            if (opt.getText() != null && opt.getText().contains("Kapı " + Senaryo6_Test1_AdminBlokTipDaireOlusturTest.DOOR_NUMBER)) {
                flatSelect.selectByVisibleText(opt.getText());
                selected = true;
                break;
            }
        }
        assertTrue(selected, "S6 daire seçilemedi");

        inputByLabel("Ay").sendKeys(DUES_MONTH);
        inputByLabel("Yıl").clear();
        inputByLabel("Yıl").sendKeys(DUES_YEAR);
        inputByLabel("Tutar").sendKeys(DUES_AMOUNT);

        driver.findElement(By.xpath("//button[normalize-space()='Oluştur']")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), '" + DUES_MONTH + "/" + DUES_YEAR + "')]")));
        assertTrue(driver.findElement(By.tagName("body")).getText().contains(DUES_MONTH + "/" + DUES_YEAR));
    }
}
