package business;

import core.Helper;
import dao.ModelDao;
import entity.Model;
import java.util.ArrayList;

public class ModelManager {
    // ModelDao instance to interact with the database
    private final ModelDao modelDao = new ModelDao();

    // Retrieve a model by ID from the database
    public Model getById(int id) {
        return this.modelDao.getById(id);
    }

    // Retrieve all models from the database
    public ArrayList<Model> findAll() {
        return this.modelDao.findAll();
    }

    // Get model data formatted for display in a table
    public ArrayList<Object[]> getForTable(int size, ArrayList<Model> modelList) {
        ArrayList<Object[]> modelObjList = new ArrayList<>();
        for (Model obj : modelList) {
            int i = 0;
            Object[] rowObject = new Object[size];
            // Populate the rowObject with model data (ID, Brand, Name, Type, Year, Fuel, Gear)
            rowObject[i++] = obj.getId();
            rowObject[i++] = obj.getBrand().getName();
            rowObject[i++] = obj.getName();
            rowObject[i++] = obj.getType();
            rowObject[i++] = obj.getYear();
            rowObject[i++] = obj.getFuel();
            rowObject[i++] = obj.getGear();
            modelObjList.add(rowObject);
        }
        return modelObjList;
    }

    // Save a model to the database
    public boolean save(Model model) {
        // Check if the model with the given ID already exists before saving
        if (this.getById(model.getId()) != null) {
            Helper.showMsg("error");
            return false;
        }
        return this.modelDao.save(model);
    }

    // Update a model in the database
    public boolean update(Model model) {
        // Check if the model with the given ID exists before updating
        if (this.getById(model.getId()) == null) {
            Helper.showMsg(model.getId() + " ID kayıtlı model bulunamadı.");
            return false;
        }
        return this.modelDao.update(model);
    }

    // Delete a model from the database by ID
    public boolean delete(int id) {
        // Check if the model with the given ID exists before deletion
        if (this.getById(id) == null) {
            Helper.showMsg(id + " ID kayıtlı model bulunamadı.");
            return false;
        }
        return this.modelDao.delete(id);
    }

    // Retrieve models by the ID of the associated brand
    public ArrayList<Model> getByListBrandId(int brandId) {
        return this.modelDao.getByListBrandId(brandId);
    }

    // Search for models based on criteria for display in a table
    public ArrayList<Model> searchForTable(int brand_id, Model.Fuel fuel, Model.Gear gear, Model.Type type) {
        // Constructing the SQL query for searching models
        String select = "SELECT * FROM public.model";
        ArrayList<String> whereList = new ArrayList<>();

        // Adding conditions based on criteria
        if (brand_id != 0) {
            whereList.add("model_brand_id=" + brand_id);
        }
        if (fuel != null) {
            whereList.add("model_fuel='" + fuel.toString() + "'");
        }
        if (gear != null) {
            whereList.add("model_gear='" + gear.toString() + "'");
        }
        if (type != null) {
            whereList.add("model_type='" + type.toString() + "'");
        }

        String whereStr = String.join(" AND ", whereList);
        String query = select;
        if (whereStr.length() > 0) {
            query += " WHERE " + whereStr;
        }

        return this.modelDao.selectByQuery(query);
    }
}
