/* Name: Ryan Ramdihal
 Course: CNT 4714 – Fall 2024
 Assignment title: Project 1 – An Event-driven Enterprise Simulation
 Date: Sunday September 8, 2024
*/
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.lang.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.Scanner;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Nile {
    private static Scanner inventoryReader; //reads from inventory file

    String filePath = "inventory.csv"; //file name
    String viewOrder = ""; // holds order details for view cart button
    String invoice = "";  // holds checkout details for check out button
    String currentDate = ""; // holds current date and time
    String transactionString = ""; // string to hold transaction and save to a file
    String transactionBufferString = ""; //formats transaction detail
    String permutation = ""; //identifier for transaction
    String itemNum = ""; // holds item #
    String itemName = ""; // holds item name
    String available = ""; // holds availability status of item from file
    String quantityInStock = ""; // holds quantity of items in stodk
    String price = ""; // holds price of string

    int numInCartCounter = 0; //counter for the # of items in cart
    int numItemAskedFor = 0; // number of items requested by user
    int tax = 6; //6% tax

    float subtotal = 0.0F; 
    float discount = 0.0F;
    float priceWdiscount = 0.0F;

    JTextField itemIDField; // Text field for entering item ID
    JTextField quantityItemsField; // Text field for entering the quantity of items
    JTextField itemDetailsField; // Text field for displaying item details
    JTextField subtotalField; // Text field for displaying the order subtotal

    // Text field for displaying items 1-5 in the cart
    JTextField item1CartField;
    JTextField item2CartField;
    JTextField item3CartField;
    JTextField item4CartField;
    JTextField item5CartField;

    // six buttons for functions
    JButton searchItemButton;
    JButton addToCartButton;
    JButton viewCartButton;
    JButton checkoutButton;
    JButton startOverButton;
    JButton exitButton;

    JLabel buffer1 = new JLabel("");
    JLabel buffer2 = new JLabel("");



    DecimalFormat twoPlaces = new DecimalFormat("0.00");// Formatter for decimal numbers (2 decimal places)
    DecimalFormat transactionFileDiscount = new DecimalFormat("0.0#"); // Formatter for discount in transaction file

    public static void main(String[] args)
    {
        new Nile(); //runs the program
    }

    public Nile()
    {
        //create gui
        JFrame gui = new JFrame();

    
        gui.setTitle("Nile.Com - Fall 2024");// title of window
        gui.setSize(850,600); //size of window
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // close application when window is closed
        gui.setLocationRelativeTo(null); // center window on screen

        JPanel nilePanel = new JPanel(); //Create a panel for the main input fields
        nilePanel.setBackground(Color.DARK_GRAY);

        //dimensions of top pannel
        nilePanel.setLayout(new GridLayout(6,1,10,0)); //5 rows, 1 column
        nilePanel.setBorder(new EmptyBorder(10,10,0,10)); // Add an empty border around the panel

        JPanel nileButtonPanel = new JPanel(); // Create a panel for buttons
        nileButtonPanel.setLayout(new GridLayout(5,2 , 10, 10)); //3 rows, 2 columns for button layout
        nileButtonPanel.setBackground(Color.BLUE);


        JPanel shoppingCartPanel = new JPanel(); // Create a panel for displaying the shopping cart
        shoppingCartPanel.setLayout(new GridLayout(7,1,10,2));// 6 rows, 1 column
        shoppingCartPanel.setBackground(Color.LIGHT_GRAY);

        JLabel shoppingCartTitleLabel = new JLabel("The Shopping Cart Currently Contains " + numInCartCounter + " Item(s)");
        shoppingCartTitleLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center the title
        shoppingCartPanel.add(shoppingCartTitleLabel); // Add the title label to the shopping cart panel

        // Create text fields for displaying items in the cart
        item1CartField = new JTextField(30);
        item2CartField = new JTextField(30);
        item3CartField = new JTextField(30);
        item4CartField = new JTextField(30);
        item5CartField = new JTextField(30);

        // Add borders to the panels 
        nilePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK)); 
        nileButtonPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK)); 
        shoppingCartPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));


        // first line item ID
        JLabel itemIDLabel = new JLabel("Enter ID for Item #1: ");
        itemIDLabel.setForeground(Color.YELLOW);
        itemIDField = new JTextField(30);
        itemIDLabel.setHorizontalAlignment(SwingConstants.RIGHT); // Align the label to the right


        // second line num of items asked for
        JLabel quantityItemsLabel = new JLabel("Enter quantity For Item #1: ");
        quantityItemsLabel.setForeground(Color.YELLOW);
        quantityItemsField = new JTextField(30);
        quantityItemsLabel.setHorizontalAlignment(SwingConstants.RIGHT); // Align the label to the right


        JLabel itemDetailsLabel = new JLabel("Details for Item #1: ");
        itemDetailsLabel.setForeground(Color.RED);
        itemDetailsField = new JTextField(30);
        itemDetailsLabel.setHorizontalAlignment(SwingConstants.RIGHT); // Align the label to the right


        JLabel subtotalLabel = new JLabel("Order subtotal for 0 items(s): ");
        subtotalLabel.setForeground(Color.CYAN);
        subtotalLabel.setHorizontalAlignment(SwingConstants.RIGHT); // Align the label to the right


        subtotalField = new JTextField(30);

        // create buttons
        searchItemButton = new JButton("Search for Item #1");
        addToCartButton = new JButton("Add Item #1 to Cart");
        viewCartButton = new JButton("View Cart");
        checkoutButton = new JButton("Check Out");
        startOverButton = new JButton("Empty Cart - Start a New Order");
        exitButton = new JButton("Exit (Close App)");

        // Add the input fields to the main panel
        nilePanel.add(buffer1);
        nilePanel.add(buffer2);
        nilePanel.add(itemIDLabel);
        nilePanel.add(itemIDField);
        nilePanel.add(quantityItemsLabel);
        nilePanel.add(quantityItemsField);
        nilePanel.add(itemDetailsLabel);
        nilePanel.add(itemDetailsField);
        nilePanel.add(subtotalLabel);
        nilePanel.add(subtotalField);
        
        JLabel controlsLabel = new JLabel("USER CONTROLS", SwingConstants.CENTER);
        controlsLabel.setForeground(Color.WHITE);
        controlsLabel.setFont(new Font("Arial", Font.BOLD, 15));

        JLabel bufferLabel = new JLabel("");

        // Add the buttons to the button panel
        nileButtonPanel.add(controlsLabel);
        nileButtonPanel.add(bufferLabel);
        nileButtonPanel.add(searchItemButton);
        nileButtonPanel.add(addToCartButton);
        nileButtonPanel.add(viewCartButton);
        nileButtonPanel.add(checkoutButton);
        nileButtonPanel.add(startOverButton);
        nileButtonPanel.add(exitButton);

        // Add the cart item fields to the shopping cart panel
       shoppingCartPanel.add(item1CartField);
       shoppingCartPanel.add(item2CartField);
       shoppingCartPanel.add(item3CartField);
       shoppingCartPanel.add(item4CartField);
       shoppingCartPanel.add(item5CartField);

        // Add the panels to the main GUI window
        gui.add(nilePanel, BorderLayout.NORTH); //top of window
        gui.add(shoppingCartPanel, BorderLayout.CENTER); //middle of window
        gui.add(nileButtonPanel, BorderLayout.SOUTH); //bottom of window

        gui.setVisible(true);// Make the GUI visible

        // Disable buttons and fields that shouldn't be accessible at startup
        addToCartButton.setEnabled(false);
        itemDetailsField.setEditable(false);
        subtotalField.setEditable(false);
        checkoutButton.setEnabled(false);
        viewCartButton.setEnabled(false);
        item1CartField.setEditable(false);
        item2CartField.setEditable(false);
        item3CartField.setEditable(false);
        item4CartField.setEditable(false);
        item5CartField.setEditable(false);

       //initial shopping cart title
        shoppingCartTitleLabel.setText("Your Shopping Cart is Currently Empty");
        shoppingCartTitleLabel.setForeground(Color.RED);
        shoppingCartTitleLabel.setFont(new Font("Arial", Font.BOLD, 14));



        //search for item
        searchItemButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) {

                if(numInCartCounter >= 5)// If cart is full, disable search
                {
                    searchItemButton.setEnabled(false);
                    itemIDField.setEnabled(false);
                    quantityItemsField .setEnabled(false);
                    return; // Exit if cart is full
                }
    
                numItemAskedFor = Integer.parseInt(quantityItemsField.getText());// Get the quantity requested by the user
                // Determine discount based on quantity
                if(numItemAskedFor < 5)
                {
                    discount = 1.0F;
                }
                if(numItemAskedFor >= 5 && numItemAskedFor <= 9)
                {discount = 0.9F;
                }
                if(numItemAskedFor >= 10 && numItemAskedFor <= 14)
                {
                    discount = 0.85F;
                }
                if(numItemAskedFor >= 15)
                {
                    discount = 0.8F;
                }
                
                
                inventorySearch(itemIDField.getText(), filePath);// search using id
                                

            }
        });


        addToCartButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                numInCartCounter++; // increments item # in cart
                 // title of middle panel as an item is in cart
                shoppingCartTitleLabel.setText("Your Shopping Cart Currently Contains " + numInCartCounter + " Item(s)");
        
                switch (numInCartCounter) { //places info in designated cart field
                    case 1:
                        item1CartField.setText("Item " + numInCartCounter + " - SKU: " + itemNum + ", Desc: " + itemName + ", Price Ea. $" + price + ", Qty: " + numItemAskedFor + ", Total: " + "$" + twoPlaces.format(priceWdiscount));
                        break;
                    case 2:
                        item2CartField.setText("Item " + numInCartCounter + " - SKU: " + itemNum + ", Desc: " + itemName + ", Price Ea. $" + price + ", Qty: " + numItemAskedFor + ", Total: " + "$" + twoPlaces.format(priceWdiscount));
                        break;
                    case 3:
                        item3CartField.setText("Item " + numInCartCounter + " - SKU: " + itemNum + ", Desc: " + itemName + ", Price Ea. $" + price + ", Qty: " + numItemAskedFor + ", Total: " + "$" + twoPlaces.format(priceWdiscount));
                        break;
                    case 4:
                        item4CartField.setText("Item " + numInCartCounter + " - SKU: " + itemNum + ", Desc: " + itemName + ", Price Ea. $" + price + ", Qty: " + numItemAskedFor + ", Total: " + "$" + twoPlaces.format(priceWdiscount));
                        break;
                    case 5:
                        item5CartField.setText("Item " + numInCartCounter + " - SKU: " + itemNum + ", Desc: " + itemName + ", Price Ea. $" + price + ", Qty: " + numItemAskedFor + ", Total: " + "$" + twoPlaces.format(priceWdiscount));
                        break;
                }
                
            
                checkoutButton.setEnabled(true);
                viewCartButton.setEnabled(true);

                //string that has item info of current item append to it as each item is confirmed
                viewOrder = viewOrder + numInCartCounter + ". " + itemDetailsField.getText() + "\n";
                subtotal = subtotal + priceWdiscount;
                subtotalField.setText("$" + twoPlaces.format(subtotal));
                searchItemButton.setEnabled(true);
                addToCartButton.setEnabled(false);

                if(numInCartCounter <=5){ 
                searchItemButton.setText("Search For Item #" + (numInCartCounter+1));
                addToCartButton.setText("Add Item #" + (numInCartCounter+1) + " to Cart");

                if(numInCartCounter >= 5){ // formats button text at max 5 
                    searchItemButton.setText("Search For Item #" + (numInCartCounter));
                    addToCartButton.setText("Add Item #" + (numInCartCounter) + " to Cart");
                    itemIDLabel.setText("Enter item ID for Item #" + (numInCartCounter) + ": ");
                    quantityItemsLabel.setText("Enter quantity for Item #" + (numInCartCounter) + ": ");

                }
                itemIDLabel.setText("Enter item ID for Item #" + (numInCartCounter+1) + ": ");
                quantityItemsLabel.setText("Enter quantity for Item #" + (numInCartCounter+1) + ": ");
                itemDetailsLabel.setText("Details for Item #" + numInCartCounter + ": ");
                subtotalLabel.setText("Current subtotal for " + numInCartCounter + " item(s): ");
                itemIDField.setText("");
                quantityItemsField.setText("");

                JOptionPane.showMessageDialog(null, "Item #" + numInCartCounter + " accepted. Added to your cart.", " Nile.com - Item Added",JOptionPane.INFORMATION_MESSAGE); //add to cart message confirmation
                    if(numInCartCounter == 5)
                    {
                    JOptionPane.showMessageDialog(null, "Your cart is full (5 items).", " Nile.com - Notification",JOptionPane.INFORMATION_MESSAGE); // cart is full notification
                    searchItemButton.setEnabled(false);
                    itemIDField.setEnabled(false);
                    itemIDField.setEditable(false);
                    quantityItemsField.setEnabled(false);
                    quantityItemsField.setEditable(false);

                    return; // Exit the method to prevent adding more items

                    }
                }

            }
        });

        viewCartButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                JOptionPane.showMessageDialog(null, viewOrder,"Nile.com Shopping Cart",JOptionPane.INFORMATION_MESSAGE);// show view order string in pop up message
                
            }
        });

        checkoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                checkoutButton.setEnabled(false);
                viewCartButton.setEnabled(false);
                searchItemButton.setEnabled(false);
                itemIDField.setEnabled(false);
                quantityItemsField.setEnabled(false);

                
                currentDate = new SimpleDateFormat("MM/dd/YY, h:mm:ss a zzz").format(new Date());
                permutation = new SimpleDateFormat("ddmmyyyyhhmmss").format(new Date());

                //invoice details
                invoice = "Date: " + currentDate + "\n\n" + "Number of line items: " + numInCartCounter + "\n\n" +" Item# / ID / Title / Price / Qty / Disc % / Subtotal:"+ "\n\n" + viewOrder + "\n\n\n"
                        + "Order subtotal: $" + twoPlaces.format(subtotal) + "\n\n"
                        + "Tax rate: 	" + tax + "%" + "\n\n"
                        + "Tax amount:  $" + twoPlaces.format(((subtotal * tax)/100))
                        + "\n\n" + "ORDER TOTAL:  $"
                        + twoPlaces.format((subtotal + ((subtotal * tax)/100))) + "\n\n"
                        + "Thanks for shopping at Nile Dot Com!";

                JOptionPane.showMessageDialog(null, invoice,"Nile.com Final Invoice",JOptionPane.INFORMATION_MESSAGE);// show view order string in pop up message"; //display the invoice 
                String [] transactionLine = transactionBufferString.split("\n");

                for(Integer x = 0; x < numInCartCounter; x++)
                {
                    transactionString = transactionString + permutation + ", " + transactionLine[x] + " " + currentDate + "\n";
                }

                //  adding all contents to transaction log file
                try {
                    FileWriter fileW = new FileWriter("transactions.csv", true);
                    BufferedWriter bufW = new BufferedWriter(fileW);
                    PrintWriter output = new PrintWriter(bufW);
                    output.print(transactionString);
                    output.print("\n");
                    output.close();
                }
                catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

    startOverButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            //reset everything
            numInCartCounter = 0;
            itemIDField.setEnabled(true);
            itemIDField.setEditable(true);
            quantityItemsField.setEnabled(true);
            quantityItemsField.setEditable(true);
            searchItemButton.setEnabled(true);
            addToCartButton.setEnabled(true);
            itemIDField.setEditable(true);
            quantityItemsField.setEditable(true);
            checkoutButton.setEnabled(false);
            viewCartButton.setEnabled(false);
            subtotal = 0.0F;
            priceWdiscount = 0.0F;
            itemIDField.setText("");
            quantityItemsField.setText("");
            itemDetailsField.setText("");
            subtotalField.setText("");
            item1CartField.setText("");
            item2CartField.setText("");
            item3CartField.setText("");
            item4CartField.setText("");
            item5CartField.setText("");


            viewOrder = "";
            invoice = "";
            transactionString = "";
            transactionBufferString = "";
            searchItemButton.setText("Search For Item #" + (numInCartCounter+1));
            addToCartButton.setText("Add Item #" + (numInCartCounter+1) + " to Cart");
            itemIDLabel.setText("Enter item ID for Item #" + (numInCartCounter+1) + ": ");
            quantityItemsLabel.setText("Enter quantity for Item #" + (numInCartCounter+1) + ": ");
            itemDetailsLabel.setText("Details for Item #" + numInCartCounter + ": ");
            subtotalLabel.setText("Current subtotal for " + numInCartCounter + " item(s): ");


            
        }
    });

    exitButton.addActionListener(new ActionListener()
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            System.exit(0); //close window
        }
    });
    }
 public void inventorySearch(String itemID, String filePath) {
        // Filter the lines in the file to find the one that matches the given itemID

    try (var lines = Files.lines(Paths.get(filePath))) {
        Optional<String> foundLine = lines
            .filter(line -> {
                String[] tokens = line.split(",");
                return tokens.length >= 5 && tokens[0].trim().equals(itemID);
            })
            .findFirst();// Get the first line that matches, if any

        if (foundLine.isPresent()) {
            String[] tokens = foundLine.get().split(",");
            itemNum = tokens[0].trim();
            itemName = tokens[1].trim(); 
            available = tokens[2].trim();
            quantityInStock = tokens[3].trim();
            price = tokens[4].trim();

            // Check availability
            if ("false".equalsIgnoreCase(available)) {
                JOptionPane.showMessageDialog(null, "Sorry... that item is out of stock, please try another item","Nile.com - Error ",JOptionPane.INFORMATION_MESSAGE);
                itemIDField.setText("");
                quantityItemsField.setText("");
            } else {
                    // If the item is available, check if the requested quantity is in stock
                int stockQuantity = Integer.parseInt(quantityInStock);
                if (numItemAskedFor > stockQuantity) {
                    JOptionPane.showMessageDialog(null, "Insufficient stock. Only " + quantityInStock + " on hand. Please reduce the quantity.", "Nile.com - Error",JOptionPane.INFORMATION_MESSAGE);
                    itemIDField.setText("");
                    quantityItemsField.setText("");
                    addToCartButton.setEnabled(false); // Disable the add to cart button since the request cannot be fulfilled
                } else {
                    priceWdiscount = numItemAskedFor * discount * Float.parseFloat(price);
                    itemDetailsField.setText(itemNum + " " + itemName + " $" + price + " " + numItemAskedFor + " " + (Math.round(100 * (1 - discount))) + "% " + "$" + twoPlaces.format(priceWdiscount));
                    // Append the transaction details to the transaction buffer string
                    transactionBufferString = transactionBufferString + itemNum + ", " + itemName + ", " + price + ", " + numItemAskedFor + ", " + transactionFileDiscount.format((1 - discount)) + ", " + "$" + twoPlaces.format(priceWdiscount) + "," + "\n";
                    searchItemButton.setEnabled(false);
                    addToCartButton.setEnabled(true);
                }
            } 
        } else {
            // Item ID not found
            JOptionPane.showMessageDialog(null, "Item ID " + itemID + " not in file", "Nile.com - Error",JOptionPane.INFORMATION_MESSAGE);
            itemIDField.setText("");
            subtotalField.setText("");
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}

}
