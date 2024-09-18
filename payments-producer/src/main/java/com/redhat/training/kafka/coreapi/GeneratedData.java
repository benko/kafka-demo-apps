package com.redhat.training.kafka.coreapi;

import java.util.Random;

import com.redhat.training.kafka.model.BankAccount;

public class GeneratedData {
    private static final String[] accounts = {
        "4f1fabc1-2dfc-475d-ad59-dbe9be76f381",
        "c2119898-eae8-45a8-b24a-83e964c3440f",
        "a29112f1-ffc8-486d-b8aa-07f14daa4ea1",
        "961eb104-ef35-46f6-9fa5-9493513157ca",
        "70998997-6acf-43f5-98c7-41315975c5cc",
        "96686115-6ca7-4739-9198-5dd52084f563",
        "cc151e37-694c-46ef-a5e5-3ece1939485c",
        "8ae565a0-0d76-464b-8f32-be4a116c0d4c",
        "ea4e728a-a33c-4fcc-a43b-aba37b58f598",
        "8e81d57f-eb56-4a39-80c3-89b0019ea316",
    };

    private static final String[] usernames = {
        "jdoe",
        "janed",
        "tjones",
        "ljohnson",
        "mikep",
        "catbat",
        "qmd",
        "py",
        "aletter",
        "abug",
    };

    private static final String[] userfullnames = {
        "John Doe",
        "Jane Doe",
        "Tom Jones",
        "Linda Johnson",
        "Mike Pearson",
        "Cathy Bates",
        "Quasi Modo",
        "老百姓",
        "Anita Letterback",
        "Aida Bugg",
    };

    private static final Random rand = new Random();

    public static String getRandomCustomerId() {
        return usernames[rand.nextInt(usernames.length)];
    }

    public static String getRandomAccountId() {
        return accounts[rand.nextInt(accounts.length)];
    }

    public static String getCustomerId(int id) {
        if (id >= usernames.length) {
            return null;
        }
        return usernames[id];
    }

    public static BankAccount getBankAccount(int id) {
        if (id >= accounts.length) {
            return null;
        }
        return new BankAccount(accounts[id], usernames[id], userfullnames[id], 0);
    }
}
