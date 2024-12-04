package com.example.salesbuddy.model;

import java.io.Serializable;

public class Sale implements Serializable {
    private String client_buddy, cpf_buddy, email_buddy, msn;
    private float value_sale_buddy, value_received;
    private int id, number_sale;
    private String[] description;

    public String[] getDescription() {
        return description;
    }

    public void setDescription(String[] description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClientBuddy() {
        return client_buddy;
    }

    public void setClientBuddy(String client_buddy) {
        this.client_buddy = client_buddy;
    }

    public String getCpfBuddy() {
        return cpf_buddy;
    }

    public void setCpfBuddy(String cpf_buddy) {
        this.cpf_buddy = cpf_buddy;
    }

    public String getEmailBuddy() {
        return email_buddy;
    }

    public void setEmailBuddy(String email_buddy) {
        this.email_buddy = email_buddy;
    }

    public float getValueSaleBuddy() {
        return value_sale_buddy;
    }

    public void setValueSaleBuddy(float value_sale_buddy) {
        this.value_sale_buddy = value_sale_buddy;
    }

    public float getValueReceivedBuddy() {
        return value_received;
    }

    public void setValueReceivedBuddy(float value_received_buddy) {
        this.value_received = value_received_buddy;
    }

    public int getNumberSale() {
        return number_sale;
    }

    public void setNumberSale(int number_sale) {
        this.number_sale = number_sale;
    }

    public String getMsn() {
        return msn;
    }

    public void setMsn(String msn) {
        this.msn = msn;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{ \"salesModel\": { ");
        builder.append("\"name\": \"" + client_buddy + "\", ");
        builder.append("\"cpf\": \"" + cpf_buddy + "\", ");
        builder.append("\"email\": \"" + email_buddy + "\", ");
        builder.append("\"value\": " + value_sale_buddy);
        builder.append(" }, ");
        builder.append("\"items\": [ ");
        if (description != null && description.length > 0) {
            for (int i = 0; i < description.length; i++) {
                builder.append("{ ");
                builder.append("\"description\": \"" + description[i] + "\", ");
                builder.append("\"quantity\": 1, ");
                builder.append("\"price\": " + value_sale_buddy);
                builder.append(" }");
                if (i < description.length - 1) {
                    builder.append(", ");
                }
            }
        }
        builder.append(" ], ");
        builder.append("\"sale_number\": " + number_sale); // Adicionando o campo number_sale
        builder.append(" }");
        return builder.toString();
    }

}
