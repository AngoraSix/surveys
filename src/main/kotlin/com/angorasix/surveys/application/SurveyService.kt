package com.angorasix.surveys.application

import com.angorasix.commons.domain.A6Contributor
import com.angorasix.surveys.domain.survey.SurveyResponse
import com.angorasix.surveys.domain.survey.SurveyResponseRepository
import com.angorasix.surveys.infrastructure.applicationevents.SurveyRegisteredApplicationEvent
import com.angorasix.surveys.infrastructure.queryfilters.ListSurveyFilter
import kotlinx.coroutines.flow.toList
import org.springframework.context.ApplicationEventPublisher

/**
 *
 *
 * @author rozagerardo
 */
class SurveyService(
    private val repository: SurveyResponseRepository,
    private val applicationEventPublisher: ApplicationEventPublisher,
) {
    suspend fun findSurveys(
        filter: ListSurveyFilter,
        requestingContributor: A6Contributor,
    ): List<SurveyResponse> =
        repository
            .findUsingFilter(
                filter = filter,
                requestingContributor = requestingContributor,
            ).toList()

    suspend fun registerSurveyResponse(
        surveyResponse: SurveyResponse,
        requestingContributor: A6Contributor?,
    ): SurveyResponse {
        val persistedSurveyResponse = repository.save(surveyResponse)
        applicationEventPublisher.publishEvent(
            SurveyRegisteredApplicationEvent(
                surveyResponseId = persistedSurveyResponse.id ?: "undefined",
                surveyKey = surveyResponse.surveyKey,
                requestingContributor = requestingContributor,
                response = surveyResponse.response,
            ),
        )
        return persistedSurveyResponse
    }

    suspend fun getSurveyResponse(
        surveyKey: String,
        requestingContributor: A6Contributor,
    ): SurveyResponse? =
        repository.findSingleUsingFilter(
            ListSurveyFilter(
                surveyKey = listOf(surveyKey),
            ),
            requestingContributor,
        )
}
