package com.tsab.pikapp.models.model

data class TutorialPostRequest(
    var merchant_id: String,
    var tutorial_type: String
)

data class TutorialPostResponse(
    var errCode: String,
    var errMessage: String
)

data class TutorialGetResponse(
    var errCode: String,
    var errMessage: String,
    var results: List<TutorialData>
)

data class TutorialData(
    var finish: Boolean,
    var tutorial_page: String,
    var update_at: String
)