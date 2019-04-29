import org.openqa.selenium.By;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.List;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import javax.swing.JOptionPane;

public class Code {
	public static void main(String[] args) {
		int pagesToCheck = 0;		//Converte il valore in intero di howManyPages
		int howManyPages = 0;	//è una copia della variabile pagesToCheck
		int filmFoundOut = 0;	//Contiene il numero dei film che soddisfano i requisiti
		String filmName;		//Contiene il nome del film
		String filmLink;		//Contiene l'url del film 
		String filmData;		//Contiene il nome del film, pagina, punteggio e url da inserire nel file
		String completeUrl;
		String url;
		int length = 0;
		int startPage = 1;		//Numero della pagina iniziale, ha valore 1 se l'url non contiene una pagina di partenza 
		
	
		System.setProperty("webdriver.chrome.driver", "C:\\Driver\\chromedriver\\chromedriver.exe");
		
		try {
		url = JOptionPane.showInputDialog("Inserisci il link di Cineblog:");
		//System.out.print("Inserire il link del sito cineblog: ");
		length = url.length();
		
		//Nel caso in cui l'url termini con il carattere '/' questo viene rimosso
		if(url.charAt(length-1) == '/') {	                
			url = url.substring(0, length-1);
		}
		//Aggiorna url da cercare
		completeUrl = url;
		
		//Se l'url non parte dalla prima pagina, ma da una già specificata bisogna individuare il numero di pagina e aggiornare l'URL:
		if(url.contains("page")){
			length = url.length();
			if(url.charAt(length-2) == '/') {	
				char temp = url.charAt(length-1);
				startPage = Character.getNumericValue(temp);
				url = url.substring(0, length-7);
			}else {
				String temp = url.substring(length - 2, length);
				startPage = Integer.parseInt(temp);
				url = url.substring(0, length-8);
			}
			completeUrl = url + "/page/" + startPage;
		}
		
	
		pagesToCheck = Integer.parseInt(JOptionPane.showInputDialog("Quante pagine vuoi analizzare?"));
		howManyPages = pagesToCheck; //Serve per il risultato finale
			
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
				
				//Il totale del punteggio è dato dalla somma di questi tre elementi:
				double totalRate = topLeft + centerLeft + bottomLeft;			
				
					//Se il film supera le 4,5 stelle stampa il nome, voto, pagina e link
				if(totalRate >= 4.5) {	
					filmFoundOut++;	//Aggiorna il contatore dei film trovati
					filmName =	fiveStarElement.get(i).findElement(By.tagName("a")).getText();
					filmLink = fiveStarElement.get(i).findElement(By.tagName("a")).getAttribute("href");
					if (filmName.equals("")) {
						filmName = "NON SONO RIUSCITO A LEGGERE IL TITOLO DI QUESTO FILM";
						filmLink = url + "/page/" + startPage;
					}
					filmData = filmName + "\tVoto: " + totalRate + " su 5" + "\tPagina " + startPage + " URL: " + filmLink;
					System.out.println(filmData);
					createFile (filmData);
				}
				
				topLeft = centerLeft = bottomLeft = 0; //reset
				i++;
			}
				
			pagesToCheck--;
			startPage ++;
			
			//Clicca sulla pagina successiva 
			String nextPage = String.format("//a[@href='%s/page/%s/']",url, startPage);
			driver.findElement(By.xpath(nextPage)).click(); 
					
			//Aspetta che l'url della pagina successiva sia caricato
			WebDriverWait wait = new WebDriverWait(driver, 20); 
			wait.until(ExpectedConditions.urlToBe(url + "/page/" + startPage +"/"));
			
		
		}while(pagesToCheck != 0);
		createFile ("\n\nHo analizzato " + howManyPages + " pagine, per un totale di " + (howManyPages * 12) + " film, di cui " + filmFoundOut + " corrispondono ai tuoi criteri di ricerca.");
		driver.close();
		System.exit(0);
		
		
	}catch (NumberFormatException e) {
		System.out.println("Errore, assicurati di aver inserito dei numeri validi: " + e.getMessage());
		JOptionPane.showMessageDialog(null, "Assicurati di aver inserito dei numeri validi", "Errore", JOptionPane.ERROR_MESSAGE);
		System.exit(0);
	}
	catch (java.lang.NullPointerException e) {
		System.out.println(e.getMessage());
		JOptionPane.showMessageDialog(null, "Grazie per aver utilizzato questo programma", "Arrivederci", JOptionPane.INFORMATION_MESSAGE);
		System.exit(0);	
	
	}catch (WebDriverException e) {
		System.out.println("Errore, controllare dati immessi" + e.getMessage());
		JOptionPane.showMessageDialog(null, "Hai chiuso il Browser manualmente, questo può influire sui risultati della ricerca", "Errore", JOptionPane.ERROR_MESSAGE);
		System.exit(0);	
	}
	catch (Exception e) {
		System.out.println("Errore, controllare dati immessi" + e.getMessage());
		JOptionPane.showMessageDialog(null, "Controllare che l'url sia corretto altrimenti contattare l'assistenza", "Errore", JOptionPane.ERROR_MESSAGE);
		System.exit(0);
	}	
}
	
	
	public static void createFile (String text) {
		String desktopPath = System.getProperty("user.home");
		String fileName = desktopPath + "\\Desktop\\ListaFilm.txt";
		PrintWriter outputStream = null;
		
		try {			
			//Crea un nuovo file solo se non esiste già, altrimenti aggiunge in coda il testo
			outputStream = new PrintWriter (new FileOutputStream (fileName, true));
		}catch (FileNotFoundException e) {
			System.out.println("Errore nell'apertura del file");
			System.exit(0);    //Termina il programma
		}
		//Inserisce nel file i dati e lo chiude
		outputStream.println(text);
		outputStream.close();
		System.out.println("File scritto correttamente");
	}
}
