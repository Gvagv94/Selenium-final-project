import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

public class OpenWebsite {
    public WebDriver driver;
    private String getUrl;
    private Boolean maximize;
    private Boolean shutdown;
    private Integer timeBeforeShutdown;




    public OpenWebsite(String getUrl, Boolean maximize, Boolean shutdown,  Integer timeBeforeShutdown){
        this.getUrl = getUrl;
        this.maximize = maximize;
        this.shutdown = shutdown;
        this.timeBeforeShutdown = timeBeforeShutdown;
    }
//Overload If shutdown is false
    public OpenWebsite(String getUrl, boolean maximize, boolean shutdown) {
        this.getUrl = getUrl;
        this.maximize = maximize;
        this.shutdown = shutdown;
    }

    public WebDriver getDriver() {
        this.driver.get(this.getUrl);
        if(maximize){
            this.driver.manage().window().maximize();
        }
        return this.driver;
    }

    public void setDriver(WebDriver driver,String browser) {
        if (browser.equalsIgnoreCase("Firefox")) {
            WebDriverManager.firefoxdriver().setup();
            this.driver = new FirefoxDriver();
        }else {
            WebDriverManager.chromedriver().setup();
            this.driver = new ChromeDriver();
        }
    }

    public String getGetUrl() {
        return getUrl;
    }

    public void setGetUrl(String getUrl) {
        this.getUrl = getUrl;
    }

    public Boolean getMaximize() {
        return maximize;
    }

    public void setMaximize(Boolean maximize) {
        this.maximize = maximize;
    }

    public Boolean getShutdown() throws InterruptedException {
        if(shutdown){
            Thread.sleep(this.timeBeforeShutdown);
            this.driver.quit();

        }
        return shutdown;
    }

    public void setShutdown(Boolean shutdown) {
        this.shutdown = shutdown;
    }

    public Integer getTimeBeforeShutdown() {
        return timeBeforeShutdown;
    }

    public void setTimeBeforeShutdown(Integer timeBeforeShutdown) {
        this.timeBeforeShutdown = timeBeforeShutdown;
    }
}
