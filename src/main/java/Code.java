	import org.openqa.selenium.By;
	import java.util.List;
	import org.openqa.selenium.WebDriver;
	import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
	import org.openqa.selenium.firefox.GeckoDriverService;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;
public class Code {
	public static void main(String[] args) {
		int howManyPages = 0;	//Number of pages to check
		int currentPage = 1;
		String completeUrl;
		int startPage = 0;		//Number of starting page (if the user insert a url with a number of page)
		//System.setProperty(GeckoDriverService.GECKO_DRIVER_EXE_PROPERTY,"C:\\Users\\amman\\Documents\\Missingtech\\PluginDriverConfig\\geckodriver\\geckodriver.exe" );
		System.setProperty("webdriver.chrome.driver", "C:\\Driver\\chromedriver\\chromedriver.exe");
		
		Scanner scan = new Scanner(System.in);
		System.out.print("Inserire il link del sito cineblog: ");
		String url = scan.next();
		int length = url.length();
		
		//Nel caso in cui l'url termini con il carattere '/' questo viene rimosso
		if(url.charAt(length-1) == '/') {	                
			url = url.substring(0, length-1);
		}
		
		//Se l'url non parte dalla prima pagina, ma da una gi� specificata bisogna individuare il numero di pagina:
		if(url.contains("page")){
			length = url.length();
			if(url.charAt(length-2) == '/') {	
				char prova = url.charAt(length-1);
				startPage = Character.getNumericValue(prova);
				url = url.substring(0, length-7);
			}else {
				String prova = url.substring(length - 2, length);
				startPage = Integer.parseInt(prova);
				url = url.substring(0, length-8);
			}
			completeUrl = url + "/page/" + startPage;
			
		}else {	//Altrimenti se l'url comincia dalla prima pagina:
			completeUrl = url;
			startPage = currentPage;
		}
		
		System.out.print("Pagine da controllare: ");
		howManyPages = scan.nextInt();
		
		//WebDriver driver = new FirefoxDriver();
		WebDriver driver = new ChromeDriver();
		System.out.println(completeUrl);
		driver.get(completeUrl);
	
	do {
		double topLeft = 0;
		double centerLeft = 0;
		double bottomLeft = 0;
		
		//Contiene il numero di elementi nella pagina (12 poster di film per pagina)
		List<WebElement> fiveStarElement = driver.findElements(By.xpath("//div[@class='card-stacked']"));
		
		int i = 0;
		while (i < fiveStarElement.size()) {
			List<WebElement> fiveIndividualStars = fiveStarElement.get(i).findElements(By.className("rating-star-icon"));
					//xpath("//div[starts-with(@id,'PDRTJS_2105735_post_') and contains(@id, 'stars')]"));
			int j = 0;
			while(j < fiveIndividualStars.size()) {
				String style = fiveIndividualStars.get(j).getAttribute("style");
				if(style.contains("left top")) {
					topLeft+=1;
				}else if(style.contains("left center")) {
					centerLeft += 0.5;
				}else if(style.contains("left bottom")) {
					bottomLeft += 0;
				}				
				j++;
			}
			
			//Il totale del punteggio � dato dalla somma di questi tre elementi:
			double totalRate = topLeft + centerLeft + bottomLeft;			
			
				//Se il film supera le 4,5 stelle stampa il nome, voto, pagina e link
			if(totalRate >= 4.5) {	
				String filmName =	fiveStarElement.get(i).findElement(By.tagName("a")).getText();
				String filmLink = fiveStarElement.get(i).findElement(By.tagName("a")).getAttribute("href");
				System.out.println(filmName + "\tVoto: " + totalRate + " su 5" + "\tPagina " + startPage + " URL: " + filmLink);
			}
			
			topLeft = centerLeft = bottomLeft = 0; //reset
			i++;
		}
			
		currentPage ++;
		startPage ++;
		//Clicca sulla pagina successiva 
		String nextPage = String.format("//a[@href='%s/page/%s/']",url, startPage);
		driver.findElement(By.xpath(nextPage)).click(); 
				
		//Aspetta che l'url della pagina successiva sia caricato
		WebDriverWait wait = new WebDriverWait(driver, 20); 
		wait.until(ExpectedConditions.urlToBe(url + "/page/" + startPage +"/"));
				
	}while(currentPage <= howManyPages);	
		driver.close();
		scan.close();
	}
}
