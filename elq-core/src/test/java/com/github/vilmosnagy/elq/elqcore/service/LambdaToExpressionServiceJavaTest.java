package com.github.vilmosnagy.elq.elqcore.service;

import com.github.vilmosnagy.elq.elqcore.cache.ConstantValueProvider;
import com.github.vilmosnagy.elq.elqcore.model.FunctionAlbumIdEqualsFive;
import com.github.vilmosnagy.elq.elqcore.model.Method;
import com.github.vilmosnagy.elq.elqcore.model.lqexpressions.filter.ParsedFilterLQExpressionLeaf;
import com.github.vilmosnagy.elq.elqcore.model.statements.GetFieldStatement;
import com.github.vilmosnagy.elq.elqcore.model.statements.MethodCallStatement;
import com.github.vilmosnagy.elq.elqcore.model.statements.Statement;
import com.github.vilmosnagy.elq.elqcore.model.statements.branch.BranchedStatement;
import com.github.vilmosnagy.elq.elqcore.model.statements.branch.CompareStatement;
import com.github.vilmosnagy.elq.elqcore.model.statements.branch.CompareType;
import com.github.vilmosnagy.elq.elqcore.test.model.TestEntity;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * @author Vilmos Nagy {@literal <vilmos.nagy@outlook.com>}
 */
public class LambdaToExpressionServiceJavaTest {

    @Mock
    private MethodParser methodParser;

    @Mock
    private JavaPropertyService javaPropertyService;

    @InjectMocks
    private LambdaToExpressionService testObj;

    @Before
    public final void before() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void should_transform_parsed_method_to_fieldName_value_pair_when_expression_is_field_equals_constant() throws NoSuchMethodException {
        java.lang.reflect.Method lambdaMethod = FunctionAlbumIdEqualsFive.class.getDeclaredMethod("test", TestEntity.class);
        Class<TestEntity> entityClass = TestEntity.class;
        java.lang.reflect.Method entityIdGetterMethod = TestEntity.class.getDeclaredMethod("getId");

        Statement getFieldStatement = new GetFieldStatement(TestEntity.class, "id");
        Statement.EvaluableStatement<?> getterReturnStatement = new Statement.ReturnStatement<>(getFieldStatement);

        Statement returnedStatement = new BranchedStatement(
                new CompareStatement(
                        new Statement.LoadConstant<>(5),
                        new MethodCallStatement<>(
                                entityClass,
                                entityIdGetterMethod,
                                Collections.singletonList(new Statement.LoadVariable(1)),
                                java.lang.Integer.TYPE,
                                new Method(entityIdGetterMethod, getterReturnStatement)
                        ),
                        CompareType.NOT_EQUALS
                ),
                new Statement.ReturnStatement<>(new Statement.LoadConstant<>(true)),
                new Statement.ReturnStatement<>(new Statement.LoadConstant<>(false))
        );

        Method getterMethod = new Method(entityIdGetterMethod, getterReturnStatement);
        Method parsedMethod = new Method(lambdaMethod, returnedStatement);

        when(methodParser.parseMethod(eq(this.getClass()), any())).thenReturn(parsedMethod);
        when(methodParser.parseMethod(entityClass, entityIdGetterMethod)).thenReturn(getterMethod);

        ParsedFilterLQExpressionLeaf<TestEntity> actual = (ParsedFilterLQExpressionLeaf<TestEntity>) testObj.parseFilterMethod(album -> album.getId() == 5, TestEntity.class);
        assertEquals(new ParsedFilterLQExpressionLeaf<>("id", new ConstantValueProvider<>(5), CompareType.EQUALS), actual);

    }
}
