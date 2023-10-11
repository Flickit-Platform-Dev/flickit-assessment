package org.flickit.flickitassessmentcore.architecture.dependency;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static org.flickit.flickitassessmentcore.architecture.constants.ArchUnitTestConstants.*;

@AnalyzeClasses(packages = {
    APPLICATION_PORT_IN_FULL_PACKAGE,
    APPLICATION_PORT_OUT_FULL_PACKAGE
})
public class PortDependencyArchUnitTest {

    @ArchTest
    static final ArchRule usecases_should_access_domain_models =
        classes()
            .that()
            .resideInAPackage(APPLICATION_PORT_IN)
            .and()
            .haveSimpleNameNotContaining(USE_CASE_PARAM_TEST_SUFFIX)
            .should()
            .onlyDependOnClassesThat()
            .resideInAnyPackage(APPLICATION_PORT_IN, APPLICATION_DOMAIN, COMMON, JAVA, JAKARTA_VALIDATION_CONSTRAINTS);

    @ArchTest
    static final ArchRule usecases_should_not_access_any_classes =
        noClasses()
            .that()
            .resideInAPackage(APPLICATION_PORT_IN)
            .and()
            .haveSimpleNameNotContaining(USE_CASE_PARAM_TEST_SUFFIX)
            .should()
            .onlyDependOnClassesThat()
            .resideInAnyPackage(ADAPTER, APPLICATION_PORT_OUT, APPLICATION_SERVICE);

    @ArchTest
    static final ArchRule out_ports_should_access_domain_model =
        classes()
            .that()
            .resideInAPackage(APPLICATION_PORT_OUT)
            .should()
            .onlyDependOnClassesThat()
            .resideInAnyPackage(APPLICATION_PORT_OUT, APPLICATION_PORT_IN, APPLICATION_DOMAIN, COMMON, JAVA);

    @ArchTest
    static final ArchRule out_ports_should_not_access_any_classes =
        noClasses()
            .that()
            .resideInAPackage(APPLICATION_PORT_OUT)
            .should()
            .onlyDependOnClassesThat()
            .resideInAnyPackage(ADAPTER, APPLICATION_SERVICE);
}
