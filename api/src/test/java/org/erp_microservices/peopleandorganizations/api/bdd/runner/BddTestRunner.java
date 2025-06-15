package org.erp_microservices.peopleandorganizations.api.bdd.runner;

import io.cucumber.junit.platform.engine.Constants;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import org.junit.jupiter.api.Tag;

/**
 * Cucumber BDD Test Runner
 *
 * This class configures and runs all BDD tests using Cucumber.
 * It integrates with JUnit Platform and uses the Cucumber engine.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, value = "pretty")
@ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = "org.erp_microservices.peopleandorganizations.api.bdd.stepdefinitions")
@ConfigurationParameter(key = Constants.FEATURES_PROPERTY_NAME, value = "src/test/resources/features")
@ConfigurationParameter(key = Constants.EXECUTION_DRY_RUN_PROPERTY_NAME, value = "false")
@ConfigurationParameter(key = Constants.PLUGIN_PUBLISH_QUIET_PROPERTY_NAME, value = "true")
@Tag("bdd")
public class BddTestRunner {
    // This class serves as the entry point for Cucumber BDD tests
    // All configuration is done via annotations
}
