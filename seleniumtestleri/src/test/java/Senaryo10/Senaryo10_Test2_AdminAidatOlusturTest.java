package Senaryo10;

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

public class Senaryo10_Test2_AdminAidatOlusturTest {

    private WebDriver driver;
    private static final String BASE_URL = "http://localhost:1313";

    static final int MONTH = 2;
    static final int YEAR = 2028;
    static final String AMOUNT = "120";

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
        return driver.findElement(By.xpath("//label[.//div[normalize-space() = '" + labelText + "'] or .//*[normalize-space() = '" + labelText + "']]//input"));
    }

    private WebElement selectByLabel(String labelText) {
        return driver.findElement(By.xpath("//label[.//div[normalize-space() = '" + labelText + "'] or .//*[normalize-space() = '" + labelText + "']]//select"));
    }

    @Test
    void adminAidatOlusturabilmeli() {
        adminLogin();
        driver.get(BASE_URL + "/admin/aidatlar");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Aidatlar']")));

        // Ay/Yıl alanları UI'da number input.
        WebElement monthInput = inputByLabel("Ay");
        monthInput.clear();
        monthInput.sendKeys(String.valueOf(MONTH));

        WebElement yearInput = inputByLabel("Yıl");
        yearInput.clear();
        yearInput.sendKeys(String.valueOf(YEAR));

        WebElement amountInput = inputByLabel("Tutar");
        amountInput.clear();
        amountInput.sendKeys(AMOUNT);

        // Daire: Kapı 2
        wait.until(d -> d.findElements(By.xpath("//label[.//div[normalize-space()='Daire'] or .//*[normalize-space()='Daire']]//option[contains(.,'Kapı 2')] ")).size() > 0);
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

        driver.findElement(By.xpath("//button[normalize-space()='Oluştur']")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), '" + MONTH + "/" + YEAR + "')]")));
        assertTrue(driver.findElement(By.tagName("body")).getText().contains(MONTH + "/" + YEAR));
    }
}
