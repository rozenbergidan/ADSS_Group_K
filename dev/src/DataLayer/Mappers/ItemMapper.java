package DataLayer.Mappers;

import BuisnnesLayer.GeneralProduct;
import BuisnnesLayer.Item;
import DataLayer.DataController;

import java.sql.*;
import java.text.ParseException;
import java.util.LinkedList;


public class ItemMapper extends Mapper {

    public ItemMapper() {
        super();
        create_table();
    }


    @Override
    void create_table() {
        String itemTable = "CREATE TABLE IF NOT EXISTS Items(\n" +
                "\tgpID INTEGER,\n" +
                "\tiID INTEGER,\n" +
                "\tlocation TEXT,\n" +
                "\tsupplied_date TEXT,\n" +
                "\tcreation_date TEXT,\n" +
                "\texpiration_date TEXT,\n" +
                "\tPRIMARY KEY (gpID, iID),\n" +
                "\tFOREIGN KEY (gpID) REFERENCES GeneralProducts (gpID)\n" +
                ");";
        //        String sql = "BEGIN TRANSACTION;" + itemTable + "COMMIT;";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            // create a new tables
            stmt.execute(itemTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean update(Item item) {
        boolean updated = false;
        try (Connection conn = connect()) {
            String statement = "UPDATE Items SET gpID=?, iID=?, location=?, supplied_date=?, creation_date=?, expiration_date=? WHERE gpID=? AND iID=? ";

            try (PreparedStatement pstmt = conn.prepareStatement(statement)) {
                pstmt.setInt(1, item.getProduct_id());
                pstmt.setInt(2, item.getItem_id());
                pstmt.setString(3, item.getLocation());
                pstmt.setString(4, DataController.getDate(item.getSupplied_date()));
                pstmt.setString(5, DataController.getDate(item.getCreation_date()));
                pstmt.setString(6, DataController.getDate(item.getExpiration_date()));
                pstmt.setInt(7, item.getProduct_id());
                pstmt.setInt(8, item.getItem_id());
                updated = pstmt.executeUpdate() != 0;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return updated;
    }

    public boolean delete(Item item) {
        boolean deleted = false;
        try (Connection conn = connect()) {
            String statement = "DELETE FROM Items WHERE gpID=? AND iID=?";

            try (PreparedStatement pstmt = conn.prepareStatement(statement)) {
                pstmt.setInt(1, item.getProduct_id());
                pstmt.setInt(2, item.getItem_id());
                deleted = pstmt.executeUpdate() != 0;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return deleted;
    }

    public boolean insertItem(Item item) {
        boolean output = false;
        try (Connection conn = connect()) {
            boolean inserted = false;
            String statement = "INSERT OR IGNORE INTO Items (gpID, iID, location, supplied_date, creation_date, expiration_date) " +
                    "VALUES (?,?,?,?,?,?)";

            try (PreparedStatement pstmt = conn.prepareStatement(statement)) {
                pstmt.setInt(1, item.getProduct_id());
                pstmt.setInt(2, item.getItem_id());
                pstmt.setString(3, item.getLocation());
                pstmt.setString(4, DataController.getDate(item.getSupplied_date()));
                pstmt.setString(5, DataController.getDate(item.getCreation_date()));
                pstmt.setString(6, DataController.getDate(item.getExpiration_date()));
                output = pstmt.executeUpdate() != 0;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return output;
    }

    public LinkedList<Item> addItemToProduct(GeneralProduct gp){
        LinkedList<Item> output = new LinkedList<>();
        try (Connection conn = connect()) {
            String statement = "SELECT * FROM Items WHERE gpID=? ";

            try (PreparedStatement pstmt = conn.prepareStatement(statement)) {
                pstmt.setInt(1, gp.getProduct_id());

                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    int gpID = rs.getInt(1);
                    int iID = rs.getInt(2);
                    String location = rs.getString(3);
                    String sup_date = rs.getString(4);
                    String create_date = rs.getString(5);
                    String exp_date = rs.getString(6);
                    Item toAdd = new Item(iID, gpID, location, DataController.getDate(sup_date), DataController.getDate(create_date), DataController.getDate(exp_date));
                    gp.addItem(toAdd);
                    output.add(toAdd);
                }
            } catch (SQLException | ParseException e) {
                e.printStackTrace();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return output;
    }
}
