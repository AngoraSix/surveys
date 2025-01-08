package com.angorasix.surveys.presentation.router

import com.angorasix.commons.reactive.presentation.filter.extractRequestingContributor
import com.angorasix.surveys.infrastructure.config.configurationproperty.api.ApiConfigs
import com.angorasix.surveys.presentation.handler.SurveyHandler
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.coRouter

/**
 * Router for all Surveys related endpoints.
 *
 * @author rozagerardo
 */
class SurveyRouter(
    private val handler: SurveyHandler,
    private val apiConfigs: ApiConfigs,
) {

    /**
     * Main RouterFunction configuration for all endpoints related to Surveys.
     *
     * @return the [RouterFunction] with all the routes for Surveys
     */
    fun surveysRouterFunction() = coRouter {
        apiConfigs.basePaths.surveys.nest {
            filter { request, next ->
                extractRequestingContributor(
                    request,
                    next,
                )
            }
            apiConfigs.basePaths.baseListCrudRoute.nest {
                method(apiConfigs.routes.listSurveys.method).nest {
                    method(
                        apiConfigs.routes.listSurveys.method,
                        handler::listSurveys,
                    )
                }
            }
            apiConfigs.basePaths.baseByKeyRoute.nest {
                apiConfigs.routes.registerSurveyResponse.path.nest {
                    method(
                        apiConfigs.routes.registerSurveyResponse.method,
                        handler::registerSurveyResponse,
                    )
                }
            }
        }
    }
}
