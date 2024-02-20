import business.UserManager;
import core.Db;
import core.Helper;
import entity.User;
import view.AdminView;
import view.LoginView;
import java.sql.Connection;


public class App {
    public static void main (String[] args) {
        Helper.setTheme();
        Connection conn = Db.getInstance();
        LoginView loginView = new LoginView();

    }

}
