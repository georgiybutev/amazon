/*
 * Client class
 * This class is used to connect to the book shop server.
 * The server is identified by host name and port number address.
 * This class is also used to buy books from the book shop inventory.
 * I - requests the entire inventory.
 * B - does nothing.
 * B xxxxxxxxxxx - buys a book if it exists. The sequence of x is the ISBN number.
 * basket - displays all the bought books during this session.
 * quit - the client and server stop.
 */

import java.io.*;
import java.net.*;
import java.util.*;

class Client{
  public static void main(String[] args)throws IOException{
    // The destination port and host are permanent.
    final short PORT = 5555;
    final String HOST = "localhost";
    // 
    //String netHost = "";

    // Create a socket object for communication with the server.
    Socket socket = null;
    try{
      // Initialise the socket object with two arguments.
      socket = new Socket(HOST, PORT);
    } catch (IOException e0) {
      System.out.println("Unknown host!");
    }
    // Initialise the following: 
    InputStream input = null;
    OutputStream output = null;
    BufferedReader in = null;
    PrintWriter out = null;
    try{
      // Create object to read input from the server.
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      // Create object to write output to the server.
      out = new PrintWriter(socket.getOutputStream(), true);
    } catch (IOException e1) {
      System.out.println("Input or Output stream problem!");
    }
    // If the socket has successfully bound to host using port number.
    if(socket.isBound()){
      System.out.println("Connection has been established.");
    }
    // Infinite loop:
    while(true){
    // Create an object to read input from the keyboard.
    InputStreamReader isr = new InputStreamReader(System.in);
    // Buffer read the file for optimum performance.
    BufferedReader br = new BufferedReader(isr);
    // Read the keyboard input into a string.
    String message = br.readLine();
    // Make decision based on the keyboard input.
    switch(message){
      // The client has requested the inventory.
      case "I": requestInventory(out, message, in);
      break;
      // The client need to used B followed by the ISBN number.
      case "B":
        System.out.println("Please, enter B followed by the ISBN number.");
        break;
      // The client has requested information about already purchased books.
      case "basket":
        System.out.println("This is the content of your shopping trolley: ");
        viewBasket();
        break;
      // The client has requested to end the session with the server.
      case "quit": quit(socket);
      break;
      // The client has bought a book.
      default:
        if(message.startsWith("B")){
          buyBook(out, message, in);
        }
      break;
    }
    }

  }

  public static void requestInventory(PrintWriter out, String message, BufferedReader in) throws IOException{
    // Send the server I (inventory) argument.
    out.println(message);
    // Read and store the input from the server into a temporary string.
    String temp = in.readLine();
    // Scan the input from the server using a delimiter of tabs.
    Scanner scanner = new Scanner(temp).useDelimiter("\t");
    System.out.println("ISBN\t" + "Book Name\t" + "Author Name\t" + "Price\t" + "Quantity in Stock");
    // Perform the following until there are no more input tokens.
    while(scanner.hasNext()){
      // There are only five tokens associated with a book.
      for(int i = 0; i<=4; i++){
        // Display all tokens on one line seperated by a pipe symbol.
        System.out.print(scanner.next() + " | ");
      }
      System.out.println();
    }
  }

  public static void buyBook(PrintWriter out, String message, BufferedReader in) throws IOException{
    // Send the server B xxxxxxxxxx (buy book) argument.
    out.println(message);
    // Read and store the input from the server into a temporary string.
    String book = in.readLine();
    // Seperate each book entry using a new line.
    System.out.println(book + "\n");

    storeInBasket(book);
    storeInAccounts(book);
  }

  public static void storeInBasket(String book){
    try{
      // Create object for existing file.
      File file = new File("basket.txt");
      // Create object for writing strings to the file. 
      // New strings are appended rather than overwritten (true argument).
      FileWriter fw = new FileWriter(file, true);
      // Buffer write the file for optimum performance.
      BufferedWriter bw = new BufferedWriter(fw);

      // Write book entries seperated by a new line.
      bw.write(book + "\n");

      // The buffer has to be flushed to finalise writing to file.
      bw.flush();
      // Close the file writer to free-up memory.
      fw.close();

    } catch (IOException e){
      System.out.println("Could not store book in basket!");
    }
  }

  public static void storeInAccounts(String book){
    // Initialise the following:
    String isbn = null;
    String title = null;
    String author = null;
    String temp = null;
    double price = 0.00;

    try{
      // Create object for existing file.
      File file = new File("total.txt");
      // Create object for writing strings to the file. 
      // New strings are appended rather than overwritten (true argument).
      FileWriter fw = new FileWriter(file, true);
      // Buffer write the file for optimum performance.
      BufferedWriter bw = new BufferedWriter(fw);
      // Scan the input from the server using a delimiter of tabs.
      Scanner scanner = new Scanner(book).useDelimiter("\t");
      // Assign the next token in the scanner buffer to the following:
      isbn = scanner.next();
      title = scanner.next();
      author = scanner.next();
      temp = scanner.next();
      // The price has to be parsed into a floating point value.
      price = Double.parseDouble(temp);
      // Write book entries seperated by a new line.
      bw.write(price + "\n");
      // The buffer has to be flushed to finalise writing to file.
      bw.flush();
      // Close the file writer to free-up memory.
      fw.close();

    } catch (IOException e){
      System.out.println("Could not store book in basket!");
    }
  }

  public static void viewBasket(){
    // Initialise the following:
    String temp = null;
    try{
      // Create object for existing file.
      File file = new File("basket.txt");
      // Create object for reading file.
      FileReader fr = new FileReader(file);
      // Buffer read the file for optimum performance.
      BufferedReader br = new BufferedReader(fr);
      // Perform the following until the end of the file.
      while((temp = br.readLine()) != null){
        // Display each line of the file contents.
        System.out.println(temp);
      }
      // Flush the reading buffer.
      br.close();
      // Close the file writer to free-up memory.
      fr.close();
      // Calculate the total sum of the purchased books and store it as double value.
      Double total = calculatePrice();
      // Display the result of the calculation to the client with .00 precision.
      System.out.printf("Total price for the whole transaction is: %.2f \n", total);
    } catch (IOException e) {
      System.out.println("Could not view read basket!");
    }
  }

  public static double calculatePrice(){
    // Initialise the following:
    String temp = null;
    double sum = 0;
    try {
      // Create object for existing file.
      File file = new File("total.txt");
      // Create object for reading file.
      FileReader fr = new FileReader(file);
      // Buffer read the file for optimum performance.
      BufferedReader br = new BufferedReader(fr);
      // Scan the input from the server using a delimiter of new line.
      Scanner scanner = new Scanner(br).useDelimiter("\n");
      // Perform the following until there are no more price entries.
      while((temp = br.readLine()) != null){
        // The total is the summary of price entries.
        sum += Double.parseDouble(temp);
      }
      // Flush the reading buffer.
      br.close();
      // Close the file writer to free-up memory.
      fr.close();
    } catch (IOException e) {
      System.out.println("Could not read the total price!");
    }
    return sum;
  }

  public static void quit(Socket socket)throws IOException{
    // Create object for existing file.
    File file1 = new File("basket.txt");
    // Delete basket file.
    file1.delete();
    // Create object for existing file.
    File file2 = new File("total.txt");
    // Delete basket file.
    file2.delete();
    // Close the connection with the server.
    socket.close();
    System.out.print("Connection was terminated!");
    System.exit(0);
  }

}