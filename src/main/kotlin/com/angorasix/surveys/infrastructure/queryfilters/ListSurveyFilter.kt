package com.angorasix.surveys.infrastructure.queryfilters

import com.angorasix.surveys.presentation.dto.SurveyQueryParams
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap

/**
 * <p>
 *     Classes containing different Request Query Filters.
 * </p>
 *
 * @author rozagerardo
 */
data class ListSurveyFilter(
    val surveyKey: Collection<String>? = null,
    val contributorId: Collection<String>? = null,
    val ids: Collection<String>? = null, // survey id
) {
    fun toMultiValueMap(): MultiValueMap<String, String> {
        val multiMap: MultiValueMap<String, String> = LinkedMultiValueMap()
        surveyKey?.let {
            multiMap.add(
                SurveyQueryParams.SURVEY_KEY.param,
                surveyKey.joinToString(","),
            )
        }
        return multiMap
    }
}
