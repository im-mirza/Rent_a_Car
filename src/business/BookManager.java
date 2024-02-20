package business;

import core.Helper;
import dao.BookDao;
import entity.Book;

import java.util.ArrayList;

public class BookManager {

    // BookDao instance to interact with the database
    private BookDao bookDao;

    // Constructor to initialize BookDao
    public BookManager() {
        this.bookDao = new BookDao();
    }

    // Save a book record to the database
    public boolean save(Book book) {
        return this.bookDao.save(book);
    }

    // Delete a book record from the database by ID
    public boolean delete(int id) {
        // Check if the book with the given ID exists before deletion
        if (this.getById(id) != null) {
            Helper.showMsg(id + " ID Kayıtlı araç bulunamadı.");
            return false;
        }
        return this.bookDao.delete(id);
    }

    // Retrieve a book by ID from the database
    public Book getById(int id) {
        return this.bookDao.getById(id);
    }

    // Retrieve all books from the database
    public ArrayList<Book> findAll() {
        return this.bookDao.findAll();
    }

    // Get data formatted for display in a table
    public ArrayList<Object[]> getForTable(int size, ArrayList<Book> books) {
        ArrayList<Object[]> bookList = new ArrayList<>();
        for (Book obj : books) {
            int i = 0;
            Object[] rowObject = new Object[size];
            // Populate the rowObject with book data
            // (ID, Plate, Brand, Model, Name, Mpno, Mail, Idno, Start_date, Finish_date, Price)
            rowObject[i++] = obj.getId();
            rowObject[i++] = obj.getCar().getPlate();
            rowObject[i++] = obj.getCar().getModel().getBrand().getName();
            rowObject[i++] = obj.getCar().getModel().getName();
            rowObject[i++] = obj.getName();
            rowObject[i++] = obj.getMpno();
            rowObject[i++] = obj.getMail();
            rowObject[i++] = obj.getIdno();
            rowObject[i++] = obj.getStrt_date().toString();
            rowObject[i++] = obj.getFnsh_date().toString();
            rowObject[i++] = obj.getPrc();
            bookList.add(rowObject);
        }
        return bookList;
    }

    // Search for books based on the given carId for display in a table
    public ArrayList<Book> searchForTable(int carId) {
        // Constructing the SQL query for searching books
        String select = "SELECT * FROM public.book";
        ArrayList<String> whereList = new ArrayList<>();

        if (carId != 0) {
            whereList.add("carId = " + carId);
        }

        String whereStr = String.join(" AND ", whereList);
        String query = select;
        if (whereStr.length() > 0) {
            query += " WHERE " + whereStr;
        }

        return this.bookDao.selectByQuery(query);
    }
}
