package com.angorasix.surveys.infrastructure.persistence.repository

import com.angorasix.commons.domain.SimpleContributor
import com.angorasix.surveys.domain.survey.Survey
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
class SurveyFilterRepositoryImpl(private val mongoOps: ReactiveMongoOperations) :
    SurveyFilterRepository {

    override suspend fun findForContributorUsingFilter(
        filter: ListSurveyFilter,
        requestingContributor: SimpleContributor,
    ): Flow<Survey> {
        return mongoOps.find(filter.toQuery(requestingContributor), Survey::class.java).asFlow()
    }
}

private fun ListSurveyFilter.toQuery(requestingContributor: SimpleContributor): Query {
    val query = Query()

    ids?.let { query.addCriteria(where("_id").`in`(it)) }
    surveyKey?.let { query.addCriteria(where("surveyKey").`in`(it)) }
    contributorId?.let { query.addCriteria(where("contributorId").`in`(it)) }
    query.addCriteria(
        where("admins.contributorId").`in`(
            mutableSetOf(
                contributorId,
                requestingContributor.contributorId,
            ),
        ),
    )

    return query
}
