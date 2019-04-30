import org.openqa.selenium.By;

import java.io.File;
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
		int howManyPages = 0;		//è una copia della variabile pagesToCheck
		int filmFoundOut = 0;		//Contiene il numero dei film che soddisfano i requisiti
		String filmName;			//Contiene il nome del film
		String filmLink;			//Contiene l'url del film 
		String filmData;			//Contiene il nome del film, pagina, punteggio e url da inserire nel file
		double checkRate = 0;
		double totalRate;			//Contiene il punteggio di ogni film analizzato nel sito
		String completeUrl;			//URL utilizzato per navigare nella prima pagina
		String url;					//URL utilizzato per navigare dopo la la prima pagina
		String PathChromeDriver;	 //Contiene il path del chrome driver per il controllo dell'esistenza del file
		int length = 0;				//Lunghezza della variabile URL
		int startPage = 1;			//Numero della pagina iniziale, ha valore 1 se l'url non contiene una pagina di partenza 
		
	try {
		PathChromeDriver = "C:\\Driver\\chromedriver";
		File chromedriverFile = new File(PathChromeDriver, "\\chromedriver.exe");
		if (!chromedriverFile.exists()) {
			System.out.println("ChromeDriver.exe non trovato");
			JOptionPane.showMessageDialog(null, "<html><font face='Arial' size='5' color='black'>Non trovo il file chromedriver. exe<br>Assicurarsi che si trovi nel seguente percorso:<br>C:\\Driver\\chromedriver\\<br>", "Errore", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}else {
			System.out.println("Ho trovato il ChromeDriver");
			System.setProperty("webdriver.chrome.driver", "C:\\Driver\\chromedriver\\chromedriver.exe");
		}
		
		url = JOptionPane.showInputDialog("<html><font face='Calibri' size='6' color='black'>Inserisci il link di Cineblog:");
		System.out.println("L'url inserito è: " + url);
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
			if(url.charAt(length-2) == '/') {				//https://cb01/page/x
				char temp = url.charAt(length-1);
				startPage = Character.getNumericValue(temp);
				url = url.substring(0, length-7);
			}else if(url.charAt(length-4) == '/') {   		//https://cb01/page/xxx		
				String temp = url.substring(length - 3, length);
				startPage = Integer.parseInt(temp);
				url = url.substring(0, length-9);
			}else if(url.charAt(length-5) == '/') {   		//https://cb01/page/xxxx
				String temp = url.substring(length - 4, length);
				startPage = Integer.parseInt(temp);
				url = url.substring(0, length-10);
			}else {											//https://cb01/page/xx
				String temp = url.substring(length - 2, length);
				startPage = Integer.parseInt(temp);
				url = url.substring(0, length-8);
			}
			completeUrl = url + "/page/" + startPage;
		}
		
		
		pagesToCheck = Integer.parseInt(JOptionPane.showInputDialog("<html><font face='Calibri' size='6' color='black'>Quante pagine vuoi analizzare?"));
		howManyPages = pagesToCheck; //Serve per il risultato finale
			
		String rate = (JOptionPane.showInputDialog("<html><font face='Calibri' size='6' color='black'>Che punteggio devono avere i tuoi film? (MAX 5) es: 4 oppure 4,5"));
		if(rate.contains(",")){
			rate = rate.replace(',','.');
		}
		checkRate = Double.parseDouble(rate);
		
		WebDriver driver = new ChromeDriver();
		System.out.println("L'url che inserisco online è: " + completeUrl);
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
				totalRate = topLeft + centerLeft + bottomLeft;			
				
					//Se il film supera le 4,5 stelle stampa il nome, voto, pagina e link
				if(totalRate >= checkRate) {	
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
		createFile ("\n\nHo analizzato " + howManyPages + " pagine, per un totale di " + (howManyPages * 12) + " film, di cui " + filmFoundOut + " corrispondono ai tuoi criteri di ricerca.\n\n\n");
		driver.close();
		JOptionPane.showMessageDialog(null, "<html><font face='Arial' size='5' color='black'>Ricerca terminata correttamente! <br>Ho trovato " + filmFoundOut + " Film che potrebbero interessarti. <br> Ho salvato i film trovati nel file \"ListFilm\" sul Desktop", "Ricerca Terminata", JOptionPane.INFORMATION_MESSAGE);
		System.exit(0);
		
	}catch (NumberFormatException e) {
		System.out.println("Errore, assicurati di aver inserito dei numeri validi: ");
		JOptionPane.showMessageDialog(null, "<html><font face='Arial' size='5' color='black'>Input non valido, assicurarsi di inserire solo numeri validi", "Errore", JOptionPane.ERROR_MESSAGE);
		System.exit(0);
	}
	catch (java.lang.NullPointerException e) {
		System.out.println("Programma chiuso correttamente");
		JOptionPane.showMessageDialog(null, "<html><font face='Arial' size='5' color='black'>Grazie per aver utilizzato questo programma", "Arrivederci", JOptionPane.INFORMATION_MESSAGE);
		System.exit(0);	
	
	}catch (WebDriverException e) {
		System.out.println("Errore, controllare dati immessi" + e.getMessage());
		JOptionPane.showMessageDialog(null, "<html><font face='Arial' size='5' color='black'>Il Browser è stato chiuso forzatamente, in caso di problemi questo può influire sui risultati della ricerca", "Errore", JOptionPane.ERROR_MESSAGE);
		System.exit(0);	
	}
	catch (Exception e) {
		System.out.println("Errore, URL vuoto" + e.getMessage());
		JOptionPane.showMessageDialog(null, "<html><font face='Arial' size='5' color='black'>Inserisci un URL prima", "Errore", JOptionPane.ERROR_MESSAGE);
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
			JOptionPane.showMessageDialog(null, "<html><font face='Arial' size='5' color='black'>Non riesco a creare e/o accedere al File sul Desktop", "Errore", JOptionPane.ERROR_MESSAGE);
			System.exit(0);    
		}
		//Inserisce nel file i dati e lo chiude
		outputStream.println(text);
		outputStream.close();
		System.out.println("File scritto correttamente");
	}
}
