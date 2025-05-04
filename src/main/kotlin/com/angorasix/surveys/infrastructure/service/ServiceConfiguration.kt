package com.angorasix.surveys.infrastructure.service

import com.angorasix.surveys.application.SurveyService
import com.angorasix.surveys.domain.survey.SurveyResponseRepository
import com.angorasix.surveys.infrastructure.config.configurationproperty.api.ApiConfigs
import com.angorasix.surveys.presentation.handler.SurveyHandler
import com.angorasix.surveys.presentation.router.SurveyRouter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ServiceConfiguration {
    @Bean
    fun surveyService(repository: SurveyResponseRepository) = SurveyService(repository)

    @Bean
    fun surveyHandler(
        service: SurveyService,
        apiConfigs: ApiConfigs,
    ) = SurveyHandler(service, apiConfigs)

    @Bean
    fun surveyRouter(
        handler: SurveyHandler,
        apiConfigs: ApiConfigs,
    ) = SurveyRouter(handler, apiConfigs).surveysRouterFunction()
}
