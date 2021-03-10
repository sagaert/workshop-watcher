package org.salex.raspberry.workshop.data;

import java.awt.*;

public class Sensor {
    public static enum Type {
        CPU,
        DHT22
    }

    private final int id;
    private final String name;
    private final Color color;
    private final String colorHex;
    private final Type type;
    private final String port;

    public Sensor(int id, String name, String color, String type, String port) {
        this.id = id;
        this.name = name;
        this.colorHex = color;
        this.color = Color.decode(color);
        this.type = Type.valueOf(type);
        this.port = port;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public String getColorHex() {
        return colorHex;
    }

    public Type getType() {
        return type;
    }

    public String getPort() {
        return port;
    }

}
