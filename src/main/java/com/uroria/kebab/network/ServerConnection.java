package com.uroria.kebab.network;

import com.uroria.kebab.KebabServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerConnection extends Thread {
    private ServerSocket serverSocket;
    private final List<ClientConnection> clients;
    private final String ip;
    private final int port;

    public ServerConnection(String ip, int port) {
        this.clients = new ArrayList<ClientConnection>();
        this.ip = ip;
        this.port = port;
        start();
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public List<ClientConnection> getClients() {
        return clients;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port, 50, InetAddress.getByName(ip));
            KebabServer.getInstance().getLogger().info("Server running on " + serverSocket.getInetAddress().getHostName() + ":" + serverSocket.getLocalPort());
            while (true) {
                Socket connection = serverSocket.accept();
                ClientConnection clientConnection = new ClientConnection(connection);
                clients.add(clientConnection);
                clientConnection.start();
            }
        } catch (Exception exception) {
            KebabServer.getInstance().getLogger().error("Error while trying to run ServerSocket ServerConnection", exception);
        }
    }
}
