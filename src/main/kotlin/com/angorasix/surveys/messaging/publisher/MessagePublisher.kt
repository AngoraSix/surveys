package com.angorasix.surveys.messaging.publisher

import com.angorasix.commons.domain.A6Contributor
import com.angorasix.commons.infrastructure.intercommunication.A6DomainResource
import com.angorasix.commons.infrastructure.intercommunication.A6InfraTopics
import com.angorasix.commons.infrastructure.intercommunication.messaging.A6InfraMessageDto
import com.angorasix.commons.infrastructure.intercommunication.survey.SurveyRegistered
import com.angorasix.surveys.infrastructure.config.amqp.AmqpConfigurations
import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.messaging.support.MessageBuilder

class MessagePublisher(
    private val streamBridge: StreamBridge,
    private val amqpConfigs: AmqpConfigurations,
) {
    fun publishSurveyRegistered(
        surveyRegistered: SurveyRegistered,
        requestingContributor: A6Contributor? = null,
    ) {
        streamBridge.send(
            amqpConfigs.bindings.surveyRegistered,
            MessageBuilder
                .withPayload(
                    A6InfraMessageDto(
                        targetId = A6DomainResource.PLATFORM_ADMIN.value,
                        targetType = A6DomainResource.PLATFORM_ADMIN,
                        objectId = surveyRegistered.surveyResponseId,
                        objectType = A6DomainResource.SURVEY.value,
                        topic = A6InfraTopics.SURVEY_REGISTERED.value,
                        requestingContributor = requestingContributor ?: A6Contributor.anonymous(),
                        messageData = surveyRegistered,
                    ),
                ).build(),
        )
    }
}
