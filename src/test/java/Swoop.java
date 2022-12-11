import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class Swoop {
    WebDriver driver;
    OpenWebsite openWebsite;
    SwoopAction elementAction;

    @BeforeTest
    @Parameters("browser")
    public void Setup(@Optional("chrome") String browser) throws InterruptedException {
        this.openWebsite = new OpenWebsite("https://www.swoop.ge", true, true, 7000);
        this.openWebsite.setDriver(this.driver, "chrome");
        this.elementAction = new SwoopAction(this.openWebsite.getDriver());
    }

    @Test
    public void testSwoop() throws Exception {
        this.elementAction.navigate("კინო");
        try {
            this.elementAction.chooseMovie(false, 1, "");
            String cinemaId = this.elementAction.chooseCinema("კავეა ისთ ფოინთი", 300, 0);
            String date = this.elementAction.chooseCalendar(-1, 0, 500);

            String[] cinemaData =  this.elementAction.chooseSeanse(date,cinemaId);
            String cinemaTime = cinemaData[0];
            String cinemaName = cinemaData[1];
            String title = this.elementAction.getMovieTitle();

            String[] fullDate = date.split("\\.");
            String formatedDate = this.elementAction.formatDate(fullDate,cinemaTime);
            this.elementAction.validatePageData(title, cinemaName, formatedDate);

            this.elementAction.chooseSeat();
            this.elementAction.register();
        } catch (Exception e) {
            Assert.fail(e.toString());
        }
//        Thread.sleep(5000);
    }


    @AfterClass
    public void TearDown() throws InterruptedException {

        this.openWebsite.getShutdown();
    }

}
