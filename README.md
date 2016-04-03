# amazon
Online shop for books written in Java.
For detailed information read the report.

![alt tag](http://i.imgur.com/U31BfgR.png)
Figure 1 - client flowchart

![alt tag](http://i.imgur.com/lttAefu.png)
Figure 2 - server flowchart

0. These two programmes have been tested using - java version "1.7.0_02". 
   The output results may differ from the 5th and 6th versions.
   Also you need to be running cmd/terminal and java using administrator/root priviliges.
   This is neccessary because the programme creates, renames, and deletes files.

1. Run server app (i.e. BookShop) with 5555 as port number argument.
2. Run the client app (i.e. Client) with no additional arguments.
3. The server console should print "New Client!", followed by computer name and IP address.
4. Type I and press enter. The client app should display visually formatted inventory data.

   ISBN    Book Name       Author Name     Price   Quantity in Stock
   0747558191 | Harry Potter | JK Rowling | 6.99 | 98 |
   0140348034 | A Wizard Of Earthsea | Ursula Le Guin | 10.99 | 200 |
   0099448823 | Norwegian Wood | Haruki Murakami | 20.00 | 277 |
   0552131067 | Mort: Discworld Novel | Sir Terry Pratchett | 7.99 | 477 |

5. Buy two items one after the other. Use B followed by the ISBN number.

   B 0747558191
   B 0140348034

6. Each bought item will be displayed. The quantity will be one less.
   
   0747558191      Harry Potter    JK Rowling      6.99    97
   0140348034      A Wizard Of Earthsea    Ursula Le Guin  10.99   199

7. Type "basket". The client application will display the two (or more items) and the total price.

   This is the content of your shopping trolley:
   0747558191      Harry Potter    JK Rowling      6.99    97
   0140348034      A Wizard Of Earthsea    Ursula Le Guin  10.99   199
   Total price for the whole transaction is: 17.98

8. Type "quit". The client and server will be shut down.
