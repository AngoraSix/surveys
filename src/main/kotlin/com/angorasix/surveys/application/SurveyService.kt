package com.angorasix.surveys.application

import com.angorasix.commons.domain.SimpleContributor
import com.angorasix.surveys.domain.survey.Survey
import com.angorasix.surveys.domain.survey.SurveyRepository
import com.angorasix.surveys.infrastructure.queryfilters.ListSurveyFilter
import kotlinx.coroutines.flow.toList

/**
 *
 *
 * @author rozagerardo
 */
class SurveyService(private val repository: SurveyRepository) {

    suspend fun findSurveys(
        filter: ListSurveyFilter,
        requestingContributor: SimpleContributor,
    ): List<Survey> =
        repository.findForContributorUsingFilter(
            filter = filter,
            requestingContributor = requestingContributor,
        ).toList()

    suspend fun createSurvey(survey: Survey): Survey =
        repository.save(survey)
}
