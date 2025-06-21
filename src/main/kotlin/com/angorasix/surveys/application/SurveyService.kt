package com.angorasix.surveys.application

import com.angorasix.commons.domain.A6Contributor
import com.angorasix.surveys.domain.survey.SurveyResponse
import com.angorasix.surveys.domain.survey.SurveyResponseRepository
import com.angorasix.surveys.infrastructure.queryfilters.ListSurveyFilter
import kotlinx.coroutines.flow.toList

/**
 *
 *
 * @author rozagerardo
 */
class SurveyService(
    private val repository: SurveyResponseRepository,
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

    suspend fun registerSurveyResponse(surveyResponse: SurveyResponse): SurveyResponse = repository.save(surveyResponse)

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
