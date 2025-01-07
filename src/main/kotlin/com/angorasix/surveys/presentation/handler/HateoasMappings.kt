package com.angorasix.surveys.presentation.handler

import com.angorasix.commons.domain.SimpleContributor
import com.angorasix.surveys.infrastructure.config.configurationproperty.api.ApiConfigs
import com.angorasix.surveys.infrastructure.queryfilters.ListSurveyFilter
import com.angorasix.surveys.presentation.dto.SurveyResponseDto
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.Link
import org.springframework.hateoas.mediatype.Affordances
import org.springframework.hateoas.server.core.EmbeddedWrapper
import org.springframework.hateoas.server.core.EmbeddedWrappers
import org.springframework.http.HttpMethod
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.util.UriComponentsBuilder

/**
 * <p>
 * </p>
 *
 * @author rozagerardo
 */
fun List<SurveyResponseDto>.generateCollectionModel(): Pair<Boolean, CollectionModel<SurveyResponseDto>> {
    val collectionModel = if (this.isEmpty()) {
        val wrappers = EmbeddedWrappers(false)
        val wrapper: EmbeddedWrapper = wrappers.emptyCollectionOf(SurveyResponseDto::class.java)
        CollectionModel.of(listOf(wrapper)) as CollectionModel<SurveyResponseDto>
    } else {
        CollectionModel.of(this).withFallbackType(SurveyResponseDto::class.java)
    }
    return Pair(this.isEmpty(), collectionModel)
}

fun CollectionModel<SurveyResponseDto>.resolveHypermedia(
    requestingContributor: SimpleContributor?,
    filter: ListSurveyFilter,
    apiConfigs: ApiConfigs,
    request: ServerRequest,
): CollectionModel<SurveyResponseDto> {
    val listSurveys = apiConfigs.routes.listSurveys
    // self
    val selfLink = Link.of(
        uriBuilder(request).path(listSurveys.resolvePath())
            .queryParams(filter.toMultiValueMap()).build()
            .toUriString(),
    ).withSelfRel()
    val selfLinkWithDefaultAffordance =
        Affordances.of(selfLink).afford(HttpMethod.OPTIONS).withName("default").toLink()
    add(selfLinkWithDefaultAffordance)
    if (requestingContributor != null && requestingContributor.isAdminHint == true) {
        // here goes admin-specific collection hypermedia
    }
    return this
}

fun SurveyResponseDto.resolveHypermedia(
    apiConfigs: ApiConfigs,
    request: ServerRequest,
): SurveyResponseDto {
    val getSingleRoute = apiConfigs.routes.getSurvey
    // self
    val selfLink =
        Link.of(uriBuilder(request).path(getSingleRoute.resolvePath()).build().toUriString())
            .withRel(getSingleRoute.name).expand(id).withSelfRel()
    val selfLinkWithDefaultAffordance =
        Affordances.of(selfLink).afford(HttpMethod.OPTIONS).withName("default").toLink()
    add(selfLinkWithDefaultAffordance)

    return this
}

private fun uriBuilder(request: ServerRequest) = request.requestPath().contextPath().let {
    UriComponentsBuilder.fromHttpRequest(request.exchange().request).replacePath(it.toString()) //
        .replaceQuery("")
}
