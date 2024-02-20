package business;

import core.Helper;
import dao.BrandDao;

import java.util.ArrayList;
import entity.Brand;
import entity.Model;

public class BrandManager {

    // BrandDao instance to interact with the database
    private final BrandDao brandDao;

    // ModelManager instance to manage associated models
    private final ModelManager modelManager;

    // Constructor to initialize BrandDao and ModelManager
    public BrandManager() {
        this.brandDao = new BrandDao();
        this.modelManager = new ModelManager();
    }

    // Get brand data formatted for display in a table
    public ArrayList<Object[]> getForTable(int size) {
        ArrayList<Object[]> brandRowList = new ArrayList<>();
        for (Brand brand : this.findAll()) {
            Object[] rowObject = new Object[size];
            int i = 0;
            // Populate the rowObject with brand data (ID, Name)
            rowObject[i++] = brand.getId();
            rowObject[i++] = brand.getName();
            brandRowList.add(rowObject);
        }
        return brandRowList;
    }

    // Retrieve all brands from the database
    public ArrayList<Brand> findAll() {
        return brandDao.findAll();
    }

    // Save a brand to the database
    public boolean save(Brand brand) {
        // Check if the brand ID is not set (0) before saving
        if (brand.getId() != 0) {
            Helper.showMsg("error");
        }
        return this.brandDao.save(brand);
    }

    // Retrieve a brand by ID from the database
    public Brand getById(int id) {
        return this.brandDao.getById(id);
    }

    // Update a brand in the database
    public boolean update(Brand brand) {
        // Check if the brand with the given ID exists before updating
        if (this.getById(brand.getId()) == null) {
            Helper.showMsg("notFound");
        }
        return this.brandDao.update(brand);
    }

    // Delete a brand from the database by ID
    public boolean delete(int id) {
        // Check if the brand with the given ID exists before deletion
        if (this.getById(id) == null) {
            Helper.showMsg(id + " ID kayıtlı marka bulunamadı.");
            return false;
        }

        // Delete associated models before deleting the brand
        for (Model model : this.modelManager.getByListBrandId(id)) {
            this.modelManager.delete(model.getId());
        }

        return this.brandDao.delete(id);
    }
}
