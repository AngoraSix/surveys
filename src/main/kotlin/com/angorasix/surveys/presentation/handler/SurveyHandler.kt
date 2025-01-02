package com.angorasix.surveys.presentation.handler

import com.angorasix.commons.domain.SimpleContributor
import com.angorasix.commons.infrastructure.constants.AngoraSixInfrastructure
import com.angorasix.commons.reactive.presentation.error.resolveBadRequest
import com.angorasix.surveys.application.SurveyService
import com.angorasix.surveys.domain.survey.Survey
import com.angorasix.surveys.infrastructure.config.configurationproperty.api.ApiConfigs
import com.angorasix.surveys.infrastructure.queryfilters.ListSurveyFilter
import com.angorasix.surveys.presentation.dto.SurveyDto
import com.angorasix.surveys.presentation.dto.SurveyQueryParams
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.IanaLinkRelations
import org.springframework.hateoas.MediaTypes
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.created
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import java.net.URI

/**
 * Surveys Handler (Controller) containing all handler functions related to Surveys endpoints.
 *
 * @author rozagerardo
 */
class SurveyHandler(
    private val service: SurveyService,
    private val apiConfigs: ApiConfigs,
) {
    /**
     * Handler for the List Surveys endpoint,
     * retrieving a Flux including all persisted Surveys.
     *
     * @param request - HTTP `ServerRequest` object
     * @return the `ServerResponse`
     */
    suspend fun listSurveys(request: ServerRequest): ServerResponse {
        val requestingContributor =
            request.attributes()[AngoraSixInfrastructure.REQUEST_ATTRIBUTE_CONTRIBUTOR_KEY]
        return if (requestingContributor is SimpleContributor) {
            val filter = request.queryParams().toQueryFilter()
            service.findSurveys(filter, requestingContributor)
                .map {
                    it.convertToDto(
                        apiConfigs,
                        request,
                    )
                }
                .let {
                    ok().contentType(MediaTypes.HAL_FORMS_JSON)
                        .bodyValueAndAwait(
                            it.convertToDto(
                                requestingContributor,
                                filter,
                                apiConfigs,
                                request,
                            ),
                        )
                }
        } else {
            resolveBadRequest("Invalid Contributor Token", "Contributor Token")
        }
    }

    /**
     * Handler for the Create Survey endpoint, to create a new Survey entity.
     *
     * @param request - HTTP `ServerRequest` object
     * @return the `ServerResponse`
     */
    suspend fun createSurvey(request: ServerRequest): ServerResponse {
        val requestingContributor =
            request.attributes()[AngoraSixInfrastructure.REQUEST_ATTRIBUTE_CONTRIBUTOR_KEY]

        return if (requestingContributor is SimpleContributor) {
            val survey = try {
                request.awaitBody<SurveyDto>().convertToDomain(requestingContributor)
            } catch (e: IllegalArgumentException) {
                return resolveBadRequest(
                    e.message ?: "Incorrect Survey body",
                    "Survey",
                )
            }

            val outputSurvey = service.createSurvey(survey)
                .convertToDto(apiConfigs, request)

            val selfLink =
                outputSurvey.links.getRequiredLink(IanaLinkRelations.SELF).href

            created(URI.create(selfLink)).contentType(MediaTypes.HAL_FORMS_JSON)
                .bodyValueAndAwait(outputSurvey)
        } else {
            resolveBadRequest("Invalid Contributor Token", "Contributor Token")
        }
    }
}

private fun Survey.convertToDto(): SurveyDto =
    SurveyDto(surveyKey = surveyKey, response = response, id = id)

private fun Survey.convertToDto(
    apiConfigs: ApiConfigs,
    request: ServerRequest,
): SurveyDto =
    convertToDto().resolveHypermedia(apiConfigs, request)

private fun SurveyDto.convertToDomain(
    requestingContributor: SimpleContributor,
): Survey {
    return Survey(
        surveyKey = surveyKey,
        contributorId = requestingContributor.contributorId,
        admins = setOf(requestingContributor),
        response = response,
    )
}

fun List<SurveyDto>.convertToDto(
    contributor: SimpleContributor?,
    filter: ListSurveyFilter,
    apiConfigs: ApiConfigs,
    request: ServerRequest,
): CollectionModel<SurveyDto> {
    // Fix this when Spring HATEOAS provides consistent support for reactive/coroutines
    val pair = generateCollectionModel()
    return pair.second.resolveHypermedia(
        contributor,
        filter,
        apiConfigs,
        request,
    )
}

private fun MultiValueMap<String, String>.toQueryFilter(): ListSurveyFilter {
    return ListSurveyFilter(
        surveyKey = get(SurveyQueryParams.SURVEY_KEY.param)?.flatMap { it.split(",") },
    )
}
