package business;

import core.Helper;
import dao.BookDao;
import entity.Book;
import entity.Car;
import dao.CarDao;
import entity.Model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class CarManager {
    // CarDao instance to interact with the database
    private final CarDao carDao;

    // BookDao instance to interact with booking data in the database
    private final BookDao bookDao;

    // Constructor to initialize CarDao and BookDao
    public CarManager(){
        this.carDao = new CarDao();
        this.bookDao = new BookDao();
    }

    // Retrieve a car by ID from the database
    public Car getById(int id){
        return this.carDao.getById(id);
    }

    // Retrieve all cars from the database
    public ArrayList<Car> findAll(){
        return this.carDao.findAll();
    }

    // Get car data formatted for display in a table
    public ArrayList<Object[]> getForTable(int size, ArrayList<Car> cars) {
        ArrayList<Object[]> carList = new ArrayList<>();
        for (Car obj : cars) {
            int i = 0;
            Object[] rowObject = new Object[size];
            // Populate the rowObject with car data (ID, Brand, Model, Plate, Color, Km, Year, Type, Fuel, Gear)
            rowObject[i++] = obj.getId();
            rowObject[i++] = obj.getModel().getBrand().getName();
            rowObject[i++] = obj.getModel().getName();
            rowObject[i++] = obj.getPlate();
            rowObject[i++] = obj.getColor();
            rowObject[i++] = obj.getKm();
            rowObject[i++] = obj.getModel().getYear();
            rowObject[i++] = obj.getModel().getType();
            rowObject[i++] = obj.getModel().getFuel();
            rowObject[i++] = obj.getModel().getGear();
            carList.add(rowObject);
        }
        return carList;
    }

    // Save a car to the database
    public boolean save(Car car) {
        // Check if the car with the given ID already exists before saving
        if (this.getById(car.getId()) != null) {
            Helper.showMsg("error");
            return false;
        }
        return this.carDao.save(car);
    }

    // Update a car in the database
    public boolean update(Car car) {
        // Check if the car with the given ID exists before updating
        if (this.getById(car.getId()) == null) {
            Helper.showMsg(car.getId() + " ID kayıtlı araç bulunamadı");
            return false;
        }
        return this.carDao.update(car);
    }

    // Delete a car from the database by ID
    public boolean delete(int id) {
        // Check if the car with the given ID exists before deletion
        if (this.getById(id) == null) {
            Helper.showMsg(id + " ID kayıtlı araç bulunamadı");
            return false;
        }
        return this.carDao.delete(id);
    }

    // Search for available cars for booking based on criteria
    public ArrayList<Car> searchForBooking(String strt_date, String fnsh_date, Model.Type type, Model.Gear gear, Model.Fuel fuel){
        // Constructing the SQL query for searching cars
        String query = "SELECT * FROM public.car as c LEFT JOIN public.model as m";

        ArrayList<String> where = new ArrayList<>();
        ArrayList<String> joinWhere = new ArrayList<>();
        ArrayList<String> bookOrWhere = new ArrayList<>();

        // Joining car and model tables
        joinWhere.add("c.car_model_id = m.model_id");

        // Formatting dates
        strt_date = LocalDate.parse(strt_date, DateTimeFormatter.ofPattern("dd/MM/yyyy")).toString();
        fnsh_date = LocalDate.parse(fnsh_date, DateTimeFormatter.ofPattern("dd/MM/yyyy")).toString();

        // Adding conditions based on criteria
        if(fuel != null) where.add("m.model_fuel = '" + fuel.toString() + "'");
        if (gear != null) where.add("m.model_gear = '" + gear.toString() + "'");
        if (type != null) where.add("m.model_type = '" + type.toString() + "'");

        String whereStr = String.join(" AND ", where);
        String joinStr = String.join(" AND ", joinWhere);
        if (joinStr.length() > 0) {
            query += " ON " + joinStr;
        }
        if (whereStr.length() > 0) {
            query += " WHERE " + whereStr;
        }

        // Performing the search
        ArrayList<Car> searchedCarList = this.carDao.selectByQuery(query);

        // Constructing the SQL query for searching booked cars during the specified dates
        bookOrWhere.add("('" + strt_date + "' BETWEEN book_strt_date AND book_fnsh_date)");
        bookOrWhere.add("('" + fnsh_date + "' BETWEEN book_strt_date AND book_fnsh_date)");
        bookOrWhere.add("(book_strt_date BETWEEN '" + strt_date + "' AND '" + fnsh_date + "')");
        bookOrWhere.add("(book_fnsh_date BETWEEN '" + strt_date + "' AND '" + fnsh_date + "')");

        String bookOrWhereStr = String.join(" OR ", bookOrWhere);
        String bookQuery = "SELECT * FROM public.book WHERE " + bookOrWhereStr;

        // Retrieving booked cars during the specified dates
        ArrayList<Book> bookList = this.bookDao.selectByQuery(bookQuery);

        // Filtering out booked cars from the search results
        ArrayList<Integer> busyCarId = new ArrayList<>();
        for (Book book : bookList) {
            busyCarId.add(book.getCar_id());
        }

        searchedCarList.removeIf(car -> busyCarId.contains(car.getId()));

        return searchedCarList;
    }
}
