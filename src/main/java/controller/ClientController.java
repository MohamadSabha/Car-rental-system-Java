package controller;

import Model.Client;
import service.ClientService;

import io.javalin.http.Context;

import java.sql.SQLException;
import java.util.List;

public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    public void getAllClients(Context ctx) throws SQLException {
        List<Client> clients = clientService.getAllClients();
        ctx.json(clients);
    }
}