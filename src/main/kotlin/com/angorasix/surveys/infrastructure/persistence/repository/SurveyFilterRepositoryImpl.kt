package com.angorasix.surveys.infrastructure.persistence.repository

import com.angorasix.commons.domain.A6Contributor
import com.angorasix.surveys.domain.survey.SurveyResponse
import com.angorasix.surveys.infrastructure.queryfilters.ListSurveyFilter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query

/**
 * <p>
 * </p>
 *
 * @author rozagerardo
 */
class SurveyFilterRepositoryImpl(
    private val mongoOps: ReactiveMongoOperations,
) : SurveyFilterRepository {
    override suspend fun findForContributorUsingFilter(
        filter: ListSurveyFilter,
        requestingContributor: A6Contributor,
    ): Flow<SurveyResponse> = mongoOps.find(filter.toQuery(requestingContributor), SurveyResponse::class.java).asFlow()
}

private fun ListSurveyFilter.toQuery(requestingContributor: A6Contributor): Query {
    val query = Query()

    ids?.let { query.addCriteria(where("_id").`in`(it as Collection<Any>)) }
    surveyKey?.let { query.addCriteria(where("surveyKey").`in`(it as Collection<Any>)) }
    contributorId?.let { query.addCriteria(where("contributorId").`in`(it as Collection<Any>)) }
    query.addCriteria(
        where("admins.contributorId").`is`(requestingContributor.contributorId),
    )

    return query
}
