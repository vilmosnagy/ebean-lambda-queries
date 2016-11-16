package com.github.vilmosnagy.elq.elqcore.model;

import com.github.vilmosnagy.elq.elqcore.interfaces.Predicate;
import com.github.vilmosnagy.elq.elqcore.test.model.TestEntity;

/**
 * @author Vilmos Nagy {@literal <vilmos.nagy@outlook.com>}
 */
public class FunctionAlbumIdEqualsFive implements Predicate<TestEntity> {
    @Override
    public Boolean test(TestEntity testEntity) {
        return testEntity.getId() == 5;
    }
}
