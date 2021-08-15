package com.tsab.pikapp.models.model

import com.google.gson.annotations.SerializedName

enum class AdvanceMenuTemplateType {
    CHECKBOX, RADIO;

    override fun toString(): String = if (this == CHECKBOX) "CHECKBOX" else "RADIO"
    fun isMandatory(): Boolean = this != CHECKBOX
}

data class AddAdvanceMenuRequest(
    @SerializedName("advance_menus")
    var advanceMenus: List<AdvanceMenu>
)

data class AddAdvanceMenuResponse(
    @SerializedName("err_code")
    var errorCode: String,
    @SerializedName("err_message")
    var errorMessage: String,

    @SerializedName("results")
    var result: List<AdvanceMenu>
)

data class ListAdvanceMenuResponse(
    @SerializedName("err_code")
    var errorCode: String,
    @SerializedName("err_message")
    var errorMessage: String,

    @SerializedName("results")
    var results: List<AdvanceMenu>
)

data class AdvanceMenu(
    @SerializedName("template_name")
    var templateName: String,
    @SerializedName("template_type")
    var templateType: String,

    @SerializedName("active")
    var isActive: Boolean,
    @SerializedName("mandatory")
    var isMandatory: Boolean,

    @SerializedName("max_choose")
    var maxChoice: Int,
    @SerializedName("ext_menus")
    var advanceAdditionalMenus: List<AdvanceAdditionalMenu>
)

data class AdvanceAdditionalMenu(
    @SerializedName("ext_menu_name")
    var advanceAdditionalMenuName: String,
    @SerializedName("ext_menu_price")
    var advanceAdditionalMenuPrice: String
)