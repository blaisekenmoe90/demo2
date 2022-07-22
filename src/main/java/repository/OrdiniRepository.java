package repository;

import model.Articolo;
import model.Ordine;


import java.sql.*;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;



import static constants.CredentialConnectionDB.*;


public class OrdiniRepository {

    public static ArrayList<Ordine> getAllOrdini(){
        // creo una lista da restituire
        ArrayList<Ordine> ordini = new ArrayList<>();

        try{
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            PreparedStatement stmt = conn.prepareStatement("select * from ordini");
            ResultSet rs = stmt.executeQuery();

            while(rs.next()){

                // creo una fermata con i dati del DB
                Ordine ordine = new Ordine(
                        rs.getInt("id"),
                        rs.getInt("numero"),
                        rs.getString("data"));

                // aggiungo alla lista
                ordini.add(ordine);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        //restituisco la lista
        return ordini;
    }


}