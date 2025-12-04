// ===== IMPORT STATEMENTS =====
// Import required Java packages
import java.io.*;
import java.util.*;

// ===== BOOK CLASS =====
// This class represents a book with all its details
class Book implements Comparable<Book>, Serializable {
    // Unique identifier for each book
    private int bookId;
    // Title of the book
    private String title;
    // Author name
    private String author;
    // Category/Genre of the book
    private String category;
    // Boolean flag to check if book is currently issued
    private boolean isIssued;
    
    // Constructor to initialize book with details
    public Book(int bookId, String title, String author, String category) {
        this.bookId = bookId;           // Set book ID
        this.title = title;             // Set book title
        this.author = author;           // Set author name
        this.category = category;       // Set category
        this.isIssued = false;          // Initially, book is not issued
    }
    
    // Getters - methods to retrieve book properties
    public int getBookId() { 
        return bookId; 
    }
    
    public String getTitle() { 
        return title; 
    }
    
    public String getAuthor() { 
        return author; 
    }
    
    public String getCategory() { 
        return category; 
    }
    
    public boolean isIssued() { 
        return isIssued; 
    }
    
    // Setter - method to mark book as issued
    public void markAsIssued() {
        this.isIssued = true;  // Set isIssued flag to true
    }
    
    // Setter - method to mark book as returned
    public void markAsReturned() {
        this.isIssued = false; // Set isIssued flag to false
    }
    
    // Method to display complete book details
    public void displayBookDetails() {
        System.out.println("Book ID: " + bookId);
        System.out.println("Title: " + title);
        System.out.println("Author: " + author);
        System.out.println("Category: " + category);
        // Display status: Available if not issued, Issued if issued
        System.out.println("Status: " + (isIssued ? "Issued" : "Available"));
        System.out.println("---");
    }
    
    // Convert book object to string format for file storage
    // Format: bookId|title|author|category|isIssued
    @Override
    public String toString() {
        return bookId + "|" + title + "|" + author + "|" + category + "|" + isIssued;
    }
    
    // Comparable method - sorts books by title in alphabetical order
    @Override
    public int compareTo(Book other) {
        // Return negative if this title comes before other
        // Return positive if this title comes after other
        // Return 0 if titles are equal
        return this.title.compareTo(other.title);
    }
}

// ===== MEMBER CLASS =====
// This class represents a library member
class Member implements Serializable {
    // Unique identifier for each member
    private int memberId;
    // Member's name
    private String name;
    // Member's email address
    private String email;
    // List to store IDs of books issued by this member
    private List<Integer> issuedBooks;
    
    // Constructor to initialize member with details
    public Member(int memberId, String name, String email) {
        this.memberId = memberId;           // Set member ID
        this.name = name;                   // Set member name
        this.email = email;                 // Set email
        this.issuedBooks = new ArrayList<>(); // Initialize empty list for issued books
    }
    
    // Getters - methods to retrieve member properties
    public int getMemberId() { 
        return memberId; 
    }
    
    public String getName() { 
        return name; 
    }
    
    public String getEmail() { 
        return email; 
    }
    
    public List<Integer> getIssuedBooks() { 
        return issuedBooks; 
    }
    
    // Method to add a book ID to the issued books list
    public void addIssuedBook(int bookId) {
        // Add book ID to the list if not already present
        if (!issuedBooks.contains(bookId)) {
            issuedBooks.add(bookId); // Add new book ID
        }
    }
    
    // Method to remove a book ID from issued books list
    public void returnIssuedBook(int bookId) {
        // Remove the book ID from the list
        issuedBooks.remove(Integer.valueOf(bookId)); // Remove specific book ID
    }
    
    // Method to display complete member details
    public void displayMemberDetails() {
        System.out.println("Member ID: " + memberId);
        System.out.println("Name: " + name);
        System.out.println("Email: " + email);
        System.out.print("Issued Books: ");
        // If member has no issued books, display "None"
        if (issuedBooks.isEmpty()) {
            System.out.println("None");
        } else {
            // Display all issued book IDs
            System.out.println(issuedBooks);
        }
        System.out.println("---");
    }
    
    // Convert member object to string format for file storage
    // Format: memberId|name|email|bookIds(comma-separated)
    @Override
    public String toString() {
        // Join book IDs with comma for storage
        StringBuilder bookIds = new StringBuilder();
        for (int i = 0; i < issuedBooks.size(); i++) {
            bookIds.append(issuedBooks.get(i));
            if (i < issuedBooks.size() - 1) {
                bookIds.append(",");
            }
        }
        return memberId + "|" + name + "|" + email + "|" + bookIds.toString();
    }
}

// ===== COMPARATOR CLASSES =====
// Comparator to sort books by author name
class BookAuthorComparator implements Comparator<Book> {
    @Override
    public int compare(Book b1, Book b2) {
        // Compare authors alphabetically
        return b1.getAuthor().compareTo(b2.getAuthor());
    }
}

// Comparator to sort books by category
class BookCategoryComparator implements Comparator<Book> {
    @Override
    public int compare(Book b1, Book b2) {
        // Compare categories alphabetically
        return b1.getCategory().compareTo(b2.getCategory());
    }
}

// ===== LIBRARY MANAGER CLASS =====
// This class manages all library operations
public class LibraryManager {
    // Map to store books with book ID as key
    private Map<Integer, Book> books;
    // Map to store members with member ID as key
    private Map<Integer, Member> members;
    // Counter to auto-generate unique book IDs
    private int bookIdCounter;
    // Counter to auto-generate unique member IDs
    private int memberIdCounter;
    // File path for storing books data
    private static final String BOOKS_FILE = "books.txt";
    // File path for storing members data
    private static final String MEMBERS_FILE = "members.txt";
    // Scanner for user input
    private Scanner sc;
    
    // Constructor to initialize LibraryManager
    public LibraryManager() {
        // Initialize HashMap for books (provides fast lookup by ID)
        this.books = new HashMap<>();
        // Initialize HashMap for members
        this.members = new HashMap<>();
        // Initialize book ID counter starting from 101
        this.bookIdCounter = 101;
        // Initialize member ID counter starting from 1001
        this.memberIdCounter = 1001;
        // Create Scanner for reading user input
        this.sc = new Scanner(System.in);
        // Load existing data from files when program starts
        loadFromFile();
    }
    
    // ===== FILE HANDLING - LOAD DATA =====
    // Method to load books from file
    private void loadBooksFromFile() {
        try {
            // Create a File object for books file
            File file = new File(BOOKS_FILE);
            // Check if file exists
            if (!file.exists()) {
                System.out.println("Books file not found. Starting with empty inventory.\n");
                return; // Exit method if file doesn't exist
            }
            
            // FileReader reads character-by-character from file
            FileReader fr = new FileReader(file);
            // BufferedReader wraps FileReader for faster reading
            BufferedReader br = new BufferedReader(fr);
            String line; // Variable to store each line read from file
            
            // Read each line from file until EOF (null)
            while ((line = br.readLine()) != null) {
                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                // Split line by "|" delimiter to extract book details
                String[] parts = line.split("\\|");
                
                // Validate that we have all required parts
                if (parts.length < 5) {
                    continue;
                }
                
                try {
                    // Extract book details from parts array
                    int bookId = Integer.parseInt(parts[0]);      // Convert string to int
                    String title = parts[1];                      // Title is at index 1
                    String author = parts[2];                     // Author is at index 2
                    String category = parts[3];                   // Category is at index 3
                    boolean isIssued = Boolean.parseBoolean(parts[4]); // Convert string to boolean
                    
                    // Create new Book object with loaded data
                    Book book = new Book(bookId, title, author, category);
                    // If book was issued in file, mark it as issued
                    if (isIssued) {
                        book.markAsIssued();
                    }
                    
                    // Store book in map using book ID as key
                    books.put(bookId, book);
                    // Update book ID counter to be higher than highest existing ID
                    if (bookId >= bookIdCounter) {
                        bookIdCounter = bookId + 1;
                    }
                } catch (NumberFormatException e) {
                    // Skip lines with invalid data
                    System.out.println("Skipping invalid book record: " + line);
                }
            }
            
            // Close BufferedReader (also closes underlying FileReader)
            br.close();
            System.out.println("Books loaded successfully.\n");
            
        } catch (FileNotFoundException e) {
            // Handle case where file is not found
            System.out.println("Books file not found: " + e.getMessage() + "\n");
        } catch (IOException e) {
            // Handle input/output errors
            System.out.println("Error reading books file: " + e.getMessage() + "\n");
        } catch (Exception e) {
            // Handle any other unexpected errors
            System.out.println("Error loading books: " + e.getMessage() + "\n");
        }
    }
    
    // Method to load members from file
    private void loadMembersFromFile() {
        try {
            // Create File object for members file
            File file = new File(MEMBERS_FILE);
            // Check if file exists
            if (!file.exists()) {
                System.out.println("Members file not found. Starting with no members.\n");
                return;
            }
            
            // FileReader for character-based reading
            FileReader fr = new FileReader(file);
            // BufferedReader for efficient reading
            BufferedReader br = new BufferedReader(fr);
            String line;
            
            // Read each line from the file
            while ((line = br.readLine()) != null) {
                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                try {
                    // Split line by "|" to get member details
                    String[] parts = line.split("\\|");
                    
                    // Validate we have minimum required parts
                    if (parts.length < 3) {
                        continue;
                    }
                    
                    // Extract member details
                    int memberId = Integer.parseInt(parts[0]);
                    String name = parts[1];
                    String email = parts[2];
                    
                    // Create new Member object
                    Member member = new Member(memberId, name, email);
                    
                    // If there are issued books data (parts[3] exists)
                    if (parts.length > 3 && !parts[3].isEmpty()) {
                        // Split issued book IDs by comma and add each to member's list
                        String[] bookIds = parts[3].split(",");
                        for (String bookId : bookIds) {
                            try {
                                // Add each book ID to member's issued books list
                                member.addIssuedBook(Integer.parseInt(bookId));
                            } catch (NumberFormatException e) {
                                // Skip invalid book IDs
                                System.out.println("Skipping invalid book ID: " + bookId);
                            }
                        }
                    }
                    
                    // Store member in map using member ID as key
                    members.put(memberId, member);
                    // Update member ID counter
                    if (memberId >= memberIdCounter) {
                        memberIdCounter = memberId + 1;
                    }
                } catch (NumberFormatException e) {
                    // Skip lines with invalid member data
                    System.out.println("Skipping invalid member record: " + line);
                }
            }
            
            // Close BufferedReader
            br.close();
            System.out.println("Members loaded successfully.\n");
            
        } catch (FileNotFoundException e) {
            System.out.println("Members file not found: " + e.getMessage() + "\n");
        } catch (IOException e) {
            System.out.println("Error reading members file: " + e.getMessage() + "\n");
        } catch (Exception e) {
            System.out.println("Error loading members: " + e.getMessage() + "\n");
        }
    }
    
    // Method to load all data from files (called during initialization)
    public void loadFromFile() {
        System.out.println("Loading data from files...\n");
        // Load books first
        loadBooksFromFile();
        // Then load members
        loadMembersFromFile();
    }
    
    // ===== FILE HANDLING - SAVE DATA =====
    // Method to save all books to file
    private void saveBooksToFile() {
        try {
            // FileWriter writes characters to file
            FileWriter fw = new FileWriter(BOOKS_FILE);
            // BufferedWriter wraps FileWriter for faster writing
            BufferedWriter bw = new BufferedWriter(fw);
            
            // Iterate through all books in the map
            for (Book book : books.values()) {
                // Convert each book to string format and write to file
                bw.write(book.toString());
                // Write newline after each book
                bw.newLine();
            }
            
            // Flush buffer to ensure all data is written
            bw.flush();
            // Close BufferedWriter
            bw.close();
            
        } catch (IOException e) {
            System.out.println("Error saving books: " + e.getMessage());
        }
    }
    
    // Method to save all members to file
    private void saveMembersToFile() {
        try {
            // Create FileWriter for members file
            FileWriter fw = new FileWriter(MEMBERS_FILE);
            // Wrap with BufferedWriter for efficiency
            BufferedWriter bw = new BufferedWriter(fw);
            
            // Iterate through all members in the map
            for (Member member : members.values()) {
                // Convert member to string and write to file
                bw.write(member.toString());
                // Write newline
                bw.newLine();
            }
            
            // Flush to write all data
            bw.flush();
            // Close writer
            bw.close();
            
        } catch (IOException e) {
            System.out.println("Error saving members: " + e.getMessage());
        }
    }
    
    // Method to save all data to files
    public void saveToFile() {
        // Save books to file
        saveBooksToFile();
        // Save members to file
        saveMembersToFile();
        System.out.println("Data saved successfully.\n");
    }
    
    // ===== LIBRARY OPERATIONS =====
    // Method to add a new book
    public void addBook() {
        try {
            System.out.print("Enter Book Title: ");
            // Read book title (entire line including spaces)
            String title = sc.nextLine().trim();
            // Validate title is not empty
            if (title.isEmpty()) {
                System.out.println("Title cannot be empty!\n");
                return;
            }
            
            System.out.print("Enter Author: ");
            // Read author name
            String author = sc.nextLine().trim();
            // Validate author is not empty
            if (author.isEmpty()) {
                System.out.println("Author cannot be empty!\n");
                return;
            }
            
            System.out.print("Enter Category: ");
            // Read category
            String category = sc.nextLine().trim();
            // Validate category is not empty
            if (category.isEmpty()) {
                System.out.println("Category cannot be empty!\n");
                return;
            }
            
            // Create new book with auto-generated ID
            Book newBook = new Book(bookIdCounter, title, author, category);
            // Store book in map
            books.put(bookIdCounter, newBook);
            System.out.println("Book added successfully with ID: " + bookIdCounter + "\n");
            // Increment ID for next book
            bookIdCounter++;
            // Save updated data to file
            saveToFile();
            
        } catch (Exception e) {
            System.out.println("Error adding book: " + e.getMessage() + "\n");
        }
    }
    
    // Method to add a new member
    public void addMember() {
        try {
            System.out.print("Enter Member Name: ");
            // Read member name
            String name = sc.nextLine().trim();
            // Validate name is not empty
            if (name.isEmpty()) {
                System.out.println("Name cannot be empty!\n");
                return;
            }
            
            System.out.print("Enter Email: ");
            // Read email address
            String email = sc.nextLine().trim();
            // Validate email format (basic check)
            if (!email.contains("@")) {
                System.out.println("Invalid email format!\n");
                return;
            }
            
            // Create new member with auto-generated ID
            Member newMember = new Member(memberIdCounter, name, email);
            // Store member in map
            members.put(memberIdCounter, newMember);
            System.out.println("Member added successfully with ID: " + memberIdCounter + "\n");
            // Increment ID for next member
            memberIdCounter++;
            // Save updated data to file
            saveToFile();
            
        } catch (Exception e) {
            System.out.println("Error adding member: " + e.getMessage() + "\n");
        }
    }
    
    // Method to issue a book to a member
    public void issueBook() {
        try {
            System.out.print("Enter Book ID: ");
            // Read book ID
            int bookId = sc.nextInt();
            System.out.print("Enter Member ID: ");
            // Read member ID
            int memberId = sc.nextInt();
            sc.nextLine(); // Clear input buffer
            
            // Check if book exists in map
            if (!books.containsKey(bookId)) {
                System.out.println("Book not found!\n");
                return;
            }
            
            // Check if member exists in map
            if (!members.containsKey(memberId)) {
                System.out.println("Member not found!\n");
                return;
            }
            
            // Get book from map
            Book book = books.get(bookId);
            // Check if book is already issued
            if (book.isIssued()) {
                System.out.println("Book is already issued!\n");
                return;
            }
            
            // Mark book as issued
            book.markAsIssued();
            // Add book ID to member's issued books list
            members.get(memberId).addIssuedBook(bookId);
            System.out.println("Book issued successfully!\n");
            // Save updated data to file
            saveToFile();
            
        } catch (InputMismatchException e) {
            System.out.println("Please enter valid numbers!\n");
            sc.nextLine();
        } catch (Exception e) {
            System.out.println("Error issuing book: " + e.getMessage() + "\n");
            sc.nextLine(); // Clear buffer
        }
    }
    
    // Method to return a book from a member
    public void returnBook() {
        try {
            System.out.print("Enter Book ID: ");
            // Read book ID
            int bookId = sc.nextInt();
            System.out.print("Enter Member ID: ");
            // Read member ID
            int memberId = sc.nextInt();
            sc.nextLine(); // Clear buffer
            
            // Check if book exists
            if (!books.containsKey(bookId)) {
                System.out.println("Book not found!\n");
                return;
            }
            
            // Check if member exists
            if (!members.containsKey(memberId)) {
                System.out.println("Member not found!\n");
                return;
            }
            
            // Get book and mark as returned
            books.get(bookId).markAsReturned();
            // Remove book ID from member's issued list
            members.get(memberId).returnIssuedBook(bookId);
            System.out.println("Book returned successfully!\n");
            // Save updated data
            saveToFile();
            
        } catch (InputMismatchException e) {
            System.out.println("Please enter valid numbers!\n");
            sc.nextLine();
        } catch (Exception e) {
            System.out.println("Error returning book: " + e.getMessage() + "\n");
            sc.nextLine();
        }
    }
    
    // Method to search books by various criteria
    public void searchBooks() {
        try {
            System.out.println("Search by: 1. Title  2. Author  3. Category");
            System.out.print("Enter choice: ");
            int choice = sc.nextInt();
            sc.nextLine(); // Clear buffer
            
            System.out.print("Enter search term: ");
            // Read search keyword
            String searchTerm = sc.nextLine().trim().toLowerCase();
            
            // List to store search results
            List<Book> results = new ArrayList<>();
            
            // Search based on user choice
            switch (choice) {
                case 1: // Search by title
                    // Iterate through all books
                    for (Book book : books.values()) {
                        // Check if title contains search term (case-insensitive)
                        if (book.getTitle().toLowerCase().contains(searchTerm)) {
                            results.add(book); // Add to results
                        }
                    }
                    break;
                    
                case 2: // Search by author
                    for (Book book : books.values()) {
                        // Check if author contains search term
                        if (book.getAuthor().toLowerCase().contains(searchTerm)) {
                            results.add(book);
                        }
                    }
                    break;
                    
                case 3: // Search by category
                    for (Book book : books.values()) {
                        // Check if category contains search term
                        if (book.getCategory().toLowerCase().contains(searchTerm)) {
                            results.add(book);
                        }
                    }
                    break;
                    
                default:
                    System.out.println("Invalid choice!\n");
                    return;
            }
            
            // Display search results
            if (results.isEmpty()) {
                System.out.println("No books found.\n");
            } else {
                System.out.println("Search Results:\n");
                for (Book book : results) {
                    book.displayBookDetails();
                }
            }
            
        } catch (InputMismatchException e) {
            System.out.println("Please enter a valid number!\n");
            sc.nextLine();
        } catch (Exception e) {
            System.out.println("Error searching books: " + e.getMessage() + "\n");
            sc.nextLine();
        }
    }
    
    // Method to sort and display books
    public void sortBooks() {
        try {
            System.out.println("Sort by: 1. Title  2. Author  3. Category");
            System.out.print("Enter choice: ");
            int choice = sc.nextInt();
            sc.nextLine(); // Clear buffer
            
            // Create list from books map values
            List<Book> bookList = new ArrayList<>(books.values());
            
            // Check if there are books to sort
            if (bookList.isEmpty()) {
                System.out.println("No books available to sort.\n");
                return;
            }
            
            // Sort based on user choice
            switch (choice) {
                case 1: // Sort by title using Comparable
                    // Collections.sort uses compareTo method defined in Book class
                    Collections.sort(bookList);
                    System.out.println("Books sorted by Title:\n");
                    break;
                    
                case 2: // Sort by author using Comparator
                    // Pass custom comparator to sort method
                    Collections.sort(bookList, new BookAuthorComparator());
                    System.out.println("Books sorted by Author:\n");
                    break;
                    
                case 3: // Sort by category using Comparator
                    Collections.sort(bookList, new BookCategoryComparator());
                    System.out.println("Books sorted by Category:\n");
                    break;
                    
                default:
                    System.out.println("Invalid choice!\n");
                    return;
            }
            
            // Display sorted books
            for (Book book : bookList) {
                book.displayBookDetails();
            }
            
        } catch (InputMismatchException e) {
            System.out.println("Please enter a valid number!\n");
            sc.nextLine();
        } catch (Exception e) {
            System.out.println("Error sorting books: " + e.getMessage() + "\n");
            sc.nextLine();
        }
    }
    
    // Method to display statistics
    public void displayStatistics() {
        System.out.println("===== Library Statistics =====");
        System.out.println("Total Books: " + books.size());
        // Count issued books
        int issuedCount = 0;
        for (Book book : books.values()) {
            if (book.isIssued()) {
                issuedCount++;
            }
        }
        System.out.println("Books Issued: " + issuedCount);
        System.out.println("Books Available: " + (books.size() - issuedCount));
        System.out.println("Total Members: " + members.size());
        System.out.println("---\n");
    }
    
    // ===== MAIN MENU =====
    // Main menu method
    public void mainMenu() {
        int choice = 0;
        
        try {
            // Loop until user exits
            while (true) {
                // Display menu
                System.out.println("\n===== Welcome to City Library Digital Management System =====");
                System.out.println("1. Add Book");
                System.out.println("2. Add Member");
                System.out.println("3. Issue Book");
                System.out.println("4. Return Book");
                System.out.println("5. Search Books");
                System.out.println("6. Sort Books");
                System.out.println("7. View Statistics");
                System.out.println("8. Exit");
                System.out.print("Enter your choice: ");
                
                choice = sc.nextInt();
                sc.nextLine(); // Clear input buffer
                
                // Handle menu choices
                switch (choice) {
                    case 1:
                        addBook(); // Add new book
                        break;
                    case 2:
                        addMember(); // Add new member
                        break;
                    case 3:
                        issueBook(); // Issue book to member
                        break;
                    case 4:
                        returnBook(); // Return book from member
                        break;
                    case 5:
                        searchBooks(); // Search books
                        break;
                    case 6:
                        sortBooks(); // Sort books
                        break;
                    case 7:
                        displayStatistics(); // Show statistics
                        break;
                    case 8:
                        // Save all data before exiting
                        saveToFile();
                        System.out.println("Thank you for using City Library Management System!");
                        return; // Exit program
                    default:
                        System.out.println("Invalid choice. Please try again.\n");
                }
            }
            
        } catch (InputMismatchException e) {
            System.out.println("Please enter a valid number!");
            sc.nextLine();
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        } finally {
            // Close scanner and save data
            if (sc != null) {
                sc.close();
                System.out.println("Program terminated.");
            }
        }
    }
    
    // Main method - entry point
    public static void main(String[] args) {
        // Create LibraryManager instance
        LibraryManager manager = new LibraryManager();
        // Start the application
        manager.mainMenu();
    }
}