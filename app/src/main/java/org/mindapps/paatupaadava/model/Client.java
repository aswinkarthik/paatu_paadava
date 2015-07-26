package org.mindapps.paatupaadava.model;

public class Client {
    private String ipAddress;
    private Long timeDelayFromServer;

    public Client(String ipAddress, Long timeDelayFromServer) {
        this.ipAddress = ipAddress;
        this.timeDelayFromServer = timeDelayFromServer;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public Long getTimeDelayFromServer() {
        return timeDelayFromServer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Client client = (Client) o;

        if (!ipAddress.equals(client.ipAddress)) return false;
        return timeDelayFromServer.equals(client.timeDelayFromServer);

    }

    @Override
    public int hashCode() {
        int result = ipAddress.hashCode();
        result = 31 * result + timeDelayFromServer.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Client{" +
                "ipAddress='" + ipAddress + '\'' +
                ", timeDelayFromServer=" + timeDelayFromServer +
                '}';
    }
}
