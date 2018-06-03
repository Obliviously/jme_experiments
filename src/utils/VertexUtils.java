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
    
    public static Vector3f calcClosestPointOnLine(Vector3f lineStart, Vector3f lineEnd, Vector3f point)
        {
            Vector3f closestPoint = lineStart;
            Vector3f line = lineStart.subtract(lineEnd);
            float linePosition = 0.1f;

            if (lineStart.distance(lineEnd) < lineStart.add(line.mult(linePosition)).distance(lineEnd))
            {
                lineStart = lineEnd;
            }

            double lastDistance = Double.MAX_VALUE;
            double currDistance = lineStart.distance(point);

            for (int i = 1; currDistance < lastDistance; i++)
            {
                lastDistance = currDistance;
                closestPoint = lineStart.add(line.mult(linePosition * i));
                currDistance = point.distance(closestPoint);
            }
            return closestPoint.subtract(line.mult(linePosition));
        }
}
