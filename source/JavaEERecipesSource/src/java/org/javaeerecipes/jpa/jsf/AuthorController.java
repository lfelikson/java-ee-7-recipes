
package org.javaeerecipes.jpa.jsf;

import java.util.List;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.javaeerecipes.jpa.entity.AuthorWork;
import org.javaeerecipes.jpa.entity.Book;
import org.javaeerecipes.jpa.entity.BookAuthor;
import org.javaeerecipes.jpa.local.AuthorWorkType;
import org.javaeerecipes.jpa.session.BookAuthorFacade;
import org.javaeerecipes.jpa.session.BookFacade;
/**
 *
 * @author juneau
 */
@ManagedBean(name = "authorController")
@SessionScoped
public class AuthorController implements Serializable {

    
    @EJB
    private BookAuthorFacade ejbFacade;
    
    @EJB
    private BookFacade bookFacade;
    
    @EJB
    private AuthorWorkType authorWorkFacade;
    
    private List<BookAuthor> authorBookList = null;
    private List<BookAuthor> authorList = null;
    private List<BookAuthor> completeAuthorList = null;
    // Recipe 10-1
    private List<BookAuthor> allAuthors = null;
    // recipe 10-5
    private List<Map>authorBooks = null;
    private String storeName = "Acme Bookstore";
    private BookAuthor current;
    private String authorLast;
    // Utilize the following variable for a different recipe.
    private String authorLastName;
    private Book currentBook;

    /**
     * Creates a new instance
     */
    public AuthorController() {
        authorLast = null;
    }

    /**
     * Searches through all Author objects and populates the authorList with
     * only those authors who were involved with the specified book
     *
     * @return
     */
    public String populateAuthorList(BigDecimal bookId) {
        System.out.println("In the populate area of authorcontroller");
        authorList = new ArrayList();
        if (getAuthorBookList() == null) {
            setAuthorBookList((List<BookAuthor>) ejbFacade.findAll());
        }

        List<AuthorWork> awList = authorWorkFacade.performFind(bookId);
        for (AuthorWork work : awList) {

            
            BookAuthor foundAuthor = ejbFacade.findByAuthorId(work.getAuthorId().getId());
            // Set the currently selected book

            authorList.add(foundAuthor);
        }

        setCurrentBook(bookFacade.find(bookId));
        return "book";
    }

    /**
     * Populates completeAuthorList with each existing Author object
     *
     * @return
     */
    private String populateCompleteAuthorList() {
        completeAuthorList = new ArrayList();
        for (BookAuthor author : getAuthorBookList()) {
            completeAuthorList.add(author);
        }
        currentBook = null;
        return "book";
    }

    public String displayAuthor(String last) {
        if(authorList == null){
            authorList = ejbFacade.findAll();
        }
        List<AuthorWork> awList = null;
        
        boolean foundFlag = false;
        for (BookAuthor author : authorList) {

            if (author.getLast().equalsIgnoreCase(last)) {
                // Find author's books and populate book list
                awList = authorWorkFacade.performFindByAuthor(author);
                List <Book> authorBooks = new ArrayList();
                for(AuthorWork authWork:awList){
                    Book newbook = bookFacade.find(authWork.getBookId());

                    authorBooks.add(newbook);
                }
               
                setCurrent(author);
            }
        }
        return "bio";
    }

    /**
     * @return the authorList
     */
    public List getAuthorList() {
        return authorList;
    }

    /**
     * @return the current
     */
    public BookAuthor getCurrent() {
        System.out.println("Current author is:  "+ current.getFirst());
        return current;
    }

    /**
     * @param current the current to set
     */
    public void setCurrent(BookAuthor current) {
        this.current = current;
    }

    /**
     * @return the authorLast
     */
    public String getAuthorLast() {
        return authorLast;
    }

    /**
     * @param authorLast the authorLast to set
     */
    public void setAuthorLast(String authorLast) {
        displayAuthor(authorLast);
    }

    /**
     * @return the storeName
     */
    public String getStoreName() {
        return storeName;
    }

    /**
     * @param storeName the storeName to set
     */
    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    /**
     * @return the completeAuthorList
     */
    public List<BookAuthor> getCompleteAuthorList() {
        return completeAuthorList;
    }

    /**
     * @param completeAuthorList the completeAuthorList to set
     */
    public void setCompleteAuthorList(List<BookAuthor> completeAuthorList) {
        this.completeAuthorList = completeAuthorList;
    }

    /**
     * @return the currentBook
     */
    public Book getCurrentBook() {
        return currentBook;
    }

    /**
     * @param currentBook the currentBook to set
     */
    public void setCurrentBook(Book currentBook) {
        this.currentBook = currentBook;
    }

    public void findAuthor() {
        if (this.authorLast != null) {
            for (BookAuthor author : authorList) {
                if (author.getLast().equals(authorLast)) {
                    this.current = author;
                }
            }
        } else {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            facesContext.addMessage(null,
                    new FacesMessage("No last name specified."));

        }
    }

    /**
     * Auto-completes author names from the authorBookList
     *
     * @param text
     * @return
     */
    public List<String> complete(String text) {
        List<String> results = new ArrayList();
        // This should print each time you type a letter in the autocomplete box
        System.out.println("completing: " + text);
        for (BookAuthor author : getAuthorBookList()) {
            if (author.getLast().toUpperCase().contains(text.toUpperCase())) {
                results.add(author.getLast().toUpperCase() + " " + author.getFirst().toUpperCase());
            }
        }

        return results;
    }

    public List<BookAuthor> getAuthorBookList(){
        return ejbFacade.getAuthorBookList();
    }

    /**
     * @param authorBookList the authorBookList to set
     */
    public void setAuthorBookList(List<BookAuthor> authorBookList) {
        this.authorBookList = authorBookList;
    }
    
    /**
     * Recipe 10-1: Querying all objects of an entity
     * @return 
     */
    
    public String populateAllAuthors(){
        allAuthors = ejbFacade.findAuthor();
        return "chapter10/recipe10_1";
    }
    
        
    public List<BookAuthor> getAllAuthors() {
        allAuthors = ejbFacade.findAuthor();
        return allAuthors;
    }

    /**
     * @param allAuthors the allAuthors to set
     */
    public void setAllAuthors(List<BookAuthor> allAuthors) {
        this.allAuthors = allAuthors;
    }
    
    /**
     * Recipe 10-5a
     */
    public List<Map> findAuthorBooks(){
        authorBooks = ejbFacade.findAuthorBooksMapping();
        return authorBooks;
    }

    /**
     * Recipe 10-5b
     */
    public List<Map> findAuthorBooksWithoutMapping(){
        authorBooks = ejbFacade.findAuthorBooks();
        return authorBooks;
    }

    
    /**
     * @return the authorBooks
     */
    public List<Map> getAuthorBooks() {
        return authorBooks;
    }

    /**
     * @param authorBooks the authorBooks to set
     */
    public void setAuthorBooks(List<Map> authorBooks) {
        this.authorBooks = authorBooks;
    }
    
    
    /**
     * Recipe 10-10
     */
    public String findAuthorByLast(){
        authorList = ejbFacade.findAuthorByLast(getAuthorLastName());
        return "/chapter10/recipe10_10b";
    }

    /**
     * @return the authorLastName
     */
    public String getAuthorLastName() {
        return authorLastName;
    }

    /**
     * @param authorLastName the authorLastName to set
     */
    public void setAuthorLastName(String authorLastName) {
        this.authorLastName = authorLastName;
    }

    
}