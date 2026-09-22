package algorithm;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import javafx.scene.shape.TriangleMesh;
import util.PointSet;

import java.util.ArrayList;

/**
 * Singleton class which holds meshes and target points globally, that are loaded from the visualization section.
 * */
public final class DataService {

    /** The mesh entry which contains of the geometrics, the name and the color. */
    private record MeshEntry(TriangleMesh mesh, String name, Color color) {}

    /** List, which contains the mesh entries. */
    private final ArrayList<MeshEntry> meshEntries = new ArrayList<>();

    /** List which contains sets of the entry and target point loaded from the .mps file. */
    private final ObservableList<PointSet> pointSetList = FXCollections.observableArrayList();

    /** Instance of the {@link DataService} */
    private static DataService instance;

    private DataService() {}

    /**
     * This method gives access to the instance of the {@link DataService}.
     * <p>
     * Ensures that the same instance is being used by all classes in runtime.
     * The synchronized keyword ensures that only one thread can access this method at a time.
     * </p>
     * @return The instance of the {@link DataService}.
     * */
    public synchronized static DataService getInstance() {
       if (instance == null) {
           instance = new DataService();
       }
       return instance;
    }

    /**
     * This method adds a loaded mesh, the associated name and its color to the list.
     *
     * @param mesh The loaded mesh.
     * @param name The mesh name.
     * @param color The mesh color.
     * */
    public void addMesh(TriangleMesh mesh, String name, Color color) {
        meshEntries.add(new MeshEntry(mesh, name, color));
    }

    /**
     * This method adds a point set to the point set list.
     *
     * @param pointSet The point set.
     * */
    public void addPointSet(PointSet pointSet) {
        pointSetList.add(pointSet);
    }

    /**
     * This method clears the point sets.
     * */
    public void clearPointSets() {
        pointSetList.clear();
    }

    /**
     * This method clears the meshes and its data.
     * */
    public void clearMeshes() {
        meshEntries.clear();
    }

    /**
     * Checks if a given mesh name appears in the list.
     *
     * @param meshName The name of the mesh.
     * */
    public boolean meshNameContainsInList(String meshName) {
        for (MeshEntry entry : meshEntries) {
            if (entry.name.contains(meshName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Updates the color of a mesh.
     *
     * @param index The index of the mesh in the list.
     * @param newColor The color to update the mesh to.
     * */
    public void updateMeshColor(int index, Color newColor) {
        MeshEntry old = meshEntries.get(index);
        meshEntries.set(index, new MeshEntry(old.mesh(), old.name(), newColor));
    }

    public ObservableList<PointSet> getPointSet() {
        return pointSetList;
    }

    public ArrayList<TriangleMesh> getMeshes() {
        ArrayList<TriangleMesh> meshes = new ArrayList<>();
        for (MeshEntry entry : meshEntries) {
            meshes.add(entry.mesh());
        }
        return meshes;
    }

    public ArrayList<String> getMeshNames() {
        ArrayList<String> names = new ArrayList<>();
        for (MeshEntry entry : meshEntries) {
            names.add(entry.name());
        }
        return names;
    }

    public ArrayList<Color> getMeshColors() {
        ArrayList<Color> colors = new ArrayList<>();
        for (MeshEntry entry : meshEntries) {
            colors.add(entry.color());
        }
        return colors;
    }

}
