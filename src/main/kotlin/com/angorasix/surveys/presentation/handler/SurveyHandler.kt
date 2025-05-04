package com.angorasix.surveys.presentation.handler

import com.angorasix.commons.domain.A6Contributor
import com.angorasix.commons.infrastructure.constants.AngoraSixInfrastructure
import com.angorasix.commons.reactive.presentation.error.resolveBadRequest
import com.angorasix.surveys.application.SurveyService
import com.angorasix.surveys.domain.survey.SurveyResponse
import com.angorasix.surveys.infrastructure.config.configurationproperty.api.ApiConfigs
import com.angorasix.surveys.infrastructure.queryfilters.ListSurveyFilter
import com.angorasix.surveys.presentation.dto.SurveyQueryParams
import com.angorasix.surveys.presentation.dto.SurveyResponseDto
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
        return if (requestingContributor is A6Contributor) {
            val filter = request.queryParams().toQueryFilter()
            service
                .findSurveys(filter, requestingContributor)
                .map {
                    it.convertToDto(
                        apiConfigs,
                        request,
                    )
                }.let {
                    ok()
                        .contentType(MediaTypes.HAL_FORMS_JSON)
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
    suspend fun registerSurveyResponse(request: ServerRequest): ServerResponse {
        val requestingContributor =
            request.attributes()[AngoraSixInfrastructure.REQUEST_ATTRIBUTE_CONTRIBUTOR_KEY]
        val surveyKey = request.pathVariable("surveyKey")

        val survey =
            try {
                request
                    .awaitBody<Map<String, Any>>()
                    .toSurveyResponseDto(surveyKey)
                    .convertToDomain(requestingContributor as A6Contributor?)
            } catch (e: IllegalArgumentException) {
                return resolveBadRequest(
                    e.message ?: "Incorrect Survey body",
                    "Survey",
                )
            }

        val outputSurvey =
            service
                .registerSurveyResponse(survey)
                .convertToDto(apiConfigs, request)

        val selfLink =
            outputSurvey.links.getRequiredLink(IanaLinkRelations.SELF).href

        return created(URI.create(selfLink))
            .contentType(MediaTypes.HAL_FORMS_JSON)
            .bodyValueAndAwait(outputSurvey)
    }
}

private fun SurveyResponse.convertToDto(): SurveyResponseDto = SurveyResponseDto(surveyKey = surveyKey, response = response, id = id)

private fun SurveyResponse.convertToDto(
    apiConfigs: ApiConfigs,
    request: ServerRequest,
): SurveyResponseDto = convertToDto().resolveHypermedia(apiConfigs, request)

private fun Map<String, Any>.toSurveyResponseDto(
    surveyKey: String,
    id: String? = null,
): SurveyResponseDto =
    SurveyResponseDto(
        surveyKey = surveyKey,
        response = this,
        id = id,
    )

private fun SurveyResponseDto.convertToDomain(requestingContributor: A6Contributor?): SurveyResponse =
    SurveyResponse(
        surveyKey = surveyKey,
        contributorId = requestingContributor?.contributorId,
        admins = requestingContributor?.let { setOf(it) } ?: emptySet(),
        response = response,
    )

fun List<SurveyResponseDto>.convertToDto(
    contributor: A6Contributor?,
    filter: ListSurveyFilter,
    apiConfigs: ApiConfigs,
    request: ServerRequest,
): CollectionModel<SurveyResponseDto> {
    // Fix this when Spring HATEOAS provides consistent support for reactive/coroutines
    val pair = generateCollectionModel()
    return pair.second.resolveHypermedia(
        contributor,
        filter,
        apiConfigs,
        request,
    )
}

private fun MultiValueMap<String, String>.toQueryFilter(): ListSurveyFilter =
    ListSurveyFilter(
        surveyKey = get(SurveyQueryParams.SURVEY_KEY.param)?.flatMap { it.split(",") },
    )
