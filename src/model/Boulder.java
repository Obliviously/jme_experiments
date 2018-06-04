/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Fabian Rauscher
 */
public class Boulder implements Serializable
{
    private final int id;
    private ColorRGBA color;
    private final ArrayList<Vector3f> marks = new ArrayList<>();

    public Boulder(int id, ColorRGBA color)
    {
        this.id = id;
        this.color = color;
    }

    public boolean addMark(Vector3f mark)
    {
        return this.marks.add(mark);
    }

    public Vector3f getLastMark()
    {
        return marks.get(marks.size() - 1);
    }

    public Vector3f getNthLastMark(int n)
    {
        if (n >= marks.size())
        {
            return null;
        }
        return marks.get(marks.size() - n);
    }

    public ColorRGBA getColor()
    {
        return this.color;
    }

    public void setColor(ColorRGBA color)
    {
        this.color = color;
    }
}
