/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import com.jme3.collision.CollisionResult;
import com.jme3.math.Triangle;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import java.util.ArrayList;

/**
 *
 * @author Fabian Rauscher
 */
public class MeshUtils
{
    /**
     * Takes the two given vertices and searches for them in the given mesh. If
     * a triangle is found that contains both vertices and one new one and has
     * the same normal value the new vertice will be returned. If no vertice
     * matching the conditions can be found null is returned.
     *
     * @param v1
     * @param v2
     * @param triangleCount
     * @param meshHit
     * @param referenceNormal
     *
     * @return
     */
    public static Vector3f missingVertexNormalAdjacentTriangle(Vector3f v1, Vector3f v2, int triangleCount, Mesh meshHit, Vector3f referenceNormal, ArrayList<Vector3f> verticesHit)
    {
        Triangle triangleMesh = new Triangle();
        for (int j = 0; j < triangleCount; j++)
        {
            Vector3f v3_new;
            // 1. get new triangle
            meshHit.getTriangle(j, triangleMesh);
            // 2. check if it is adjacent to the current vertices
            if ((v3_new = TriangleUtils.twoMatchingVertices(triangleMesh, v1, v2)) != null)
            {
                // 3. check if its normal (direction) is equal
                if (triangleMesh.getNormal().distance(referenceNormal) <= 0.001f)
                {
                    if (!verticesHit.contains(v3_new))
                    {
                        return v3_new;
                    }

                }
            }
        }
        return null;
    }

    public static void selectFlatArea(CollisionResult closest)
    {
        Mesh meshHit = closest.getGeometry().getMesh();
        final int TRIANGLECOUNT = meshHit.getTriangleCount();

        Vector3f contactPoint = closest.getContactPoint();

        Triangle triangleHit = closest.getTriangle(null);
        final Vector3f REF_NORMAL = triangleHit.getNormal();

        ArrayList<Vector3f> verticesHit = new ArrayList<>();
        ArrayList<Integer> verticesHitIndex = new ArrayList<>();
        //Add initial vertices to start search from
        verticesHit.add(triangleHit.get1());
        verticesHit.add(triangleHit.get2());
        verticesHit.add(triangleHit.get3());

        /*
         * This algorithm searches for every triangle that is adjacent to the
         * hit triangle and has the same normal value. This means you get the
         * biggest possible flat plane that contains the hit triangle.
         */
        Boolean added;
        Vector3f v3_new;
        do // as long as new vertices were added
        {
            added = false;
            /*
             * iterate over every continues vertice pair and find triangles in
             * the list that have exactly these two vertices in common and have
             * the same normal value.
             */
            for (int i = 0; i < verticesHit.size() - 1; i++)
            {
                v3_new = MeshUtils.missingVertexNormalAdjacentTriangle(verticesHit.get(i), verticesHit.get(i + 1), TRIANGLECOUNT, meshHit, REF_NORMAL, verticesHit);
                if (v3_new != null)
                {
                    verticesHit.add(i + 1, v3_new);
                    added = true;
                }

            }
            //Also compare the last and first vertex in the list
            v3_new = MeshUtils.missingVertexNormalAdjacentTriangle(verticesHit.get(0), verticesHit.get(verticesHit.size() - 1), TRIANGLECOUNT, meshHit, REF_NORMAL, verticesHit);
            if (v3_new != null)
            {
                verticesHit.add(v3_new);
                added = true;
            }
        }
        while (added);
        VertexUtils.changeColorOfVertices(closest.getGeometry(), verticesHit);
    }
}
