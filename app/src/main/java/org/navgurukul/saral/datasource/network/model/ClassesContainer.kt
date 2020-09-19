package org.navgurukul.saral.datasource.network.model


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ClassesContainer(
    @SerializedName("classes")
    var classes: List<Classes?>?
) {
    data class Classes(
        @SerializedName("category_id")
        var categoryId: Int?,
        @SerializedName("class_type")
        var classType: String?,
        @SerializedName("course_id")
        var courseId: String?,
        @SerializedName("description")
        var description: String?,
        @SerializedName("end_time")
        var endTime: String?,
        @SerializedName("exercise_id")
        var exerciseId: Any?,
        @SerializedName("facilitator_id")
        var facilitatorId: Int?,
        @SerializedName("id")
        var id: Int?,
        @SerializedName("lang")
        var lang: String?,
        @SerializedName("start_time")
        var startTime: String?,
        @SerializedName("title")
        var title: String?,
        @SerializedName("video_id")
        var videoId: String?
    ) : Serializable
}