package com.angorasix.surveys.infrastructure.persistence.repository

import com.angorasix.commons.domain.A6Contributor
import com.angorasix.surveys.domain.survey.SurveyResponse
import com.angorasix.surveys.infrastructure.queryfilters.ListSurveyFilter
import kotlinx.coroutines.flow.Flow

/**
 * <p>
 * </p>
 *
 * @author rozagerardo
 */
interface SurveyFilterRepository {
    suspend fun findUsingFilter(
        filter: ListSurveyFilter,
        requestingContributor: A6Contributor?,
    ): Flow<SurveyResponse>

    suspend fun findSingleUsingFilter(
        filter: ListSurveyFilter,
        requestingContributor: A6Contributor?,
    ): SurveyResponse?
}
