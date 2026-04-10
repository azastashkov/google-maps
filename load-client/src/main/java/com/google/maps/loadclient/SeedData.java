package com.google.maps.loadclient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class SeedData {

    public record Address(String address, double latitude, double longitude) {}

    private static final List<Address> ADDRESSES;

    static {
        List<Address> list = new ArrayList<>(100);
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                int num = row * 10 + col + 1;
                String address = num + " Grid Street";
                double lat = 40.70 + row * 0.01;
                double lng = -74.02 + col * 0.01;
                list.add(new Address(address, lat, lng));
            }
        }
        ADDRESSES = Collections.unmodifiableList(list);
    }

    public static List<Address> all() {
        return ADDRESSES;
    }

    public static Address random() {
        return ADDRESSES.get(ThreadLocalRandom.current().nextInt(ADDRESSES.size()));
    }
}
