package com.angorasix.surveys.infrastructure.persistence.repository

import com.angorasix.commons.domain.A6Contributor
import com.angorasix.surveys.domain.survey.SurveyResponse
import com.angorasix.surveys.infrastructure.queryfilters.ListSurveyFilter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.query.Criteria
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
    override suspend fun findUsingFilter(
        filter: ListSurveyFilter,
        requestingContributor: A6Contributor?,
    ): Flow<SurveyResponse> = mongoOps.find(filter.toQuery(requestingContributor), SurveyResponse::class.java).asFlow()

    override suspend fun findSingleUsingFilter(
        filter: ListSurveyFilter,
        requestingContributor: A6Contributor?,
    ): SurveyResponse? = mongoOps.find(filter.toQuery(requestingContributor), SurveyResponse::class.java).awaitFirstOrNull()
}

private fun ListSurveyFilter.toQuery(requestingContributor: A6Contributor?): Query {
    val query = Query()

    ids?.let { query.addCriteria(where("_id").`in`(it as Collection<Any>)) }
    surveyKey?.let { query.addCriteria(where("surveyKey").`in`(it as Collection<Any>)) }
    contributorId?.let {
        if (requestingContributor != null) {
            val requestingContributorId = requestingContributor.contributorId
            // if we're requesting a contributor, it should be either for the requesing contributor (myself)
            // or for an account administered by me
            val orCriteria =
                mutableListOf(
                    where("contributorId").`in`(it + requestingContributorId),
                    where("contributorId").`in`(it).andOperator(where("admins.contributorId").`in`(requestingContributorId)),
                )
            query.addCriteria(Criteria().orOperator(orCriteria))
        } else {
            query.addCriteria(where("contributorId").`in`(it))
        }
    }

    return query
}
