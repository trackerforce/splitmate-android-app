package com.trackerforce.splitmate.dao.wrapper;

/**
 * Resolve eventual joins or other necessary information for completeness of the object gathered from
 * the local storage
 */
public interface EntityResolver<T> {

    /**
     * Implements the necessary DB calls to include external information to the entity
     */
    T resolve(T entity) throws Exception;

}
