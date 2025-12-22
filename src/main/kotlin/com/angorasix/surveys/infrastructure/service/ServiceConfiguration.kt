package com.angorasix.surveys.infrastructure.service

import com.angorasix.surveys.application.SurveyService
import com.angorasix.surveys.domain.survey.SurveyResponseRepository
import com.angorasix.surveys.infrastructure.applicationevents.ApplicationEventsListener
import com.angorasix.surveys.infrastructure.config.amqp.AmqpConfigurations
import com.angorasix.surveys.infrastructure.config.api.ApiConfigs
import com.angorasix.surveys.messaging.publisher.MessagePublisher
import com.angorasix.surveys.presentation.handler.SurveyHandler
import com.angorasix.surveys.presentation.router.SurveyRouter
import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ServiceConfiguration {
    @Bean
    fun surveyService(
        repository: SurveyResponseRepository,
        applicationEventPublisher: ApplicationEventPublisher,
    ) = SurveyService(repository, applicationEventPublisher)

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

    @Bean
    fun messagePublisher(
        streamBridge: StreamBridge,
        amqpConfigs: AmqpConfigurations,
    ) = MessagePublisher(streamBridge, amqpConfigs)

    @Bean
    fun applicationEventsListener(messagePublisher: MessagePublisher) = ApplicationEventsListener(messagePublisher)
}
