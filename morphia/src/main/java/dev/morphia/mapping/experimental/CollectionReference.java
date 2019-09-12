package dev.morphia.mapping.experimental;

import com.mongodb.DBRef;
import com.mongodb.client.MongoCursor;
import dev.morphia.AdvancedDatastore;
import dev.morphia.Datastore;
import dev.morphia.mapping.MappedClass;
import dev.morphia.mapping.MappedField;
import dev.morphia.mapping.Mapper;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static java.util.Arrays.asList;

/**
 * @param <C>
 * @morphia.internal
 */
public abstract class CollectionReference<C extends Collection> extends MorphiaReference<C> {
    private List<Object> ids;
    private Map<String, List<Object>> collections = new HashMap<>();

    CollectionReference() {
    }

    CollectionReference(final Datastore datastore, final List ids) {
        super(datastore);
        List<Object> unwrapped = ids;
        if (ids != null) {
            for (final Object o : ids) {
                collate(datastore, collections, o);
            }
        }

        this.ids = unwrapped;
    }

    static void collate(final Datastore datastore, final Map<String, List<Object>> collections,
                        final Object o) {
        final String collectionName;
        final Object id;
        if (o instanceof DBRef) {
            final DBRef dbRef = (DBRef) o;
            collectionName = dbRef.getCollectionName();
            id = dbRef.getId();
        } else {
            collectionName = datastore.getMapper().getMappedClass(o.getClass()).getCollectionName();
            id = o;
        }

        register(collections, collectionName).add(id);
    }

    static List register(final Map<String, List<Object>> collections, final String name) {
        return collections.computeIfAbsent(name, k -> new ArrayList<>());
    }

    /**
     * {@inheritDoc}
     */
    public final boolean isResolved() {
        return getValues() != null;
    }

    abstract Collection<?> getValues();

    /**
     * {@inheritDoc}
     */
    public abstract C get();

    final List<Object> getIds() {
        return ids;
    }

    @SuppressWarnings("unchecked")
    final List<?> find() {
        final List<Object> values = new ArrayList(asList(new Object[ids.size()]));
        for (final Entry<String, List<Object>> entry : collections.entrySet()) {
            query(entry.getKey(), entry.getValue(), values);
        }
        return values;
    }

    void query(final String collection, final List<Object> collectionIds, final List<Object> values) {

        try (MongoCursor<?> cursor = ((AdvancedDatastore) getDatastore()).find(collection)
                                                                         .disableValidation()
                                                                         .filter("_id in ", collectionIds)
                                                                         .execute()) {
            final Map<Object, Object> idMap = new HashMap<>();
            while (cursor.hasNext()) {
                final Object entity = cursor.next();
                idMap.put(getDatastore().getMapper().getId(entity), entity);
            }

            for (int i = 0; i < ids.size(); i++) {
                final Object id = ids.get(i);
                final Object value = idMap.get(id instanceof DBRef ? ((DBRef) id).getId() : id);
                if (value != null) {
                    values.set(i, value);
                }
            }
        }
    }

    @Override
    public Object encode(final Mapper mapper, final Object value, final MappedField field) {
        if (isResolved()) {
            List ids = new ArrayList();
            for (final Object entity : get()) {
                ids.add(wrapId(mapper, field, entity));
            }
            return ids;
        } else {
            return null;
        }
    }

    /**
     * Decodes a document in to entities
     * @param datastore the datastore
     * @param mapper the mapper
     * @param mappedField the MappedField
     * @param paramType the type of the underlying entity
     * @param document the Document to decode
     * @return the entities
     */
    public static MorphiaReference<?> decode(final Datastore datastore,
                                             final Mapper mapper,
                                             final MappedField mappedField,
                                             final Class paramType,
                                             final Document document) {
        MorphiaReference reference = null;
        if (1 == 1) {
            //TODO:  implement this
            throw new UnsupportedOperationException();
        }

/*
        final List dbVal = (List) mappedField.getDocumentValue(document);
        if (dbVal != null) {
            final Class subType = mappedField.getTypeData().getTypeParameters().get(0).getType();
            final MappedClass mappedClass = mapper.getMappedClass(subType);

            if (Set.class.isAssignableFrom(paramType)) {
                reference = new SetReference(datastore, mappedClass, dbVal);
            } else {
                reference = new ListReference(datastore, mappedClass, dbVal);
            }
        }
*/

        return reference;
    }
}
