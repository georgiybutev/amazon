/*
 * Bookshop class
 * This class is used to create the TCP server and bind to port (via args).
 * This class is also used to create a socket, listen, and accept connetction from client.
 * java.io is used for input/output readers, buffers, exceptions.
 * java.net is used to create the server and client socket.
 * java.util Scanner is used to scan strings useing a delimiter.
 */

import java.io.*;
import java.net.*;
import java.util.*;

class BookShop {
  public static void main(String[] args){
    // The port address has a value between 0 - 65535.
    short port = 0;
    try{
      // Parse command line argument port number.
      port = Short.parseShort(args[0]);
      startServer(port);
    }catch (Exception e) {
      System.out.println("Please, specify port.");
    }
  }

  public static void startServer(short port){
    ServerSocket socket = null;
    try{
      // Create socket using the specified port number.
      socket = new ServerSocket(port);
      // If the socket has binded to the localhost or InetAddress.
      if(socket.isBound()){
          System.out.println("Server is ready.");
        }
    } catch (IOException e) {
      System.out.println("Could not bind!");
    }
    try{
      // Listen for client connection.
      Socket socketClient = socket.accept();
      System.out.println("New client!");
      // Find out the loca host information.
      InetAddress address = InetAddress.getLocalHost();
      // Store the local host information into a temporary string literal.
      String temp = address.toString();
      // Display the host name and IP address of the computer station.
      System.out.println(temp);

      readClientInput(socketClient);
    } catch (IOException e1) {
      System.out.println("No connection!");
    }
  }

  public static void readClientInput(Socket socket)throws IOException{
    // Initialise the following:
    InputStreamReader input = null;
    OutputStream output = null;
    BufferedReader in = null;
    PrintWriter out = null;
    String message = null;
    // Create object to read input from connected client.
    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    // Create object to send data to connected client.
    out = new PrintWriter(socket.getOutputStream(), true);
    // Read input from client. Stop reading if there is no client input.
    while((message = in.readLine()) != null){
      // Make a decision based on the message received from client.
      switch(message){
        // This should triger a method which would send the inventory to the client.
        case "I":
          sendInventory(out);
        break;
        // This should triger a method which deals with buying books.
        default:
          if(message.startsWith("B")){
            // The ISBN number follows B. We do not need B and the white space.
            String isbn = message.substring(2);
            buyBook(out, isbn);
          }
        break;
      }
    }
    // The client has disconnected. Close the socket and exit.
    System.out.println("Connection has been terminated!");
    socket.close();
    if(socket.isClosed()){
      System.out.println("Server has been shut.");
      System.exit(0);
    }
  }

  public static void sendInventory(PrintWriter out){
    // Initialise temporary string used for reading the file line by line.
    String temp = null;

    try{
      // Create object for existing file.
      File file = new File("inventory.txt");
      // Create object for reading file.
      FileReader fr = new FileReader(file);
      // Buffer read the file for optimum performance.
      BufferedReader br = new BufferedReader(fr);

      // If the file exists, can be read and written:
      if(file.exists() && file.canRead() && file.canWrite()){
        // Print the path of the file and its length in bytes.
        System.out.println(file.getAbsolutePath() + " " + file.length() + " bytes");
        // Read the file and send it to to client. Stop when there are no more lines to be read.
        while((temp = br.readLine()) != null){
          out.println(temp);
        }
      }
    } catch (IOException e) {
      System.out.println("Could not read file!");
    }
  }

  public static void buyBook(PrintWriter out, String isbn){
    // Initialise temporary string array with a limit of 100 elements.
    String[] temp = new String[100];
    // Initialise string which would store the found ISBN number.
    String found = null;
    // Initialise string which would store the title of the book.
    String title = null;
    // Initialise string which would store the author of the book.
    String author = null;
    // Initialise string which would store the price of the book.
    String price = null;
    // Initialise string which would store the quantity of books in the inventory.
    String quantity = null;
    // Initialise string which would store the quantity after it has been decreased.
    String qForWrite = null;
    // Initialise temporary string which would store the next token.
    String tempNext = null;
    // Initialise three integer variables used for loop iteration.
    int q = 0;
    int i = 0;
    int j = 0;

    try{
      // Create object for existing file.
      File file = new File("inventory.txt");
      // Create object for reading file.
      FileReader fr = new FileReader(file);
      // Buffer read the file for optimum performance.
      BufferedReader br = new BufferedReader(fr);
      // Scan the file. Tokenize the file contents using tab delimiter.
      Scanner scanner = new Scanner(br).useDelimiter("\t");

      // Create object for file.
      File tempFile1 = new File("temp.txt");
      // Create the file in the local directory.
      tempFile1.createNewFile();
      // Create object for writing strings to the file. 
      // New strings are appended rather than overwritten (true argument).
      FileWriter fw1 = new FileWriter(tempFile1, true);
      // Buffer write the file for optimum performance.
      BufferedWriter firstPass = new BufferedWriter(fw1);

      // Infinite while loop. The loop stop when the ISBN number has been found.
      while(true){
        // Assign next string tokens to string array as elements.
        temp[i] = scanner.next();
        // Write to temp.txt file seperating the tokens using tabs.
        firstPass.write(temp[i] + "\t");
        // If any client supplied ISBN number matches string array elemt:
        if (temp[i].equals(isbn)){
          // Found ISBN number.
          found = temp[i];
          // Next token should be the title.
          title = scanner.next();
          // Next token should be the author.
          author = scanner.next();
          // Next token should be the price
          price = scanner.next();
          // Next token should be the quantity.
          quantity = scanner.next();
          // Parse the quantity to integer. 
          // Decrease the quantity because the book has been bought.
          q = Integer.parseInt(quantity, 10) - 1;
          // Prepare for writing to file. Parse the integer into string.
          qForWrite = Integer.toString(q);
          // Respond to the client with the following:
          out.println(found + "\t" + title + "\t" + author + "\t" + price + "\t" + q + "\t");
          break;
        }
        i++;        
      }
      // The buffer has to be flushed to finalise writing to file.
      firstPass.flush();
      // Close the file writer to free-up memory.
      fw1.close();

      // Create object for file.
      File tempFile2 = new File("temp.txt");
      // Create object for writing strings to the file. 
      // New string are appended rather than overwritten (true argument).
      FileWriter fw2= new FileWriter(tempFile2, true);
      // Buffer write the file for optimum performance.
      BufferedWriter secondPass = new BufferedWriter(fw2);

      // Write to temp.txt file the following, seperated by tabs.
      secondPass.write(title + "\t" + author + "\t" + price + "\t" + qForWrite + "\t");

      // The buffer has to be flushed to finalise writing to file.
      secondPass.flush();
      // Close the file writer to free-up memory.
      fw2.close();

      // Create object for file.
      File tempFile3 = new File("temp.txt");
      // Create object for writing strings to the file. 
      // New string are appended rather than overwritten (true argument).
      FileWriter fw3 = new FileWriter(tempFile3, true);
      // Buffer write the file for optimum performance.
      BufferedWriter thirdPass = new BufferedWriter(fw3);

      // Loop until there are no more tokens left.
      while(scanner.hasNext()){
        // Write the left tokens in the scanner, seperated by tabs.
        thirdPass.write(scanner.next() + "\t");
      }
      // The buffer has to be flushed to finalise writing to file.
      thirdPass.flush();
      // Close the file writer to free-up memory.
      fw3.close();
      // Close the file writer to free-up memory.
      fr.close();
      // Delete the original inventory file.
      file.delete();
      // Rename the temporary file to match the original.
      // After three passes of writing the contents should be identical.
      tempFile1.renameTo(file);

    } catch (Exception e) {
      out.println("Problem with buying item!");
    }
  }
}