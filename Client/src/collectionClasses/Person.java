package collectionClasses;

import java.io.Serializable;

public class Person implements Serializable {
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Integer height; //Поле может быть null, Значение поля должно быть больше 0
    private Color hairColor; //Поле может быть null
    private Country nationality; //Поле не может быть null
    private Location location; //Поле может быть null

    public Person(String name, Integer height, Color hairColor, Country nationality, Location location) {
        this.name = name;
        this.height = height;
        this.hairColor = hairColor;
        this.nationality = nationality;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public Integer getHeight() {
        return height;
    }

    public Color getHairColor() {
        return hairColor;
    }

    public Country getNationality() {
        return nationality;
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "(" +
                "name = '" + name + '\'' +
                ", height = " + height +
                ", hairColor = " + hairColor +
                ", nationality = " + nationality +
                ", location = " + location +
                ')';
    }
}
