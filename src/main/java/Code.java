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
import javax.swing.JTextField;

public class Code {
	public static void main(String[] args) {
		int pagesToCheck = 0;		//Converte il valore in intero di howManyPages
		int howManyPages = 0;		//è una copia della variabile pagesToCheck
		int filmFoundOut = 0;		//Contiene il numero dei film che soddisfano i requisiti
		String filmName;			//Contiene il nome del film
		String filmLink;			//Contiene l'url del film 
		String filmData;			//Contiene il nome del film, pagina, punteggio e url da inserire nel file
		double checkRate = 0;		//Contiene il punteggio che l'utente vorrebbe utilizzare come minimo
		double totalRate;			//Contiene il punteggio di ogni film analizzato nel sito
		String completeUrl = null;	//URL utilizzato per navigare nella prima pagina
		String url = null;			//URL utilizzato per navigare dopo la la prima pagina
		String PathChromeDriver;	 //Contiene il path del chrome driver per il controllo dell'esistenza del file
		int length = 0;				//Lunghezza della variabile URL
		int startPage = 1;			//Numero della pagina iniziale, ha valore 1 se l'url non contiene una pagina di partenza 
		int menuResult;				//Contiene il respondo del menu iniziale (ok/annulla)
		boolean allRight = true;
		JTextField urlField = new JTextField(10);
	    JTextField pageField = new JTextField(10);
	    JTextField rateField = new JTextField(10);
	      
	      Object[] fields = {
	    	"<html><font face='Calibri' size='6' color='black'>URL del sito:", urlField,
	    	"<html><font face='Calibri' size='6' color='black'>Quante pagine vuoi analizzare?", pageField,
	    	"<html><font face='Calibri' size='6' color='black'>Punteggio minimo dei film che ti interessano (MAX 5):", rateField
	      };
		
	try {
		//Controllo dell'esistenza del webdriver nel path designato
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
		
		do {
		  menuResult = JOptionPane.showConfirmDialog(null, fields, "Cineblog Film Star Finder", JOptionPane.OK_OPTION);
	      if (menuResult == JOptionPane.OK_OPTION) {
	    	
	    	  try {
	    	  //CAMPO DELLO STAR RATING DEI FILM
		 		String rate = rateField.getText();
		        //Sistema automaticamente la virgola con il punto
		        if(rate.contains(",")){
		 			rate = rate.replace(',','.');
		 		}
		 		checkRate = Double.parseDouble(rate);
		 		System.out.println("Voto: " + rate);
	    	  }catch(Exception ex) {
	    		  if(checkRate != 0) {
	    			  JOptionPane.showMessageDialog(null, "<html><font face='Arial' size='5' color='black'>Il punteggio dei film non può superare le 5 stelle o essere inferiore a 1! <br>Riprova inserendo un numero corretto.<br>(Esempio: 3 oppure 4,5)", "Attenzione", JOptionPane.ERROR_MESSAGE);
	    			  allRight = false;
	    		  }
	    	  }
	      
	    	  //GESTIONE ERRORI INPUT
	    	  if (urlField.getText().equalsIgnoreCase("") || pageField.getText().equalsIgnoreCase("") || checkRate == 0) {	//Campi vuoti
	    		  JOptionPane.showMessageDialog(null, "<html><font face='Arial' size='5' color='black'>Riempi tutti i campi correttamente prima", "Attenzione", JOptionPane.ERROR_MESSAGE);
	    		  allRight = false;
	    	  }else if(checkRate > 5 || checkRate < 1) { //Numero rate non corretto
	    		  JOptionPane.showMessageDialog(null, "<html><font face='Arial' size='5' color='black'>Il punteggio dei film non può superare le 5 stelle o essere inferiore a 1! <br>Riprova inserendo un numero corretto.<br>(Esempio: 3 oppure 4,5)", "Attenzione", JOptionPane.ERROR_MESSAGE);
	    		  rateField.setText("");
	    		  allRight = false;
	    	  }else if(Integer.parseInt(pageField.getText()) < 1 ) {	//Numero pagina non corretto
	    		  JOptionPane.showMessageDialog(null, "<html><font face='Arial' size='5' color='black'>Devo controllare almeno una pagina!", "Attenzione", JOptionPane.ERROR_MESSAGE);
	    		  pageField.setText("");
	    		  allRight = false;
	    	  }
	    	  else {
	    		  	allRight = true;
	    		  	//CAMPO DELL'URL
	    		  	url = urlField.getText();
		    	  	System.out.println("URL: " + url);
		    	  	length = url.length();
		         
		    	  	//Nel caso in cui l'url termini con il carattere '/' questo viene rimosso
		    	  	if(url.charAt(length-1) == '/') {	                
		    	  		url = url.substring(0, length-1);
		    	  	}
		 		
		    	  	//Trova in che pagina del sito si trova il link immesso e aggiorna l'url
		    	  	startPage = CheckStartPage (url);
		    	  	completeUrl = CheckUrl (url, startPage);
		    	  	System.out.println("Start page : " + startPage);
		    	  	
		    	  	//Aggiornare l'url per cercare le pagine successive
		    	  	if(url.contains("page")){
		    	  		int pageIndex = url.indexOf("page", 0);
		    	  		url = url.substring(0, pageIndex-1);
		    	  	}
	    	  
	 			
		 		//CAMPO DELLE PAGINE DA ANALIZZARE
		 		pagesToCheck = Integer.parseInt(pageField.getText());
		 		howManyPages = pagesToCheck; //Serve per il risultato finale
		 		System.out.println("Pagine: " + pageField.getText());
		         
	    	  }
	      }else {
	    	  JOptionPane.showMessageDialog(null, "<html><font face='Arial' size='5' color='black'>Grazie per aver utilizzato questo programma", "Arrivederci" , JOptionPane.INFORMATION_MESSAGE);
	    	  System.exit(0);
	      }
		}while (allRight == false);
		
		//TODO Ridurre automaticamente la finestra di chrome a icona
		//Avviso per l'utente
		JOptionPane.showMessageDialog(null, "<html><font face='Arial' size='5' color='black'>Appena questa finestra sarà chiusa partirà il robot che cercherà tutti i film che potrebbero interessarti.<br>"
				+ "Si aprirà una finestra di Chrome che navigherà nelle pagine automaticamente, <br>"
				+ "NON cliccare al suo interno, puoi anche ridurla a icona se ti da fastidio.<br>"
				+ "Se la chiudi il programma smette di funzionare. <br><br>Quando il programma avrà terminato il suo lavoro chiuderà da solo Google Chrome e ti avviserà.", "Cineblog Film Star Finder", JOptionPane.INFORMATION_MESSAGE);
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
					CreateFile (filmData);
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
		CreateFile ("\n\nHo analizzato " + howManyPages + " pagine, per un totale di " + (howManyPages * 12) + " film, di cui " + filmFoundOut + " corrispondono ai tuoi criteri di ricerca.\n\n\n");
		driver.close();
		JOptionPane.showMessageDialog(null, "<html><font face='Arial' size='5' color='black'>Ricerca terminata correttamente! <br><br>Ho trovato " + filmFoundOut + " Film che potrebbero interessarti. <br> Ho salvato i tuoi film nel file \"Lista Film\" sul Desktop", "Ricerca Terminata", JOptionPane.INFORMATION_MESSAGE);
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
	
	
	public static String CheckUrl (String url, int startPage) {
		int urlLength = url.length();
 		//Aggiorna url da cercare
 		String completeUrl = url;
 		
 		//Se l'url non parte dalla prima pagina, ma da una già specificata bisogna aggiornare l'URL togliendo quella pagina:
 		if(url.contains("page")){
 			urlLength = url.length();
 			if(url.charAt(urlLength-2) == '/') {				//https://cb01/page/x
 				char temp = url.charAt(urlLength-1);
 				startPage = Character.getNumericValue(temp);
 				url = url.substring(0, urlLength-7);
 			}else if(url.charAt(urlLength-4) == '/') {   		//https://cb01/page/xxx		
 				String temp = url.substring(urlLength - 3, urlLength);
 				startPage = Integer.parseInt(temp);
 				url = url.substring(0, urlLength-9);
 			}else if(url.charAt(urlLength-5) == '/') {   		//https://cb01/page/xxxx
 				String temp = url.substring(urlLength - 4, urlLength);
 				startPage = Integer.parseInt(temp);
 				url = url.substring(0, urlLength-10);
 			}else {											//https://cb01/page/xx
 				String temp = url.substring(urlLength - 2, urlLength);
 				startPage = Integer.parseInt(temp);
 				url = url.substring(0, urlLength-8);
 			}
 			completeUrl = url + "/page/" + startPage;
 		}
 		return completeUrl;
	}
	
	public static int CheckStartPage (String url) {
		int urlLength = url.length();
		int startPage = 1;
 		
 		//Se l'url non parte dalla prima pagina, ma da una già specificata bisogna individuare il numero di pagina:
 		if(url.contains("page")){
 			urlLength = url.length();
 			if(url.charAt(urlLength-2) == '/') {				//https://cb01/page/x
 				char temp = url.charAt(urlLength-1);
 				startPage = Character.getNumericValue(temp);
 			}else if(url.charAt(urlLength-4) == '/') {   		//https://cb01/page/xxx		
 				String temp = url.substring(urlLength - 3, urlLength);
 				startPage = Integer.parseInt(temp);
 			}else if(url.charAt(urlLength-5) == '/') {   		//https://cb01/page/xxxx
 				String temp = url.substring(urlLength - 4, urlLength);
 				startPage = Integer.parseInt(temp);
 			}else {											//https://cb01/page/xx
 				String temp = url.substring(urlLength - 2, urlLength);
 				startPage = Integer.parseInt(temp);
 			}
 		}
 		return startPage;
	}
	
	
	public static void CreateFile (String text) {
		String desktopPath = System.getProperty("user.home");
		String fileName = desktopPath + "\\Desktop\\Lista Film.txt";
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
