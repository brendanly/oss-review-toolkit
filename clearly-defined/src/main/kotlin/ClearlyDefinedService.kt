/*
 * Copyright (C) 2019 Bosch Software Innovations GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * License-Filename: LICENSE
 */

package com.here.ort.clearlydefined

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

import java.io.File
import java.net.URL

import okhttp3.OkHttpClient

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

/**
 * Interface for the ClearlyDefined REST API, based on code generated by https://app.quicktype.io/ from
 * https://github.com/clearlydefined/service/tree/master/schemas.
 */
interface ClearlyDefinedService {
    companion object {
        /**
         * Create a ClearlyDefined service instance for communicating with the given [server], optionally using a
         * pre-built OkHttp [client].
         */
        fun create(server: Server, client: OkHttpClient? = null): ClearlyDefinedService {
            val retrofit = Retrofit.Builder()
                .apply { if (client != null) client(client) }
                .baseUrl(server.url)
                .addConverterFactory(JacksonConverterFactory.create(JsonMapper().registerKotlinModule()))
                .build()

            return retrofit.create(ClearlyDefinedService::class.java)
        }
    }

    /**
     * See https://github.com/clearlydefined/service/blob/661934a/schemas/swagger.yaml#L8-L14.
     */
    enum class Server(val url: String) {
        /**
         * This creates PRs against https://github.com/clearlydefined/curated-data.
         */
        PRODUCTION("https://api.clearlydefined.io"),

        /**
         * This creates PRs against https://github.com/clearlydefined/curated-data-dev.
         */
        DEVELOPMENT("https://dev-api.clearlydefined.io"),

        LOCALHOST("http://localhost:4000")
    }

    /**
     * See https://github.com/clearlydefined/service/blob/b339cb7/schemas/curation-1.0.json#L7-L16.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    data class Curation(
        val described: Described? = null,
        val files: List<FileEntry>? = null,
        val licensed: Licensed? = null
    )

    /**
     * See https://github.com/clearlydefined/service/blob/b339cb7/schemas/curation-1.0.json#L66-L115.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    data class Described(
        val facets: Facets? = null,
        val issueTracker: URL? = null,
        val projectWebsite: URL? = null,
        val releaseDate: String? = null,
        val sourceLocation: SourceLocation? = null
    )

    /**
     * See https://github.com/clearlydefined/service/blob/b339cb7/schemas/curation-1.0.json#L70-L86.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    data class Facets(
        val data: List<String>? = null,
        val dev: List<String>? = null,
        val doc: List<String>? = null,
        val examples: List<String>? = null,
        val tests: List<String>? = null
    )

    /**
     * See https://github.com/clearlydefined/service/blob/b339cb7/schemas/curation-1.0.json#L137-L177.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    data class SourceLocation(
        // The following properties match those of Coordinates, except that the revision is mandatory here.
        val type: ComponentType,
        val provider: Provider,
        val namespace: String? = null,
        val name: String,
        val revision: String,

        val path: String? = null,
        val url: String? = null
    )

    /**
     * See https://github.com/clearlydefined/service/blob/b339cb7/schemas/curation-1.0.json#L46-L57.
     */
    enum class Provider(val value: String) {
        COCOAPODS("cocoapods"),
        CRATES_IO("cratesio"),
        DEBIAN("debian"),
        GITHUB("github"),
        MAVEN_CENTRAL("mavencentral"),
        NPM_JS("npmjs"),
        NUGET("nuget"),
        PACKAGIST("packagist"),
        PYPI("pypi"),
        RUBYGEMS("rubygems");

        companion object {
            @JsonCreator
            @JvmStatic
            fun fromString(value: String) = enumValues<Provider>().single { value.equals(it.value, ignoreCase = true) }
        }

        @JsonValue
        override fun toString() = value
    }

    /**
     * See https://github.com/clearlydefined/service/blob/b339cb7/schemas/curation-1.0.json#L25-L38.
     */
    enum class ComponentType(val value: String) {
        COMPOSER("composer"),
        CRATE("crate"),
        DEBIAN("deb"),
        DEBIAN_SOURCES("debsrc"),
        GEM("gem"),
        GIT("git"),
        MAVEN("maven"),
        NPM("npm"),
        NUGET("nuget"),
        POD("pod"),
        PYPI("pypi"),
        SOURCE_ARCHIVE("sourcearchive");

        companion object {
            @JsonCreator
            @JvmStatic
            fun fromString(value: String) =
                enumValues<ComponentType>().single { value.equals(it.value, ignoreCase = true) }
        }

        @JsonValue
        override fun toString() = value
    }

    /**
     * See https://github.com/clearlydefined/service/blob/b339cb7/schemas/curation-1.0.json#L190-L218.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    data class FileEntry(
        val attributions: List<String>? = null,
        val license: String? = null,
        val path: File
    )

    /**
     * See https://github.com/clearlydefined/service/blob/b339cb7/schemas/curation-1.0.json#L232-L236.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    data class Licensed(
        val declared: String? = null
    )

    /**
     * See https://github.com/clearlydefined/service/blob/4e210d7/schemas/swagger.yaml#L84-L101.
     */
    data class ContributionPatch(
        val contributionInfo: ContributionInfo,
        val patches: List<Patch>
    )

    /**
     * See https://github.com/clearlydefined/service/blob/4e210d7/schemas/swagger.yaml#L87-L97.
     */
    data class ContributionInfo(
        val type: ContributionType,

        /**
         * Short (100 char) description. This will also be used as the PR title.
         */
        val summary: String,

        /**
         * Describe here the problem(s) being addressed.
         */
        val details: String,

        /**
         * What does this PR do to address the issue? Include references to docs where the new data was found and, for
         * example, links to public conversations with the affected project team.
         */
        val resolution: String,

        /**
         * Remove contributed definitions from the list.
         */
        val removedDefinitions: Boolean
    )

    /**
     * See https://github.com/clearlydefined/website/blob/43ec5e3/src/components/ContributePrompt.js#L78-L82.
     */
    enum class ContributionType {
        MISSING,
        INCORRECT,
        INCOMPLETE,
        AMBIGUOUS,
        OTHER;

        companion object {
            @JsonCreator
            @JvmStatic
            fun fromString(value: String) =
                enumValues<ContributionType>().single { value.equals(it.name, ignoreCase = true) }
        }

        @JsonValue
        override fun toString() = name.toLowerCase().capitalize()
    }

    /**
     * See https://github.com/clearlydefined/service/blob/b339cb7/schemas/curations-1.0.json#L8-L15.
     */
    data class Patch(
        val coordinates: Coordinates,
        val revisions: Map<String, Curation>
    )

    /**
     * See https://github.com/clearlydefined/service/blob/b339cb7/schemas/curations-1.0.json#L64-L83 and
     * https://docs.clearlydefined.io/using-data#a-note-on-definition-coordinates.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    data class Coordinates(
        /**
         * The type of the component. For example, npm, git, nuget, maven, etc. This talks about the shape of the
         * component.
         */
        val type: ComponentType,

        /**
         * Where the component can be found. Examples include npmjs, mavencentral, github, nuget, etc.
         */
        val provider: Provider,

        /**
         * Many component systems have namespaces: GitHub orgs, NPM namespace, Maven group id, etc. This segment must be
         * supplied. If your component does not have a namespace, use '-' (ASCII hyphen).
         */
        val namespace: String? = null,

        /**
         * The name of the component. Given the mentioned [namespace] segment, this is just the simple name.
         */
        val name: String,

        /**
         * Components typically have some differentiator like a version or commit id. Use that here. If this segment is
         * omitted, the latest revision is used (if that makes sense for the provider).
         */
        val revision: String? = null
    ) {
        override fun toString() = listOfNotNull(type, provider, namespace ?: "-", name, revision).joinToString("/")
    }

    /**
     * See https://github.com/clearlydefined/service/blob/53acc01/routes/curations.js#L86-L89.
     */
    data class ContributionSummary(
        val prNumber: Int,
        val url: String
    )

    /**
     * See https://github.com/clearlydefined/service/blob/c47a989/app.js#L201-L205.
     */
    data class ErrorResponse(
        val error: Error
    )

    data class Error(
        val code: String,
        val message: String,
        val innererror: InnerError
    )

    data class InnerError(
        val name: String,
        val message: String,
        val stack: String
    )

    /**
     * Get a curation for a component revision, see
     * https://api.clearlydefined.io/api-docs/#/curations/get_curations__type___provider___namespace___name___revision_.
     */
    @GET("curations/{type}/{provider}/{namespace}/{name}/{revision}")
    fun getCuration(
        @Path("type") type: ComponentType,
        @Path("provider") provider: Provider,
        @Path("namespace") namespace: String,
        @Path("name") name: String,
        @Path("revision") revision: String
    ): Call<Curation>

    /**
     * Upload curation data, see
     * https://api.clearlydefined.io/api-docs/#/curations/patch_curations.
     */
    @PATCH("curations")
    fun putCuration(@Body patch: ContributionPatch): Call<ContributionSummary>
}
