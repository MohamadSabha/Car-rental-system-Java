package service;

import DB.DatabaseManager;
import Model.Client;

import java.sql.SQLException;
import java.util.List;

public class ClientService {

    private final DatabaseManager databaseManager;

    public ClientService(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public List<Client> getAllClients() throws SQLException {
        return databaseManager.getAllClients();
    }

}