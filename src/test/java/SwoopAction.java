import com.github.javafaker.Faker;
import com.google.common.util.concurrent.FakeTimeLimiter;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Integer.parseInt;

public class SwoopAction {

    public WebDriver driver;
    protected Actions action;
    JavascriptExecutor js;

    public SwoopAction(WebDriver driver) {
        this.driver = driver;
        this.action = new Actions(driver);
        this.js = (JavascriptExecutor) driver;
    }

    public void navigate(String menuName) {
        WebElement navigation = this.driver.findElement(By.className("category-navigation-ul"));
        List<WebElement> li = navigation.findElements(By.tagName("li"));
        for (int i = 0; i < li.size(); i++) {
            if (li.get(i).getText().trim().equalsIgnoreCase(menuName)) {
                li.get(i).click();
                break;
            }
        }
    }

    public void movieByName(List<WebElement> movies, String movieName, Boolean trailer) {
        if (!movieName.equals("")) {
            for (int i = 0; i < movies.size(); i++) {
                if (movieName.equals(movies.get(i).getText())) {
                    this.action.moveToElement(movies.get(i)).perform();
                    if (trailer) {
                        movies.get(i).findElement(By.className("trailer")).click();
                    } else {
                        movies.get(i).findElement(By.className("info-cinema-ticket")).click();
                    }
                    break;
                }
            }
        }
    }

    public void movieById(List<WebElement> movies, Integer id, Boolean trailer) {
        if (id != -1) {
            for (int i = 0; i < movies.size(); i++) {
                if (i == id - 1) {
                    this.action.moveToElement(movies.get(i)).perform();
                    if (trailer) {
                        movies.get(i).findElement(By.className("trailer")).click();
                    } else {
                        movies.get(i).findElement(By.className("info-cinema-ticket")).click();
                    }
                    break;
                }
            }
        }
    }

    public void chooseMovie(Boolean trailer, Integer id, String movieName) throws Exception {
        if ((!movieName.equals("") && id != -1) || (movieName.equals("") && id == -1)) {
            throw new Exception("Choose Only One Method");

        }
        WebElement navigation = this.driver.findElement(By.className("cinema_container"));
        List<WebElement> movies = navigation.findElements(By.className("movies-deal"));
        this.movieByName(movies, movieName, trailer);
        this.movieById(movies, id, trailer);
    }

    public void moveElement(String element, Integer x, Integer y) {
        this.js.executeScript(element + ".scrollBy(" + x + "," + y + ")");
    }

    public void checkIfOnlyCinemaNameSelected(List<WebElement> cinemaList, String cinemaName) throws Exception {
        for (int i = 0; i < cinemaList.size(); i++) {
            if (cinemaList.get(i).getText().trim().equalsIgnoreCase(cinemaName)) {
                if (!cinemaList.get(i).getAttribute("aria-selected").equalsIgnoreCase("true")) {
                    throw new Exception("Not Selected: " + cinemaName);
                }
            } else {
                if (cinemaList.get(i).getAttribute("aria-selected").equalsIgnoreCase("true")) {
                    throw new Exception("Not Selected: " + cinemaName);
                }
            }
        }
    }

    public String getMovieTitle() {
        return this.driver.findElement(By.className("movie_first_section")).findElement(By.className("name")).getText();
    }

    public String chooseCinema(String cinemaName, Integer x, Integer y) {
        this.moveElement("document.getElementsByClassName('cinema-tabs')[0]", x, y);
        WebElement cinemas = this.driver.findElement(By.className("cinema-tabs"));
        List<WebElement> cinemaList = cinemas.findElements(By.tagName("li"));
        String cinemaId = "";
        for (int i = 0; i < cinemaList.size(); i++) {
            if (cinemaList.get(i).getText().trim().equalsIgnoreCase(cinemaName)) {
                cinemaList.get(i).click();
                cinemaId = cinemaList.get(i).getAttribute("aria-controls");
            }
        }
        try {
            this.checkIfOnlyCinemaNameSelected(cinemaList, cinemaName);
        } catch (Exception e) {
            Assert.fail(e.toString());
        }
        return cinemaId;
    }

    public String chooseCalendar(Integer number, Integer x, Integer y) {
        this.moveElement("window", x, y);
        WebElement calendar = this.driver.findElement(By.xpath("//*[@id=\"384933\"]/div/ul"));
        List<WebElement> days = calendar.findElements(By.tagName("li"));
        if (number == -1) {
            days.get(days.size() - 1).click();
            return days.get(days.size() - 1).getAttribute("aria-controls");
        } else {
            days.get(number).click();
            return days.get(number).getAttribute("aria-controls");
        }

    }

    public String[] chooseSeanse(String dateId, String cinemaId) {
        List<WebElement> seanse = this.driver.findElement(By.id(cinemaId)).findElements(By.id(dateId));

        String cinemaTime = seanse.get(seanse.size() - 1).findElements(By.tagName("p")).get(0).getText();
        String cinemaTitle = seanse.get(seanse.size() - 1).findElement(By.className("cinema-title")).getText();

        seanse.get(seanse.size() - 1).click();

        return new String[]{cinemaTime, cinemaTitle};
    }

    public String getMonthText(Integer month) {
        String[] monthNames = {"იანვარი", "თებერვალი", "მარტი", "აპრილი", "მაისი", "ივნისი", "ივლისი", "აგვისტო", "სექტემბერი",
                "ოქტომბერი", "ნოემბერი", "დეკემბერი"};
        return monthNames[month];
    }

    public String formatDate(String[] fullDate, String time) {
        String getDay = fullDate[0].split("day-choose-")[1];
        String getMonth = this.getMonthText(parseInt(fullDate[1]) - 1);
        return getDay + " " + getMonth + " " + time;
    }

    public void webWait(Integer time, String getClassName, String getId) {
        WebDriverWait wait = new WebDriverWait(this.driver, time);
        if (!getClassName.equalsIgnoreCase("")) {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className(getClassName)));
        }
        if (!getId.equalsIgnoreCase("")) {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(getId)));
        }
    }

    public void validatePageData(String title, String cinema, String date) {
        this.webWait(6000, "content-header", "");
        WebElement content = this.driver.findElement(By.className("content-header"));
        List<WebElement> details = content.findElements(By.tagName("p"));
        ArrayList<String> data = new ArrayList<String>();
        data.add(title);
        data.add(cinema);
        data.add(date);
        for (int i = 0; i < details.size(); i++) {
            if (details.get(i).getText().equals(data.get(i))) {
                System.out.println(details.get(i).getText() + " ===>is correct");
            } else {
                Assert.fail("Data not equal");
            }
        }
    }

    public void chooseSeat() {
        WebElement seat = this.driver.findElement(By.className("free"));
        seat.click();
    }

    public void selectOption(WebElement selector, Integer index) {
        Select el = new Select(selector);
        el.selectByIndex(index);
    }

    public void inputWriteValue(String elementId, String fakeData) {
        this.driver.findElement(By.id(elementId)).sendKeys(fakeData);
    }

    public void register() throws AWTException {
        this.webWait(6000, "register", "");
        this.driver.findElement(By.className("register")).click();
        this.driver.findElement(By.className("juridial")).click();
//FILL FORM
        WebElement legalform = driver.findElement(By.id("lLegalForm"));
        this.selectOption(legalform,1);

        Faker faker = new Faker();
        this.inputWriteValue("lName",faker.funnyName().name());
        this.inputWriteValue("lTaxCode",faker.number().digits(9));
        this.inputWriteValue("registred","12122012");

        this.inputWriteValue("lAddress",faker.address().fullAddress());

        WebElement country = driver.findElement(By.id("lCountryCode"));
        this.selectOption(country, 10);

        this.inputWriteValue("lCity",faker.address().city());
        this.inputWriteValue("lPostalCode",faker.number().digits(4));

        this.inputWriteValue("lWebSite","www." + faker.animal().name());

        this.inputWriteValue("lBank",faker.name().name());

        this.inputWriteValue("lBankAccount","GE10TB" + faker.number().digits(16));

//        this.inputWriteValue("lContactPersonEmail",faker.number().digits(5));



        int pasword = Integer.parseInt(faker.number().digits(9));
        this.inputWriteValue("lContactPersonName",faker.name().fullName());

        WebElement gender = this.driver.findElement(By.id("lContactPersonGender"));
        this.selectOption(gender, 1);

        this.inputWriteValue("birthday","12051994");


        WebElement citizen = this.driver.findElement(By.id("lContactPersonCountryCode"));
        this.selectOption(citizen, 2);

        this.inputWriteValue("lContactPersonPersonalID",faker.idNumber().valid());

        this.inputWriteValue("lContactPersonPhone","G"+faker.phoneNumber().phoneNumber());

        WebElement regitrationbutton = this.driver.findElement(By.xpath("//*[@id=\"register-content-2\"]/div[2]/a/div/input"));
        regitrationbutton.click();

        this.moveElement("window", 0, 500);

        this.webWait(6000, "", "legalInfoMassage");
        WebElement errortext = driver.findElement(By.id("legalInfoMassage"));
        this.moveElement("document.getElementsByClassName('login-register-outer')[0]"
                ,0,500);
        if (errortext.isDisplayed()) {
            System.out.println("რეგისტრაციის დროს დაფიქსირდა შეცდომა!");
            Assert.fail("რეგისტრაციის დროს დაფიქსირდა შეცდომა!");
        } else {
            System.out.println("element is not displayd");
        }
        this.inputWriteValue("lContactPersonPassword",Integer.toString(pasword));
        this.inputWriteValue("lContactPersonConfirmPassword",Integer.toString(pasword));
        regitrationbutton.click();


    }
}
