package Senaryo5;

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

public class Senaryo5_Test4_AdminSakinOlusturVeProfilGorTest {

    private WebDriver driver;
    private static final String BASE_URL = "http://localhost:1313";

    private static final String EMAIL = "s5_profile@mail.com";
    private static final String PHONE = "05500000016";

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
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[autocomplete='username']"))).sendKeys("admin");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[autocomplete='current-password']"))).sendKeys("1234");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(ExpectedConditions.urlContains("/admin"));
    }

    private void residentLogin() {
        driver.get(BASE_URL + "/sakin/giris");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Sakin Girişi')]")));

        inputByLabel("E-posta").sendKeys(EMAIL);
        inputByLabel("Telefon").sendKeys(PHONE);
        driver.findElement(By.xpath("//button[normalize-space()='Giriş Yap']")).click();

        wait.until(ExpectedConditions.urlContains("/sakin"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Sakin Paneli')]")));
    }

    private WebElement inputByLabel(String labelText) {
        return driver.findElement(By.xpath("//label[.//div[normalize-space() = '" + labelText + "']]//input"));
    }

    private WebElement selectByLabel(String labelText) {
        return driver.findElement(By.xpath("//label[.//div[normalize-space() = '" + labelText + "']]//select"));
    }

    @Test
    void adminYeniSakinOlusturupSakinProfildeDaireyiGormeli() {
        adminLogin();
        driver.get(BASE_URL + "/admin/sakinler");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Sakinler']")));

        inputByLabel("Ad").sendKeys("S5");
        inputByLabel("Soyad").sendKeys("Resident");
        inputByLabel("Telefon").sendKeys(PHONE);
        inputByLabel("E-posta").sendKeys(EMAIL);

        new Select(selectByLabel("Tür")).selectByVisibleText("Kiracı");

        // Senaryo5'te oluşturduğumuz daireyi seç
        Select flatSelect = new Select(selectByLabel("Daire"));
        boolean selected = false;
        for (WebElement opt : flatSelect.getOptions()) {
            if (opt.getText() != null && opt.getText().contains("Kapı " + Senaryo5_Test3_AdminDaireOlusturTest.DOOR_NUMBER)) {
                flatSelect.selectByVisibleText(opt.getText());
                selected = true;
                break;
            }
        }
        assertTrue(selected, "S5 oluşturulan daire seçilemedi");

        driver.findElement(By.xpath("//button[normalize-space()='Ekle']")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), '" + EMAIL + "')]")));

        // Sakin giriş + profil doğrulama
        residentLogin();
        driver.get(BASE_URL + "/sakin/profil");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Profil']")));

        String body = driver.findElement(By.tagName("body")).getText();
        assertTrue(body.contains(EMAIL), "Profilde e-posta görünmeli");
        assertTrue(body.contains(Senaryo5_Test1_AdminBlokOlusturTest.BLOCK_NAME), "Profilde blok adı görünmeli");
        assertTrue(body.contains(Senaryo5_Test3_AdminDaireOlusturTest.DOOR_NUMBER), "Profilde kapı no görünmeli");
    }
}
