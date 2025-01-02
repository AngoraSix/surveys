package com.angorasix.surveys.infrastructure.persistence.repository

import com.angorasix.commons.domain.SimpleContributor
import com.angorasix.surveys.domain.survey.Survey
import com.angorasix.surveys.infrastructure.queryfilters.ListSurveyFilter
import kotlinx.coroutines.flow.Flow

/**
 * <p>
 * </p>
 *
 * @author rozagerardo
 */
interface SurveyFilterRepository {
    suspend fun findForContributorUsingFilter(
        filter: ListSurveyFilter,
        requestingContributor: SimpleContributor,
    ): Flow<Survey>
}
