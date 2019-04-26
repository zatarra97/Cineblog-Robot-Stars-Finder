	import org.openqa.selenium.By;
	import java.util.List;
	import org.openqa.selenium.WebDriver;
	import org.openqa.selenium.WebElement;
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
		boolean startUrl = true;
		int startPage = 0;		//Number of starting page (if the user insert a url with a number of page)
		System.setProperty(GeckoDriverService.GECKO_DRIVER_EXE_PROPERTY,"C:\\Users\\amman\\Documents\\Missingtech\\PluginDriverConfig\\geckodriver\\geckodriver.exe" );
		
		Scanner scan = new Scanner(System.in);
		System.out.print("Inserire il link del sito cineblog: ");
		String url = scan.next();
		int length = url.length();
		
		//Nel caso in cui l'url termini con un carattere '/' questi viene rimosso
		if(url.charAt(length-1) == '/') {	                
			url = url.substring(0, length-1);
		}
		if(url.contains("page"))		{
			startUrl = false;
			length = url.length();
			
			if(url.charAt(length-2) == '/') {	
				char prova = url.charAt(length-1);
				startPage = Character.getNumericValue(prova);
				url = url.substring(0, length-2);
			}else {
				String prova = url.substring(length - 2, length);
				startPage = Integer.parseInt(prova);
				url = url.substring(0, length-3);
			}
			//System.out.println("L'url contiene una pagina di inizio: " + startPage);
		}else {
			startUrl = true;
		}
		
		
		System.out.print("Quante pagine vuoi controllare? (-1 per arrivare fino all'ultima disponibile): ");
		howManyPages = scan.nextInt();
		WebDriver driver = new FirefoxDriver();
		
		if(startUrl== true) {
			System.out.println(url);
		driver.get(url);			//LINK: https://cb01.date
		}else{
			String completeUrl = url + "/" + startPage;
			System.out.println(completeUrl);
			driver.get(completeUrl);
		}
	
	do {
		double topLeft = 0;
		double centerLeft = 0;
		double bottomLeft = 0;
		
		//Contiene il numero di elementi nella pagina (di solito 12 poster di film)
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
			
			//Il totale del punteggio è dato dalla somma di questi tre elementi:
			double totalRate = topLeft + centerLeft + bottomLeft;			
			
				//Se il film supera le 4,5 stelle stampa il nome, voto, pagina e link
			if(totalRate >= 4.5) {	
				String filmName =	fiveStarElement.get(i).findElement(By.tagName("a")).getText();
				String filmLink = fiveStarElement.get(i).findElement(By.tagName("a")).getAttribute("href");
				if (startUrl == true) {
					System.out.println(filmName + "\tVoto: " + totalRate + " su 5" + "\tPagina " + currentPage + " URL: " + filmLink);
				}else {
					System.out.println(filmName + "\tVoto: " + totalRate + " su 5" + "\tPagina " + startPage + " URL: " + filmLink);
				}
			}
			
			topLeft = centerLeft = bottomLeft = 0; //reset
			i++;
				
			}
			
			currentPage ++;
			//Clicca sulla pagina successiva
			if (startUrl == true) {
				String nextPage = String.format("//a[@href='%s/page/%s/']",url, currentPage);
				//System.out.println("Controllerò url: " + nextPage);
				driver.findElement(By.xpath(nextPage)).click(); 
				
				//Aspetta che l'url sia caricato  (POSSIBILE PROBLEMA
				WebDriverWait wait = new WebDriverWait(driver, 20); 
				wait.until(ExpectedConditions.urlToBe(url + "/page/" + currentPage +"/"));
			}else if (startUrl == false) {
				startPage ++;
				String nextPage = String.format("//a[@href='%s/%s/']",url, startPage);
				//System.out.println("Controllerò url con pagina iniziale: " + nextPage);
				driver.findElement(By.xpath(nextPage)).click(); 
				
				//Aspetta che l'url sia caricato  (POSSIBILE PROBLEMA
				WebDriverWait wait = new WebDriverWait(driver, 20); 
				wait.until(ExpectedConditions.urlToBe(url +"/" + startPage +"/"));
			}
			
			
			
			
	}while(currentPage < howManyPages);	
		driver.close();
		scan.close();
	}
}
