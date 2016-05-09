package com.Modelclasses.Dataclasses;

import java.util.ArrayList;

/**
 * Created by Gustav on 2016-05-02.
 * Class for containing and storing data about a section, also what sensors belong to that section
 */
public class Section
{
    private int id;
    private int amount;
    public Section(int id)
    {
        this.amount = 0;
        this.id = id;
    }
    public void increment()
    {
        this.amount++;
    }
    public void decrement()
    {
        this.amount--;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
