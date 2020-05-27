package it.gov.pagopa.fa.common;

import eu.sia.meda.connector.jpa.CrudJpaDAO;
import eu.sia.meda.layers.connector.query.CriteriaFilter;
import eu.sia.meda.layers.connector.query.CriteriaQuery;
import eu.sia.meda.util.ColoredPrinters;
import eu.sia.meda.util.ReflectionUtils;
import eu.sia.meda.util.TestUtils;
import it.gov.pagopa.fa.common.model.entity.BaseEntity;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;


public abstract class BaseCrudJpaDAOTest<D extends CrudJpaDAO<E, K>, E extends BaseEntity, K extends Serializable>
        extends BaseJpaIntegrationTest {

    protected final Class<D> daoClass;
    protected final Class<E> entityClass;

    @PersistenceContext
    private EntityManager entityManager;
    private E storedEntity;


    @SuppressWarnings({"unchecked"})
    public BaseCrudJpaDAOTest() {
        this.daoClass = (Class<D>) ReflectionUtils.getGenericTypeClass(getClass(), 0);
        this.entityClass = (Class<E>) ReflectionUtils.getGenericTypeClass(getClass(), 1);

        CriteriaQuery<? super E> queryCriteria = TestUtils.mockInstance(getMatchAlreadySavedCriteria());
        org.springframework.util.ReflectionUtils.doWithFields(queryCriteria.getClass(), f -> {
            Field entityField = org.springframework.util.ReflectionUtils.findField(entityClass, f.getName());
            f.setAccessible(true);
            if (entityField == null) {
                throw new IllegalStateException(String.format("The provided QueryCriteria has a not valid field '%s': not allowed for the %s", f.getName(), entityClass));
            }
            if (f.getType().isPrimitive()) {
                throw new IllegalStateException(String.format("The QueryCriteria cannot contains primitive types! field '%s' of %s", f.getName(), queryCriteria.getClass()));
            }
            if ((!entityField.getType().equals(f.getType()) && (CriteriaFilter.class.isAssignableFrom(f.getType()) && !((CriteriaFilter<?>) f.get(queryCriteria)).accept(entityField.getType()))) && (!entityField.getType().isPrimitive() || !ReflectionUtils.isPrimitiveWrapperOf(f.getType(), entityField.getType()))) {
                throw new IllegalStateException(String.format("The provided QueryCriteria has a not valid field '%s' for the %s: the type %s mismatch the entity type %s", f.getName(), entityClass, f.getType(), entityField.getType()));
            }
        });
    }


    protected abstract CriteriaQuery<? super E> getMatchAlreadySavedCriteria();

    protected abstract D getDao();

    protected abstract void setId(E entity, K id);

    protected abstract K getId(E entity);

    protected abstract void alterEntityToUpdate(E entity);


    /**
     * Describe the way to create the id for the entity, starting by a bias (Interger) value.
     *
     * @return a Functional Interface
     */
    protected abstract Function<Integer, K> idBuilderFn();


    @Before
    public void beforeTest() throws IllegalAccessException, InstantiationException {
        final E entity = getAlreadyStoredEntityToSave();
        storedEntity = performSave(entity);
    }


    private E getAlreadyStoredEntityToSave() throws IllegalAccessException, InstantiationException {
        final E entity = getMockedEntity(1);
        setId(entity, getStoredId());
        return entity;
    }


    @Test
    public void findAll() {
        Page<E> list = performFindAll(null, null);
        Assert.assertNotNull(list);
        Assert.assertEquals(1, list.getContent().size());
    }


    protected final Page<E> performFindAll(CriteriaQuery<? super E> criteria, PageRequest pageable) {
        if (criteria == null && pageable == null) {
            return new PageImpl<>(getDao().findAll());
        } else {
            return getDao().findAll(criteria, pageable);
        }
    }


    @Test
    public void findAllPageable() throws Exception {
        PageRequest pageable = PageRequest.of(0, 1, getSortBy());
        E saved = performSave(getEntityToSave());

        clearContext(saved);

        Page<E> list1 = performFindAll(null, pageable);

        Assert.assertEquals(getDao().count(), list1.getTotalElements());
        Assert.assertEquals(1, list1.getPageable().getPageSize());
        Assert.assertEquals(0, list1.getPageable().getPageNumber());
        Assert.assertNotNull(list1.getContent());
        Assert.assertEquals(1, list1.getContent().size());
        compare(getStoredEntity(), list1.getContent().get(0));

        pageable = PageRequest.of(1, 1, getSortBy());

        Page<E> list2 = performFindAll(null, pageable);
        Assert.assertNotNull(list2.getContent());
        Assert.assertEquals(1, list2.getContent().size());
        compare(saved, list2.getContent().get(0));
    }


    /**
     * To override in order to sort the result in such a way to have the already stored entity as first entity
     */
    protected Sort getSortBy() {
        return Sort.by(getIdName());
    }


    protected String getIdName() {
        if (org.springframework.util.ReflectionUtils.findField(entityClass, "id") == null) {
            throw new IllegalStateException("The entity has not a field named 'id'. Please override getIdName method inside the test!");
        }
        return "id";
    }


    @Test
    public void findAllCriteria() throws Exception {
        E saved = performSave(getEntityToSave());

        clearContext(saved);

        ColoredPrinters.PRINT_GREEN.println("Fetching alreadyStored");
        CriteriaQuery<? super E> criteriaQuery = getMatchAlreadySavedCriteria();
        Page<E> list1 = performFindAll(criteriaQuery, null);

        Assert.assertNotNull(list1.getContent());
        Assert.assertEquals(1, list1.getContent().size());
        compare(getStoredEntity(), list1.getContent().get(0));

        ColoredPrinters.PRINT_GREEN.println("Fetching new saved entities");
        BeanUtils.copyProperties(saved, criteriaQuery);
        list1 = performFindAll(criteriaQuery, null);

        Assert.assertNotNull(list1.getContent());
        Assert.assertEquals(1, list1.getContent().size());
        compare(saved, list1.getContent().get(0));
    }


    @Test
    public void deleteById() {
        performDeleteById();
        Optional<E> entity = getDao().findById(getStoredId());
        Assert.assertFalse(entity.isPresent());
    }


    protected final void performDeleteById() {
        getDao().deleteById(getStoredId());
    }


    @Test
    public void findById() {
        Optional<E> entity = performFindById();

        Assert.assertTrue(entity.isPresent());
        Assert.assertNotNull(entity.get());
    }


    protected final Optional<E> performFindById() {
        return getDao().findById(getStoredId());
    }


    @Test
    public void save() throws Exception {
        E saved = performSave(getEntityToSave());

        Assert.assertNotNull(saved);
        Assert.assertNotNull(getId(saved));

        clearContext(saved);
        Optional<E> optional = getDao().findById(getId(saved));

        Assert.assertTrue(optional.isPresent());
        compare(saved, optional.get());
    }


    protected final E performSave(E entity) {
        return getDao().save(entity);
    }


    @Test
    public void update() {
        Optional<E> foundEntity = getDao().findById(getStoredId());

        Assert.assertTrue(foundEntity.isPresent());

        E entity = foundEntity.get();
        clearContext(entity);
        alterEntityToUpdate(entity);
        E entityUpdated = performUpdate(entity);

        compare(entity, entityUpdated);

        clearContext(entityUpdated);
        foundEntity = getDao().findById(getId(entityUpdated));

        Assert.assertTrue(foundEntity.isPresent());
        compare(entityUpdated, foundEntity.get());

        if (entity instanceof BaseEntity) {
            BaseEntity baseEntity = (BaseEntity) entity;
            BaseEntity baseEntityUpdated = (BaseEntity) entityUpdated;
            if (baseEntity.getUpdateDate() == null) {
                Assert.assertNotNull(baseEntityUpdated.getUpdateDate());
            } else {
                Assert.assertTrue(baseEntity.getUpdateDate().isBefore(baseEntityUpdated.getUpdateDate()));
            }
        }
    }


    protected final E performUpdate(E entity) {
        return getDao().update(entity);
    }


    @Test
    public void count() {
        List<E> list = getDao().findAll();

        Assert.assertEquals(list.size(), performCount());
    }


    protected final long performCount() {
        return getDao().count();
    }


    @Test
    public void deleteAll() {
        performDeleteAll();
        Assert.assertEquals(0, getDao().count());
    }


    protected final void performDeleteAll() {
        getDao().deleteAll();
    }


    protected final E getEntityToSave() throws InstantiationException, IllegalAccessException {
        E e = getMockedEntity(2);
        setId(e, getNextId());
        return e;
    }


    /**
     * This function has to return a valid E object with all right parameters
     * according to entity type
     */
    protected E getMockedEntity(int bias) throws IllegalAccessException, InstantiationException {
        E e = TestUtils.mockInstance(entityClass.newInstance(), bias);
        e.setEnabled(true);
        return e;
    }


    /**
     * The Id has to be just stored before test
     * and has to be smaller than each next id {@link #getNextId()}
     */
    protected final K getStoredId() {
        K storedId = idBuilderFn().apply(1);

        if (storedId == null && storedEntity != null) {
            storedId = getId(storedEntity);
        }

        return storedId;
    }


    /**
     * This function return an id greater than {@link #getStoredId()}
     */
    protected final K getNextId() {
        return idBuilderFn().apply(2);
    }


    protected void clearContext(E saved) {
        lazyLoadCollectionToCheck(saved);

        entityManager.flush();
        entityManager.clear();
    }


    /**
     * To override in order to lazy load collection before {@link EntityManager#clear()}
     */
    protected void lazyLoadCollectionToCheck(E saved) {
        //Do Nothing
    }


    /**
     * This method has to return the entity already present in db
     */
    protected final E getStoredEntity() {
        return storedEntity;
    }


    protected void compare(E entityToSave, E saved) {
        TestUtils.reflectionEqualsByName(entityToSave, saved, "insertDate", "insertUser", "updateDate", "updateUser", "enabled");
    }

}
