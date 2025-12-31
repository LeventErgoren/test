package Senaryo5;

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

public class Senaryo5FlowTest {

    private WebDriver driver;
    private static final String BASE_URL = "http://localhost:1313";

    private static final String BLOCK_NAME = "S5 Blok";
    private static final String FLAT_TYPE_NAME = "S5 Tip";
    private static final String DOOR_NUMBER = "505";

    private static final String EMAIL = "s5_profile@mail.com";
    private static final String PHONE = "05500000016";

    @Test
    void senaryo5_bastanSona() {
        step1_adminBlokOlustur();
        step2_adminDaireTipiOlustur();
        step3_adminDaireOlustur();
        step4_adminSakinOlustur_ve_profilGor();
    }

    private void withDriver(Runnable step) {
        driver = new ChromeDriver();
        try {
            step.run();
        } finally {
            try {
                if (driver != null) driver.quit();
            } finally {
                driver = null;
            }
        }
    }

    private WebElement inputByLabel(String labelText) {
        return driver.findElement(By.xpath("//label[.//div[normalize-space() = '" + labelText + "']]//input"));
    }

    private WebElement selectByLabel(String labelText) {
        return driver.findElement(By.xpath("//label[.//div[normalize-space() = '" + labelText + "']]//select"));
    }

    private void adminLogin() {
        driver.get(BASE_URL + "/admin/giris");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[autocomplete='username']"))).sendKeys("admin");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[autocomplete='current-password']"))).sendKeys("1234");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlToBe(BASE_URL + "/admin"),
                ExpectedConditions.urlToBe(BASE_URL + "/admin/"),
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(normalize-space(),'Yönetim Paneli')]"))
        ));
    }

    private void residentLogin() {
        driver.get(BASE_URL + "/sakin/giris");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Sakin Girişi')]")));

        WebElement email = inputByLabel("E-posta");
        email.clear();
        email.sendKeys(EMAIL);

        WebElement phone = inputByLabel("Telefon");
        phone.clear();
        phone.sendKeys(PHONE);
        driver.findElement(By.xpath("//button[normalize-space()='Giriş Yap']")).click();

        wait.until(ExpectedConditions.urlContains("/sakin"));
    }

    private void step1_adminBlokOlustur() {
        withDriver(() -> {
            adminLogin();
            driver.get(BASE_URL + "/admin/bloklar");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Bloklar']")));

            inputByLabel("Blok adı").sendKeys(BLOCK_NAME);
            inputByLabel("Toplam kat").sendKeys("3");
            driver.findElement(By.xpath("//button[normalize-space()='Ekle']")).click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), '" + BLOCK_NAME + "')]")));
            assertTrue(driver.findElement(By.tagName("body")).getText().contains(BLOCK_NAME));
        });
    }

    private void step2_adminDaireTipiOlustur() {
        withDriver(() -> {
            adminLogin();
            driver.get(BASE_URL + "/admin/daire-tipleri");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Daire Tipleri']")));

            inputByLabel("Tip adı").sendKeys(FLAT_TYPE_NAME);
            inputByLabel("Varsayılan aidat").sendKeys("50");
            driver.findElement(By.xpath("//button[normalize-space()='Ekle']")).click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), '" + FLAT_TYPE_NAME + "')]")));
            assertTrue(driver.findElement(By.tagName("body")).getText().contains(FLAT_TYPE_NAME));
        });
    }

    private void step3_adminDaireOlustur() {
        withDriver(() -> {
            adminLogin();
            driver.get(BASE_URL + "/admin/daireler");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Daireler']")));

            inputByLabel("Kapı No").sendKeys(DOOR_NUMBER);
            inputByLabel("Kat").sendKeys("1");

            new Select(selectByLabel("Durum")).selectByVisibleText("Boş");
            new Select(selectByLabel("Blok")).selectByVisibleText(BLOCK_NAME);
            new Select(selectByLabel("Daire tipi")).selectByVisibleText(FLAT_TYPE_NAME);

            driver.findElement(By.xpath("//button[normalize-space()='Ekle']")).click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), '" + DOOR_NUMBER + "')]")));
            assertTrue(driver.findElement(By.tagName("body")).getText().contains(DOOR_NUMBER));
        });
    }

    private void step4_adminSakinOlustur_ve_profilGor() {
        withDriver(() -> {
            adminLogin();
            driver.get(BASE_URL + "/admin/sakinler");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Sakinler']")));

            inputByLabel("Ad").sendKeys("S5");
            inputByLabel("Soyad").sendKeys("Resident");
            inputByLabel("Telefon").sendKeys(PHONE);
            inputByLabel("E-posta").sendKeys(EMAIL);

            new Select(selectByLabel("Tür")).selectByVisibleText("Kiracı");

            Select flatSelect = new Select(selectByLabel("Daire"));
            boolean selected = false;
            for (WebElement opt : flatSelect.getOptions()) {
                if (opt.getText() != null && opt.getText().contains("Kapı " + DOOR_NUMBER)) {
                    flatSelect.selectByVisibleText(opt.getText());
                    selected = true;
                    break;
                }
            }
            assertTrue(selected, "S5 oluşturulan daire seçilemedi");

            driver.findElement(By.xpath("//button[normalize-space()='Ekle']")).click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), '" + EMAIL + "')]")));
        });

        withDriver(() -> {
            residentLogin();

            driver.get(BASE_URL + "/sakin/profil");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Profil']")));

            String body = driver.findElement(By.tagName("body")).getText();
            assertTrue(body.contains(EMAIL), "Profilde e-posta görünmeli");
            assertTrue(body.contains(BLOCK_NAME), "Profilde blok adı görünmeli");
            assertTrue(body.contains(DOOR_NUMBER), "Profilde kapı no görünmeli");
        });
    }
}
