package de.dangschulz;

public class Account {

    private String PartitionKey;
    private String RowKey;
    private String name;
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPartitionKey() {
        return this.PartitionKey;
    }

    public void setPartitionKey(String key) {
        this.PartitionKey = key;
    }

    public String getRowKey() {
        return this.RowKey;
    }

    public void setRowKey(String key) {
        this.RowKey = key;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
