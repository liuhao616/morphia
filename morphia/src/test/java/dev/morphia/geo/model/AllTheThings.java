package dev.morphia.geo.model;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Indexed;
import dev.morphia.utils.IndexDirection;
import com.mongodb.client.model.geojson.GeometryCollection;
import org.bson.types.ObjectId;

@Entity
public final class AllTheThings {
    @Id
    private ObjectId id;
    private String name;

    @Indexed(IndexDirection.GEO2DSPHERE)
    private GeometryCollection everything;

    @SuppressWarnings("UnusedDeclaration")
        // used by morphia
    AllTheThings() {
    }

    public AllTheThings(final String name, final GeometryCollection everything) {
        this.name = name;
        this.everything = everything;
    }

    @Override
    public int hashCode() {
        int result = everything.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AllTheThings that = (AllTheThings) o;

        if (!everything.equals(that.everything)) {
            return false;
        }
        if (!name.equals(that.name)) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "AllTheThings{"
               + "everything=" + everything
               + ", name='" + name + '\''
               + '}';
    }
}
