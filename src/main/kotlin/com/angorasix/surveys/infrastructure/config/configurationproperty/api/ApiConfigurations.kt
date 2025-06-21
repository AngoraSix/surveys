package com.angorasix.surveys.infrastructure.config.configurationproperty.api

import com.angorasix.commons.infrastructure.config.configurationproperty.api.Route
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty

/**
 * <p>
 *  Base file containing all Service configurations.
 * </p>
 *
 * @author rozagerardo
 */
@ConfigurationProperties(prefix = "configs.api")
data class ApiConfigs(
    @NestedConfigurationProperty
    var routes: RoutesConfigs,
    @NestedConfigurationProperty
    var basePaths: BasePathConfigs,
)

data class BasePathConfigs(
    val surveys: String,
    val baseListCrudRoute: String,
    val baseResponsesByKeyRoute: String,
)

data class RoutesConfigs(
    val listSurveys: Route,
    val registerSurveyResponse: Route,
    val getSurveyResponse: Route,
    val getSurvey: Route,
)
