package com.angorasix.surveys.infrastructure.applicationevents

import com.angorasix.commons.infrastructure.intercommunication.survey.SurveyRegistered
import com.angorasix.surveys.messaging.publisher.MessagePublisher
import org.springframework.context.event.EventListener

class ApplicationEventsListener(
    private val messagePublisher: MessagePublisher,
) {
    @EventListener
    fun handleSurveyRegistered(evt: SurveyRegisteredApplicationEvent) =
        messagePublisher.publishSurveyRegistered(
            surveyRegistered =
                SurveyRegistered(
                    surveyKey = evt.surveyKey,
                    surveyResponseId = evt.surveyResponseId,
                    contributorId = evt.requestingContributor?.contributorId,
                    response = evt.response,
                ),
            requestingContributor = evt.requestingContributor,
        )
}
