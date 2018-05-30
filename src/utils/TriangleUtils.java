/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import com.jme3.math.Triangle;
import com.jme3.math.Vector3f;

/**
 *
 * @author Fabian Rauscher
 */
public class TriangleUtils
{
    /**
     * Compares the given triangle with the two vertices. If both vertices are
     * part of the triangle it returns the third one. Else NULL is returned.
     *
     * @param triangle
     * @param v1
     * @param v2
     *
     * @return
     */
    public static Vector3f twoMatchingVertices(Triangle triangle, Vector3f v1, Vector3f v2)
    {
        // t3
        if (triangle.get1().equals(v1) && triangle.get2().equals(v2))
        {
            return triangle.get3();
        }
        // t3
        if (triangle.get1().equals(v2) && triangle.get2().equals(v1))
        {
            return triangle.get3();
        }
        // t2
        if (triangle.get1().equals(v1) && triangle.get3().equals(v2))
        {
            return triangle.get2();
        }
        // t2
        if (triangle.get1().equals(v2) && triangle.get3().equals(v1))
        {
            return triangle.get2();
        }
        // t1
        if (triangle.get2().equals(v1) && triangle.get3().equals(v2))
        {
            return triangle.get1();
        }
        // t1
        if (triangle.get2().equals(v2) && triangle.get3().equals(v1))
        {
            return triangle.get1();
        }
        return null;
    }
}
