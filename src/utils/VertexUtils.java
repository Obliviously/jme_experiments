/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import java.nio.FloatBuffer;
import java.util.ArrayList;

/**
 *
 * @author Fabian Rauscher
 */
public class VertexUtils
{
    public static void changeColorOfVertices(Geometry geometry, ArrayList<Vector3f> changeVertices)
    {
        Mesh mesh = geometry.getMesh();
        mesh.clearBuffer(VertexBuffer.Type.Color);

        FloatBuffer colorArray = BufferUtils.createFloatBuffer(new float[4 * mesh.getVertexCount()]);
        FloatBuffer vertices = (FloatBuffer) mesh.getBuffer(VertexBuffer.Type.Position).getData();
        float color = 0.8f;
        float offColor = 0.2f;

        for (int i = 0, j; i < mesh.getVertexCount(); i++)
        {
            j = i * 4; // RGBA Value for every Vertex
            if (changeVertices == null || changeVertices.contains(new Vector3f(vertices.get(i * 3), vertices.get(i * 3 + 1), vertices.get(i * 3 + 2))))
            {
                colorArray.put(j, color);
                colorArray.put(j + 1, color);
                colorArray.put(j + 2, color);
                colorArray.put(j + 3, 1.0f);
            }
            else
            {
                colorArray.put(j, offColor);
                colorArray.put(j + 1, offColor);
                colorArray.put(j + 2, offColor);
                colorArray.put(j + 3, 1.0f);
            }
        }
        mesh.setBuffer(VertexBuffer.Type.Color, 4, colorArray);
    }
}
