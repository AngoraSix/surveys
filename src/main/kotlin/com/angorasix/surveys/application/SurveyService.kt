package com.angorasix.surveys.application

import com.angorasix.commons.domain.SimpleContributor
import com.angorasix.surveys.domain.survey.SurveyResponse
import com.angorasix.surveys.domain.survey.SurveyResponseRepository
import com.angorasix.surveys.infrastructure.queryfilters.ListSurveyFilter
import kotlinx.coroutines.flow.toList

/**
 *
 *
 * @author rozagerardo
 */
class SurveyService(private val repository: SurveyResponseRepository) {

    suspend fun findSurveys(
        filter: ListSurveyFilter,
        requestingContributor: SimpleContributor,
    ): List<SurveyResponse> =
        repository.findForContributorUsingFilter(
            filter = filter,
            requestingContributor = requestingContributor,
        ).toList()

    suspend fun registerSurveyResponse(surveyResponse: SurveyResponse): SurveyResponse =
        repository.save(surveyResponse)
}
